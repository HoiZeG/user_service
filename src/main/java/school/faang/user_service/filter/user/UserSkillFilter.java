package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
class UserSkillFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getSkillPattern() != null && !userFilterDto.getSkillPattern().isBlank();
    }

    @Override
    public Stream<User> apply(Stream<User> userStream, UserFilterDto userFilterDto) {
        return userStream.filter(user -> user.getSkills().parallelStream()
                .anyMatch(skill ->
                        skill.getTitle() != null && skill.getTitle().contains(userFilterDto.getSkillPattern())));
    }
}