package fr.uge.revue.dto.response;

import fr.uge.revue.dto.user.UserDTO;
import fr.uge.revue.model.Response;

import java.util.Date;

public record ResponseDTO(long id, String content, Date date, int likes, UserDTO author) {
    public static ResponseDTO from(Response response) {
        return new ResponseDTO(response.getId(), response.getContent(), response.getDate(), response.getLikes(), UserDTO.from(response.getAuthor()));
    }
}
