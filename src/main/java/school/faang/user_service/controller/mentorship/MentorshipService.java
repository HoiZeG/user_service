package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;

    public List<UserDTO> getMentees(long userId) {
        User mentor = mentorshipRepository.findById(userId).orElse(null);
        if (mentor == null) {
            return new ArrayList<>();
        }
        return mentor.getMentees().stream()
                .map(UserMapper.INSTANCE::userToUserDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getMentors(long userId) {
        User user = mentorshipRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ArrayList<>();
        }
        return user.getMentors().stream()
                .map(UserMapper.INSTANCE::userToUserDTO)
                .collect(Collectors.toList());
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = mentorshipRepository.findById(mentorId).orElse(null);
        User mentee = mentorshipRepository.findById(menteeId).orElse(null);

        if (mentor != null && mentee != null && mentor.getMentees().contains(mentee)) {
            mentor.getMentees().remove(mentee);
            mentorshipRepository.save(mentor);
        }
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentor = mentorshipRepository.findById(mentorId).orElse(null);
        User mentee = mentorshipRepository.findById(menteeId).orElse(null);

        if (mentor != null && mentee != null && mentee.getMentors().contains(mentor)) {
            mentee.getMentors().remove(mentor);
            mentorshipRepository.save(mentee);
        }
    }
}