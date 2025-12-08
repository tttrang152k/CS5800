package com.startup.trucking.web;

import com.startup.trucking.domain.Load;
import com.startup.trucking.domain.LoadBuilder;
import com.startup.trucking.notify.ChannelType;
import com.startup.trucking.persistence.Document;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/loads")
public class LoadController {

    private static final String VIEW_LOAD_LIST = "loads";
    private static final String VIEW_LOAD_DETAIL = "load-detail";
    private static final String DEFAULT_SORT = "";

    private final LoadService loadService;
    private final DocumentService documentService;
    private final InvoiceService invoiceService;
    private final NotificationService notificationService;

    public LoadController(LoadService loadService,
                          DocumentService documentService,
                          InvoiceService invoiceService,
                          NotificationService notificationService) {
        this.loadService = loadService;
        this.documentService = documentService;
        this.invoiceService = invoiceService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String listLoads(@RequestParam(name = "sort", required = false) String sortKey,
                            Model model) {

        Map<String, Invoice> invoicesByLoadId = groupInvoicesByLoadId();

        List<Load> loads = loadService.listLoadsSorted(sortKey);
        List<LoadRow> rows = loads.stream()
                .map(load -> toLoadRow(load, invoicesByLoadId))
                .toList();

        model.addAttribute("rows", rows);
        model.addAttribute("currentSort", sortKey == null ? DEFAULT_SORT : sortKey);

        return VIEW_LOAD_LIST;
    }

    @GetMapping("/{id}")
    public String loadDetail(@PathVariable String id,
                             Model model,
                             @ModelAttribute("toast") String toast) {

        Load load = loadService.getLoad(id);
        model.addAttribute("load", load);
        model.addAttribute("documents", documentService.list(id));
        return VIEW_LOAD_DETAIL;
    }

    @PostMapping("/{id}/dispatch")
    public String dispatchLoad(@PathVariable String id,
                               RedirectAttributes redirectAttributes) {
        loadService.updateStatus(id, "Dispatched");
        redirectAttributes.addFlashAttribute("toast", "Load " + id + " dispatched.");
        return "redirect:/loads";
    }

    @PostMapping("/{id}/deliver")
    public String deliverLoad(@PathVariable String id,
                              RedirectAttributes redirectAttributes) {
        loadService.updateStatus(id, "Delivered");
        redirectAttributes.addFlashAttribute("toast", "Load " + id + " marked Delivered.");
        return "redirect:/loads";
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable String id,
                               @RequestParam String status,
                               RedirectAttributes redirectAttributes) {
        loadService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("toast", "Status → " + status);
        return "redirect:/loads/" + id;
    }

    @PostMapping("/{id}/notify")
    public String notifyCustomer(@PathVariable String id,
                                 @RequestParam(value = "channel", required = false) String channel,
                                 @RequestParam(value = "recipient", required = false) String recipient,
                                 RedirectAttributes redirectAttributes) {
        if (isBlank(channel) || isBlank(recipient)) {
            redirectAttributes.addFlashAttribute(
                    "toast",
                    "Notification skipped (channel/recipient missing)."
            );
            return "redirect:/loads/" + id;
        }

        Load load = loadService.getLoad(id);

        try {
            ChannelType channelType = ChannelType.valueOf(channel);
            notificationService.sendLoadConfirmed(
                    channelType,
                    recipient,
                    load.getReferenceNo(),
                    load.getId()
            );
            redirectAttributes.addFlashAttribute("toast", "Customer notified: Load Confirmed.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("toast", "Notification failed: " + ex.getMessage());
        }

        return "redirect:/loads/" + id;
    }

    @PostMapping("/{id}/documents/upload")
    public String uploadDocument(@PathVariable String id,
                                 @RequestParam String type,
                                 @RequestParam String url,
                                 RedirectAttributes redirectAttributes) {
        documentService.upload(id, type, URI.create(url));
        redirectAttributes.addFlashAttribute("toast", type + " uploaded.");
        return "redirect:/loads/" + id;
    }

    @GetMapping("/new")
    public String newLoadForm(Model model) {
        model.addAttribute("form", new BookingForm());
        return "load-new";
    }

    @PostMapping
    public String createLoad(@ModelAttribute("form") BookingForm form,
                             RedirectAttributes redirectAttributes) {
        String id = resolveLoadId(form);
        String status = resolveStatus(form.getStatus());

        Load load = new LoadBuilder()
                .setId(id)
                .setReferenceNo(form.getReferenceNo())
                .setStatus(status)
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

        loadService.putLoad(load);
        redirectAttributes.addFlashAttribute("toast", "Load " + id + " created.");
        return "redirect:/loads";
    }

    // ---------------------------------------------------------------------
    // Helper methods
    // ---------------------------------------------------------------------

    private Map<String, Invoice> groupInvoicesByLoadId() {
        return invoiceService.list().stream()
                .collect(Collectors.toMap(
                        Invoice::getLoadId,
                        invoice -> invoice,
                        (existing, duplicate) -> existing
                ));
    }

    private LoadRow toLoadRow(Load load, Map<String, Invoice> invoicesByLoadId) {
        String customerName = defaultString(load.getReferenceNo());
        String driver = defaultString(load.getDriverId());
        String documentUrl = resolveDocumentUrl(load.getId());

        Invoice invoice = invoicesByLoadId.get(load.getId());
        boolean invoiced = invoice != null;
        String invoiceId = invoiced ? invoice.getId() : null;

        return new LoadRow(
                load.getId(),
                customerName,
                load.getStatus(),
                load.getRateAmount(),
                driver,
                load.getPickupAddress(),
                load.getDeliveryAddress(),
                load.getPickupDate(),
                load.getDeliveryDate(),
                documentUrl,
                invoiced,
                invoiceId
        );
    }

    private String resolveDocumentUrl(String loadId) {
        List<Document> documents = documentService.list(loadId);
        if (documents.isEmpty()) {
            return null;
        }

        URI downloadUri = documents.get(0).downloadUri();
        return downloadUri != null ? downloadUri.toString() : null;
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String resolveLoadId(BookingForm form) {
        if (form.getId() == null || form.getId().isBlank()) {
            return "L-" + UUID.randomUUID();
        }
        return form.getId();
    }

    private String resolveStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            return "Requested";
        }
        return rawStatus;
    }
}
