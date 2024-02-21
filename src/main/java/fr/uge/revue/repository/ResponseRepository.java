package fr.uge.revue.repository;

import fr.uge.revue.model.Response;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponseRepository extends CrudRepository<Response, Long> {
    @Query("SELECT r FROM Response r LEFT JOIN FETCH r.comment r2 LEFT JOIN FETCH r2.review WHERE r.id = :id")
    Optional<Response> findByIdWithComment(long id);
}
