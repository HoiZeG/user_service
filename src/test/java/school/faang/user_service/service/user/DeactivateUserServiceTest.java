package school.faang.user_service.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.repository.DeactivateUserFacade;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class DeactivateUserServiceTest {

  @Mock
  private DeactivateUserFacade deactivateUserFacade;

  @InjectMocks
  private DeactivateUserService userService;

  private UserDto createUserDto() {
    return UserDto.builder()
        .id(2L)
        .username("JaneSmith")
        .email("janesmith@example.com")
        .phone("0987654321")
        .aboutMe("About Jane Smith")
        .city("London")
        .active(false)
        .premium("")
        .build();
  }

  @Test
  @DisplayName("Проверка деактивации пользователя по его id.")
  void testDeactivateUserForService() {
    when(deactivateUserFacade.deactivateUser(2L)).thenReturn(createUserDto());
    final var userDto = userService.deactivateUser(2L);
    assertThat(userDto.isActive()).isEqualTo(Boolean.FALSE);
  }

}