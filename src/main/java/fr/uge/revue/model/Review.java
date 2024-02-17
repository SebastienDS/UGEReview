package fr.uge.revue.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
public final class Review implements Likeable {
    @Id
    @GeneratedValue
    private long id;
    private String title;
    private Date date;
    private String commentary;
    private String code;
    private String test;
    private int likes;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "review")
    private Set<Comment> comments;

    public Review() {
    }


    public Review(String title, String commentary, String code, String test, User author) {
        this.title = Objects.requireNonNull(title);
        this.commentary = Objects.requireNonNull(commentary);
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
        Objects.requireNonNull(title);
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        Objects.requireNonNull(date);
        this.date = date;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        Objects.requireNonNull(code);
        this.code = code;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        Objects.requireNonNull(test);
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
        Objects.requireNonNull(author);
        this.author = author;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        Objects.requireNonNull(comments);
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

    @Override
    public boolean equals(Object o) {
        return o instanceof Review r && r.id == id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
