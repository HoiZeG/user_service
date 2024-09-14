package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.premium.Currency;
import school.faang.user_service.dto.premium.PaymentRequest;
import school.faang.user_service.dto.premium.PaymentResponse;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionType;
import school.faang.user_service.mapper.PromotionMapper;
import school.faang.user_service.repository.PromotionRepository;
import school.faang.user_service.repository.UserRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {
    private final UserRepository userRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PromotionMapper promotionMapper;
    private final PromotionRepository promotionRepository;


    @Transactional
    public PromotionDto buyPromotion(long userId, PromotionType type, String target) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User was not found"));

        paymentPromotion(type);

        Promotion savePromotion = savePromotionToRepository(user, type, target);
        return promotionMapper.toPromotionDto(savePromotion);
    }

    private void paymentPromotion(PromotionType type) {
        long paymentNumber = promotionRepository.getPromotionPaymentNumber();
        PaymentRequest paymentRequest =
                new PaymentRequest(paymentNumber, new BigDecimal(type.getPrice()), Currency.USD);
        ResponseEntity<PaymentResponse> paymentResponseEntity = paymentServiceClient.sendPayment(paymentRequest);
        PaymentResponse response = paymentResponseEntity.getBody();

        if (response != null) {
            log.info("Payment response received: {}", response.message());
        } else {
            log.warn("No response from payment service.");
        }
    }

    private Promotion savePromotionToRepository(User user, PromotionType type, String target) {
        Promotion promotion = new Promotion();
        promotion.setUser(user);
        promotion.setPriorityLevel(type.getPriorityLevel());
        promotion.setRemainingShows(type.getNumberOfShows());
        promotion.setPromotionTarget(target);
        return promotionRepository.save(promotion);
    }
}
