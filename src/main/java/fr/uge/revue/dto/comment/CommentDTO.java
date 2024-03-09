package fr.uge.revue.dto.comment;

import fr.uge.revue.dto.response.ResponseDTO;
import fr.uge.revue.model.LikeState;
import fr.uge.revue.dto.user.UserDTO;
import fr.uge.revue.model.Comment;
import fr.uge.revue.model.Response;
import fr.uge.revue.model.User;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

public record CommentDTO(long id, String content, Date date, int likes, UserDTO author, List<ResponseDTO> responses, LikeState likeState) {
    public static CommentDTO from(Comment comment) {
        return from(comment, null);
    }

    public static CommentDTO from(Comment comment, User user) {
        var responses = comment.getResponses()
                .stream()
                .sorted(Comparator.comparing(Response::getDate))
                .map(response -> ResponseDTO.from(response, user))
                .toList();
        return new CommentDTO(comment.getId(), comment.getContent(), comment.getDate(), comment.getLikes(),
                UserDTO.from(comment.getAuthor()), responses, getLikeState(comment, user));
    }

    private static LikeState getLikeState(Comment comment, User user) {
        if (user == null) return LikeState.NONE;
        if (user.getCommentsLikes().contains(comment)) return LikeState.LIKE;
        if (user.getCommentsDislikes().contains(comment)) return LikeState.DISLIKE;
        return LikeState.NONE;
    }
}
