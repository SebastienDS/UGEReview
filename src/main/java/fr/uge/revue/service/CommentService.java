package fr.uge.revue.service;

import fr.uge.revue.model.Comment;
import fr.uge.revue.model.Response;
import fr.uge.revue.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;

    public CommentService(CommentRepository commentRepository, NotificationService notificationService) {
        this.commentRepository = Objects.requireNonNull(commentRepository);
        this.notificationService = Objects.requireNonNull(notificationService);
    }

    public Optional<Comment> getComment(long commentId) {
        return commentRepository.findByIdWithReview(commentId);
    }

    @Transactional
    public void saveComment(Comment comment) {
        Objects.requireNonNull(comment);
        commentRepository.save(comment);
        notificationService.notifyNewComment(comment);
    }

    public Optional<Comment> getCommentWithResponse(long id) {
        return commentRepository.findByIdWithResponses(id);
    }

    public void delete(long id) {
        var comment = getComment(id);
        if(comment.isEmpty()){
            return;
        }
        commentRepository.delete(comment.get());
    }
}
