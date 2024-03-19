package fr.uge.revue.repository;

import fr.uge.revue.model.ResetPasswordToken;
import fr.uge.revue.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetPasswordRepository extends CrudRepository<ResetPasswordToken, Long> {
    @Query("SELECT t from ResetPasswordToken t where t.token = :token")
    Optional<ResetPasswordToken> findByToken(@Param("token") String token);

    @Query("SELECT u from User u where u.token = :token")
    Optional<User> findUserOfToken(@Param("token") ResetPasswordToken token);
}
