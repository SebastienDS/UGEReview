package fr.uge.revue.service;

import fr.uge.revue.model.Response;
import fr.uge.revue.repository.ResponseRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class ResponseService {
    private final ResponseRepository responseRepository;

    public ResponseService(ResponseRepository responseRepository) {
        this.responseRepository = Objects.requireNonNull(responseRepository);
    }

    public Optional<Response> getResponse(long responseId) {
        return responseRepository.findByIdWithComment(responseId);
    }
}
