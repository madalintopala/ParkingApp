package calitei.parking.api.models.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @Getter
    @Setter
    private String emailOrPhoneNumber;
    @Getter
    @Setter
    private String password;
}
