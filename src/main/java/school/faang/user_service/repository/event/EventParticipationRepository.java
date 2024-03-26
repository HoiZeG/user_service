package school.faang.user_service.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Repository
public interface EventParticipationRepository extends JpaRepository<User, Long> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO user_event (event_id, user_id) VALUES (:eventId, :userId)")
    void register(long eventId, long userId);
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM user_event WHERE event_id = :eventId and user_id = :userId")
    void unregister(long eventId, long userId);

    @Query(nativeQuery = true, value = """
            SELECT u.* FROM users u
            JOIN  user_event ue ON u.id = ue.user_id
            WHERE ue.event_id = :eventId
            """)
    List<User> findAllParticipantsByEventId(long eventId);

    @Query(nativeQuery = true, value = """
            SELECT COUNT(ue.id) FROM user_event ue
            WHERE ue.event_id = :eventId
            """)
    int countParticipants(long eventId);

}