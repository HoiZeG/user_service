package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillValidator skillValidator;

    @Transactional
    public SkillDto create(SkillDto skillDto) {
        skillValidator.validateSkillByTitle(skillDto);
        Skill skillEntity = skillMapper.toEntity(skillDto);
        return skillMapper.toDto(skillRepository.save(skillEntity));
    }

    public List<SkillDto> getUserSkills(Long userId) {
        List<Skill> allSkillsByUserId = skillRepository.findAllByUserId(userId);
        return skillMapper.toDto(allSkillsByUserId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skillsOfferedToUser = skillRepository.findSkillsOfferedToUser(userId);
        return skillsOfferedToUser.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> skillCandidateMapper.toDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        skillValidator.validateOfferedSkill(skillId, userId);
        List<SkillOffer> allSkillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        skillValidator.validateSkillByMinSkillOffers(allSkillOffers.size(), skillId, userId);
        skillRepository.assignSkillToUser(skillId, userId);
        addUserSkillGuarantee(allSkillOffers);
        return skillMapper.toDto(allSkillOffers.get(0).getSkill());
    }

    private void addUserSkillGuarantee(List<SkillOffer> skillOffers) {
        List<UserSkillGuarantee> guarantees = skillOffers.stream()
                .map(offer -> UserSkillGuarantee.builder()
                        .user(offer.getRecommendation().getReceiver())
                        .skill(offer.getSkill())
                        .guarantor(offer.getRecommendation().getAuthor())
                        .build())
                .distinct()
                .toList();
        userSkillGuaranteeRepository.saveAll(guarantees);
    }

    public List<Skill> getSkillsByTitle(List<String> skillsTitle) {
        serviceValidator.validateSkill(skillsTitle, skillRepository);
        return skillRepository.findByTitleIn(skillsTitle);
    }

    public void assignSkillToUser(long skillId, long userId) {
        skillRepository.assignSkillToUser(skillId, userId);
    }

    public void deleteSkillFromGoal(long goalId) {
        skillRepository.deleteAll(skillRepository.findSkillsByGoalId(goalId));
    }
}