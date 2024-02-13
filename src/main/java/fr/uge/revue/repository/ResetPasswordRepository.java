package fr.uge.revue.repository;

import fr.uge.revue.model.ResetPasswordToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetPasswordRepository extends CrudRepository<ResetPasswordToken, Long> {
    @Query("SELECT t from ResetPasswordToken t LEFT JOIN FETCH t.user where t.token = :token")
    Optional<ResetPasswordToken> findByToken(String token);
}
