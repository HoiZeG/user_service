package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventParticipationService;

@Component
@RequiredArgsConstructor
@Controller
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public void registerParticipant(long eventId, long userId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);

        validateParams(user, event);

        eventParticipationService.registerParticipant(event.getId(), user.getId());
    }

    public void unregisterParticipant(long eventId, long userId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);

        validateParams(user, event);

        eventParticipationService.unregisterParticipant(event.getId(), user.getId());
    }

    private static void validateParams(User user, Event event) {
        if (user == null) {
            throw new NullPointerException("User not found");
        }
        if (event == null) {
            throw new NullPointerException("Event not found");
        }
    }

    private Event getEvent(long eventId) {
        return eventRepository.findById(eventId).orElse(null);
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
