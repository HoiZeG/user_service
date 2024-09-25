package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendation) {
        return recommendationService.create(recommendation);
    }

    @PutMapping
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto updated) {
        return recommendationService.update(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteRecommendation(@PathVariable long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/{receiver_id}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable("receiver_id") long receiverId){
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping("/{receiver_id}/given_recommendations")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable("receiver_id") long receiverId){
        return recommendationService.getAllGivenRecommendations(receiverId);
    }
}
