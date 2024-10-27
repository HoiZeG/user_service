package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@Component
@RequiredArgsConstructor
@RestController
@RequestMapping("/events")
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @PostMapping("register/{eventId}")
    public void registerParticipant(@PathVariable long eventId, @RequestParam long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @DeleteMapping("unregister/{eventId}")
    public void unregisterParticipant(@PathVariable long eventId, @RequestParam long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("countParticipants/{eventId}")
    public int getParticipantsCount(@PathVariable long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }

    @GetMapping("participants/{eventId}")
    public List<UserDto> getParticipant(@PathVariable long eventId) {
        return eventParticipationService.getParticipant(eventId);
    }
}
