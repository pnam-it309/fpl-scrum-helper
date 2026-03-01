package udpm.hn.server.infrastructure.log;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogEntryTodoList {

    private String id;

    private String code;

    private String todo;

    private String status;

    private String message;

}
