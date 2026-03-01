package udpm.hn.server.core.member.projectdetails.model.response;

import lombok.*;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MBMeBoardResponse {

    private String id;

    private String code;

    private String name;

    private Byte indexTodoList;

    private List<MBMeConvertTodoResponse> tasks;

}
