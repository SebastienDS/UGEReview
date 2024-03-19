package fr.uge.revue.service;

import fr.uge.revue.model.ResetPasswordToken;
import fr.uge.revue.model.User;
import fr.uge.revue.repository.ResetPasswordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class ResetPasswordService {
    private final ResetPasswordRepository resetPasswordRepository;
    private final UserService userService;

    public ResetPasswordService(ResetPasswordRepository resetPasswordRepository, UserService userService) {
        this.resetPasswordRepository = Objects.requireNonNull(resetPasswordRepository);
        this.userService = Objects.requireNonNull(userService);
    }

    public void createResetPasswordTokenForUser(User user, String token) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(token);
        var resetToken = new ResetPasswordToken(token, user, LocalDateTime.now().plusMinutes(15));
        resetPasswordRepository.save(resetToken);
    }

    public Optional<ResetPasswordToken> findByToken(String token) {
        return resetPasswordRepository.findByToken(Objects.requireNonNull(token));
    }

    @Transactional
    public void updateUserPassword(ResetPasswordToken resetToken, String password) {
        Objects.requireNonNull(resetToken);
        Objects.requireNonNull(password);
        if (resetToken.isExpired()) throw new IllegalArgumentException();
        userService.updateUserPassword(resetPasswordRepository.findUserOfToken(resetToken).orElseThrow(), password);
        resetPasswordRepository.delete(resetToken);
    }
}
