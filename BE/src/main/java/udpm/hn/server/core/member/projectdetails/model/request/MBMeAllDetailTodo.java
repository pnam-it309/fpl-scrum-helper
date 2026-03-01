package udpm.hn.server.core.member.projectdetails.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeImageResponse;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeLabelResponse;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeTodoChildResponse;
import udpm.hn.server.core.member.projectdetails.model.response.MBMemberInfoTodoResponse;
import udpm.hn.server.infrastructure.constant.StatusReminder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MBMeAllDetailTodo {

    private String id;
    private String code;
    private String name;
    private String descriptions;
    private Long deadline;
    private Long reminderTime;
    private StatusReminder statusReminder;
    private Long completionTime;
    private Integer priorityLevel;
    private Float progress;
    private String imageId;
    private String urlImage;
    private Short indexTodo;
    private String todoId;
    private String todoListId;
    private String todoListName;
    private Integer statusTodo;
    private String type;
    private String idType;
    private List<MBMemberInfoTodoResponse> members;
    private List<MBMeLabelResponse> labels;
    private List<MBMeImageResponse> images;
    private List<MBMeTodoChildResponse> listTodoChild;

}
