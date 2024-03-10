package fr.uge.revue.repository;

import fr.uge.revue.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.reviews LEFT JOIN FETCH u.comments LEFT JOIN FETCH u.responses WHERE u.id = :userId")
    Optional<User> findByIdWithContent(@Param("userId") long userId);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.reviewsLikes LEFT JOIN FETCH u.reviewsDislikes WHERE u.id = :userId")
    Optional<User> findByIdWithReviewLikes(@Param("userId") long userId);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.commentsLikes LEFT JOIN FETCH u.commentsDislikes WHERE u.id = :userId")
    Optional<User> findByIdWithCommentLikes(@Param("userId") long userId);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.responsesLikes LEFT JOIN FETCH u.responsesDislikes WHERE u.id = :userId")
    Optional<User> findByIdWithResponseLikes(@Param("userId") long userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.followers WHERE u.id = :userId")
    Optional<User> findByIdWithFollowers(@Param("userId") long userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.comments c LEFT JOIN FETCH c.review WHERE u.id = :userId")
    Optional<User> findByIdWithComments(@Param("userId") long userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.responses r LEFT JOIN FETCH r.comment c LEFT JOIN FETCH c.review WHERE u.id = :userId")
    Optional<User> findByIdWithResponses(@Param("userId") long userId);

    @Query("SELECT u.username FROM User u")
    List<String> findAllUsernames();

    @Query("SELECT u.email FROM User u")
    List<String> findAllEmails();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.reviewsLikes r LEFT JOIN FETCH r.author LEFT JOIN FETCH u.commentsLikes c LEFT JOIN FETCH c.author LEFT JOIN FETCH u.responsesLikes r2 LEFT JOIN FETCH r2.author LEFT JOIN FETCH c.review LEFT JOIN FETCH r2.comment r3 LEFT JOIN FETCH r3.review WHERE u.id = :userId")
    Optional<User> findByIdWithLikes(@Param("userId") long userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.reviewsLikes LEFT JOIN FETCH u.commentsLikes c LEFT JOIN FETCH u.responsesLikes r LEFT JOIN FETCH u.reviewsDislikes LEFT JOIN FETCH u.commentsDislikes LEFT JOIN FETCH u.responsesDislikes WHERE u.id = :userId")
    Optional<User> findByIdWithLikesAndDislikes(@Param("userId") long userId);
}
