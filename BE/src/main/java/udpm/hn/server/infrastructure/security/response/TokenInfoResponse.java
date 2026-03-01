package udpm.hn.server.infrastructure.security.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class TokenInfoResponse {

    private String userId;

    private String userCode;

    private String userName;

    private String fullName;

    private String pictureUrl;

    private String host;

    private List<String> rolesCode;

    private List<String> rolesName;

    private String emailFe;

    private String emailFPT;

    private String emailSV;

    private String email;

    private String roleSwitch;

    private String roleScreen ;

    private String idFacility;

}
