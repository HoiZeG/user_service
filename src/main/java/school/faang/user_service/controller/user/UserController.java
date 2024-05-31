package school.faang.user_service.controller.user;

import com.json.student.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.converter.starter.ConverterCsvToPerson;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.UserFilterDtoValidator;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${servlet.PathV1}/users")
public class UserController {
    private final UserService userService;
    private final UserFilterDtoValidator userFilterDtoValidator;
    private final ConverterCsvToPerson converterCsvToPerson;

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        userFilterDtoValidator.checkIsNull(userFilterDto);
        log.info("Received userFilterDto: {}", userFilterDto);
        return userService.getPremiumUsers(userFilterDto);
    }

    @PostMapping("/add/file")
    public void convertScvFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            log.error("Received empty csv file");
            throw new DataValidationException("The file can't be empty");
        }

        List<Person> persons = converterCsvToPerson.convertCsvToPerson(file);
        log.info("Received Persons: {}", persons);
        userService.convertCsvFile(persons);
    }
}