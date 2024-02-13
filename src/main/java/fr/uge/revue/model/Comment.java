package fr.uge.revue.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
public class Comment {
    @Id
    @GeneratedValue
    private long id;
    private String content;
    private Date date;
    private int likes;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comment")
    private List<Response> responses;

    public Comment() {
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

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        Objects.requireNonNull(review);
        this.review = review;
    }

    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        Objects.requireNonNull(responses);
        this.responses = responses;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", date=" + date +
                ", likes=" + likes +
                '}';
    }
}
