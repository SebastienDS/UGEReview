package fr.uge.revue.repository;

import fr.uge.revue.model.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends CrudRepository<Review, Long> {
    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.author")
    List<Review> findAll();

    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.author WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :search, '%'))" +
        "OR LOWER(r.author.username) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Review> searchReview(String search);
}
