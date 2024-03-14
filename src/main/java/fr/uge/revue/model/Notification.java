package fr.uge.revue.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Notification {
    public enum NotificationType {
        NEW_COMMENT, NEW_RESPONSE
    }
    @Id
    @GeneratedValue
    private long id;
    private boolean alreadyRead;
    @ManyToOne(fetch = FetchType.LAZY)
    private User notifiedUser;
    @ManyToOne(fetch = FetchType.LAZY)
    private User userWhoNotify;
    @Enumerated
    private NotificationType type;

    private long reviewId;
    private long commentId;
    private long responseId;

    public Notification() {
    }

    private Notification(User notifiedUser, User userWhoNotify,  NotificationType type, long reviewId) {
        this.notifiedUser = Objects.requireNonNull(notifiedUser);
        this.userWhoNotify = Objects.requireNonNull(userWhoNotify);
        this.type = Objects.requireNonNull(type);
        this.reviewId = reviewId;
    }

    public static Notification newComment(User notifiedUser, User userWhoNotify, long reviewId, long commentId) {
        var notification = new Notification(notifiedUser, userWhoNotify, NotificationType.NEW_COMMENT, reviewId);
        notification.commentId = commentId;
        return notification;
    }

    public static Notification newResponse(User notifiedUser, User userWhoNotify, long reviewId, long responseId) {
        var notification = new Notification(notifiedUser, userWhoNotify, NotificationType.NEW_RESPONSE, reviewId);
        notification.responseId = responseId;
        return notification;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isAlreadyRead() {
        return alreadyRead;
    }

    public void setAlreadyRead(boolean alreadyRead) {
        this.alreadyRead = alreadyRead;
    }

    public User getNotifiedUser() {
        return notifiedUser;
    }

    public void setNotifiedUser(User notifiedUser) {
        this.notifiedUser = notifiedUser;
    }

    public User getUserWhoNotify() {
        return userWhoNotify;
    }

    public void setUserWhoNotify(User userWhoNotify) {
        this.userWhoNotify = Objects.requireNonNull(userWhoNotify);
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public long getReviewId() {
        return reviewId;
    }

    public void setReviewId(long reviewId) {
        this.reviewId = reviewId;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public long getResponseId() {
        return responseId;
    }

    public void setResponseId(long responseId) {
        this.responseId = responseId;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", alreadyRead=" + alreadyRead +
                ", type=" + type +
                ", reviewId=" + reviewId +
                ", commentId=" + commentId +
                ", responseId=" + responseId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Notification n && n.id == id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getLink(String prefix) {
        return switch (type) {
            case NEW_COMMENT -> prefix + "/reviews/" + reviewId + "#comment_" + commentId;
            case NEW_RESPONSE -> prefix + "/reviews/" + reviewId + "#response_" + responseId;
        };
    }

    public String getLink() {
        return getLink("");
    }

    public String getMessage() {
        return switch (type) {
            case NEW_COMMENT -> "L'utilisateur " + userWhoNotify.getUsername() + " a commenté une revue que vous suivez";
            case NEW_RESPONSE -> "L'utilisateur " + userWhoNotify.getUsername() + " a ajouté un commentaire à une revue que vous suivez";
        };
    }
}
