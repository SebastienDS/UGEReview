package fr.uge.revue.repository;

import fr.uge.revue.model.Review;
import fr.uge.revue.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = "SELECT r FROM Review r LEFT JOIN FETCH r.author WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(r.author.username) LIKE LOWER(CONCAT('%', :search, '%')) OR r.code LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(r.commentary) LIKE LOWER(CONCAT('%', :search, '%')) ORDER BY r.date DESC",
            countQuery = "SELECT count(r) FROM Review r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(r.author.username) LIKE LOWER(CONCAT('%', :search, '%')) OR r.code LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(r.commentary) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Review> searchReview(@Param("search") String search, Pageable pageable);

    @Query(value = "SELECT r FROM Review r LEFT JOIN FETCH r.author WHERE r.author.id = :userId ORDER BY r.date DESC",
            countQuery = "SELECT count(r) FROM Review r WHERE r.author.id = :userId")
    Page<Review> findAllUserReviews(@Param("userId") long userId, Pageable pageable);

    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.author WHERE r.author.id = :userId AND ( " +
            "   LOWER(r.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR r.code LIKE LOWER(CONCAT('%', :search, '%'))" +
            "   OR LOWER(r.commentary) LIKE LOWER(CONCAT('%', :search, '%')) " +
            ")")
    List<Review> findAllUserReviewsMatching(@Param("userId") long userId, @Param("search") String search);

    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.requestNotifications WHERE r.id = :reviewId")
    Optional<Review> findByIdWithNotifications(@Param("reviewId") long reviewId);

    @Query(value = "SELECT r FROM Review r LEFT JOIN FETCH r.author  ORDER BY r.date DESC",
            countQuery = "SELECT count(r) FROM Review r")
    Page<Review> findReviewPage(Pageable pageable);

    @Query(value = "SELECT r FROM Review r LEFT JOIN FETCH r.author WHERE r.author.id IN :userIds ORDER BY r.date DESC",
            countQuery = "SELECT count(r) FROM Review r WHERE r.author.id IN :userIds")
    Page<Review> findUsersPageReviewsOrderDesc(@Param("userIds")List<Long> userIds, Pageable pageable);

    @Query(value = "SELECT r FROM Review r LEFT JOIN FETCH r.author WHERE r.author.id NOT IN :userIds ORDER BY r.date DESC",
            countQuery = "SELECT count(r) FROM Review r WHERE r.author.id NOT IN :userIds")
    List<Review> findReviewPageWithoutUserIds(@Param("userIds")List<Long> userIds, Pageable pageable);

    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.requestNotifications WHERE :user MEMBER OF r.requestNotifications")
    List<Review> findAllReviewsWhereUserIsRequestingNotifications(@Param("user") User user);
}
