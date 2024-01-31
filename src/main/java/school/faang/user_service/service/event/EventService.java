package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final UserService userService;

    public EventDto updateEvent(EventDto eventDto) {
        Event event = getEvent(eventDto.getId());
        eventValidator.validateEventToUpdate(eventDto);
        User owner = userService.findUserById(eventDto.getOwnerId());
        event.setOwner(owner);
        event.setStartDate(eventDto.getStartDate());

        return eventMapper.toDto(eventRepository.save(event));
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventMapper.toListDto(eventRepository.findAllByUserId(userId));
    }

    public EventDto create(EventDto eventDto) {
        eventValidator.checkIfOwnerExistsById(eventDto.getOwnerId());
        eventValidator.checkIfOwnerHasSkillsRequired(eventDto);
        Event eventEntity = eventMapper.toEntity(eventDto);
        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    public List<EventDto> getParticipatedEventsByUserId(long userId) {
        List<Event> participatedEventsByUserId = eventRepository.findParticipatedEventsByUserId(userId);
        return eventMapper.toListDto(participatedEventsByUserId);
    }

    public Event getEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Not found event by Id - " + eventId));
    }

    public EventDto getEventDto(long eventId) {
        Event event = getEvent(eventId);
        return eventMapper.toDto(event);
    }

    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

}
