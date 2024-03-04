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

import java.util.List;
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
        var result = testRunnerService.launchTests(testRequestDTO.classToTest(), testRequestDTO.testClass());
        TestResponseDTO response;
        if (result.compilationError()) {
            response = new TestResponseDTO(true, null, result.errors());
        } else {
            response = new TestResponseDTO(false, new TestResponseDTO.Result(result.summary().getTestsSucceededCount(), result.summary().getTestsFoundCount()), List.of());
        }
        return ResponseEntity.ok().body(response);
    }
}
