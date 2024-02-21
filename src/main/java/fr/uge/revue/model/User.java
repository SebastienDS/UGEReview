package fr.uge.revue.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "Users")
public class User implements UserDetails {
    @Id
    @GeneratedValue
    private long id;
    private String username;
    private String email;
    private String password;
    @Enumerated
    private Role role;
    private Date creationDate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    private Set<Review> reviews;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    private Set<Comment> comments;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    private Set<Response> responses;
    @ManyToMany
    private Set<User> followers;
    @ManyToMany
    private Set<Comment> commentsLikes;
    @ManyToMany
    private Set<Comment> commentsDislikes;
    @ManyToMany
    private Set<Response> responsesLikes;
    @ManyToMany
    private Set<Response> responsesDislikes;
    @ManyToMany
    private Set<Review> reviewsLikes;
    @ManyToMany
    private Set<Review> reviewsDislikes;

    public User() {
    }

    public User(String username, String email, String password, Role role) {
        this.username = Objects.requireNonNull(username);
        this.email = Objects.requireNonNull(email);
        this.password = Objects.requireNonNull(password);
        this.role = Objects.requireNonNull(role);
        this.creationDate = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = Objects.requireNonNull(username);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = Objects.requireNonNull(password);
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = Objects.requireNonNull(role);
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = Objects.requireNonNull(creationDate);
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = Objects.requireNonNull(reviews);
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = Objects.requireNonNull(comments);
    }

    public Set<Response> getResponses() {
        return responses;
    }

    public void setResponses(Set<Response> responses) {
        this.responses = Objects.requireNonNull(responses);
    }

    public Set<Comment> getCommentsLikes() {
        return commentsLikes;
    }

    public void setCommentsLikes(Set<Comment> commentsLikes) {
        this.commentsLikes = Objects.requireNonNull(commentsLikes);
    }

    public Set<Comment> getCommentsDislikes() {
        return commentsDislikes;
    }

    public void setCommentsDislikes(Set<Comment> commentsDislikes) {
        this.commentsDislikes = Objects.requireNonNull(commentsDislikes);
    }

    public Set<Response> getResponsesLikes() {
        return responsesLikes;
    }

    public void setResponsesLikes(Set<Response> responsesLikes) {
        this.responsesLikes = Objects.requireNonNull(responsesLikes);
    }

    public Set<Response> getResponsesDislikes() {
        return responsesDislikes;
    }

    public void setResponsesDislikes(Set<Response> responsesDislikes) {
        this.responsesDislikes = Objects.requireNonNull(responsesDislikes);
    }

    public Set<Review> getReviewsLikes() {
        return reviewsLikes;
    }

    public void setReviewsLikes(Set<Review> reviewsLikes) {
        this.reviewsLikes = Objects.requireNonNull(reviewsLikes);
    }

    public Set<Review> getReviewsDislikes() {
        return reviewsDislikes;
    }

    public void setReviewsDislikes(Set<Review> reviewsDislikes) {
        this.reviewsDislikes = Objects.requireNonNull(reviewsDislikes);
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = Objects.requireNonNull(followers);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", creationDate=" + creationDate +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var authority = new SimpleGrantedAuthority(role.name());
        return List.of(authority);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof User u && u.id == id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }
}
