package com.startup.trucking.web;

import com.startup.trucking.notify.ChannelType;
import com.startup.trucking.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notify;

    public NotificationController(NotificationService notify) { this.notify = notify; }

    @PostMapping("/load-confirmed")
    public String loadConfirmed(@RequestParam String loadId,
                                @RequestParam String customerRef,
                                @RequestParam String channel,
                                @RequestParam String recipient,
                                RedirectAttributes ra) {
        notify.sendLoadConfirmed(ChannelType.valueOf(channel), recipient, customerRef, loadId);
        ra.addFlashAttribute("toast", "Notification sent for Load " + loadId);
        return "redirect:/loads/" + loadId;
    }

    @PostMapping("/invoice-created")
    public String invoiceCreated(@RequestParam String invoiceId,
                                 @RequestParam String loadId,
                                 @RequestParam String customerRef,
                                 @RequestParam String total,
                                 @RequestParam String channel,
                                 @RequestParam String recipient,
                                 RedirectAttributes ra) {
        notify.sendInvoiceCreated(ChannelType.valueOf(channel), recipient, customerRef, invoiceId, loadId, total);
        ra.addFlashAttribute("toast", "Notification sent for Invoice " + invoiceId);
        return "redirect:/invoices";
    }

    @PostMapping("/payment-received")
    public String paymentReceived(@RequestParam String invoiceId,
                                  @RequestParam String customerRef,
                                  @RequestParam String amount,
                                  @RequestParam String method,
                                  @RequestParam String channel,
                                  @RequestParam String recipient,
                                  RedirectAttributes ra) {
        notify.sendPaymentReceived(ChannelType.valueOf(channel), recipient, customerRef, invoiceId, amount, method);
        ra.addFlashAttribute("toast", "Payment notification sent for " + invoiceId);
        return "redirect:/invoices/" + invoiceId + "/pay";
    }
}