package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataGettingException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filter.EventFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.exception.ExceptionMessage.INAPPROPRIATE_OWNER_SKILLS_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.NO_SUCH_EVENT_EXCEPTION;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillMapper skillMapper;
    @Mock
    private EventMapper eventMapper;

    @Spy
    @InjectMocks
    private EventService eventService;

    private EventDto eventDto;
    private Event event;
    private List<EventFilter> filters;

    @BeforeEach
    void setUp() {
        eventDto = new EventDto();
        eventDto.setTitle("Title");
        eventDto.setStartDate(LocalDateTime.of(2024, 6, 12, 12, 12));
        eventDto.setOwnerId(1L);
        eventDto.setDescription("Description");

        var skillADto = new SkillDto();
        skillADto.setTitle("SQL");
        var skillBDto = new SkillDto();
        skillBDto.setTitle("Java");
        eventDto.setRelatedSkills(List.of(skillADto, skillBDto));
        eventDto.setLocation("Location");
        eventDto.setMaxAttendees(10);

        event = new Event();
        event.setTitle("Title");
        event.setStartDate(LocalDateTime.of(2024, 6, 12, 12, 12));
        var owner = new User();
        owner.setId(1L);
        event.setOwner(owner);
        event.setDescription("Description");

        var skillA = new Skill();
        skillA.setTitle("SQL");
        var skillB = new Skill();
        skillB.setTitle("Java");
        event.setRelatedSkills(List.of(skillA, skillB));
        event.setLocation("Location");
        event.setMaxAttendees(10);


        EventFilter filterA = mock(EventFilter.class);
        EventFilter filterB = mock(EventFilter.class);
        EventFilter filterC = mock(EventFilter.class);

        filters = List.of(filterA, filterB, filterC);

        eventService.setFilters(filters);
    }

    @Nested
    class PositiveTests {
        @DisplayName("should create event when owner has required skills")
        @Test
        void shouldCreateEventWhenOwnerHasRequiredSkills() {
            when(skillRepository.findAllByUserId(eventDto.getOwnerId())).thenReturn(List.of());
            when(skillMapper.toDto(List.of())).thenReturn(eventDto.getRelatedSkills());

            when(eventMapper.toEntity(eventDto)).thenReturn(event);


            assertDoesNotThrow(() -> eventService.create(eventDto));

            verify(eventRepository).save(event);
        }

        @DisplayName("should get event when such event exists")
        @Test
        void shouldGetEventWhenItsExists() {
            when(eventRepository.findById(anyLong())).thenReturn(Optional.of(new Event()));

            assertDoesNotThrow(() -> eventService.getEvent(anyLong()));

            verify(eventRepository).findById(anyLong());
            verify(eventMapper).toDto(new Event());
        }

        @DisplayName("should return filtered events when filter isn't empty")
        @Test
        void shouldReturnFilteredEventsWhenFilterIsntEmpty() {
            EventFilterDto eventFilterDto = new EventFilterDto();
            when(eventRepository.findAll()).thenReturn(List.of(event));
            when(eventMapper.toDto(any(Event.class))).thenReturn(eventDto);

            filters.forEach(filter -> {
                when(filter.isApplicable(eventFilterDto)).thenReturn(true);
                when(filter.apply(any(List.class), eq(eventFilterDto))).thenReturn(Stream.of(event));
            });


            var filteredEvents = eventService.getEventsByFilter(eventFilterDto);


            verify(eventRepository).findAll();

            filters.forEach(filter -> {
                verify(filter).isApplicable(eventFilterDto);
                verify(filter).apply(any(List.class), eq(eventFilterDto));
            });
            verify(eventMapper).toDto(any(Event.class));
            assertEquals(List.of(eventDto), filteredEvents);
        }

        @DisplayName("should delete event when such event exists")
        @Test
        void shouldDeleteEventWhenItExists() {
            when(eventRepository.findById(anyLong())).thenReturn(Optional.of(new Event()));

            assertDoesNotThrow(() -> eventService.deleteEvent(anyLong()));

            verify(eventRepository).findById(anyLong());
            verify(eventRepository).delete(any(Event.class));
        }

        @DisplayName("should update event when dto is valid")
        @Test
        void shouldUpdateEventWhenDtoIsValid() {
            doReturn(null).when(eventService).create(eventDto);

            eventService.updateEvent(eventDto);

            verify(eventService).create(eventDto);
        }

        @DisplayName("should return owned by user events")
        @Test
        void shouldReturnOwnedEvents() {
            eventService.getOwnedEvents(anyLong());

            verify(eventRepository).findAllByUserId(anyLong());
            verify(eventMapper).toDtos(any(List.class));
        }

        @DisplayName("should return participated by user events")
        @Test
        void shouldReturnParticipatedEvents() {
            eventService.getParticipatedEvents(anyLong());

            verify(eventRepository).findParticipatedEventsByUserId(anyLong());
            verify(eventMapper).toDtos(any(List.class));
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should throw exception when owner doesn't have required skills")
        @Test
        void shouldThrowExceptionWhenOwnersSkillsDoesntMatchRequired() {
            when(skillRepository.findAllByUserId(eventDto.getOwnerId())).thenReturn(List.of());
            when(skillMapper.toDto(List.of())).thenReturn(List.of());

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventService.create(eventDto));

            verify(eventRepository, times(0)).save(event);
            assertEquals(INAPPROPRIATE_OWNER_SKILLS_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("should throw exception when such event doesn't exist")
        @Test
        void shouldThrowExceptionWhenSuchEventDoesntExist() {
            when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

            DataGettingException exception = assertThrows(DataGettingException.class, () -> eventService.getEvent(anyLong()));

            verify(eventRepository).findById(anyLong());
            verify(eventMapper, times(0)).toDto(any());
            assertEquals(NO_SUCH_EVENT_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("should return empty event list when filter is empty")
        @Test
        void shouldReturnEmptyListWhenFilterIsEmpty() {
            EventFilterDto eventFilterDto = new EventFilterDto();
            when(eventRepository.findAll()).thenReturn(List.of(event));


            var filteredEvents = eventService.getEventsByFilter(eventFilterDto);


            verify(eventRepository).findAll();

            filters.forEach(filter -> {
                verify(filter).isApplicable(eventFilterDto);
                verify(filter, times(0)).apply(any(List.class), eq(eventFilterDto));
            });
            verify(eventMapper, times(0)).toDto(any(Event.class));
            assertEquals(List.of(), filteredEvents);
        }

        @DisplayName("should throw exception when such event doesn't exists")
        @Test
        void shouldThrowExceptionWhenEventToBeDeletedDoesntExist() {
            when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

            DataGettingException exception = assertThrows(DataGettingException.class, () -> eventService.deleteEvent(anyLong()));

            verify(eventRepository).findById(anyLong());
            verify(eventRepository, times(0)).delete(any());
            assertEquals(NO_SUCH_EVENT_EXCEPTION.getMessage(), exception.getMessage());
        }
    }
}