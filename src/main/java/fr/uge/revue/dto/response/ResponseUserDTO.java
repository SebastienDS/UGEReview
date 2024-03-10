package fr.uge.revue.dto.response;

import fr.uge.revue.dto.user.UserDTO;
import fr.uge.revue.model.LikeState;
import fr.uge.revue.model.Response;
import fr.uge.revue.model.User;

import java.util.Date;

public record ResponseUserDTO(long id, String content, Date date, UserDTO author, long reviewId) {
    public static ResponseUserDTO from(Response response) {
        return new ResponseUserDTO(response.getId(), response.getContent(), response.getDate(),
                UserDTO.from(response.getAuthor()), response.getComment().getReview().getId());
    }
}
