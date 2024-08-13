package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.validator.user.UserFilterValidation;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserFilterValidation userFilterValidation;

    private UserMapper userMapper;

    private UserFilter nameUserFilter = Mockito.mock(UserFilter.class);

    @InjectMocks
    private UserService userService;

    private List<UserFilter> filters;

    private User user;
    private UserDto userDto;
    private UserFilterDto userFilterDto;

    @BeforeEach
    void beforeEachInit() {
        user = new User();
        userDto = UserDto.builder().build();
        userFilterDto = UserFilterDto.builder().build();

        filters = List.of(nameUserFilter/*, emailUserFilter*/);
        userRepository = Mockito.mock(UserRepository.class);
        userFilterValidation = Mockito.mock(UserFilterValidation.class);
        userMapper = Mockito.mock(UserMapper.class);

        userService = new UserService(userRepository, filters, userFilterValidation, userMapper);
    }

    @Test
    @DisplayName(value = "Getting premium users throws exception cause userFilter is null")
    public void testGetPremiumUsersWithNullableUserFilter() {
        UserFilterDto userFilterDto = null;

        when(userFilterValidation.isNullable(userFilterDto)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> userService.getPremiumUsers(userFilterDto));
    }

    @Test
    @DisplayName(value = "Getting premium users returns all users cause userFilters aren't any applicable")
    public void testGetPremiumUsersWithEmptyUserFilter() {
        List<UserDto> expected = List.of(userDto);
        when(userFilterValidation.isNullable(userFilterDto)).thenReturn(false);
        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(user));
        when(userFilterValidation.isAnyFilterApplicable(filters, userFilterDto)).thenReturn(false);
        when(userMapper.toDtoList(List.of(user))).thenReturn(List.of(userDto));

        List<UserDto> actual = userService.getPremiumUsers(userFilterDto);

        assertIterableEquals(expected, actual);
    }

    @Test
    @DisplayName(value = "Getting premium users returns filtered users")
    public void testGetPremiumUsersByUserFilter() {
        user = User.builder().id(1L).email("email@email.com").username("user name").build();
        userFilterDto = UserFilterDto.builder().emailPattern("email").namePattern("name").build();
        userDto = UserDto.builder().id(1L).username("user name").build();
        List<UserDto> expected = List.of(userDto);

        when(userFilterValidation.isNullable(userFilterDto)).thenReturn(false);
        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(user));

        when(userFilterValidation.isAnyFilterApplicable(filters, userFilterDto)).thenReturn(true);

        when(filters.get(0).isApplicable(any())).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(Stream.of(user));

        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> actual = userService.getPremiumUsers(userFilterDto);

        assertIterableEquals(expected, actual);
    }

    @Test
    public void testExistsByIdReturnsTrue() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        when(userFilterValidation.isNullable(userFilterDto)).thenReturn(true);
        boolean exists = userService.existsById(userId);

        assertThrows(DataValidationException.class, () -> userService.getRegularUsers(userFilterDto));
        assertTrue(exists);
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    @DisplayName(value = "Getting regular users returns all users cause userFilters aren't any applicable")
    public void testGetRegularUsersWithEmptyUserFilter() {
        List<UserDto> expected = List.of(userDto);
        when(userFilterValidation.isNullable(userFilterDto)).thenReturn(false);
        when(userRepository.findRegularUsers()).thenReturn(Stream.of(user));
        when(userFilterValidation.isAnyFilterApplicable(filters, userFilterDto)).thenReturn(false);
        when(userMapper.toDtoList(List.of(user))).thenReturn(List.of(userDto));

        List<UserDto> actual = userService.getRegularUsers(userFilterDto);

        assertIterableEquals(expected, actual);
    }

    @Test
    @DisplayName(value = "Getting premium users returns filtered users")
    public void testGetRegularUsersByUserFilter() {
        user = User.builder().id(1L).email("email@email.com").username("user name").build();
        userFilterDto = UserFilterDto.builder().emailPattern("email").namePattern("name").build();
        userDto = UserDto.builder().id(1L).username("user name").build();
        List<UserDto> expected = List.of(userDto);

        when(userFilterValidation.isNullable(userFilterDto)).thenReturn(false);
        when(userRepository.findRegularUsers()).thenReturn(Stream.of(user));

        when(userFilterValidation.isAnyFilterApplicable(filters, userFilterDto)).thenReturn(true);

        when(filters.get(0).isApplicable(any())).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(Stream.of(user));

        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> actual = userService.getRegularUsers(userFilterDto);

        assertIterableEquals(expected, actual);
    }

        @Test
        public void testExistsByIdReturnsFalse() {
            Long userId = 2L;
            when(userRepository.existsById(userId)).thenReturn(false);

            boolean exists = userService.existsById(userId);

            assertFalse(exists);
            verify(userRepository, times(1)).existsById(userId);
        }
}