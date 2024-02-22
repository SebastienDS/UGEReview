package fr.uge.revue.controller;

import fr.uge.revue.model.User;
import fr.uge.revue.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Objects;

@Controller
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = Objects.requireNonNull(notificationService);
    }

    @PostMapping("/reviews/{reviewId}/notifications/activate")
    public RedirectView activateNotifications(@PathVariable long reviewId, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        notificationService.activateNotifications(reviewId, user);
        return new RedirectView("/reviews/" + reviewId);
    }

    @PostMapping("/reviews/{reviewId}/notifications/deactivate")
    public RedirectView deactivateNotifications(@PathVariable long reviewId, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        notificationService.deactivateNotifications(reviewId, user);
        return new RedirectView("/reviews/" + reviewId);
    }
}
