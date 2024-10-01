package school.faang.user_service.service.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class RequestFilterByStatus implements RequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getStatus() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filterDto) {
        return requests.filter(request -> {
            String status = request.getStatus().toString();
            return Objects.equals(status, filterDto.getStatus());
        });
    }
}