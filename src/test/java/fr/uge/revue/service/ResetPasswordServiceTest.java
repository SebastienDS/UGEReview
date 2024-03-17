package fr.uge.revue.service;

import fr.uge.revue.model.ResetPasswordToken;
import fr.uge.revue.model.Role;
import fr.uge.revue.model.User;
import fr.uge.revue.repository.ResetPasswordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ResetPasswordServiceTest {
    private ResetPasswordService resetPasswordService;
    @Mock
    private ResetPasswordRepository resetPasswordRepository;
    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        resetPasswordService = new ResetPasswordService(resetPasswordRepository, userService);
    }

    @Test
    void createResetPasswordTokenForUser() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var token = "token";
        resetPasswordService.createResetPasswordTokenForUser(user, token);

        var captor = ArgumentCaptor.forClass(ResetPasswordToken.class);
        verify(resetPasswordRepository).save(captor.capture());
        assertFalse(captor.getValue().isExpired());
    }

    @Test
    void findByToken() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var resetPasswordToken = new ResetPasswordToken("token", user, LocalDateTime.now().plusMinutes(15));
        given(resetPasswordRepository.findByToken("token"))
                .willReturn(Optional.of(resetPasswordToken));

        var tokenFound = resetPasswordService.findByToken("token");
        assertTrue(tokenFound.isPresent());
        assertEquals(resetPasswordToken, tokenFound.get());
    }

    @Test
    void updateUserPassword() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var resetPasswordToken = new ResetPasswordToken("token", user, LocalDateTime.now().plusMinutes(15));
        var newPassword = "newPassword";

        resetPasswordService.updateUserPassword(resetPasswordToken, newPassword);

        verify(userService).updateUserPassword(user, newPassword);
        verify(resetPasswordRepository).delete(resetPasswordToken);
        assertFalse(resetPasswordToken.isExpired());
    }

    @Test
    void updateUserPassword_expiredTokenThrowException() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var resetPasswordToken = new ResetPasswordToken("token", user, LocalDateTime.now().minusMinutes(15));
        var newPassword = "newPassword";

        assertThrows(IllegalArgumentException.class, () -> resetPasswordService.updateUserPassword(resetPasswordToken, newPassword));
    }
}