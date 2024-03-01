package fr.uge.revue.repository;

import fr.uge.revue.model.TestsReview;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestsReviewRepository extends CrudRepository<TestsReview, Long> {
}
