package school.faang.user_service.filter;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public interface EventFilter {

    boolean isApplicable(EventFilterDto eventFilterDto);

    Stream<Event> apply(Stream<Event> events, EventFilterDto eventFilterDto);
}
