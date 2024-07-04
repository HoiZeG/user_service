package school.faang.user_service.service.user.parse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static school.faang.user_service.exception.message.ExceptionMessage.INPUT_IS_EMPTY;

@ExtendWith(MockitoExtension.class)
public class ReadAndDivideTest {

    @InjectMocks
    private ReadAndDivide readAndDivide;

    @Nested
    class PositiveTests {

        @DisplayName("should return data with 4 elements when passed")
        @Test
        void testForToCsvPartDivider() throws FileNotFoundException {
            Util util = new Util();
            assertEquals(4, readAndDivide.toCsvPartDivider(util.getInputStream()).size());
        }
    }

    @Nested
    class NegativeTests {

        @DisplayName("should throw exception when lines.isEmpty()")
        @Test
        void toCsvPartDividerIfInputIsEmptyTest() {
            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> readAndDivide.toCsvPartDivider(new ByteArrayInputStream(new byte[0])));
            assertEquals(INPUT_IS_EMPTY.getMessage(), exception.getMessage());
        }

        @DisplayName("should throw exception when inputStream == null")
        @Test
        void toCsvPartDividerIfInputIsNullTest() {
            assertThrows(RuntimeException.class,
                    () -> readAndDivide.toCsvPartDivider(null));
        }
    }
}
