package udpm.hn.server.infrastructure.job.student.model.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StudentExcelRequest {

    private int orderNumber;

    private String name;

    private String code;

    private String email;

    private String major;

    private String department;

    private String facility;


}
