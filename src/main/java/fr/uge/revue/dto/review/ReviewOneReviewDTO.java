package fr.uge.revue.dto.review;

import fr.uge.revue.dto.comment.CommentDTO;
import fr.uge.revue.dto.user.UserDTO;
import fr.uge.revue.model.*;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

public record ReviewOneReviewDTO(long id, String title, String commentary, String code, String test, int likes, UserDTO author, Date date, List<CommentDTO> comments, TestsReview unitTests, LikeState likeState) {
    public static ReviewOneReviewDTO from(Review review) {
        return from(review, null);
    }

    public static ReviewOneReviewDTO from(Review review, User user) {
        var comments = review.getComments()
                .stream()
                .sorted(Comparator.comparing(Comment::getDate))
                .map(comment -> CommentDTO.from(comment, user))
                .toList();
        return new ReviewOneReviewDTO(review.getId(), review.getTitle(), review.getCommentary(), review.getCode(), review.getTest(), review.getLikes(),
                UserDTO.from(review.getAuthor()), review.getDate(), comments, review.getUnitTests(), getLikeState(review, user));
    }

    private static LikeState getLikeState(Review review, User user) {
        if (user == null) return LikeState.NONE;
        if (user.getReviewsLikes().contains(review)) return LikeState.LIKE;
        if (user.getReviewsDislikes().contains(review)) return LikeState.DISLIKE;
        return LikeState.NONE;
    }
}