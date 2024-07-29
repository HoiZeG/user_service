package school.faang.user_service.service.recommendation.request.validator.recommendation.requestvalidator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Period;

@Configuration
public class ValidatorConfiguration {
    @Bean
    Period cooldownOfRequestRecommendation(){
        return Period.ofMonths(6);
    }
}
