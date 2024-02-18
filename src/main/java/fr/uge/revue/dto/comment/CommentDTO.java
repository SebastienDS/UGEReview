package fr.uge.revue.dto.comment;

import fr.uge.revue.dto.response.ResponseDTO;
import fr.uge.revue.dto.user.UserDTO;
import fr.uge.revue.model.Comment;
import fr.uge.revue.model.Response;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

public record CommentDTO(long id, String content, Date date, int likes, UserDTO author, List<ResponseDTO> responses) {
    public static CommentDTO from(Comment comment) {
        var responses = comment.getResponses()
                .stream()
                .sorted(Comparator.comparing(Response::getDate))
                .map(ResponseDTO::from)
                .toList();
        return new CommentDTO(comment.getId(), comment.getContent(), comment.getDate(), comment.getLikes(), UserDTO.from(comment.getAuthor()), responses);
    }
}
