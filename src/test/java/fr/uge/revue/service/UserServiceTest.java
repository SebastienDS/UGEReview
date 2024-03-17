package fr.uge.revue.service;

import fr.uge.revue.dto.user.UserSignUpDTO;
import fr.uge.revue.model.*;
import fr.uge.revue.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ResponseRepository responseRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userService = new UserService(userRepository, reviewRepository, commentRepository, responseRepository, notificationRepository, passwordEncoder);
    }

    @Test
    void loadUserByUsername_withUsername() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);

        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));

        var userFound = userService.loadUserByUsername(user.getUsername());

        assertEquals(user, userFound);
    }

    @Test
    void loadUserByUsername_withEmail() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);

        given(userRepository.findByUsername(user.getEmail())).willReturn(Optional.of(user));

        var userFound = userService.loadUserByUsername(user.getEmail());

        assertEquals(user, userFound);
    }

    @Test
    void loadUserByUsername_notFound() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);

        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->  userService.loadUserByUsername(user.getUsername()));
    }

    @Test
    void register() {
        var userSignup = new UserSignUpDTO("testuser", "test@example.com", "password");
        var user = new User("testuser", "test@example.com", "encodedPassword", Role.USER);

        given(userRepository.findByUsername(userSignup.username())).willReturn(Optional.empty());
        given(userRepository.findByEmail(userSignup.email())).willReturn(Optional.empty());
        given(passwordEncoder.encode(userSignup.password())).willReturn("encodedPassword");

        var res = userService.register(userSignup);

        var captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        assertTrue(res);
        assertEquals(user, captor.getValue());
    }

    @Test
    void register_usernameAlreadyExist() {
        var userSignup = new UserSignUpDTO("testuser", "test@example.com", "password");
        var user = new User("testuser", "test@example.com", "encodedPassword", Role.USER);

        given(userRepository.findByUsername(userSignup.username())).willReturn(Optional.of(user));

        var res = userService.register(userSignup);

        verify(userRepository, never()).save(any(User.class));
        assertFalse(res);
    }

    @Test
    void register_emailAlreadyExist() {
        var userSignup = new UserSignUpDTO("testuser", "test@example.com", "password");
        var user = new User("testuser", "test@example.com", "encodedPassword", Role.USER);

        given(userRepository.findByUsername(userSignup.username())).willReturn(Optional.empty());
        given(userRepository.findByEmail(userSignup.email())).willReturn(Optional.of(user));

        var res = userService.register(userSignup);

        verify(userRepository, never()).save(any(User.class));
        assertFalse(res);
    }

    @Test
    void getUserById() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        var userFound = userService.getUserById(user.getId());

        assertTrue(userFound.isPresent());
        assertEquals(user, userFound.get());
    }

    @Test
    void getUserById_notFound() {
        given(userRepository.findById(5L)).willReturn(Optional.empty());

        var userFound = userService.getUserById(5);

        assertTrue(userFound.isEmpty());
    }

    @Test
    void findByEmail() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        var userFound = userService.findByEmail(user.getEmail());

        assertTrue(userFound.isPresent());
        assertEquals(user, userFound.get());
    }

    @Test
    void findByEmail_notFound() {
        var email = "test@example.com";
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        var userFound = userService.findByEmail(email);

        assertTrue(userFound.isEmpty());
    }

    @Test
    void updateUserPassword() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var newPassword = "newPassord";
        var encodedPassword = "encodedPassword";

        given(passwordEncoder.encode(newPassword)).willReturn(encodedPassword);

        userService.updateUserPassword(user, newPassword);

        var captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(encodedPassword, user.getPassword());
    }

    @Test
    void toggleLikeReview() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);

        given(userRepository.findByIdWithReviewLikes(user.getId())).willReturn(Optional.of(user));

        var state = userService.toggleLikeReview(user.getId(), review);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        var reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(userRepository).save(userCaptor.capture());
        verify(reviewRepository).save(reviewCaptor.capture());

        assertNotNull(state);
        assertEquals(1, state.likes());
        assertEquals(LikeState.LIKE, state.likeState());
        assertEquals(1, reviewCaptor.getValue().getLikes());
        assertTrue(userCaptor.getValue().getReviewsLikes().contains(review));
        assertFalse(userCaptor.getValue().getReviewsDislikes().contains(review));
    }

    @Test
    void toggleLikeReview_notFound() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);

        given(userRepository.findByIdWithReviewLikes(5)).willReturn(Optional.empty());

        var state = userService.toggleLikeReview(5, review);

        verify(userRepository, never()).save(any());
        verify(reviewRepository, never()).save(any());

        assertNull(state);
    }

    @Test
    void toggleLikeReview_cancelLike() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        user.getReviewsLikes().add(review);
        review.setLikes(1);

        given(userRepository.findByIdWithReviewLikes(user.getId())).willReturn(Optional.of(user));

        var state = userService.toggleLikeReview(user.getId(), review);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        var reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(userRepository).save(userCaptor.capture());
        verify(reviewRepository).save(reviewCaptor.capture());

        assertNotNull(state);
        assertEquals(0, state.likes());
        assertEquals(LikeState.NONE, state.likeState());
        assertEquals(0, reviewCaptor.getValue().getLikes());
        assertFalse(userCaptor.getValue().getReviewsLikes().contains(review));
        assertFalse(userCaptor.getValue().getReviewsDislikes().contains(review));
    }

    @Test
    void toggleLikeReview_cancelDislikeAndLike() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        user.getReviewsDislikes().add(review);
        review.setLikes(-1);

        given(userRepository.findByIdWithReviewLikes(user.getId())).willReturn(Optional.of(user));

        var state = userService.toggleLikeReview(user.getId(), review);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        var reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(userRepository).save(userCaptor.capture());
        verify(reviewRepository).save(reviewCaptor.capture());

        assertNotNull(state);
        assertEquals(1, state.likes());
        assertEquals(LikeState.LIKE, state.likeState());
        assertEquals(1, reviewCaptor.getValue().getLikes());
        assertTrue(userCaptor.getValue().getReviewsLikes().contains(review));
        assertFalse(userCaptor.getValue().getReviewsDislikes().contains(review));
    }

    @Test
    void toggleLikeComment() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        review.getComments().add(comment);

        given(userRepository.findByIdWithCommentLikes(user.getId())).willReturn(Optional.of(user));

        var state = userService.toggleLikeComment(user.getId(), comment);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        var commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(userRepository).save(userCaptor.capture());
        verify(commentRepository).save(commentCaptor.capture());

        assertNotNull(state);
        assertEquals(1, state.likes());
        assertEquals(LikeState.LIKE, state.likeState());
        assertEquals(1, commentCaptor.getValue().getLikes());
        assertTrue(userCaptor.getValue().getCommentsLikes().contains(comment));
        assertFalse(userCaptor.getValue().getCommentsDislikes().contains(comment));
    }

    @Test
    void toggleLikeComment_notFound() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        review.getComments().add(comment);

        given(userRepository.findByIdWithCommentLikes(5)).willReturn(Optional.empty());

        var state = userService.toggleLikeComment(5, comment);

        verify(userRepository, never()).save(any());
        verify(reviewRepository, never()).save(any());

        assertNull(state);
    }

    @Test
    void toggleLikeComment_cancelLike() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        review.getComments().add(comment);
        user.getCommentsLikes().add(comment);
        comment.setLikes(1);

        given(userRepository.findByIdWithCommentLikes(user.getId())).willReturn(Optional.of(user));

        var state = userService.toggleLikeComment(user.getId(), comment);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        var commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(userRepository).save(userCaptor.capture());
        verify(commentRepository).save(commentCaptor.capture());

        assertNotNull(state);
        assertEquals(0, state.likes());
        assertEquals(LikeState.NONE, state.likeState());
        assertEquals(0, commentCaptor.getValue().getLikes());
        assertFalse(userCaptor.getValue().getCommentsLikes().contains(comment));
        assertFalse(userCaptor.getValue().getCommentsDislikes().contains(comment));
    }

    @Test
    void toggleLikeComment_cancelDislikeAndLike() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        review.getComments().add(comment);
        user.getCommentsDislikes().add(comment);
        comment.setLikes(-1);

        given(userRepository.findByIdWithCommentLikes(user.getId())).willReturn(Optional.of(user));

        var state = userService.toggleLikeComment(user.getId(), comment);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        var commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(userRepository).save(userCaptor.capture());
        verify(commentRepository).save(commentCaptor.capture());

        assertNotNull(state);
        assertEquals(1, state.likes());
        assertEquals(LikeState.LIKE, state.likeState());
        assertEquals(1, commentCaptor.getValue().getLikes());
        assertTrue(userCaptor.getValue().getCommentsLikes().contains(comment));
        assertFalse(userCaptor.getValue().getCommentsDislikes().contains(comment));
    }

    @Test
    void toggleLikeResponse() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);
        review.getComments().add(comment);
        comment.getResponses().add(response);

        given(userRepository.findByIdWithResponseLikes(user.getId())).willReturn(Optional.of(user));

        var state = userService.toggleLikeResponse(user.getId(), response);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        var responseCaptor = ArgumentCaptor.forClass(Response.class);
        verify(userRepository).save(userCaptor.capture());
        verify(responseRepository).save(responseCaptor.capture());

        assertNotNull(state);
        assertEquals(1, state.likes());
        assertEquals(LikeState.LIKE, state.likeState());
        assertEquals(1, responseCaptor.getValue().getLikes());
        assertTrue(userCaptor.getValue().getResponsesLikes().contains(response));
        assertFalse(userCaptor.getValue().getResponsesDislikes().contains(response));
    }

    @Test
    void toggleLikeResponse_notFound() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);
        review.getComments().add(comment);
        comment.getResponses().add(response);

        given(userRepository.findByIdWithResponseLikes(5)).willReturn(Optional.empty());

        var state = userService.toggleLikeResponse(5, response);

        verify(userRepository, never()).save(any());
        verify(responseRepository, never()).save(any());

        assertNull(state);
    }

    @Test
    void toggleLikeResponse_cancelLike() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);
        review.getComments().add(comment);
        comment.getResponses().add(response);
        user.getResponsesLikes().add(response);
        response.setLikes(1);

        given(userRepository.findByIdWithResponseLikes(user.getId())).willReturn(Optional.of(user));

        var state = userService.toggleLikeResponse(user.getId(), response);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        var responseCaptor = ArgumentCaptor.forClass(Response.class);
        verify(userRepository).save(userCaptor.capture());
        verify(responseRepository).save(responseCaptor.capture());

        assertNotNull(state);
        assertEquals(0, state.likes());
        assertEquals(LikeState.NONE, state.likeState());
        assertEquals(0, responseCaptor.getValue().getLikes());
        assertFalse(userCaptor.getValue().getResponsesLikes().contains(response));
        assertFalse(userCaptor.getValue().getResponsesDislikes().contains(response));
    }

    @Test
    void toggleLikeResponse_cancelDislikeAndLike() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);
        review.getComments().add(comment);
        comment.getResponses().add(response);
        user.getResponsesDislikes().add(response);
        response.setLikes(-1);

        given(userRepository.findByIdWithResponseLikes(user.getId())).willReturn(Optional.of(user));

        var state = userService.toggleLikeResponse(user.getId(), response);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        var responseCaptor = ArgumentCaptor.forClass(Response.class);
        verify(userRepository).save(userCaptor.capture());
        verify(responseRepository).save(responseCaptor.capture());

        assertNotNull(state);
        assertEquals(1, state.likes());
        assertEquals(LikeState.LIKE, state.likeState());
        assertEquals(1, responseCaptor.getValue().getLikes());
        assertTrue(userCaptor.getValue().getResponsesLikes().contains(response));
        assertFalse(userCaptor.getValue().getResponsesDislikes().contains(response));
    }

    @Test
    void findByIdWithFollowers() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var user2 = new User("testuser2", "test@example.com", "password", Role.USER);
        user2.setId(2);
        user.getFollowers().add(user2);

        given(userRepository.findByIdWithFollowers(user.getId())).willReturn(Optional.of(user));

        var userFound = userService.findByIdWithFollowers(user.getId());

        assertTrue(userFound.isPresent());
        assertEquals(user, userFound.get());
        assertEquals(1, userFound.get().getFollowers().size());
    }

    @Test
    void findByIdWithFollowers_notFound() {
        given(userRepository.findByIdWithFollowers(5L)).willReturn(Optional.empty());

        var userFound = userService.findByIdWithFollowers(5);

        assertTrue(userFound.isEmpty());
    }

    @Test
    void follow() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var user2 = new User("testuser2", "test@example.com", "password", Role.USER);
        user2.setId(2);

        given(userRepository.findByIdWithFollowers(user.getId())).willReturn(Optional.of(user));
        given(userRepository.findById(user2.getId())).willReturn(Optional.of(user2));

        var res = userService.follow(user.getId(), user2.getId());

        var captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        assertTrue(res);
        assertEquals(user, captor.getValue());
        assertTrue(captor.getValue().getFollowers().contains(user2));
    }

    @Test
    void follow_user1NotFound() {
        var user2 = new User("testuser2", "test@example.com", "password", Role.USER);
        user2.setId(2);

        given(userRepository.findByIdWithFollowers(5)).willReturn(Optional.empty());

        var res = userService.follow(5, user2.getId());

        verify(userRepository, never()).save(any());
        assertFalse(res);
    }

    @Test
    void follow_user2NotFound() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);

        given(userRepository.findByIdWithFollowers(user.getId())).willReturn(Optional.of(user));
        given(userRepository.findById(5L)).willReturn(Optional.empty());


        var res = userService.follow(user.getId(), 5);

        verify(userRepository, never()).save(any());
        assertFalse(res);
    }

    @Test
    void unfollow() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var user2 = new User("testuser2", "test@example.com", "password", Role.USER);
        user2.setId(2);
        user.getFollowers().add(user2);

        given(userRepository.findByIdWithFollowers(user.getId())).willReturn(Optional.of(user));
        given(userRepository.findById(user2.getId())).willReturn(Optional.of(user2));

        var res = userService.unfollow(user.getId(), user2.getId());

        var captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        assertTrue(res);
        assertEquals(user, captor.getValue());
        assertFalse(captor.getValue().getFollowers().contains(user2));
    }

    @Test
    void unfollow_user1NotFound() {
        var user2 = new User("testuser2", "test@example.com", "password", Role.USER);
        user2.setId(2);

        given(userRepository.findByIdWithFollowers(5)).willReturn(Optional.empty());

        var res = userService.unfollow(5, user2.getId());

        verify(userRepository, never()).save(any());
        assertFalse(res);
    }

    @Test
    void unfollow_user2NotFound() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);

        given(userRepository.findByIdWithFollowers(user.getId())).willReturn(Optional.of(user));
        given(userRepository.findById(5L)).willReturn(Optional.empty());


        var res = userService.unfollow(user.getId(), 5);

        verify(userRepository, never()).save(any());
        assertFalse(res);
    }

    @Test
    void toggleDislikeReview() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);

        given(userRepository.findByIdWithReviewLikes(user.getId())).willReturn(Optional.of(user));

        var state = userService.toggleDislikeReview(user.getId(), review);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        var reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(userRepository).save(userCaptor.capture());
        verify(reviewRepository).save(reviewCaptor.capture());

        assertNotNull(state);
        assertEquals(-1, state.likes());
        assertEquals(LikeState.DISLIKE, state.likeState());
        assertEquals(-1, reviewCaptor.getValue().getLikes());
        assertFalse(userCaptor.getValue().getReviewsLikes().contains(review));
        assertTrue(userCaptor.getValue().getReviewsDislikes().contains(review));
    }

    @Test
    void toggleDislikeReview_notFound() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);

        given(userRepository.findByIdWithReviewLikes(5)).willReturn(Optional.empty());

        var state = userService.toggleDislikeReview(5, review);

        verify(userRepository, never()).save(any());
        verify(reviewRepository, never()).save(any());

        assertNull(state);
    }

    @Test
    void toggleDislikeReview_cancelDislike() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        user.getReviewsDislikes().add(review);
        review.setLikes(-1);

        given(userRepository.findByIdWithReviewLikes(user.getId())).willReturn(Optional.of(user));

        var state = userService.toggleDislikeReview(user.getId(), review);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        var reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(userRepository).save(userCaptor.capture());
        verify(reviewRepository).save(reviewCaptor.capture());

        assertNotNull(state);
        assertEquals(0, state.likes());
        assertEquals(LikeState.NONE, state.likeState());
        assertEquals(0, reviewCaptor.getValue().getLikes());
        assertFalse(userCaptor.getValue().getReviewsLikes().contains(review));
        assertFalse(userCaptor.getValue().getReviewsDislikes().contains(review));
    }

    @Test
    void toggleDislikeReview_cancelLikeAndDislike() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        user.getReviewsLikes().add(review);
        review.setLikes(1);

        given(userRepository.findByIdWithReviewLikes(user.getId())).willReturn(Optional.of(user));

        var state = userService.toggleDislikeReview(user.getId(), review);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        var reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(userRepository).save(userCaptor.capture());
        verify(reviewRepository).save(reviewCaptor.capture());

        assertNotNull(state);
        assertEquals(-1, state.likes());
        assertEquals(LikeState.DISLIKE, state.likeState());
        assertEquals(-1, reviewCaptor.getValue().getLikes());
        assertFalse(userCaptor.getValue().getReviewsLikes().contains(review));
        assertTrue(userCaptor.getValue().getReviewsDislikes().contains(review));
    }

    @Test
    void toggleDislikeComment() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        review.getComments().add(comment);

        given(userRepository.findByIdWithCommentLikes(user.getId())).willReturn(Optional.of(user));

        var state = userService.toggleDislikeComment(user.getId(), comment);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        var commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(userRepository).save(userCaptor.capture());
        verify(commentRepository).save(commentCaptor.capture());

        assertNotNull(state);
        assertEquals(-1, state.likes());
        assertEquals(LikeState.DISLIKE, state.likeState());
        assertEquals(-1, commentCaptor.getValue().getLikes());
        assertFalse(userCaptor.getValue().getCommentsLikes().contains(comment));
        assertTrue(userCaptor.getValue().getCommentsDislikes().contains(comment));
    }

    @Test
    void toggleDislikeComment_notFound() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        review.getComments().add(comment);

        given(userRepository.findByIdWithCommentLikes(5)).willReturn(Optional.empty());

        var state = userService.toggleDislikeComment(5, comment);

        verify(userRepository, never()).save(any());
        verify(reviewRepository, never()).save(any());

        assertNull(state);
    }

    @Test
    void toggleDislikeComment_cancelDislike() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        review.getComments().add(comment);
        user.getCommentsDislikes().add(comment);
        comment.setLikes(-1);

        given(userRepository.findByIdWithCommentLikes(user.getId())).willReturn(Optional.of(user));

        var state = userService.toggleDislikeComment(user.getId(), comment);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        var commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(userRepository).save(userCaptor.capture());
        verify(commentRepository).save(commentCaptor.capture());

        assertNotNull(state);
        assertEquals(0, state.likes());
        assertEquals(LikeState.NONE, state.likeState());
        assertEquals(0, commentCaptor.getValue().getLikes());
        assertFalse(userCaptor.getValue().getCommentsLikes().contains(comment));
        assertFalse(userCaptor.getValue().getCommentsDislikes().contains(comment));
    }

    @Test
    void toggleDislikeComment_cancelLikeAndDislike() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        review.getComments().add(comment);
        user.getCommentsLikes().add(comment);
        comment.setLikes(1);

        given(userRepository.findByIdWithCommentLikes(user.getId())).willReturn(Optional.of(user));

        var state = userService.toggleDislikeComment(user.getId(), comment);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        var commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(userRepository).save(userCaptor.capture());
        verify(commentRepository).save(commentCaptor.capture());

        assertNotNull(state);
        assertEquals(-1, state.likes());
        assertEquals(LikeState.DISLIKE, state.likeState());
        assertEquals(-1, commentCaptor.getValue().getLikes());
        assertFalse(userCaptor.getValue().getCommentsLikes().contains(comment));
        assertTrue(userCaptor.getValue().getCommentsDislikes().contains(comment));
    }

    @Test
    void toggleDislikeResponse() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);
        review.getComments().add(comment);
        comment.getResponses().add(response);

        given(userRepository.findByIdWithResponseLikes(user.getId())).willReturn(Optional.of(user));

        var state = userService.toggleDislikeResponse(user.getId(), response);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        var responseCaptor = ArgumentCaptor.forClass(Response.class);
        verify(userRepository).save(userCaptor.capture());
        verify(responseRepository).save(responseCaptor.capture());

        assertNotNull(state);
        assertEquals(-1, state.likes());
        assertEquals(LikeState.DISLIKE, state.likeState());
        assertEquals(-1, responseCaptor.getValue().getLikes());
        assertFalse(userCaptor.getValue().getResponsesLikes().contains(response));
        assertTrue(userCaptor.getValue().getResponsesDislikes().contains(response));
    }

    @Test
    void toggleDislikeResponse_notFound() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);
        review.getComments().add(comment);
        comment.getResponses().add(response);

        given(userRepository.findByIdWithResponseLikes(5)).willReturn(Optional.empty());

        var state = userService.toggleDislikeResponse(5, response);

        verify(userRepository, never()).save(any());
        verify(responseRepository, never()).save(any());

        assertNull(state);
    }

    @Test
    void toggleDislikeResponse_cancelDislike() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);
        review.getComments().add(comment);
        comment.getResponses().add(response);
        user.getResponsesDislikes().add(response);
        response.setLikes(-1);

        given(userRepository.findByIdWithResponseLikes(user.getId())).willReturn(Optional.of(user));

        var state = userService.toggleDislikeResponse(user.getId(), response);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        var responseCaptor = ArgumentCaptor.forClass(Response.class);
        verify(userRepository).save(userCaptor.capture());
        verify(responseRepository).save(responseCaptor.capture());

        assertNotNull(state);
        assertEquals(0, state.likes());
        assertEquals(LikeState.NONE, state.likeState());
        assertEquals(0, responseCaptor.getValue().getLikes());
        assertFalse(userCaptor.getValue().getResponsesLikes().contains(response));
        assertFalse(userCaptor.getValue().getResponsesDislikes().contains(response));
    }

    @Test
    void toggleDislikeResponse_cancelLikeAndDislike() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);
        review.getComments().add(comment);
        comment.getResponses().add(response);
        user.getResponsesLikes().add(response);
        response.setLikes(1);

        given(userRepository.findByIdWithResponseLikes(user.getId())).willReturn(Optional.of(user));

        var state = userService.toggleDislikeResponse(user.getId(), response);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        var responseCaptor = ArgumentCaptor.forClass(Response.class);
        verify(userRepository).save(userCaptor.capture());
        verify(responseRepository).save(responseCaptor.capture());

        assertNotNull(state);
        assertEquals(-1, state.likes());
        assertEquals(LikeState.DISLIKE, state.likeState());
        assertEquals(-1, responseCaptor.getValue().getLikes());
        assertFalse(userCaptor.getValue().getResponsesLikes().contains(response));
        assertTrue(userCaptor.getValue().getResponsesDislikes().contains(response));
    }

    @Test
    void getUsernames() {
        var allUsernames = List.of("test", "test2", "test3");

        given(userRepository.findAllUsernames()).willReturn(allUsernames);

        var usernames = userService.getUsernames();
        assertEquals(allUsernames, usernames);
    }

    @Test
    void setUsername() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var newUsername = "newUsername";

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        userService.setUsername(user.getId(), newUsername);

        var captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(newUsername, captor.getValue().getUsername());
    }

    @Test
    void getEmails() {
        var allEmails = List.of("test@example.com", "test2@example.com", "test3@example.com");

        given(userRepository.findAllEmails()).willReturn(allEmails);

        var emails = userService.getEmails();
        assertEquals(allEmails, emails);
    }

    @Test
    void setEmail() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var newEmail = "new@example.com";

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        userService.setEmail(user.getId(), newEmail);

        var captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(newEmail, captor.getValue().getEmail());
    }

    @Test
    void findUserWithLikesAndDislikes() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);
        review.getComments().add(comment);
        comment.getResponses().add(response);
        user.getReviewsLikes().add(review);
        user.getCommentsDislikes().add(comment);
        user.getResponsesLikes().add(response);

        given(userRepository.findByIdWithLikesAndDislikes(user.getId())).willReturn(Optional.of(user));

        var userFound = userService.findUserWithLikesAndDislikes(user.getId());

        assertTrue(userFound.isPresent());
        assertEquals(user, userFound.get());
        assertEquals(1, userFound.get().getReviewsLikes().size());
        assertEquals(1, userFound.get().getCommentsDislikes().size());
        assertEquals(1, userFound.get().getReviewsLikes().size());
    }

    @Test
    void getComments() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        var comment1 = new Comment("comment1", user, review);
        comment1.setId(1);
        review.getComments().add(comment1);
        var comment2 = new Comment("comment2", user, review);
        comment2.setId(2);
        review.getComments().add(comment2);

        given(commentRepository.findByAuthorId(user.getId(), PageRequest.of(1, 5)))
                .willReturn(new PageImpl<>(List.of(comment1, comment2)));

        var comments = userService.getComments(user.getId(), 1, 5);
        assertEquals(2, comments.size());
    }

    @Test
    void getResponses() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("title", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response1 = new Response("response1", user, comment);
        response1.setId(1);
        var response2 = new Response("response2", user, comment);
        response2.setId(2);
        review.getComments().add(comment);
        comment.setResponses(Set.of(response1, response2));

        given(responseRepository.findByAuthorIdPage(user.getId(), PageRequest.of(1, 5)))
                .willReturn(new PageImpl<>(List.of(response1, response2)));

        var responses = userService.getResponses(user.getId(), 1, 5);
        assertEquals(2, responses.size());
    }
}