package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.UserGoalsValidationException;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillService skillService;

    @Test
    void testGoalServiceWhenUserHaveActiveGoalsGrandThenMaxValue() {
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(3);

        assertThrows(UserGoalsValidationException.class, () -> goalService.createGoal(1L, new Goal()));
    }

    @Test
    void testGoalServiceWhenSomeSkillsDoesntExistInDB() {
        Goal goal = init(false);

        assertThrows(DataValidationException.class, () -> goalService.createGoal(1L, goal));
    }

    @Test
    void testGoalServiceWhenAllCorrect() {
        Goal goal = init(true);

        goalService.createGoal(1L, goal);

        verify(goalRepository, times(1)).create(goal.getTitle(), goal.getDescription(), goal.getId());
    }

    private Goal init(boolean param) {
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setTitle("New");
        goal.setDescription("descr");
        List<Skill> skills = List.of(new Skill(), new Skill());
        goal.setSkillsToAchieve(skills);

        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(0);
        when(skillService.checkSkillsInDB(goal.getSkillsToAchieve())).thenReturn(param);
        return goal;
    }
}