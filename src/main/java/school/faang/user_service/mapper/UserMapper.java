package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD)

public interface UserMapper {
    @Mapping(target = "country.id", source = "countryId")
    @Mapping(target = "contactPreference.preference", source = "preference")
    User toEntity(UserDto userDto);

    @Mapping(target = "countryId", source = "country.id")
    @Mapping(target = "preference", source = "contactPreference.preference")
    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);
}