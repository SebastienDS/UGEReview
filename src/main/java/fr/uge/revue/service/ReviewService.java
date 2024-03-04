package fr.uge.revue.service;

import fr.uge.revue.dto.microservice.TestRequestDTO;
import fr.uge.revue.dto.microservice.TestResponseDTO;
import fr.uge.revue.dto.review.CreateReviewDTO;
import fr.uge.revue.model.Review;
import fr.uge.revue.model.TestsReview;
import fr.uge.revue.model.User;
import fr.uge.revue.repository.ReviewRepository;
import fr.uge.revue.repository.TestsReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final TestsReviewRepository testsReviewRepository;
    private final WebClient webClient;

    public ReviewService(ReviewRepository reviewRepository, TestsReviewRepository testsReviewRepository, WebClient webClient) {
        this.reviewRepository = Objects.requireNonNull(reviewRepository);
        this.testsReviewRepository = Objects.requireNonNull(testsReviewRepository);
        this.webClient = Objects.requireNonNull(webClient);
    }

    public List<Review> allReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> searchReview(String search) {
        return reviewRepository.searchReview(Objects.requireNonNull(search));
    }

    public Optional<Review> getReview(long reviewID) { return reviewRepository.findByIdWithFullContent(reviewID); }

    @Transactional
    public Review createReview(CreateReviewDTO createReviewDTO, User user) {
        Objects.requireNonNull(createReviewDTO);
        Objects.requireNonNull(user);
        var review = new Review(createReviewDTO.title(), createReviewDTO.commentary(), createReviewDTO.code(), createReviewDTO.test(), user);
        review.setRequestNotifications(Set.of(user));
        reviewRepository.save(review);

        launchTests(createReviewDTO.code(), createReviewDTO.test())
                .subscribe(
                    response -> {
                        var testsReview = new TestsReview(review);
                        if (response.compilationError()) {
                            testsReview.setErrors(response.errors());
                        } else {
                            testsReview.setSucceededCount(response.result().succeededCount());
                            testsReview.setTotalCount(response.result().totalCount());
                        }
                        testsReviewRepository.save(testsReview);
                    }
                );
        return review;
    }

    private Mono<TestResponseDTO> launchTests(String code, String test) {
        return webClient.post()
                .uri("/microservice/api/v1/launchTests")
                .bodyValue(new TestRequestDTO(code, test))
                .retrieve()
                .bodyToMono(TestResponseDTO.class);
    }

    @Transactional
    public boolean delete(long id) {
        var review = getReview(id);
        if(review.isEmpty()){
            return false;
        }
        reviewRepository.delete(review.get());
        return true;
    }
}
