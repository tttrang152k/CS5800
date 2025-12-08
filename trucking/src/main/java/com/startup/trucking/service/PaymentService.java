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

@Service
public class PaymentService {

    private static final String STATUS_PAID = "Paid";
    private static final String STATUS_PARTIALLY_PAID = "Partially Paid";

    private static final String ERROR_AMOUNT_MUST_BE_POSITIVE = "Amount must be positive";
    private static final String ERROR_INVOICE_NOT_FOUND_PREFIX = "Invoice not found: ";
    private static final String ERROR_INVOICE_ALREADY_PAID = "Invoice already Paid";
    private static final String ERROR_PAYMENT_EXCEEDS_REMAINING = "Payment exceeds remaining balance";
    private static final String ERROR_PAYMENT_NOT_FOUND_PREFIX = "Payment not found: ";

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentGatewayRegistry gatewayRegistry;

    public PaymentService(PaymentRepository paymentRepository,
                          InvoiceRepository invoiceRepository,
                          PaymentGatewayRegistry gatewayRegistry) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.gatewayRegistry = gatewayRegistry;
    }

    public List<Payment> listForInvoice(String invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId);
    }

    public Payment get(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(ERROR_PAYMENT_NOT_FOUND_PREFIX + paymentId));
    }

    @Transactional
    public Payment pay(String invoiceId,
                       PaymentMethod method,
                       BigDecimal amount,
                       String reference) {
        validateAmount(amount);

        Invoice invoice = loadInvoice(invoiceId);
        ensureInvoiceNotPaid(invoice);

        BigDecimal paidSoFar = calculatePaidSoFar(invoiceId);
        BigDecimal remaining = calculateRemaining(invoice.getTotal(), paidSoFar);

        if (amount.compareTo(remaining) > 0) {
            throw new IllegalArgumentException(ERROR_PAYMENT_EXCEEDS_REMAINING);
        }

        PaymentGateway gateway = gatewayRegistry.resolve(method);
        PaymentReceipt receipt = gateway.charge(invoiceId, amount.setScale(2), reference);

        Payment payment = buildPayment(invoiceId, method, amount, receipt);
        paymentRepository.save(payment);

        BigDecimal newPaid = paidSoFar.add(payment.getAmount());
        updateInvoiceStatus(invoice, newPaid);
        invoiceRepository.save(invoice);

        return payment;
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(ERROR_AMOUNT_MUST_BE_POSITIVE);
        }
    }

    private Invoice loadInvoice(String invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new IllegalArgumentException(ERROR_INVOICE_NOT_FOUND_PREFIX + invoiceId));
    }

    private void ensureInvoiceNotPaid(Invoice invoice) {
        if (STATUS_PAID.equalsIgnoreCase(invoice.getStatus())) {
            throw new IllegalStateException(ERROR_INVOICE_ALREADY_PAID);
        }
    }

    private BigDecimal calculatePaidSoFar(String invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId).stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateRemaining(BigDecimal total, BigDecimal paidSoFar) {
        return total.subtract(paidSoFar);
    }

    private Payment buildPayment(String invoiceId,
                                 PaymentMethod method,
                                 BigDecimal amount,
                                 PaymentReceipt receipt) {
        Payment payment = new Payment();
        payment.setId(PaymentIdGenerator.getInstance().nextId());
        payment.setInvoiceId(invoiceId);
        payment.setAmount(amount.setScale(2));
        payment.setMethod(method.name());
        payment.setPaidAt(receipt.at() != null ? receipt.at() : OffsetDateTime.now());
        payment.setReference(receipt.authCode());
        payment.setStatus(receipt.status());
        payment.setProvider(receipt.provider());
        payment.setAuthCode(receipt.authCode());
        return payment;
    }

    private void updateInvoiceStatus(Invoice invoice, BigDecimal totalPaid) {
        String newStatus = totalPaid.compareTo(invoice.getTotal()) >= 0
                ? STATUS_PAID
                : STATUS_PARTIALLY_PAID;
        invoice.setStatus(newStatus);
    }
}
