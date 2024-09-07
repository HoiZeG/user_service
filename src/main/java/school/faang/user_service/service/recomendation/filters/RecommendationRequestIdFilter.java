package school.faang.user_service.service.recomendation.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recomendation.FilterRecommendationRequestsDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestIdFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(FilterRecommendationRequestsDto filter) {
        return filter.getId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(
        Stream<RecommendationRequest> requestStream,
        FilterRecommendationRequestsDto filter
    ) {
        return requestStream.filter(
                recommendationRequest -> recommendationRequest.getId() == filter.getId()
        );
    }
}