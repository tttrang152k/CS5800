package com.startup.trucking.web;

import com.startup.trucking.persistence.Document;
import com.startup.trucking.domain.Load;
import com.startup.trucking.domain.LoadBuilder;
import com.startup.trucking.notify.ChannelType;
import com.startup.trucking.persistence.Invoice;
import com.startup.trucking.service.DocumentService;
import com.startup.trucking.service.InvoiceService;
import com.startup.trucking.service.LoadService;
import com.startup.trucking.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/loads")
public class LoadController {
    private final LoadService loads;
    private final DocumentService docs;
    private final InvoiceService invoices;
    private final NotificationService notificationService;

    public LoadController(LoadService loads, DocumentService docs, InvoiceService invoices, NotificationService notificationService) {
        this.loads = loads;
        this.docs = docs;
        this.invoices = invoices;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String loads(@RequestParam(name = "sort", required = false) String sortKey,
                        Model model) {

        Map<String, Invoice> invoiceByLoadId = invoices.list().stream()
                .collect(Collectors.toMap(Invoice::getLoadId, i -> i, (a, b) -> a));

        var loadList = loads.listLoadsSorted(sortKey);

        List<LoadRow> rows = new ArrayList<>();
        for (Load l : loadList) {
            String customerName = l.getReferenceNo() != null ? l.getReferenceNo() : "";
            String driver = l.getDriverId() != null ? l.getDriverId() : "";

            String docUrl = null;
            List<Document> docList = docs.list(l.getId());
            if (!docList.isEmpty() && docList.get(0).downloadUri() != null) {
                docUrl = docList.get(0).downloadUri().toString();
            }

            Invoice inv = invoiceByLoadId.get(l.getId());
            boolean invoiced = inv != null;
            String invoiceId = invoiced ? inv.getId() : null;

            rows.add(new LoadRow(
                    l.getId(),
                    customerName,
                    l.getStatus(),
                    l.getRateAmount(),
                    driver,
                    l.getPickupAddress(),
                    l.getDeliveryAddress(),
                    l.getPickupDate(),
                    l.getDeliveryDate(),
                    docUrl,
                    invoiced,
                    invoiceId
            ));
        }

        model.addAttribute("rows", rows);
        model.addAttribute("currentSort", sortKey == null ? "" : sortKey);

        return "loads"; // templates/loads.html
    }


    @GetMapping("/{id}")
    public String detail(@PathVariable String id, Model model,
                         @ModelAttribute("toast") String toast) {
        Load l = loads.getLoad(id);
        model.addAttribute("load", l);
        model.addAttribute("documents", docs.list(id));
        return "load-detail"; // templates/load-detail.html
    }

    @PostMapping("/{id}/dispatch")
    public String dispatch(@PathVariable String id, RedirectAttributes ra) {
        loads.updateStatus(id, "Dispatched");
        ra.addFlashAttribute("toast", "Load " + id + " dispatched.");
        return "redirect:/loads";
    }

    @PostMapping("/{id}/deliver")
    public String deliver(@PathVariable String id, RedirectAttributes ra) {
        loads.updateStatus(id, "Delivered");
        ra.addFlashAttribute("toast", "Load " + id + " marked Delivered.");
        return "redirect:/loads";
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable String id,
                               @RequestParam String status,
                               RedirectAttributes ra) {
        loads.updateStatus(id, status);
        ra.addFlashAttribute("toast", "Status → " + status);
        return "redirect:/loads/" + id;
    }

    @PostMapping("/{id}/notify")
    public String notifyCustomer(@PathVariable String id,
                                 @RequestParam(value = "channel", required = false) String channel,
                                 @RequestParam(value = "recipient", required = false) String recipient,
                                 RedirectAttributes ra) {
        // If no recipient or no channel is provided, do nothing and toast
        if (recipient == null || recipient.isBlank() || channel == null || channel.isBlank()) {
            ra.addFlashAttribute("toast", "Notification skipped (channel/recipient missing).");
            return "redirect:/loads/" + id;
        }

        var load = loads.getLoad(id);

        try {
            notificationService.sendLoadConfirmed(
                    ChannelType.valueOf(channel),
                    recipient,
                    load.getReferenceNo(),
                    load.getId()
            );
            ra.addFlashAttribute("toast", "Customer notified: Load Confirmed.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("toast", "Notification failed: " + ex.getMessage());
        }

        return "redirect:/loads/" + id;
    }

    @PostMapping("/{id}/documents/upload")
    public String uploadDoc(@PathVariable String id,
                            @RequestParam String type,  // BOL or POD
                            @RequestParam String url,
                            RedirectAttributes ra) {
        docs.upload(id, type, URI.create(url));
        ra.addFlashAttribute("toast", type + " uploaded.");
        return "redirect:/loads/" + id;
    }

    @GetMapping("/new")
    public String newLoadForm(Model model) {
        model.addAttribute("form", new BookingForm());
        return "load-new";
    }

    @PostMapping
    public String create(@ModelAttribute("form") BookingForm form, RedirectAttributes ra) {
        String id = (form.getId() == null || form.getId().isBlank())
                ? "L-" + UUID.randomUUID()
                : form.getId();

        Load load = new LoadBuilder()
                .setId(id)
                .setReferenceNo(form.getReferenceNo())
                .setStatus(form.getStatus() == null || form.getStatus().isBlank() ? "Requested" : form.getStatus())
                .setRateAmount(form.getRateAmount())
                .setTrackingId(form.getTrackingId())
                .setRateConfirmationRef(form.getRateConfirmationRef())
                .setDriverId(form.getDriverId())
                .setTrailerId(form.getTrailerId())
                .setPickupAddress(form.getPickupAddress())
                .setDeliveryAddress(form.getDeliveryAddress())
                .setPickupDate(form.getPickupDate())
                .setDeliveryDate(form.getDeliveryDate())
                .createLoad();

        loads.putLoad(load);
        ra.addFlashAttribute("toast", "Load " + id + " created.");
        return "redirect:/loads";
    }
}
