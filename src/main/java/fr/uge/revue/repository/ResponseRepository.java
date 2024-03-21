package fr.uge.revue.repository;

import fr.uge.revue.model.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponseRepository extends CrudRepository<Response, Long> {
    @Query("SELECT r FROM Response r LEFT JOIN FETCH r.comment r2 LEFT JOIN FETCH r2.review WHERE r.id = :id")
    Optional<Response> findByIdWithComment(long id);

    @Query(value = "SELECT r FROM Response r LEFT JOIN FETCH r.author WHERE r.author.id = :userId ORDER BY r.date DESC",
            countQuery = "SELECT count(r) FROM Response r WHERE r.author.id = :userId")
    Page<Response> findByAuthorIdPage(long userId, Pageable pageable);

    @Query("SELECT r FROM Response r LEFT JOIN FETCH r.comment r2 LEFT JOIN FETCH r2.review LEFT JOIN FETCH r.likesSet LEFT JOIN FETCH r.dislikes WHERE r.id = :responseId")
    Optional<Response> findByIdWithLikeAndReviewAndComment(long responseId);
}
