package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MBMeCreateNotificationCommentRequest {

    private List<String> mentionedIds;

    private List<String> mentionedEmails;

    private String idProject;

    private String idTodo;

    private String url;

    private String idUser;

    private String email;

    private String username;

}
