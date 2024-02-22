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
    @ManyToOne
    private User notifiedUser;
    @Enumerated
    private NotificationType type;

    private long reviewId;
    private long commentId;
    private long responseId;

    public Notification() {
    }

    private Notification(User notifiedUser, NotificationType type, long reviewId) {
        this.notifiedUser = Objects.requireNonNull(notifiedUser);
        this.type = Objects.requireNonNull(type);
        this.reviewId = reviewId;
    }

    public static Notification newComment(User notifiedUser, long reviewId, long commentId) {
        var notification = new Notification(notifiedUser, NotificationType.NEW_COMMENT, reviewId);
        notification.commentId = commentId;
        return notification;
    }

    public static Notification newResponse(User notifiedUser, long reviewId, long responseId) {
        var notification = new Notification(notifiedUser, NotificationType.NEW_RESPONSE, reviewId);
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
}
