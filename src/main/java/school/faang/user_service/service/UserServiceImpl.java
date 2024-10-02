package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    private final List<UserFilter> filters;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        return StreamSupport.stream(premiumRepository.findAll().spliterator(), false)
                .map(premium -> userMapper.toDto(premium.getUser()))
                .filter(userDto -> filters.stream()
                        .allMatch(filter -> filter.apply(userDto, userFilterDto)))
                .toList();
    }

    @Override
    public UserDto getUser(long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }
}
