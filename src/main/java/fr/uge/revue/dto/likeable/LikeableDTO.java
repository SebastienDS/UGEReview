package fr.uge.revue.dto.likeable;

import fr.uge.revue.dto.user.UserDTO;
import fr.uge.revue.model.Likeable;

import java.util.Date;
import java.util.Locale;

public record LikeableDTO(long id, String content, Date date, UserDTO author, long reviewId, String className) {
    public static LikeableDTO from(Likeable likeable){
        return new LikeableDTO(likeable.getId(), likeable.getContent(), likeable.getDate(), UserDTO.from(likeable.getAuthor()),
                likeable.getReviewId(), likeable.getClass().getSimpleName().toLowerCase(Locale.ROOT));
    }
}
