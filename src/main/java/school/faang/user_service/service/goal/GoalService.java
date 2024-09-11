package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.GoalFilter;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {
    private static final int MAX_AMOUNT_ACTIVE_GOAL = 3;

    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final List<GoalFilter> goalFilters;

    public Goal createGoal(Long userId, Goal goal) {
        validateTitle(goal);
        validateUserActiveGoalCount(userId);
        validateGoalSkillsExist(goal);

        User user = userRepository.findById(userId)
                .orElseThrow();
        goal.setUsers(List.of(user));

        return goalRepository.save(goal);
    }

    public Goal updateGoal(Goal goal) {
        validateTitle(goal);
        validateGoalSkillsExist(goal);
        validateExistingGoalStatus(goal.getId());

        Goal existingGoal = goalRepository.findById(goal.getId())
                .orElseThrow();

        if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
            existingGoal.setStatus(GoalStatus.COMPLETED);
            List<Skill> existingSkills = existingGoal.getSkillsToAchieve();
            List<User> existingUsers = existingGoal.getUsers();
            existingSkills.forEach(skill -> skill.getUsers().addAll(existingUsers));
        } else {
            existingGoal.getSkillsToAchieve().clear();
            List<Skill> newSkills = goal.getSkillsToAchieve();
            existingGoal.getSkillsToAchieve().addAll(newSkills);
        }

        return goalRepository.save(existingGoal);
    }

    public void deleteGoal(Long goalId) {
        validateGoalId(goalId);
        goalRepository.deleteById(goalId);
    }

    @Transactional
    public List<Goal> findSubtaskByGoalId(Long goalId) {
        validateGoalId(goalId);
        return goalRepository.findByParent(goalId).toList();
    }

    
    public List<Goal> findGoalsByUser(Long userId, GoalFilterDto filterDto) {
        User user = userRepository.findById(userId)
                .orElseThrow();
        List<Goal> userGoals = user.getGoals();

        return goalFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(
                        userGoals,
                        (list, filter) -> filter.applyFilter(list, filterDto),
                        (list, filter) -> list
                );
    }

    private void validateGoalId(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            log.error("Goal with id={} does not exist", goalId);
            throw new IllegalArgumentException("Goal with this id does not exist");
        }
    }

    private void validateTitle(Goal goal) {
        if (Objects.nonNull(goal.getTitle()) || goal.getTitle().isBlank()) {
            log.error("Title cannot be null or empty");
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
    }

    private void validateUserActiveGoalCount(Long userId) {
        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_AMOUNT_ACTIVE_GOAL) {
            log.error("The number of active user goals has been exceeded");
            throw new IllegalStateException("The number of active user goals has been exceeded");
        }
    }

    private void validateGoalSkillsExist(Goal goal) {
        List<Skill> skills = goal.getSkillsToAchieve();
        skills.stream()
                .map(Skill::getId)
                .forEach(skillId -> {
                    if (!skillRepository.existsById(skillId)) {
                        log.error("Skill={} does not exist", skillId);
                        throw new IllegalArgumentException("Skill does not exist");
                    }
                });
    }

    private void validateExistingGoalStatus(Long existingGoalId) {
        Goal exsitingGoal = goalRepository.findById(existingGoalId)
                .orElseThrow();
        if (exsitingGoal.getStatus().equals(GoalStatus.COMPLETED)) {
            log.error("It is impossible to change a completed goal={}", existingGoalId);
            throw new IllegalArgumentException("It is impossible to change a completed goal");
        }
    }


}
