package fr.uge.revue.controller.rest;

import fr.uge.revue.dto.notification.NotificationDTO;
import fr.uge.revue.dto.notification.NotificationStateDTO;
import fr.uge.revue.model.User;
import fr.uge.revue.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class NotificationRestController {
    private final NotificationService notificationService;

    public NotificationRestController(NotificationService notificationService) {
        this.notificationService = Objects.requireNonNull(notificationService);
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> allNotifications(Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        var notifications = notificationService.findAllUserNotifications(user.getId())
                .stream()
                .map(NotificationDTO::from)
                .toList();
        return ResponseEntity.ok().body(notifications);
    }

    @GetMapping("/reviews/{reviewId}/notifications/state")
    public ResponseEntity<NotificationStateDTO> isUserRequestingNotification(@PathVariable long reviewId, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        var isUserRequestingNotification = notificationService.isUserRequestingNotification(reviewId, user.getId());
        return ResponseEntity.ok().body(new NotificationStateDTO(isUserRequestingNotification));
    }

    @PostMapping("/reviews/{reviewId}/notifications/activate")
    public ResponseEntity<?> activateNotifications(@PathVariable long reviewId, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        notificationService.activateNotifications(reviewId, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reviews/{reviewId}/notifications/deactivate")
    public ResponseEntity<?> deactivateNotifications(@PathVariable long reviewId, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        notificationService.deactivateNotifications(reviewId, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/notifications/{notificationId}/markAsRead")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable long notificationId, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        notificationService.markAsRead(user, notificationId);
        return ResponseEntity.ok().build();
    }
}
