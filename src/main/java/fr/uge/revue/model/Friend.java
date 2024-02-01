package fr.uge.revue.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@IdClass(Friend.FriendId.class)
public class Friend {
    public static class FriendId implements Serializable {
        private User user1;
        private User user2;
    }
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private User user1;
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private User user2;
    private Date date;

    public Friend() {
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "user1=" + user1 +
                ", user2=" + user2 +
                ", date=" + date +
                '}';
    }
}
