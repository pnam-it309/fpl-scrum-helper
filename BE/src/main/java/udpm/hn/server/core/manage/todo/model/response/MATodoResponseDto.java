package udpm.hn.server.core.manage.todo.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.infrastructure.constant.PriorityLevel;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MATodoResponseDto {
    private String name;
    private String id;
    private String code;
    private Long createdDate;
    private String progress;
    private String nameStaff;
    private String nameStudent;
    private String idStaffProject;
    private  String idStaff;
    private String idStudent;
    private String imageStaff;
    private String imageStudent;
    private PriorityLevel priorityLevel;
}
