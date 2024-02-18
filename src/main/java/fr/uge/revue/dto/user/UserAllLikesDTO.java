package fr.uge.revue.dto.user;

import fr.uge.revue.model.Comment;
import fr.uge.revue.model.Response;
import fr.uge.revue.model.Review;
import fr.uge.revue.model.User;

import java.util.Set;

public record UserAllLikesDTO(long id, Set<Review> reviews, Set<Comment> comments, Set<Response> responses) {
    public static UserAllLikesDTO from(User user) {
        return new UserAllLikesDTO(user.getId(), user.getReviewsLikes(), user.getCommentsLikes(), user.getResponsesLikes());
    }
}