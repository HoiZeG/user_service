package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.util.TestDataFactory;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserPhoneFilterTest {

    private UserPhoneFilter phoneFilter = new UserPhoneFilter();
    private User user;

    @BeforeEach
    public void setUp() {
        user = TestDataFactory.createUser();
    }

    @Test
    public void givenValidUserWhenApplyThenReturnUser() {
        // Given
        var filter = TestDataFactory.createFilterDto();
        filter.setPhonePattern("911");

        // When
        var result = phoneFilter.apply(Stream.of(user), filter);

        // Then
        assertNotNull(result);
        assertEquals(result.findFirst().get(), user);
    }

    @Test
    public void givenValidUserWhenApllyThenReturnEmptyStream() {
        // Given
        var user = TestDataFactory.createUser();
        UserFilterDto filter = new UserFilterDto();
        filter.setPhonePattern("812");

        // When
        var result = phoneFilter.apply(Stream.of(user), filter);

        // Then
        assertNotNull(result);
        assertFalse(result.findAny().isPresent());
    }
}