package fr.uge.revue.repository;

import fr.uge.revue.model.Notification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {
    @Query("SELECT case when (count(r) > 0) then true else false end FROM Review r JOIN r.requestNotifications n " +
            "WHERE r.id = :reviewId AND n.id = :userId")
    boolean isUserRequestingNotification(@Param("reviewId") long reviewId, @Param("userId") long userId);

    @Query("SELECT n FROM Notification n LEFT JOIN FETCH n.userWhoNotify WHERE n.notifiedUser.id = :userId")
    Set<Notification> findAllUserNotifications(@Param("userId") long userId);

    @Query("SELECT n FROM Notification n LEFT JOIN FETCH n.notifiedUser WHERE n.id = :notificationId")
    Optional<Notification> findByIdWithNotifiedUser(@Param("notificationId") long notificationId);
}

