package fr.uge.revue.model;

public sealed interface Likeable permits Comment, Review, Response{
    void setLikes(int likes);
    int getLikes();

}
