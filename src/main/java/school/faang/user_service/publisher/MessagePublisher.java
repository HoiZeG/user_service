package school.faang.user_service.publisher;

import org.springframework.stereotype.Component;

@Component
public interface MessagePublisher<T> {

    void publish(T message);
}