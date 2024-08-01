package school.faang.user_service.exception.user;

import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.exception.ExceptionMessages;

@Slf4j
public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String userId) {
    super(String.format(ExceptionMessages.USER_IS_NULL, userId));
    log.error(String.format(ExceptionMessages.USER_IS_NULL, userId));
  }

}