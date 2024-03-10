package fr.uge.revue.dto.review;

import fr.uge.revue.model.LikeState;

public record LikeStateDTO(int likes, LikeState likeState) {
}
