package fr.uge.revue.repository;

import fr.uge.revue.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.review WHERE c.id = :id")
    Optional<Comment> findByIdWithReview(long id);
}
