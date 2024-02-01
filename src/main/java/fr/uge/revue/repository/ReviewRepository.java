package fr.uge.revue.repository;

import fr.uge.revue.model.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReviewRepository extends CrudRepository<Review, Long> {
    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.author")
    List<Review> findAll();
}
