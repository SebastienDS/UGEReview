package fr.uge.revue.dto.notification;

import fr.uge.revue.model.Notification;

public record NotificationDTO(long id, String link, String message) {
    public static NotificationDTO from(Notification notification) {
        return new NotificationDTO(notification.getId(), notification.getLink("/front"), notification.getMessage());
    }
}
