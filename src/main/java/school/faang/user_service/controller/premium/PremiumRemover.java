package school.faang.user_service.controller.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import school.faang.user_service.service.premium.PremiumService;

import java.time.LocalDateTime;

@Controller
@Slf4j
public class PremiumRemover {
    private final PremiumService service;
    private final int batchForDeleted;

    @Autowired
    public PremiumRemover(
            PremiumService service,
            @Value("${premium-remover.batch-delete}") int batchForDeleted
    ) {
        this.service = service;
        this.batchForDeleted = batchForDeleted;
    }

    @Scheduled(cron = "${premium-remover.cron}")
    public void removePremiums() {
        log.info(LocalDateTime.now() + " Запуск удаления просроченных премиумов");
        service.removePremiums(batchForDeleted);
        log.info(LocalDateTime.now() + " Завершение удаления просроченных премиумов");
    }
}