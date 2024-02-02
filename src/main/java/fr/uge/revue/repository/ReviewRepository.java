package fr.uge.revue.repository;

import fr.uge.revue.model.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends CrudRepository<Review, Long> {
    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.author")
    List<Review> findAll();
}
