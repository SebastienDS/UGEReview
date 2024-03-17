package fr.uge.revue.model;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public final class Review implements Likeable {
    @Id
    @GeneratedValue
    private long id;
    @Column(columnDefinition = "text")
    private String title;
    private Date date;
    @Column(columnDefinition = "text")
    private String commentary;
    @Column(columnDefinition = "text")
    private String code;
    @Column(columnDefinition = "text")
    private String test;
    private int likes;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "review")
    private Set<Comment> comments = new HashSet<>();
    @ManyToMany
    private Set<User> requestNotifications = new HashSet<>();
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn
    private TestsReview unitTests;


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

    @Override
    public String getContent() {
        return title;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = Objects.requireNonNull(date);
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = Objects.requireNonNull(commentary);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = Objects.requireNonNull(code);
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = Objects.requireNonNull(test);
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
        this.author = Objects.requireNonNull(author);
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = Objects.requireNonNull(comments);
    }

    public Set<User> getRequestNotifications() {
        return requestNotifications;
    }

    public void setRequestNotifications(Set<User> requestNotifications) {
        this.requestNotifications = Objects.requireNonNull(requestNotifications);
    }

    public TestsReview getUnitTests() {
        return unitTests;
    }

    public void setUnitTests(TestsReview unitTests) {
        this.unitTests = Objects.requireNonNull(unitTests);
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

    @Override
    public long getReviewId(){
        return id;
    }
}
