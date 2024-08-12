package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.skillOffer.SkillOfferService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferService skillOfferService;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private RecommendationMapper recommendationMapper;
    @InjectMocks
    private RecommendationService recommendationService;
    Recommendation recommendation;
    Recommendation recommendationFindFirst;
    RecommendationDto recommendationDto;
    User author;
    User receiver;
    Skill skill;
    SkillOffer skillOffer;
    SkillOfferDto skillOfferDto;
    long authorId;
    long receiverId;
    Pageable pageable;

    @BeforeEach
    void init() {
        authorId = 1L;
        receiverId = 1L;

        pageable = PageRequest.of(0, 10);

        skill = new Skill();
        skill.setId(1L);

        skillOffer = new SkillOffer();
        skillOffer.setId(1L);
        skillOffer.setSkill(skill);

        skillOfferDto = new SkillOfferDto();
        skillOfferDto.setId(1L);

        author = User.builder()
                .id(1L)
                .build();
        receiver = User.builder()
                .id(2L)
                .build();
        recommendation = Recommendation.builder()
                .id(1L) //
                .author(author)
                .receiver(receiver)
                .skillOffers(List.of(skillOffer))
                .build();

        recommendationFindFirst = Recommendation.builder()
                .author(author)
                .receiver(receiver)
                .skillOffers(List.of(skillOffer))
                .updatedAt(LocalDateTime.now().minus(7, ChronoUnit.MONTHS))
                .build();

        recommendationDto = RecommendationDto.builder()
                .authorId(1L)
                .receiverId(2L)
                .skillOffers(List.of(skillOfferDto))
                .build();
    }

    @Test
    @DisplayName("findFirstOrderException")
    void testCheckNotRecommendBeforeSixMonthsOrderException() {
        when(recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new RuntimeException("exception"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                recommendationService.create(recommendationDto));

        assertEquals("exception", exception.getMessage());
    }

    @Test
    @DisplayName("BeforeSixMonthsException")
    void testCheckNotRecommendBeforeSixMonthsException() {
        findFirstOrderValid();
        recommendationFindFirst.setUpdatedAt(LocalDateTime.now().minus(5, ChronoUnit.MONTHS));

        assertThrows(IllegalStateException.class, () ->
                recommendationService.create(recommendationDto));
    }

//    @Test
//    @DisplayName("checkForSkills")
//    void testCheckForSkillsException() {
//        SkillOfferDto skillOfferDto = new SkillOfferDto();
//        skillOfferDto.setSkillId(1L);
//
//        findFirstOrderValid();
//        recommendationFindFirst.setUpdatedAt(LocalDateTime.now().minus(7, ChronoUnit.MONTHS));
//
//        when(skillOfferRepository.findAllById(anyList()).spliterator())
//                .thenThrow(new NullPointerException("exception"));
//        Exception exception = assertThrows(NullPointerException.class, () ->
//                recommendationService.create(recommendationDto));
//
//        assertEquals("exception", exception.getMessage());
//    }

//    @Test
//    @DisplayName("saveSkillOffersAddAndSaveGuarantee")
//    void testAddAndSaveGuarantee() {
//        when(recommendationMapper.toEntity(any(RecommendationDto.class)))
//                .thenReturn(recommendation);
//        findFirstOrderValid();
//        when(skillOfferRepository.findAllById(anyList())).thenReturn(List.of(skillOffer));
//        when(skillRepository.findAllByUserId(anyLong())).thenReturn(List.of(skill));
//        when(userSkillGuaranteeRepository.existsById(anyLong())).thenReturn(false);
//        when(userSkillGuaranteeRepository.saveAll(anyList())).thenReturn(List.of(new UserSkillGuarantee()));
//
//        recommendation.setUpdatedAt(LocalDateTime.now());
//
//        assertDoesNotThrow(() -> recommendationService.create(recommendationDto));
//
//        verify(skillOfferRepository, times(1)).findAllById(anyList());
//        verify(skillRepository, times(1)).findAllByUserId(anyLong());
//        verify(userSkillGuaranteeRepository, times(1)).existsById(anyLong());
//        verify(userSkillGuaranteeRepository, times(1)).saveAll(anyList());
//        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
//    }

//    @Test
//    @DisplayName("saveSkillOffers")
//    void testSaveSkillOffers() {
//        when(recommendationMapper.toEntity(any(RecommendationDto.class)))
//                .thenReturn(recommendation);
//        findFirstOrderValid();
//        when(skillOfferRepository.findAllById(anyList())).thenReturn(List.of(skillOffer));
//        when(skillRepository.findAllByUserId(Mockito.anyLong()))
//                .thenReturn(List.of(skill));
//        when(userSkillGuaranteeRepository.existsById(Mockito.anyLong()))
//                .thenReturn(true);
//        when(skillOfferRepository.saveAll(anyList())).thenReturn(List.of(new SkillOffer()));
//
//        recommendationService.create(recommendationDto);
//
//        verify(skillOfferRepository, times(1)).findAllById(anyList());
//        verify(skillRepository, times(1)).findAllByUserId(anyLong());
//        verify(userSkillGuaranteeRepository, times(1)).existsById(anyLong());
//        verify(skillOfferRepository, times(1)).saveAll(anyList());
//        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
//    }

    @Test
    @DisplayName("fullMethodCreate")
    void testCreate() {
        when(recommendationMapper.toEntity(any(RecommendationDto.class)))
                .thenReturn(recommendation);
        findFirstOrderValid();
        doNothing().when(skillOfferService).checkForSkills(anyList());
        doNothing().when(skillOfferService).saveSkillOffers(any(Recommendation.class));
//        when(skillOfferRepository.findAllById(anyList())).thenReturn(List.of(skillOffer));
//        when(skillRepository.findAllByUserId(Mockito.anyLong()))
//                .thenReturn(List.of(skill));
//        when(userSkillGuaranteeRepository.existsById(Mockito.anyLong()))
//                .thenReturn(true);
//        when(skillOfferRepository.saveAll(anyList())).thenReturn(List.of(new SkillOffer()));
        when(recommendationRepository.save(any(Recommendation.class)))
                .thenReturn(new Recommendation());
        when(recommendationMapper.toDto(any(Recommendation.class)))
                .thenReturn(recommendationDto);

        recommendationService.create(recommendationDto);

//        verify(skillOfferRepository, times(1)).findAllById(anyList());
//        verify(skillRepository, times(1)).findAllByUserId(anyLong());
//        verify(userSkillGuaranteeRepository, times(1)).existsById(anyLong());
//        verify(skillOfferRepository, times(1)).saveAll(anyList());
//        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
        verify(recommendationRepository, times(1))
                .save(any(Recommendation.class));
        verify(recommendationMapper, times(1))
                .toDto(any(Recommendation.class));
    }

    @Test
    @DisplayName("updateFindFirstOrderException")
    void testUpdateCheckNotRecommendBeforeSixMonthsOrderException() {
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenThrow(new RuntimeException("exception"));
        Exception exception = assertThrows(RuntimeException.class, () -> recommendationService.update(recommendationDto));

        assertEquals("exception", exception.getMessage());

    }

    @Test
    @DisplayName("updateRecommendationMoreThanOneTimeException")
    void testUpdateRecommendationMoreThanOneTimeException() {
        findFirstOrderValid();
        recommendationFindFirst.setUpdatedAt(LocalDateTime.now().minus(5, ChronoUnit.MONTHS));
        assertThrows(IllegalStateException.class, () -> recommendationService.update(recommendationDto));
    }

//    @Test
//    @DisplayName("updateCheckForSkillsException")
//    void testUpdateCheckForSkillsException() {
//        findFirstOrderValid();
//        recommendationFindFirst.setUpdatedAt(LocalDateTime.now().minus(7, ChronoUnit.MONTHS));
//        when(skillOfferRepository.findAllById(anyList()).spliterator())
//                .thenThrow(new NullPointerException("exception"));
//        Exception exception = assertThrows(NullPointerException.class, () ->
//                recommendationService.create(recommendationDto));
//
//        assertEquals("exception", exception.getMessage());
//    }

    @Test
    @DisplayName("updateSkillOfferSave")
    void testUpdateSkillOfferSave() {
        findFirstOrderValid();
//        when(skillOfferRepository.findAllById(anyList())).thenReturn(List.of(skillOffer));
        doNothing().when(skillOfferService).checkForSkills(anyList());
        when(recommendationMapper.toEntity(any(RecommendationDto.class))).thenReturn(recommendation);
//        when(skillRepository.findAllByUserId(anyLong())).thenReturn(List.of(skill));
//        when(userSkillGuaranteeRepository.existsById(anyLong())).thenReturn(true);
//        when(skillOfferRepository.saveAll(anyList())).thenReturn(List.of(new SkillOffer()));
        doNothing().when(skillOfferService).saveSkillOffers(any(Recommendation.class));
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);
        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationDto);

        recommendationService.update(recommendationDto);

        verify(skillOfferRepository, times(1)).deleteAllByRecommendationId(anyLong());
