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

    private static final String STATUS_DRAFT = "Draft";

    private final LoadService loadService;
    private final InvoiceService invoiceService;
    private final NotificationRepository notificationRepository;

    public CustomerController(LoadService loadService,
                              InvoiceService invoiceService,
                              NotificationRepository notificationRepository) {
        this.loadService = loadService;
        this.invoiceService = invoiceService;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/{customerRef}")
    public String detail(@PathVariable String customerRef, Model model) {
        List<Load> loads = findLoadsForCustomer(customerRef);
        List<Invoice> invoices = findInvoicesForCustomer(customerRef);

        model.addAttribute("customerRef", customerRef);
        model.addAttribute("loads", loads);
        model.addAttribute("invoices", invoices);
        model.addAttribute(
                "notifications",
                notificationRepository.findByCustomerRefOrderByCreatedAtDesc(customerRef)
        );

        return "customer-detail";
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private List<Load> findLoadsForCustomer(String customerRef) {
        return loadService.listLoads().stream()
                .filter(load -> customerRef.equals(load.getReferenceNo()))
                .toList();
    }

    private List<Invoice> findInvoicesForCustomer(String customerRef) {
        return invoiceService.list().stream()
                .filter(invoice -> customerRef.equals(invoice.getCustomerRef()))
                .filter(invoice -> !STATUS_DRAFT.equalsIgnoreCase(invoice.getStatus()))
                .toList();
    }
}
