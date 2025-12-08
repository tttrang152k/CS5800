package com.startup.trucking.service;

import com.startup.trucking.billing.tax.TaxStrategyResolver;
import com.startup.trucking.domain.Load;
import com.startup.trucking.persistence.Invoice;
import com.startup.trucking.persistence.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class InvoiceService {

    private static final String INVOICE_PREFIX = "INV-";
    private static final int MAX_SEQUENCE = 100_000;
    private static final String STATUS_DRAFT = "Draft";
    private static final String STATUS_SENT = "Sent";
    private static final String STATUS_DELIVERED = "Delivered";

    private final InvoiceRepository invoiceRepository;
    private final LoadService loadService;
    private final AtomicInteger invoiceSequence;
    private final TaxStrategyResolver taxResolver;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          LoadService loadService,
                          TaxStrategyResolver taxResolver) {
        this.invoiceRepository = invoiceRepository;
        this.loadService = loadService;
        this.taxResolver = taxResolver;
        this.invoiceSequence = initializeSequence(invoiceRepository.findAll());
    }

    // ---------------------------------------------------------------------
    // Public API
    // ---------------------------------------------------------------------

    public Optional<Invoice> findByLoadId(String loadId) {
        return invoiceRepository.findByLoadId(loadId);
    }

    public List<Invoice> list() {
        return invoiceRepository.findAll();
    }

    public Invoice get(String id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + id));
    }

    @Transactional
    public Invoice createFromLoad(String loadId) {
        Load load = loadService.getLoad(loadId);
        ensureInvoiceCanBeCreated(loadId, load);

        BigDecimal subtotal = BigDecimal.valueOf(load.getRateAmount()).setScale(2);
        return createInvoice(loadId, load, subtotal);
    }

    @Transactional
    public Invoice createManual(String loadId, float amount) {
        Load load = loadService.getLoad(loadId);
        ensureInvoiceCanBeCreated(loadId, load);

        BigDecimal subtotal = BigDecimal.valueOf(amount).setScale(2);
        return createInvoice(loadId, load, subtotal);
    }

    @Transactional
    public void markSent(String invoiceId) {
        Invoice invoice = get(invoiceId);
        invoice.setStatus(STATUS_SENT);
        invoiceRepository.save(invoice);
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private AtomicInteger initializeSequence(List<Invoice> existingInvoices) {
        int seed = existingInvoices.stream()
                .map(Invoice::getId)
                .filter(Objects::nonNull)
                .filter(id -> id.startsWith(INVOICE_PREFIX))
                .mapToInt(this::parseInvoiceNumberSafely)
                .max()
                .orElse(-1);
        return new AtomicInteger(seed);
    }

    private int parseInvoiceNumberSafely(String id) {
        try {
            return Integer.parseInt(id.substring(INVOICE_PREFIX.length()));
        } catch (Exception ex) {
            return -1;
        }
    }

    private String nextInvoiceId() {
        int next = invoiceSequence.updateAndGet(current -> {
            int candidate = current + 1;
            return (candidate > MAX_SEQUENCE) ? 0 : candidate;
        });

        String candidateId = formatInvoiceId(next);

        if (invoiceRepository.findById(candidateId).isPresent()) {
            for (int i = 0; i <= MAX_SEQUENCE; i++) {
                String alternativeId = formatInvoiceId(i);
                if (invoiceRepository.findById(alternativeId).isEmpty()) {
                    invoiceSequence.set(i);
                    return alternativeId;
                }
            }
            throw new IllegalStateException(
                    "No available invoice IDs in range 0.." + MAX_SEQUENCE);
        }

        return candidateId;
    }

    private String formatInvoiceId(int sequence) {
        return String.format("%s%06d", INVOICE_PREFIX, sequence);
    }

    private void ensureInvoiceCanBeCreated(String loadId, Load load) {
        invoiceRepository.findByLoadId(loadId).ifPresent(existing -> {
            throw new IllegalArgumentException("Invoice already exists for load " + loadId);
        });

        if (!STATUS_DELIVERED.equalsIgnoreCase(load.getStatus())) {
            throw new IllegalArgumentException("Load must be Delivered to create invoice");
        }
    }

    private Invoice createInvoice(String loadId, Load load, BigDecimal subtotal) {
        BigDecimal tax = taxResolver.compute(load, subtotal);
        BigDecimal total = subtotal.add(tax);

        Invoice invoice = new Invoice();
        invoice.setId(nextInvoiceId());
        invoice.setLoadId(loadId);
        invoice.setCustomerRef(load.getReferenceNo());
        invoice.setIssuedAt(OffsetDateTime.now());
        invoice.setStatus(STATUS_DRAFT);
        invoice.setSubtotal(subtotal);
        invoice.setTax(tax);
        invoice.setTotal(total);

        return invoiceRepository.save(invoice);
    }
}
