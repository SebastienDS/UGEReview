package fr.uge.revue.service;

import fr.uge.revue.dto.likeable.LikeableDTO;
import fr.uge.revue.dto.review.LikeStateDTO;
import fr.uge.revue.dto.user.UserAllLikesDTO;
import fr.uge.revue.dto.user.UserSignUpDTO;
import fr.uge.revue.model.*;
import fr.uge.revue.repository.CommentRepository;
import fr.uge.revue.repository.ResponseRepository;
import fr.uge.revue.repository.ReviewRepository;
import fr.uge.revue.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Stream;

@Service
public class UserService implements UserDetailsService {
    private static final long USER_DELETED_ID = 1L;
    private static final long USER_BANNED_ID = 2L;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final ResponseRepository responseRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ReviewRepository reviewRepository, CommentRepository commentRepository, ResponseRepository responseRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.reviewRepository = Objects.requireNonNull(reviewRepository);
        this.commentRepository = Objects.requireNonNull(commentRepository);
        this.responseRepository = Objects.requireNonNull(responseRepository);
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(Objects.requireNonNull(username))
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    }

    public boolean register(UserSignUpDTO user) {
        Objects.requireNonNull(user);
        var userAlreadyExist = userRepository.findByUsername(user.username())
                .or(() -> userRepository.findByEmail(user.email()))
                .isPresent();
        if (userAlreadyExist) {
            return false;
        }
        var encodedPassword = passwordEncoder.encode(user.password());
        var signUpUser = new User(user.username(), user.email(), encodedPassword, Role.USER);
        userRepository.save(signUpUser);
        return true;
    }

    public Optional<User> getUserById(long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(Objects.requireNonNull(email));
    }

    public void updateUserPassword(User user, String password) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(password);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Transactional
    public LikeStateDTO toggleLikeReview(long userId, Review review) {
        var user = userRepository.findByIdWithReviewLikes(userId);
        if (user.isEmpty()) {
            return null;
        }
        var likes = user.get().getReviewsLikes();
        var dislikes = user.get().getReviewsDislikes();
        var likeIsRemoved = likes.remove(review);
        var dislikeIsRemoved = dislikes.remove(review);
        if (likeIsRemoved) {
            review.setLikes(review.getLikes() - 1);
        }
        else if (dislikeIsRemoved) {
            likes.add(review);
            review.setLikes(review.getLikes() + 2); //like + remove dislike
        }
        else {
            likes.add(review);
            review.setLikes(review.getLikes() + 1);
        }
        userRepository.save(user.get());
        reviewRepository.save(review);
        return new LikeStateDTO(review.getLikes(),
                getLikeState(user.get(), user.get().getReviewsLikes(), user.get().getReviewsDislikes(), review));
    }

    @Transactional
    public LikeStateDTO toggleLikeComment(long userId, Comment comment) {
        var user = userRepository.findByIdWithCommentLikes(userId);
        if (user.isEmpty()) {
            return null;
        }
        var likes = user.get().getCommentsLikes();
        var dislikes = user.get().getCommentsDislikes();
        var likeIsRemoved = likes.remove(comment);
        var dislikeIsRemoved = dislikes.remove(comment);
        if (likeIsRemoved) {
            comment.setLikes(comment.getLikes() - 1);
        }
        else if (dislikeIsRemoved) {
            likes.add(comment);
            comment.setLikes(comment.getLikes() + 2); //like + remove dislike
        }
        else {
            likes.add(comment);
            comment.setLikes(comment.getLikes() + 1);
        }
        userRepository.save(user.get());
        commentRepository.save(comment);
        return new LikeStateDTO(comment.getLikes(),
                getLikeState(user.get(), user.get().getCommentsLikes(), user.get().getCommentsDislikes(), comment));
    }

    @Transactional
    public LikeStateDTO toggleLikeResponse(long userId, Response response) {
        var user = userRepository.findByIdWithResponseLikes(userId);
        if (user.isEmpty()) {
            return null;
        }
        var likes = user.get().getResponsesLikes();
        var dislikes = user.get().getResponsesDislikes();
        var likeIsRemoved = likes.remove(response);
        var dislikeIsRemoved = dislikes.remove(response);
        if (likeIsRemoved) {
            response.setLikes(response.getLikes() - 1);
        }
        else if (dislikeIsRemoved) {
            likes.add(response);
            response.setLikes(response.getLikes() + 2); //like + remove dislike
        }
        else {
            likes.add(response);
            response.setLikes(response.getLikes() + 1);
        }
        userRepository.save(user.get());
        responseRepository.save(response);
        return new LikeStateDTO(response.getLikes(),
                getLikeState(user.get(), user.get().getResponsesLikes(), user.get().getResponsesDislikes(), response));
    }

    private static <T> LikeState getLikeState(User user, Set<T> likes, Set<T> dislikes, T entity) {
        if (user == null) return LikeState.NONE;
        if (likes.contains(entity)) return LikeState.LIKE;
        if (dislikes.contains(entity)) return LikeState.DISLIKE;
        return LikeState.NONE;
    }

    public Optional<User> findByIdWithFollowers(long userId) {
        return userRepository.findByIdWithFollowers(userId);
    }

    public boolean follow(long userId, long userFollowedId) {
        var userFound = userRepository.findByIdWithFollowers(userId);
        var userFollowedFound = getUserById(userFollowedId);
        if (userFound.isEmpty() || userFollowedFound.isEmpty()) {
            return false;
        }
        var user = userFound.get();
        user.getFollowers().add(userFollowedFound.get());
        userRepository.save(user);
        return true;
    }


    public boolean unfollow(long userId, long userFollowedId) {
        var userFound = userRepository.findByIdWithFollowers(userId);
        var userFollowedFound = getUserById(userFollowedId);
        if (userFound.isEmpty() || userFollowedFound.isEmpty()) {
            return false;
        }
        var user = userFound.get();
        user.getFollowers().remove(userFollowedFound.get());
        userRepository.save(user);
        return true;
    }

    @Transactional
    public LikeStateDTO toggleDislikeReview(long userId, Review review) {
        var user = userRepository.findByIdWithReviewLikes(userId);
        if (user.isEmpty()) {
            return null;
        }
        var likes = user.get().getReviewsLikes();
        var dislikes = user.get().getReviewsDislikes();
        var likeIsRemoved = likes.remove(review);
        var dislikeIsRemoved = dislikes.remove(review);
        if (likeIsRemoved) {
            dislikes.add(review);
            review.setLikes(review.getLikes() - 2); //remove like +  dislike
        }
        else if (dislikeIsRemoved) {
            review.setLikes(review.getLikes() + 1);
        }
        else {
            dislikes.add(review);
            review.setLikes(review.getLikes() - 1);
        }
        userRepository.save(user.get());
        reviewRepository.save(review);
        return new LikeStateDTO(review.getLikes(),
                getLikeState(user.get(), user.get().getReviewsLikes(), user.get().getReviewsDislikes(), review));
    }

    @Transactional
    public LikeStateDTO toggleDislikeComment(long userId, Comment comment) {
        var user = userRepository.findByIdWithCommentLikes(userId);
        if (user.isEmpty()) {
            return null;
        }
        var likes = user.get().getCommentsLikes();
        var dislikes = user.get().getCommentsDislikes();
        var likeIsRemoved = likes.remove(comment);
        var dislikeIsRemoved = dislikes.remove(comment);
        if (likeIsRemoved) {
            dislikes.add(comment);
            comment.setLikes(comment.getLikes() - 2); //remove like +  dislike
        }
        else if (dislikeIsRemoved) {
            comment.setLikes(comment.getLikes() + 1);
        }
        else {
            dislikes.add(comment);
            comment.setLikes(comment.getLikes() - 1);
        }
        userRepository.save(user.get());
        commentRepository.save(comment);
        return  new LikeStateDTO(comment.getLikes(),
                getLikeState(user.get(), user.get().getCommentsLikes(), user.get().getCommentsDislikes(), comment));
    }

    @Transactional
    public LikeStateDTO toggleDislikeResponse(long userId, Response response) {
        var user = userRepository.findByIdWithResponseLikes(userId);
        if (user.isEmpty()) {
            return null;
        }
        var likes = user.get().getResponsesLikes();
        var dislikes = user.get().getResponsesDislikes();
        var likeIsRemoved = likes.remove(response);
        var dislikeIsRemoved = dislikes.remove(response);
        if (likeIsRemoved) {
            dislikes.add(response);
            response.setLikes(response.getLikes() - 2); //remove like +  dislike
        }
        else if (dislikeIsRemoved) {
            response.setLikes(response.getLikes() + 1);
        }
        else {
            dislikes.add(response);
            response.setLikes(response.getLikes() - 1);
        }
        userRepository.save(user.get());
        responseRepository.save(response);
        return new LikeStateDTO(response.getLikes(),
                getLikeState(user.get(), user.get().getResponsesLikes(), user.get().getResponsesDislikes(), response));
    }

    @Transactional
    public void deleteUser(long userId) {
        var userDeleted = userRepository.findById(USER_DELETED_ID).orElseThrow();
        var user = userRepository.findByIdWithContent(userId).orElseThrow();
        user.getReviews().forEach(review -> review.setAuthor(userDeleted));
        user.getComments().forEach(comment -> comment.setAuthor(userDeleted));
        user.getResponses().forEach(response -> response.setAuthor(userDeleted));
        user.setReviews(Set.of());
        user.setComments(Set.of());
        user.setResponses(Set.of());
        userRepository.delete(user);
        userRepository.save(userDeleted);
    }

    public Set<Comment> getComments(long userId) {
        var user = userRepository.findByIdWithComments(userId).orElseThrow();
        return user.getComments();
    }

    public Set<Response> getResponses(long userId) {
        var user = userRepository.findByIdWithResponses(userId).orElseThrow();
        return user.getResponses();
    }

    public List<Review> findAllUserReviews(long userId) {
        return reviewRepository.findAllUserReviews(userId);
    }

    public List<Review> findAllUserReviewsMatching(long userId, String search) {
        return reviewRepository.findAllUserReviewsMatching(userId, Objects.requireNonNull(search));
    }

    public List<String> getUsernames() {
        return userRepository.findAllUsernames();
    }

    @Transactional
    public void setUsername(long userId, String username) {
        Objects.requireNonNull(username);
        User user = userRepository.findById(userId).orElseThrow();
        user.setUsername(username);
        userRepository.save(user);
    }

    public List<String> getEmails() {
        return userRepository.findAllEmails();
    }

    @Transactional
    public void setEmail(long userId, String email) {
        Objects.requireNonNull(email);
        User user = userRepository.findById(userId).orElseThrow();
        user.setEmail(email);
        userRepository.save(user);
    }

    public List<LikeableDTO> getLikedListFromUser(long userId) {
        var user = userRepository.findByIdWithLikes(userId).orElseThrow();
        return Stream.of(user.getReviewsLikes(), user.getCommentsLikes(), user.getResponsesLikes())
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(Likeable::getDate).reversed())
                .map(LikeableDTO::from)
                .toList();
    }

    public Optional<User> findUserWithLikesAndDislikes(long userId) {
        return userRepository.findByIdWithLikesAndDislikes(userId);
    }

    @Transactional
    public void banUser(long id) {
        var userBanned = userRepository.findById(USER_BANNED_ID).orElseThrow();
        var user = userRepository.findByIdWithContent(id).orElseThrow();
        user.getReviews().forEach(review -> review.setAuthor(userBanned));
        user.getComments().forEach(comment -> comment.setAuthor(userBanned));
        user.getResponses().forEach(response -> response.setAuthor(userBanned));
        user.setReviews(new HashSet<>());
        user.setComments(new HashSet<>());
        user.setResponses(new HashSet<>());
        user.setAccountNonLocked(false);
        userRepository.save(user);
        userRepository.save(userBanned);
    }
}
