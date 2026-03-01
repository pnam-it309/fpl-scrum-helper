package udpm.hn.server.infrastructure.configemail;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class Email {

    private String [] toEmail;
    private String subject;
    private String body;
    private String titleEmail;
}

