package fr.uge.revue.repository;

import fr.uge.revue.model.ResetPasswordToken;
import fr.uge.revue.model.Role;
import fr.uge.revue.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ResetPasswordRepositoryTest {

    @Autowired
    private ResetPasswordRepository resetPasswordRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByToken_ReturnsResetPasswordTokenWithUser() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var resetPasswordToken = new ResetPasswordToken("token", user, LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        resetPasswordRepository.save(resetPasswordToken);

        var foundTokenOptional = resetPasswordRepository.findByToken("token");

        assertTrue(foundTokenOptional.isPresent());
        var foundToken = foundTokenOptional.get();
        assertEquals(user, foundToken.getUser());
        assertFalse(resetPasswordToken.isExpired());
    }

    @Test
    public void findByToken_ReturnsExpiredResetPasswordToken() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var resetPasswordToken = new ResetPasswordToken("token", user, LocalDateTime.now().minusMinutes(10));
        userRepository.save(user);
        resetPasswordRepository.save(resetPasswordToken);

        var foundTokenOptional = resetPasswordRepository.findByToken("token");

        assertTrue(foundTokenOptional.isPresent());
        var foundToken = foundTokenOptional.get();
        assertEquals(user, foundToken.getUser());
        assertTrue(resetPasswordToken.isExpired());
    }

}