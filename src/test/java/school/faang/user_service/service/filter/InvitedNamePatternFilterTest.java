package school.faang.user_service.service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InvitedNamePatternFilterTest {
    @InjectMocks
    private InvitedNamePatternFilter invitedNamePatternFilter;
    private InvitationFilterDto invitationFilterDto;
    private TestData testData;

    @BeforeEach
    void prepareInvitationFilterDto() {
        invitationFilterDto = new InvitationFilterDto();
        invitationFilterDto.setInvitedId(2L);
        invitationFilterDto.setInviterId(1L);
        invitationFilterDto.setInviterNamePattern("John");
        invitationFilterDto.setInvitedNamePattern("Mike");
        invitationFilterDto.setStatus(RequestStatus.ACCEPTED);
        testData = new TestData();
    }


    @Test
    void testIsApplicableFalse() {
        invitationFilterDto.setInvitedNamePattern(null);
        assertFalse(invitedNamePatternFilter.isApplicable(invitationFilterDto));
    }

    @Test
    void testIsApplicableTrue() {
        assertTrue(invitedNamePatternFilter.isApplicable(invitationFilterDto));
    }

    @Test
    void testApplyWithGoalInvitation() {
        assertEquals(testData.prepareGoalInvitationStream().toList().size(), invitedNamePatternFilter.apply(testData.prepareGoalInvitationStream(),
                invitationFilterDto).toList().size());
    }

    @Test
    void testApplyWithoutGoalInvitation() {
        invitationFilterDto.setInvitedNamePattern("Kelly");
        assertEquals(0, invitedNamePatternFilter.apply(testData.prepareGoalInvitationStream(), invitationFilterDto).toList().size());
    }
}
