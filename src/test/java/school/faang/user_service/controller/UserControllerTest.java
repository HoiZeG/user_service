package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private long userId;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("testing getUser method")
    void testGetUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(userId).build();
        when(userService.getUser(userId)).thenReturn(userDto);
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());
        verify(userService, times(1)).getUser(userId);
    }

    @Test
    @DisplayName("testing checkUserExistence method")
    void testCheckUserExistence() throws Exception {
        when(userService.checkUserExistence(userId)).thenReturn(true);
        mockMvc.perform(get("/users/exists/{userId}", userId))
                .andExpect(status().isOk());
        verify(userService, times(1)).checkUserExistence(userId);
    }
}