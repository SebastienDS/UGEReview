package fr.uge.revue.repository;

import fr.uge.revue.model.Review;
import fr.uge.revue.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends CrudRepository<Review, Long> {
    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.author LEFT JOIN FETCH r.unitTests t LEFT JOIN FETCH t.errors " +
            "LEFT JOIN FETCH r.comments c LEFT JOIN FETCH c.author LEFT JOIN FETCH c.responses r2 LEFT JOIN FETCH r2.author " +
            "WHERE r.id = :id")
    Optional<Review> findByIdWithFullContent(@Param("id") long id);

    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.author")
    List<Review> findAll();

    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.author WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
        "OR LOWER(r.author.username) LIKE LOWER(CONCAT('%', :search, '%')) OR r.code LIKE LOWER(CONCAT('%', :search, '%')) " +
        "OR LOWER(r.commentary) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Review> searchReview(@Param("search") String search);

    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.author WHERE r.author.id = :userId")
    List<Review> findAllUserReviews(@Param("userId") long userId);

    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.author WHERE r.author.id = :userId AND ( " +
            "   LOWER(r.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR r.code LIKE LOWER(CONCAT('%', :search, '%'))" +
            "   OR LOWER(r.commentary) LIKE LOWER(CONCAT('%', :search, '%')) " +
            ")")
    List<Review> findAllUserReviewsMatching(@Param("userId") long userId, @Param("search") String search);

    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.requestNotifications WHERE r.id = :reviewId")
    Optional<Review> findByIdWithNotifications(@Param("reviewId") long reviewId);
}
