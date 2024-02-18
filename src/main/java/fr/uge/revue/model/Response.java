package fr.uge.revue.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;


@Entity
public final class Response implements Likeable {
    @Id
    @GeneratedValue
    private long id;
    private String content;
    private Date date;
    private int likes;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;

    public Response() {
    }

    public Response(String content, User author, Comment comment) {
        this.content = Objects.requireNonNull(content);
        this.author = Objects.requireNonNull(author);
        this.comment = Objects.requireNonNull(comment);
        date = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        Objects.requireNonNull(content);
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        Objects.requireNonNull(date);
        this.date = date;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        Objects.requireNonNull(author);
        this.author = author;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        Objects.requireNonNull(comment);
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Response{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", date=" + date +
                ", likes=" + likes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Response r && r.id == id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}