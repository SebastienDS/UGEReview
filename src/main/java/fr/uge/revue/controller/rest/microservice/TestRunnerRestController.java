package fr.uge.revue.controller.rest.microservice;

import fr.uge.revue.dto.microservice.TestRequestDTO;
import fr.uge.revue.dto.microservice.TestResponseDTO;
import fr.uge.revue.model.TestsReview;
import fr.uge.revue.service.TestRunnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/microservice/api/v1/")
public class TestRunnerRestController {
    private final TestRunnerService  testRunnerService;

    public TestRunnerRestController(TestRunnerService testRunnerService) {
        this.testRunnerService = Objects.requireNonNull(testRunnerService);
    }

    @PostMapping("/launchTests")
    public ResponseEntity<TestResponseDTO> launchTests(@RequestBody TestRequestDTO testRequestDTO) {
        try {
            var result = testRunnerService.launchTests(testRequestDTO.classToTest(), testRequestDTO.testClass());
            return ResponseEntity.ok().body(new TestResponseDTO(result.summary().getTestsSucceededCount(), result.summary().getTestsFoundCount()));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
