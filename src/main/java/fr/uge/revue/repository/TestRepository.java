package fr.uge.revue.repository;

import fr.uge.revue.model.Test;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TestRepository extends CrudRepository<Test, Long> {
    Optional<Test> findById(long id);
    List<Test> findAll();
}
