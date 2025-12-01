package com.startup.trucking.service;

import com.startup.trucking.billing.PaymentGateway;
import com.startup.trucking.billing.PaymentGateway.PaymentReceipt;
import com.startup.trucking.billing.PaymentGatewayRegistry;
import com.startup.trucking.billing.PaymentMethod;
import com.startup.trucking.persistence.Invoice;
import com.startup.trucking.persistence.InvoiceRepository;
import com.startup.trucking.persistence.Payment;
import com.startup.trucking.persistence.PaymentRepository;
import com.startup.trucking.util.PaymentIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository payments;
    private final InvoiceRepository invoices;
    private final PaymentGatewayRegistry gateways;

    public PaymentService(PaymentRepository payments, InvoiceRepository invoices,
                          PaymentGatewayRegistry gateways) {
        this.payments = payments;
        this.invoices = invoices;
        this.gateways = gateways;
    }

    public List<Payment> listForInvoice(String invoiceId) {
        return payments.findByInvoiceId(invoiceId);
    }

    public Payment get(String paymentId) {
        return payments.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
    }

    @Transactional
    public Payment pay(String invoiceId, PaymentMethod method, BigDecimal amount, String reference) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Invoice inv = invoices.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));

        if ("Paid".equalsIgnoreCase(inv.getStatus())) {
            throw new IllegalStateException("Invoice already Paid");
        }

        BigDecimal paidSoFar = payments.findByInvoiceId(invoiceId).stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remaining = inv.getTotal().subtract(paidSoFar);
        if (amount.compareTo(remaining) > 0) {
            throw new IllegalArgumentException("Payment exceeds remaining balance");
        }

        PaymentGateway gateway = gateways.resolve(method);
        PaymentReceipt r = gateway.charge(invoiceId, amount.setScale(2), reference);

        Payment p = new Payment();
//        p.setId("PAY-" + UUID.randomUUID());
        p.setId(PaymentIdGenerator.getInstance().nextId());
        p.setInvoiceId(invoiceId);
        p.setAmount(amount.setScale(2));
        p.setMethod(method.name());
        p.setPaidAt(r.at() != null ? r.at() : OffsetDateTime.now());
        p.setReference(r.authCode());
        p.setStatus(r.status());
        p.setProvider(r.provider());
        p.setAuthCode(r.authCode());
        payments.save(p);

        // update invoice status
        BigDecimal newPaid = paidSoFar.add(p.getAmount());
        inv.setStatus(newPaid.compareTo(inv.getTotal()) >= 0 ? "Paid" : "Partially Paid");
        invoices.save(inv);

        return p;
    }
}
