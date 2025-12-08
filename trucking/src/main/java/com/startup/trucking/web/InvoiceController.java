package com.startup.trucking.web;

import com.startup.trucking.billing.PaymentMethod;
import com.startup.trucking.domain.Load;
import com.startup.trucking.notify.ChannelType;
import com.startup.trucking.persistence.Invoice;
import com.startup.trucking.persistence.Payment;
import com.startup.trucking.service.InvoiceService;
import com.startup.trucking.service.LoadService;
import com.startup.trucking.service.NotificationService;
import com.startup.trucking.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    private static final String STATUS_DELIVERED = "Delivered";

    private final InvoiceService invoiceService;
    private final LoadService loadService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    public record Row(Invoice inv, BigDecimal paid, BigDecimal balance) { }

    public InvoiceController(InvoiceService invoiceService,
                             LoadService loadService,
                             PaymentService paymentService,
                             NotificationService notificationService) {
        this.invoiceService = invoiceService;
        this.loadService = loadService;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String list(Model model, @ModelAttribute("toast") String toast) {
        List<Invoice> invoices = invoiceService.list();
        List<Row> rows = buildInvoiceRows(invoices);
        List<Load> deliveredLoads = findDeliveredLoadsWithoutInvoice(invoices);

        model.addAttribute("rows", rows);
        model.addAttribute("deliveredLoads", deliveredLoads);
        return "invoices";
    }

    @PostMapping("/from-load/{loadId}")
    public String createFromLoad(@PathVariable String loadId,
                                 @RequestParam(value = "channel", required = false) String channel,
                                 @RequestParam(value = "recipient", required = false) String recipient,
                                 RedirectAttributes redirectAttributes) {
        Invoice invoice = invoiceService.createFromLoad(loadId);
        notifyInvoiceCreatedIfRequested(invoice, channel, recipient, redirectAttributes);
        return "redirect:/invoices";
    }

    @PostMapping
    public String createManualOrFromLoad(@RequestParam String loadId,
                                         @RequestParam(value = "amount", required = false) BigDecimal amount,
                                         @RequestParam(value = "channel", required = false) String channel,
                                         @RequestParam(value = "recipient", required = false) String recipient,
                                         RedirectAttributes redirectAttributes) {

        Invoice invoice = createInvoice(loadId, amount);
        notifyInvoiceCreatedIfRequested(invoice, channel, recipient, redirectAttributes);

        return "redirect:/invoices";
    }

    @PostMapping("/{id}/send")
    public String send(@PathVariable String id, RedirectAttributes redirectAttributes) {
        invoiceService.markSent(id);
        redirectAttributes.addFlashAttribute("toast", "Invoice " + id + " marked Sent.");
        return "redirect:/invoices";
    }

    @GetMapping("/{id}/pay")
    public String payPage(@PathVariable String id,
                          Model model,
                          @RequestParam(value = "error", required = false) String error) {
        Invoice invoice = invoiceService.get(id);

        BigDecimal paid = paymentService.listForInvoice(id).stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal balance = invoice.getTotal().subtract(paid);

        model.addAttribute("invoice", invoice);
        model.addAttribute("paid", paid);
        model.addAttribute("balance", balance);
        model.addAttribute("methods", PaymentMethod.values());
        model.addAttribute("error", error);
        return "invoice-pay";
    }

    @PostMapping("/{id}/pay")
    public String pay(@PathVariable String id,
                      @RequestParam String method,
                      @RequestParam BigDecimal amount,
                      @RequestParam(value = "channel", required = false) String channel,
                      @RequestParam(value = "recipient", required = false) String recipient,
                      RedirectAttributes redirectAttributes) {
        Payment payment = paymentService.pay(id, PaymentMethod.valueOf(method), amount, "DEMO");
        notifyPaymentReceivedIfRequested(id, amount, method, channel, recipient, redirectAttributes);
        return "redirect:/invoices/" + id + "/pay/confirm?paymentId=" + payment.getId();
    }

    @GetMapping("/{id}/pay/confirm")
    public String confirm(@PathVariable String id,
                          @RequestParam String paymentId,
                          Model model) {
        Invoice invoice = invoiceService.get(id);
        Payment payment = paymentService.get(paymentId);

        BigDecimal paid = paymentService.listForInvoice(id).stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal balance = invoice.getTotal().subtract(paid);

        model.addAttribute("invoice", invoice);
        model.addAttribute("payment", payment);
        model.addAttribute("paid", paid);
        model.addAttribute("balance", balance);
        return "payment-confirmation";
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private List<Row> buildInvoiceRows(List<Invoice> invoices) {
        return invoices.stream()
                .map(invoice -> {
                    BigDecimal paid = paymentService.listForInvoice(invoice.getId()).stream()
                            .map(Payment::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal balance = invoice.getTotal().subtract(paid);
                    return new Row(invoice, paid, balance);
                })
                .toList();
    }

    private List<Load> findDeliveredLoadsWithoutInvoice(List<Invoice> invoices) {
        Set<String> invoicedLoadIds = invoices.stream()
                .map(Invoice::getLoadId)
                .collect(Collectors.toSet());

        return loadService.listLoads().stream()
                .filter(load -> STATUS_DELIVERED.equalsIgnoreCase(load.getStatus()))
                .filter(load -> !invoicedLoadIds.contains(load.getId()))
                .toList();
    }

    private Invoice createInvoice(String loadId, BigDecimal amount) {
        if (amount == null) {
            return invoiceService.createFromLoad(loadId);
        }
        return invoiceService.createManual(loadId, amount.floatValue());
    }

    private void notifyInvoiceCreatedIfRequested(Invoice invoice,
                                                 String channel,
                                                 String recipient,
                                                 RedirectAttributes redirectAttributes) {
        if (recipient == null || recipient.isBlank()
                || channel == null || channel.isBlank()) {
            redirectAttributes.addFlashAttribute(
                    "toast",
                    "Invoice " + invoice.getId() + " created."
            );
            return;
        }

        try {
            notificationService.sendInvoiceCreated(
                    ChannelType.valueOf(channel),
                    recipient,
                    invoice.getCustomerRef(),
                    invoice.getId(),
                    invoice.getLoadId(),
                    invoice.getTotal().toPlainString()
            );
            redirectAttributes.addFlashAttribute(
                    "toast",
                    "Invoice " + invoice.getId() + " created and customer notified."
            );
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute(
                    "toast",
                    "Invoice " + invoice.getId()
                            + " created (notification skipped: " + ex.getMessage() + ")."
            );
        }
    }

    private void notifyPaymentReceivedIfRequested(String invoiceId,
                                                  BigDecimal amount,
                                                  String method,
                                                  String channel,
                                                  String recipient,
                                                  RedirectAttributes redirectAttributes) {
        if (channel == null || channel.isBlank()
                || recipient == null || recipient.isBlank()) {
            redirectAttributes.addFlashAttribute("toast", "Payment recorded.");
            return;
        }

        Invoice invoice = invoiceService.get(invoiceId);
        try {
            notificationService.sendPaymentReceived(
                    ChannelType.valueOf(channel),
                    recipient,
                    invoice.getCustomerRef(),
                    invoice.getId(),
                    amount.toPlainString(),
                    method
            );
            redirectAttributes.addFlashAttribute(
                    "toast",
                    "Payment recorded and customer notified."
            );
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute(
                    "toast",
                    "Payment recorded (notification skipped: " + ex.getMessage() + ")."
            );
        }
    }

}



