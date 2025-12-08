package com.startup.trucking.web;

import com.startup.trucking.notify.ChannelType;
import com.startup.trucking.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/load-confirmed")
    public String notifyLoadConfirmed(@RequestParam String loadId,
                                      @RequestParam String customerRef,
                                      @RequestParam String channel,
                                      @RequestParam String recipient,
                                      RedirectAttributes redirectAttributes) {

        ChannelType channelType = ChannelType.valueOf(channel);
        notificationService.sendLoadConfirmed(channelType, recipient, customerRef, loadId);

        redirectAttributes.addFlashAttribute(
                "toast",
                "Notification sent for Load " + loadId
        );
        return "redirect:/loads/" + loadId;
    }

    @PostMapping("/invoice-created")
    public String notifyInvoiceCreated(@RequestParam String invoiceId,
                                       @RequestParam String loadId,
                                       @RequestParam String customerRef,
                                       @RequestParam String total,
                                       @RequestParam String channel,
                                       @RequestParam String recipient,
                                       RedirectAttributes redirectAttributes) {

        ChannelType channelType = ChannelType.valueOf(channel);
        notificationService.sendInvoiceCreated(
                channelType,
                recipient,
                customerRef,
                invoiceId,
                loadId,
                total
        );

        redirectAttributes.addFlashAttribute(
                "toast",
                "Notification sent for Invoice " + invoiceId
        );
        return "redirect:/invoices";
    }

    @PostMapping("/payment-received")
    public String notifyPaymentReceived(@RequestParam String invoiceId,
                                        @RequestParam String customerRef,
                                        @RequestParam String amount,
                                        @RequestParam String method,
                                        @RequestParam String channel,
                                        @RequestParam String recipient,
                                        RedirectAttributes redirectAttributes) {

        ChannelType channelType = ChannelType.valueOf(channel);
        notificationService.sendPaymentReceived(
                channelType,
                recipient,
                customerRef,
                invoiceId,
                amount,
                method
        );

        redirectAttributes.addFlashAttribute(
                "toast",
                "Payment notification sent for " + invoiceId
        );
        return "redirect:/invoices/" + invoiceId + "/pay";
    }
}
