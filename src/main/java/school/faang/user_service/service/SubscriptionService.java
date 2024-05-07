package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.user.filter.UserFilter;

import java.util.List;
import java.util.stream.Stream;

import static school.faang.user_service.exception.ExceptionMessage.REPEATED_SUBSCRIPTION_EXCEPTION;

@Service
@AllArgsConstructor
@Slf4j
@Setter
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepo;
    private List<UserFilter> filters;
    private final UserMapper userMapper;

    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepo.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(REPEATED_SUBSCRIPTION_EXCEPTION.getMessage());
        }

        subscriptionRepo.followUser(followerId, followeeId);
        log.info("User + (id=" + followerId + ") subscribed to user (id=" + followeeId + ").");
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionRepo.unfollowUser(followerId, followeeId);
        log.info("User + (id=" + followerId + ") canceled subscription to user (id=" + followeeId + ").");
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return filterUsers(subscriptionRepo.findByFolloweeId(followeeId), filter);
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepo.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        return filterUsers(subscriptionRepo.findByFollowerId(followerId), filter);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepo.findFolloweesAmountByFollowerId(followerId);
    }

    private List<UserDto> filterUsers(Stream<User> users, UserFilterDto filterDto) {
        return filters.stream()
                .filter(userFilter -> userFilter.isApplicable(filterDto))
                .flatMap(userFilter -> userFilter.apply(users, filterDto))
                .distinct()
                .map(userMapper::toDto)
                .toList();
    }
}

