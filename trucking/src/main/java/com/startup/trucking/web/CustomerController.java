package com.startup.trucking.web;

import com.startup.trucking.domain.Load;
import com.startup.trucking.persistence.Invoice;
import com.startup.trucking.persistence.NotificationRepository;
import com.startup.trucking.service.InvoiceService;
import com.startup.trucking.service.LoadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final LoadService loadService;
    private final InvoiceService invoiceService;
    private final NotificationRepository notifications;

    public CustomerController(LoadService loadService, InvoiceService invoiceService, NotificationRepository notifications) {
        this.loadService = loadService;
        this.invoiceService = invoiceService;
        this.notifications = notifications;
    }

    @GetMapping("/{customerRef}")
    public String detail(@PathVariable String customerRef, Model model) {

        List<Load> loads = loadService.listLoads().stream()
                .filter(l -> customerRef.equals(l.getReferenceNo()))
                .toList();

        List<Invoice> invoices = invoiceService.list().stream()
                .filter(i -> customerRef.equals(i.getCustomerRef()))
                .filter(i -> !"Draft".equalsIgnoreCase(i.getStatus()))
                .toList();

        model.addAttribute("customerRef", customerRef);
        model.addAttribute("loads", loads);
        model.addAttribute("invoices", invoices);
        model.addAttribute("customerRef", customerRef);
        model.addAttribute("notifications", notifications.findByCustomerRefOrderByCreatedAtDesc(customerRef));

        return "customer-detail";
    }
}
