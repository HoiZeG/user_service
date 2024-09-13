package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

@Mapper
public interface UserMapper {
    UserDto toDto(User user);
    User userDtoToUser(UserDto userDto);
}