//        verify(skillOfferRepository, times(1)).findAllById(anyList());
//        verify(skillRepository, times(1)).findAllByUserId(anyLong());
//        verify(userSkillGuaranteeRepository, times(1)).existsById(anyLong());
//        verify(skillOfferRepository, times(1)).saveAll(anyList());
        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
        verify(recommendationMapper, times(1)).toDto(any(Recommendation.class));
    }

    @Test
    @DisplayName("delete")
    void testDelete() {
        long recommendationId = 1L;
        Mockito.doNothing().when(recommendationRepository).deleteById(anyLong());
        recommendationService.delete(recommendationId);
    }

    @Test
    @DisplayName("getAllUserRecommendationsException")
    void testGetAllUserRecommendationsException() {
        when(recommendationRepository.findAllByReceiverId(anyLong(), any(Pageable.class)))
                .thenThrow(new RuntimeException("exception"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                recommendationService.getAllUserRecommendations(receiverId, pageable));

        assertEquals("exception", exception.getMessage());
    }

    @Test
    @DisplayName("getAllUserRecommendationsValid")
    void testGetAllUserRecommendationsValid() {
        List<Recommendation> recommendations = List.of(recommendation);
        Page<Recommendation> recommendationPage = new PageImpl<>(recommendations);
        List<RecommendationDto> expectedDtos = List.of(recommendationDto);

        when(recommendationRepository.findAllByReceiverId(receiverId, pageable)).thenReturn(recommendationPage);
        when(recommendationMapper.toDto(recommendation)).thenReturn(recommendationDto);

        List<RecommendationDto> actualDtos = recommendationService.getAllUserRecommendations(receiverId, pageable);

        assertEquals(expectedDtos, actualDtos);
        verify(recommendationRepository, times(1)).findAllByReceiverId(receiverId, pageable);
        verify(recommendationMapper, times(1)).toDto(recommendation);
    }

    @Test
    @DisplayName("getAllGivenRecommendationsException")
    void testGetAllGivenRecommendationsException() {
        when(recommendationRepository.findAllByAuthorId(anyLong(), any(Pageable.class)))
                .thenThrow(new RuntimeException("exception"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                recommendationService.getAllGivenRecommendations(authorId, pageable));

        assertEquals("exception", exception.getMessage());
    }

    @Test
    @DisplayName("getAllGivenRecommendationsValid")
    void testGetAllGivenRecommendationsValid() {
        List<Recommendation> recommendations = List.of(recommendation);
        Page<Recommendation> recommendationPage = new PageImpl<>(recommendations);
        List<RecommendationDto> expectedDtos = List.of(recommendationDto);

        when(recommendationRepository.findAllByAuthorId(authorId, pageable)).thenReturn(recommendationPage);
        when(recommendationMapper.toDto(recommendation)).thenReturn(recommendationDto);

        List<RecommendationDto> actualDtos = recommendationService.getAllGivenRecommendations(authorId, pageable);

        assertEquals(expectedDtos, actualDtos);
        verify(recommendationRepository, times(1)).findAllByAuthorId(authorId, pageable);
        verify(recommendationMapper, times(1)).toDto(recommendation);
    }

    private void findFirstOrderValid() {
        when(recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(recommendationFindFirst));
    }
}