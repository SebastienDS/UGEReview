package fr.uge.revue.repository;

import fr.uge.revue.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.reviews LEFT JOIN FETCH u.comments LEFT JOIN FETCH u.responses WHERE u.id = :id")
    Optional<User> findByIdWithContent(long id);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.reviewsLikes LEFT JOIN FETCH u.reviewsDislikes WHERE u.id = :id")
    Optional<User> findByIdWithReviewLikes(long id);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.commentsLikes LEFT JOIN FETCH u.commentsDislikes WHERE u.id = :id")
    Optional<User> findByIdWithCommentLikes(long id);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.responsesLikes LEFT JOIN FETCH u.responsesDislikes WHERE u.id = :id")
    Optional<User> findByIdWithResponseLikes(long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.followers WHERE u.id = :userId")
    Optional<User> findByIdWithFollowers(long userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.comments WHERE u.id = :userId")
    Optional<User> findByIdWithComments(long userId);

    @Query("SELECT u.username FROM User u")
    List<String> findAllUsernames();

    @Query("SELECT u.email FROM User u")
    List<String> findAllEmails();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.reviewsLikes LEFT JOIN FETCH u.commentsLikes c LEFT JOIN FETCH u.responsesLikes r LEFT JOIN FETCH c.review LEFT JOIN FETCH r.comment r2 LEFT JOIN FETCH r2.review WHERE u.id = :userId")
    Optional<User> findByIdWithLikes(long userId);
}
