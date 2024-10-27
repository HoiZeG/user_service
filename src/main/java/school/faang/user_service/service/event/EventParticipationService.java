package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    @Transactional
    public void registerParticipant(long eventId, long userId) {
        validateIsRegistered(eventId, userId);
        eventParticipationRepository.register(eventId, userId);
    }

    @Transactional
    public void unregisterParticipant(long eventId, long userId) {
        validateUnregistered(eventId, userId);
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipant(long eventId) {
        List<User> allUsers = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        return allUsers.stream().map(userMapper::toDto).toList();
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }

    private boolean isUserRegistered(long eventId, long userId) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        return participants.stream().anyMatch(user -> user.getId() == userId);
    }

    private void validateIsRegistered(long eventId, long userId) {
        if (isUserRegistered(eventId, userId)) {
            throw new IllegalArgumentException("User already registered for event with ID: " + eventId);
        }
    }

    private void validateUnregistered(long eventId, long userId) {
        if (!isUserRegistered(eventId, userId)) {
            throw new IllegalArgumentException("User not registered for event with ID: " + eventId);
        }
    }
}
