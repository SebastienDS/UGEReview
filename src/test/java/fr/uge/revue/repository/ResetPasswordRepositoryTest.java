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

    @Test
    public void findByToken_ReturnsResetPasswordToken() {
        var resetPasswordToken = new ResetPasswordToken("token", LocalDateTime.now().plusMinutes(10));
        resetPasswordRepository.save(resetPasswordToken);

        var foundTokenOptional = resetPasswordRepository.findByToken("token");

        assertTrue(foundTokenOptional.isPresent());
        assertFalse(resetPasswordToken.isExpired());
    }

    @Test
    public void findByToken_ReturnsExpiredResetPasswordToken() {
        var resetPasswordToken = new ResetPasswordToken("token", LocalDateTime.now().minusMinutes(10));
        resetPasswordRepository.save(resetPasswordToken);

        var foundTokenOptional = resetPasswordRepository.findByToken("token");

        assertTrue(foundTokenOptional.isPresent());
        assertTrue(resetPasswordToken.isExpired());
    }

}