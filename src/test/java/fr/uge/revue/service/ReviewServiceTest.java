package fr.uge.revue.service;

import fr.uge.revue.dto.microservice.TestRequestDTO;
import fr.uge.revue.dto.microservice.TestResponseDTO;
import fr.uge.revue.dto.review.CreateReviewDTO;
import fr.uge.revue.model.*;
import fr.uge.revue.repository.ReviewRepository;
import fr.uge.revue.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    private ReviewService reviewService;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private WebClient webClient;

    @BeforeEach
    public void setUp() {
        reviewService = new ReviewService(reviewRepository, userRepository, webClient);
    }

    @Test
    void getReview() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);
        user.getReviews().add(review);
        review.getComments().add(comment);
        comment.addResponse(response);

        given(reviewRepository.findByIdWithFullContent(review.getId()))
                .willReturn(Optional.of(review));

        var reviewFound = reviewService.getReview(review.getId());

        assertTrue(reviewFound.isPresent());
        assertEquals(review, reviewFound.get());
        assertEquals(1, reviewFound.get().getComments().size());
        assertEquals(1, reviewFound.get().getComments().stream().findFirst().orElseThrow().getResponses().size());
    }

    @Test
    void getReview_notFound() {
        given(reviewRepository.findByIdWithFullContent(5))
                .willReturn(Optional.empty());

        var reviewFound = reviewService.getReview(5);

        assertTrue(reviewFound.isEmpty());
    }

    private void mockRequest(CreateReviewDTO createReviewDTO, Mono<TestResponseDTO> returnValue) {
        given(webClient.post()).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.uri("/microservice/api/v1/launchTests")).willReturn(requestBodySpec);
        given(requestBodySpec.bodyValue(new TestRequestDTO(createReviewDTO.code(), createReviewDTO.test()))).willReturn(requestHeadersSpec);
        given(requestHeadersSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToMono(TestResponseDTO.class)).willReturn(returnValue);
    }

    @Test
    void createReview() {
        var createReviewDTO = new CreateReviewDTO("title", "commentary", "code", "test", null, null);
        User user = new User("testuser", "test@example.com", "password", Role.USER);

        mockRequest(createReviewDTO, Mono.just(new TestResponseDTO(false, new TestResponseDTO.Result(2, 2), List.of())));

        var review = reviewService.createReview(createReviewDTO, user);

        var captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository, times(2)).save(captor.capture());
        var savedReviews = captor.getAllValues();
        var reviewMaybeWithoutTest = savedReviews.get(0);
        var reviewWithTest = savedReviews.get(1);

        assertEquals(review, reviewMaybeWithoutTest);
        assertEquals(review, reviewWithTest);
        assertNotNull(reviewWithTest.getUnitTests());
        assertTrue(reviewMaybeWithoutTest.getRequestNotifications().contains(user));
        assertTrue(reviewWithTest.getRequestNotifications().contains(user));
        assertEquals(createReviewDTO.title(), review.getTitle());
        assertEquals(createReviewDTO.commentary(), review.getCommentary());
        assertEquals(createReviewDTO.code(), review.getCode());
        assertEquals(createReviewDTO.test(), review.getTest());
        assertEquals(user, review.getAuthor());
        assertEquals(2, review.getUnitTests().getSucceededCount());
        assertEquals(2, review.getUnitTests().getTotalCount());
        assertEquals(0, review.getUnitTests().getErrors().size());
    }

    @Test
    void createReview_error() {
        var createReviewDTO = new CreateReviewDTO("title", "commentary", "code", "test", null, null);
        User user = new User("testuser", "test@example.com", "password", Role.USER);

        mockRequest(createReviewDTO, Mono.just(new TestResponseDTO(true, new TestResponseDTO.Result(0, 0), List.of("wrong class format"))));


        var review = reviewService.createReview(createReviewDTO, user);

        var captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository, times(2)).save(captor.capture());
        var savedReviews = captor.getAllValues();
        var reviewMaybeWithoutTest = savedReviews.get(0);
        var reviewWithTest = savedReviews.get(1);

        assertEquals(review, reviewMaybeWithoutTest);
        assertEquals(review, reviewWithTest);
        assertNotNull(reviewWithTest.getUnitTests());
        assertTrue(reviewMaybeWithoutTest.getRequestNotifications().contains(user));
        assertTrue(reviewWithTest.getRequestNotifications().contains(user));
        assertEquals(createReviewDTO.title(), review.getTitle());
        assertEquals(createReviewDTO.commentary(), review.getCommentary());
        assertEquals(createReviewDTO.code(), review.getCode());
        assertEquals(createReviewDTO.test(), review.getTest());
        assertEquals(user, review.getAuthor());
        assertEquals(0, review.getUnitTests().getSucceededCount());
        assertEquals(0, review.getUnitTests().getTotalCount());
        assertEquals(1, review.getUnitTests().getErrors().size());
        assertEquals("wrong class format", review.getUnitTests().getErrors().get(0));
    }

    @Test
    void delete() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);

        given(reviewRepository.findByIdWithFullContent(review.getId())).willReturn(Optional.of(review));

        var res = reviewService.delete(review.getId());

        verify(reviewRepository).delete(review);

        assertTrue(res);
    }

    @Test
    void delete_notFound() {
        given(reviewRepository.findByIdWithFullContent(5)).willReturn(Optional.empty());

        var res = reviewService.delete(5);

        verify(reviewRepository, never()).delete(any(Review.class));

        assertFalse(res);
    }

    @Test
    void getReviews() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var review1 = new Review("review1", "commentary", "code", "test", user);
        review1.setId(1);
        var review2 = new Review("review2", "commentary", "code", "test", user);
        review2.setId(2);

        given(reviewRepository.findReviewPage(PageRequest.of(1, 5)))
                .willReturn(new PageImpl<>(List.of(review1, review2)));

        var reviews = reviewService.getReviews(1, 5);
        assertEquals(2, reviews.size());
    }

    @Test
    void searchReviewPage() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var review1 = new Review("review1", "commentary", "code", "test", user);
        review1.setId(1);
        var review2 = new Review("review2", "commentary", "code", "test", user);
        review2.setId(2);

        given(reviewRepository.searchReview("review1", PageRequest.of(1, 5)))
                .willReturn(new PageImpl<>(List.of(review1)));
        given(reviewRepository.searchReview("review2", PageRequest.of(1, 5)))
                .willReturn(new PageImpl<>(List.of(review2)));
        given(reviewRepository.searchReview("", PageRequest.of(1, 5)))
                .willReturn(new PageImpl<>(List.of(review1, review2)));

        var reviews1 = reviewService.searchReviewPage("review1", 1, 5);
        assertEquals(1, reviews1.size());
        assertEquals(review1, reviews1.get(0));

        var reviews2 = reviewService.searchReviewPage("review2", 1, 5);
        assertEquals(1, reviews2.size());
        assertEquals(review2, reviews2.get(0));

        var reviews = reviewService.searchReviewPage("", 1, 5);
        assertEquals(2, reviews.size());
    }
}