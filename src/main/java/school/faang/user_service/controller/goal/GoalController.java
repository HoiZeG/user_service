package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exeptions.DataValidationException;
import school.faang.user_service.service.goal.GoalService;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.exception.DataValidationException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoalController {
    private final GoalService service;

    public void deleteGoal(Long goalId) {
        if (goalId < 1) {
            throw new DataValidationException("If cannot be less than 1");
        }
        service.deleteGoal(goalId);
    }

    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto filter) {
        validate(userId);
        return service.getGoalsByUser(userId, filter);
    }

    public List<GoalDto> getSubGoalsByUser(long userId, GoalFilterDto filter) {
        validate(userId);
        return service.getSubGoalsByUser(userId, filter);
    }

    private void validate (long userId) {
        if (userId < 1) {
            throw new DataValidationException("userId can not be less than 1");
        }
    private final GoalValidator validator;

    @PutMapping("/create/goal")
    public GoalDto createGoal(GoalDto goalDto) {
        validator.createGoalControllerValidation(goalDto);
        return service.createGoal(goalDto);
    }
}
