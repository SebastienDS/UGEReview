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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        var code = getContent(createReviewDTO.code(), createReviewDTO.codeFile());
        var test = getContent(createReviewDTO.test(), createReviewDTO.testFile());
        var review = new Review(createReviewDTO.title(), createReviewDTO.commentary(), code, test, user);
        review.setRequestNotifications(Set.of(user));
        reviewRepository.save(review);

        launchTests(code, test)
                .subscribe(
                    response -> {
                        var testsReview = new TestsReview();
                        if (response.compilationError()) {
                            testsReview.setErrors(response.errors());
                        } else {
                            testsReview.setSucceededCount(response.result().succeededCount());
                            testsReview.setTotalCount(response.result().totalCount());
                        }
                        review.setUnitTests(testsReview);
                        reviewRepository.save(review);
                    }
                );
        return review;
    }

    private static String getContent(String text, MultipartFile file) {
        if (text.isEmpty()) {
            return getFileContent(file);
        }
        return text;
    }

    private static String getFileContent(MultipartFile file) {
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            return "";
        }
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
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
