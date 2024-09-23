package school.faang.user_service.entity.student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
}
