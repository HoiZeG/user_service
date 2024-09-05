package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

import java.util.List;

@Repository
public interface MentorshipRepository extends JpaRepository<User, Long> {
    @Query(value = """
            SELECT m FROM User u
            JOIN u.mentees m
            WHERE u.id = :userId
            """)
    List<User> findMenteesByMentorId(long userId);
}
