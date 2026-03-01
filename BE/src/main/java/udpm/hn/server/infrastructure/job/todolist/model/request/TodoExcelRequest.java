package udpm.hn.server.infrastructure.job.todolist.model.request;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TodoExcelRequest {

    private int orderNumber;

    private String name;

    private String code;

    private String describe;

//    private Byte indexTodoList;



    private String todoList;

}
