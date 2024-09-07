package school.faang.user_service.service.event.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.event.Event;

@Component
public class EventTitleFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getTitlePattern() != null &&
                !filter.getTitlePattern().isBlank();
    }

    @Override
    public boolean applyFilter(Event event, EventFilterDto filter) {
        return event.getTitle().toLowerCase()
                .contains(filter.getTitlePattern().toLowerCase());

    }
}
