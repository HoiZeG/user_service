package school.faang.user_service.filter.mentorship_request;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class RequesterFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getRequesterId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filters) {
        return requests.filter(mentorshipRequest -> mentorshipRequest.getRequester().getId() == filters.getRequesterId());
    }
}
