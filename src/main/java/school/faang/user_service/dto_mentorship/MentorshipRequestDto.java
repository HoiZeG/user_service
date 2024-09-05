package school.faang.user_service.dto_mentorship;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
public class MentorshipRequestDto {
    private Long id;
    private String description;
    private String rejectionReason;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
}