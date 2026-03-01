package udpm.hn.server.infrastructure.log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogEntry {

    private String id;

    private String student;

    private String status;

    private String message;

}
