package udpm.hn.server.core.member.projectdetails.model.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MBMeConvertTodoResponse {

    private String id;

    private String code;

    private String name;

    private String priorityLevel;

    private Long deadline;

    private Long completionTime;

    private Short indexTodo;

    private Short progress;

    private String imageId;

    private String urlImage;

    private Short numberTodoComplete;

    private Short numberTodo;

    private Integer progressOfTodo;

    private String deadlineString;

    private String todoListId;

    private List<MBMeConvertLabelResponse> labels;

    private List<MBMemberInfoTodoResponse> memberTodo;

    private Integer numberCommnets;

    private Integer numberAttachments;

    private String typeTodo ;

//    private MBMeListResponse list;
}
