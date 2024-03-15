package fr.uge.revue.service;

import fr.uge.revue.dto.microservice.TestRequestDTO;
import fr.uge.revue.dto.microservice.TestResponseDTO;
import fr.uge.revue.dto.review.CreateReviewDTO;
import fr.uge.revue.model.Review;
import fr.uge.revue.model.TestsReview;
import fr.uge.revue.model.User;
import fr.uge.revue.repository.ReviewRepository;
import fr.uge.revue.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final WebClient webClient;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, WebClient webClient) {
        this.reviewRepository = Objects.requireNonNull(reviewRepository);
        this.userRepository = Objects.requireNonNull(userRepository);
        this.webClient = Objects.requireNonNull(webClient);
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

    public List<Review> getReviews(int page, int pageSize) {
        page = Math.max(page, 0);
        pageSize = Math.max(pageSize, 1);
        return reviewRepository.findReviewPage(PageRequest.of(page, pageSize)).stream().toList();
    }

    public List<Review> getFriendsReview(User user, int pageNumber, int pageSize) {
        pageNumber = Math.max(pageNumber, 0);
        pageSize = Math.max(pageSize, 1);
        Objects.requireNonNull(user);
        var listReview = new ArrayList<Review>();
        var follows = userRepository.findByIdWithFollowers(user.getId()).get().getFollowers().stream().toList();
        var idSet = new HashSet<Long>();
        var numberToSkip = (long) pageNumber * pageSize;
        while(listReview.size() < pageSize && !follows.isEmpty() ){
            var friends = follows.stream().map(User::getId).toList();
            var reviews = reviewRepository.findUsersPageReviewsOrderDesc(friends,
                    PageRequest.of(0, pageSize - listReview.size()));
            listReview.addAll(reviews.stream().skip(numberToSkip).limit(pageSize).toList());
            numberToSkip = Math.max(0, numberToSkip - reviews.getTotalElements());

            idSet.addAll(friends);
            follows = follows.stream()
                    .map(user1 -> userRepository.findByIdWithFollowers(user1.getId()).get().getFollowers())
                    .flatMap(Collection::stream)
                    .filter(follow -> !idSet.contains(follow.getId()))
                    .toList();
        }
        if(listReview.size() < pageSize){
            var reviews = reviewRepository.findReviewPageWithoutUserIds(idSet.stream().toList(),
                    PageRequest.of(0, pageNumber * pageSize + (pageSize - listReview.size())));
            listReview.addAll(reviews.stream().skip(numberToSkip).limit(pageSize).toList());
        }
        return listReview;
    }

    public List<Review> searchReviewPage(String search, int pageNumber, int pageSize) {
        pageNumber = Math.max(pageNumber, 0);
        pageSize = Math.max(pageSize, 1);
        return reviewRepository.searchReview(Objects.requireNonNull(search), PageRequest.of(pageNumber, pageSize)).stream().toList();
    }
}
