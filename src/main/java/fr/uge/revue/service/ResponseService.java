package fr.uge.revue.service;

import fr.uge.revue.model.Response;
import fr.uge.revue.repository.ResponseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
public class ResponseService {
    private final ResponseRepository responseRepository;
    private final NotificationService notificationService;

    public ResponseService(ResponseRepository responseRepository, NotificationService notificationService) {
        this.responseRepository = Objects.requireNonNull(responseRepository);
        this.notificationService = Objects.requireNonNull(notificationService);
    }

    public Optional<Response> getResponse(long responseId) {
        return responseRepository.findByIdWithComment(responseId);
    }

    @Transactional
    public void saveResponse(Response response) {
        Objects.requireNonNull(response);
        responseRepository.save(response);
        notificationService.notifyNewResponse(response);
    }

    public boolean delete(long id) {
        var response = getResponse(id);
        if(response.isEmpty()){
            return false;
        }
        responseRepository.delete(response.get());
        return true;
    }
}
