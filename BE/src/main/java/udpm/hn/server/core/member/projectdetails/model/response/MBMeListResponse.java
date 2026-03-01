package udpm.hn.server.core.member.projectdetails.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MBMeListResponse {
    private String id;

    private String code;

    private String name;

    private Byte indexTodoList;
}
