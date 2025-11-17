package com.startup.trucking.service;

import com.startup.trucking.domain.Load;
import com.startup.trucking.persistence.Invoice;
import com.startup.trucking.persistence.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class InvoiceService {

    private final InvoiceRepository invoices;
    private final LoadService loads;
    private final AtomicInteger invoiceSeq;

    public InvoiceService(InvoiceRepository invoices, LoadService loads) {
        this.invoices = invoices;
        this.loads = loads;

        int seed = invoices.findAll().stream()
                .map(Invoice::getId)
                .filter(Objects::nonNull)
                .filter(id -> id.startsWith("INV-"))
                .mapToInt(id -> {
                    try { return Integer.parseInt(id.substring(4)); }
                    catch (Exception e) { return -1; }
                })
                .max().orElse(-1);
        this.invoiceSeq = new AtomicInteger(seed);
    }

    private String nextInvoiceId() {
        int next = invoiceSeq.updateAndGet(i -> {
            int n = i + 1;
            return (n > 100000) ? 0 : n;   // wrap at 100000
        });
        String candidate = String.format("INV-%06d", next);

        if (invoices.findById(candidate).isPresent()) {
            for (int i = 0; i <= 100000; i++) {
                String alt = String.format("INV-%06d", i);
                if (invoices.findById(alt).isEmpty()) {
                    invoiceSeq.set(i);
                    return alt;
                }
            }
            throw new IllegalStateException("No available invoice IDs in range 0..100000");
        }
        return candidate;
    }

    public java.util.Optional<Invoice> findByLoadId(String loadId) {
        return invoices.findByLoadId(loadId);
    }

    public List<Invoice> list() { return invoices.findAll(); }

    public Invoice get(String id) {
        return invoices.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + id));
    }

    @Transactional
    public Invoice createFromLoad(String loadId) {
        invoices.findByLoadId(loadId).ifPresent(i -> {
            throw new IllegalArgumentException("Invoice already exists for load " + loadId);
        });

        Load l = loads.getLoad(loadId);
        if (!"Delivered".equalsIgnoreCase(l.getStatus())) {
            throw new IllegalArgumentException("Load must be Delivered to create invoice");
        }

        BigDecimal subtotal = BigDecimal.valueOf(l.getRateAmount()).setScale(2);
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal total = subtotal.add(tax);

        Invoice inv = new Invoice();
        inv.setId(nextInvoiceId());
        inv.setLoadId(loadId);
        inv.setCustomerRef(l.getReferenceNo());
        inv.setIssuedAt(OffsetDateTime.now());
        inv.setStatus("Draft");
        inv.setSubtotal(subtotal);
        inv.setTax(tax);
        inv.setTotal(total);

        return invoices.save(inv);
    }

    @Transactional
    public Invoice createManual(String loadId, float amount) {
        Load l = loads.getLoad(loadId);
        if (!"Delivered".equalsIgnoreCase(l.getStatus())) {
            throw new IllegalArgumentException("Load must be Delivered to create invoice");
        }
        invoices.findByLoadId(loadId).ifPresent(i -> {
            throw new IllegalArgumentException("Invoice already exists for load " + loadId);
        });

        BigDecimal subtotal = BigDecimal.valueOf(amount).setScale(2);
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal total = subtotal;

        Invoice inv = new Invoice();
        inv.setId(nextInvoiceId());
        inv.setLoadId(loadId);
        inv.setCustomerRef(l.getReferenceNo());
        inv.setIssuedAt(OffsetDateTime.now());
        inv.setStatus("Draft");
        inv.setSubtotal(subtotal);
        inv.setTax(tax);
        inv.setTotal(total);
        return invoices.save(inv);
    }

    @Transactional
    public void markSent(String invoiceId) {
        var inv = get(invoiceId);
        inv.setStatus("Sent");
        invoices.save(inv);
    }
}
