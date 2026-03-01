package udpm.hn.server.core.member.projectdetails.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class MBMeCreateCommentRequest {

    private String idTodo;

    private String content;

    private String email;

    private String idProject;

    private List<String> mentionedEmails;

    private String idUser;

    private String url ;

}
