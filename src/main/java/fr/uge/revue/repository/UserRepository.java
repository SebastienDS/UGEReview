package fr.uge.revue.repository;

import fr.uge.revue.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
