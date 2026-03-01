package udpm.hn.server.core.member.projectdetails.model.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class MBMeUpdateDescriptionsTodoRequest extends MBMeTodoAndTodoListRequest {

    private String idTodoUpdate;

    @Length(max = 5000, message = "Mô tả tối đa 5000 ký tự")
    private String descriptions;

}
