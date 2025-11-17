package com.startup.trucking.web;

import com.startup.trucking.domain.Load;
import com.startup.trucking.service.InvoiceService;
import com.startup.trucking.service.LoadService;
import com.startup.trucking.persistence.Invoice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.startup.trucking.billing.PaymentMethod;
import com.startup.trucking.persistence.Payment;
import com.startup.trucking.service.PaymentService;
import java.math.BigDecimal;


import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService billing;
    private final LoadService loads;
    private final PaymentService paymentService;

    public InvoiceController(InvoiceService billing, LoadService loads, PaymentService paymentService) {
        this.billing = billing;
        this.loads = loads;
        this.paymentService = paymentService;
    }

    @GetMapping
    public String list(Model model, @ModelAttribute("toast") String toast) {
        List<Invoice> items = billing.list();

        // For the "create invoice" form: only Delivered loads that don't have invoices yet
        List<Load> delivered = loads.listLoads().stream()
                .filter(l -> "Delivered".equalsIgnoreCase(l.getStatus()))
                .filter(l -> billing.list().stream().noneMatch(i -> i.getLoadId().equals(l.getId())))
                .collect(Collectors.toList());

        record Row(Invoice inv, BigDecimal paid, BigDecimal balance) {}
        var rows = items.stream().map(i -> {
            var paid = paymentService.listForInvoice(i.getId()).stream()
                    .map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            var balance = i.getTotal().subtract(paid);
            return new Row(i, paid, balance);
        }).toList();

        model.addAttribute("rows", rows);
        model.addAttribute("deliveredLoads", delivered);
        return "invoices";
    }

    // Create from a Delivered load
    @PostMapping("/from-load/{loadId}")
    public String createFromLoad(@PathVariable String loadId, RedirectAttributes ra) {
        var inv = billing.createFromLoad(loadId);
        ra.addFlashAttribute("toast", "Invoice " + inv.getId() + " created for load " + loadId);
        return "redirect:/invoices";
    }

    // Manual/Direct create
    @PostMapping
    public String createManual(@RequestParam String loadId,
                               @RequestParam float amount,
                               RedirectAttributes ra) {
        var inv = billing.createManual(loadId, amount);
        ra.addFlashAttribute("toast", "Invoice " + inv.getId() + " created.");
        return "redirect:/invoices";
    }

    @PostMapping("/{id}/send")
    public String send(@PathVariable String id, RedirectAttributes ra) {
        billing.markSent(id);
        ra.addFlashAttribute("toast", "Invoice " + id + " marked Sent.");
        return "redirect:/invoices";
    }

    @GetMapping("/{id}/pay")
    public String payPage(@PathVariable String id, Model model,
                          @RequestParam(value = "error", required = false) String error) {
        var inv = billing.get(id);

        var paid = paymentService.listForInvoice(id).stream()
                .map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        var balance = inv.getTotal().subtract(paid);

        model.addAttribute("invoice", inv);
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
                      RedirectAttributes ra) {
        var p = paymentService.pay(id, PaymentMethod.valueOf(method), amount, "DEMO");
        return "redirect:/invoices/" + id + "/pay/confirm?paymentId=" + p.getId();
    }

    @GetMapping("/{id}/pay/confirm")
    public String confirm(@PathVariable String id,
                          @RequestParam String paymentId,
                          Model model) {
        var inv = billing.get(id);
        var payment = paymentService.get(paymentId);

        var paid = paymentService.listForInvoice(id).stream()
                .map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        var balance = inv.getTotal().subtract(paid);

        model.addAttribute("invoice", inv);
        model.addAttribute("payment", payment);
        model.addAttribute("paid", paid);
        model.addAttribute("balance", balance);
        return "payment-confirmation";
    }
}
