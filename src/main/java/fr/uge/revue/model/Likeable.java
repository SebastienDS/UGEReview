package fr.uge.revue.model;

import java.util.Date;

public sealed interface Likeable permits Comment, Review, Response{
    void setLikes(int likes);
    int getLikes();
    Date getDate();

    long getId();

    String getContent();

    User getAuthor();
    long getReviewId();
}
