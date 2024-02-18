package fr.uge.revue.service;

import fr.uge.revue.model.Comment;
import fr.uge.revue.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = Objects.requireNonNull(commentRepository);
    }

    public Optional<Comment> getComment(long commentId) {
        return commentRepository.findByIdWithReview(commentId);
    }
}
