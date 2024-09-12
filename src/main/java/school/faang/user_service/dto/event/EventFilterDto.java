package school.faang.user_service.dto.event;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Builder
public record EventFilterDto(
        String titleFilter,
        Long ownerIdFilter,
        LocalDateTime startDateFilter
) {
}
