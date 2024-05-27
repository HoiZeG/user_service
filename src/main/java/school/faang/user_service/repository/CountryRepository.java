package school.faang.user_service.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.dto.CountryDto;
import school.faang.user_service.entity.Country;

import java.util.List;

@Repository
public interface CountryRepository extends CrudRepository<Country, Long> {
}