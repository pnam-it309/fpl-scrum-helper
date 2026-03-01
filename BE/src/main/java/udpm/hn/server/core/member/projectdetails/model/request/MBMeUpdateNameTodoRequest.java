package udpm.hn.server.core.member.projectdetails.model.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class MBMeUpdateNameTodoRequest {

    @Length(max = 250, message = "Mô tả tối đa 300 ký tự")
    private String nameTodo;

    private String idTodo;

}
