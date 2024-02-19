package fr.uge.revue.repository;

import fr.uge.revue.model.Response;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseRepository extends CrudRepository<Response, Long> {
}
