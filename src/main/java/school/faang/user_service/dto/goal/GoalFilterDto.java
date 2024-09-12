package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalFilterDto {
    private String title;
    private String description;
    private GoalStatus status;
    private List<Long> skillIds;
}