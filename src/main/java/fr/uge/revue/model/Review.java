package fr.uge.revue.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
public class Review {
    @Id
    @GeneratedValue
    private long id;
    private String title;
    private Date date;
    private String code;
    private String test;
    private int likes;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "review")
    private List<Comment> comments;

    public Review() {
    }

    public Review(String title, String code, String test, User author) {
        this.title = Objects.requireNonNull(title);
        this.code = Objects.requireNonNull(code);
        this.test = Objects.requireNonNull(test);
        this.author = Objects.requireNonNull(author);
        this.date = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
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
        this.author = author;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", date=" + date +
                ", code='" + code + '\'' +
                ", test='" + test + '\'' +
                ", likes=" + likes +
                '}';
    }
}
