package fr.uge.revue.dto.comment;

import fr.uge.revue.dto.response.ResponseDTO;
import fr.uge.revue.dto.user.UserDTO;
import fr.uge.revue.model.Comment;
import fr.uge.revue.model.LikeState;

import java.util.Date;
import java.util.List;

public record CommentUserDTO(long id, String content, Date date, UserDTO author, long reviewId) {
    public static CommentUserDTO from(Comment comment){
        return new CommentUserDTO(comment.getId(), comment.getContent(), comment.getDate(),
                UserDTO.from(comment.getAuthor()), comment.getReview().getId());
    }
}
