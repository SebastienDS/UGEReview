package fr.uge.revue.dto.response;

import fr.uge.revue.model.LikeState;
import fr.uge.revue.dto.user.UserDTO;
import fr.uge.revue.model.Response;
import fr.uge.revue.model.User;

import java.util.Date;

public record ResponseDTO(long id, String content, Date date, int likes, UserDTO author, LikeState likeState) {
    public static ResponseDTO from(Response response) {
        return from(response, null);
    }

    public static ResponseDTO from(Response response, User user) {
        return new ResponseDTO(response.getId(), response.getContent(), response.getDate(), response.getLikes(),
                UserDTO.from(response.getAuthor()), getLikeState(response, user));
    }

    private static LikeState getLikeState(Response response, User user) {
        if (user == null) return LikeState.NONE;
        if (user.getResponsesLikes().contains(response)) return LikeState.LIKE;
        if (user.getResponsesDislikes().contains(response)) return LikeState.DISLIKE;
        return LikeState.NONE;
    }
}
