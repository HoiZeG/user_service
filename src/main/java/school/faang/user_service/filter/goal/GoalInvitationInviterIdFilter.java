package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class GoalInvitationInviterIdFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filter) {
        return filter.inviterId() != null;
    }

    @Override
    public void apply(Stream<GoalInvitation> goalInvitations, InvitationFilterDto filter) {
    }
}