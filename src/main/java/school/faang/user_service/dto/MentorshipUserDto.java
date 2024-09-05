package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MentorshipUserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
}