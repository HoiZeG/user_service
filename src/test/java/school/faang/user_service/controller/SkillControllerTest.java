package school.faang.user_service.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.skill.SkillService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {
    @InjectMocks
    private SkillController skillController;
    @Mock
    private SkillService skillService;
    SkillDto skillDto = SkillDto.builder().id(1L).title("flexibility").build();

    @Test
    void testBlankTitleIsInvalid() {
        assertThrows(DataValidationException.class,
                () -> skillController.create(SkillDto.builder().id(1L).title("   ").build()));
    }

    @Test
    void testNullTitleIsInvalid() {
        assertThrows(DataValidationException.class,
                () -> skillController.create(SkillDto.builder().id(1L).title(null).build()));
    }

    @Test
    void testTitleIsValid() {
        assertDoesNotThrow(
                () -> skillController.create(skillDto));
    }

    @Test
    void createNewSkill() {
        when(skillService.create(skillDto)).thenReturn(skillDto);
        SkillDto skillDto1 = skillController.create(skillDto);
        verify(skillService, times(1))
                .create(skillDto);
        assertEquals(skillDto, skillDto1);
    }

    @Test
    void testCallMethodGetUserSkillsFromSkillService(){
        skillController.getUserSkills(1L);
        verify(skillService, times(1))
                .getUserSkills(1L);
    }

    @Test
    void testCallMethodGetOfferedSkillsFromSkillService(){
        skillController.getOfferedSkills(1L);
        verify(skillService, times(1))
                .getOfferedSkills(1L);
    }

    @Test
    void testCallMethodAcquireSkillFromOffersFromSkillService(){
        skillController.acquireSkillFromOffers(1L,1L);
        verify(skillService, times(1))
                .acquireSkillFromOffers(1L,1L);
    }
}