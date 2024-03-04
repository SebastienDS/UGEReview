package fr.uge.revue.dto.microservice;

import java.util.List;

public record TestResponseDTO(boolean compilationError, Result result, List<String> errors) {
    public record Result(long succeededCount, long totalCount) {}
}
