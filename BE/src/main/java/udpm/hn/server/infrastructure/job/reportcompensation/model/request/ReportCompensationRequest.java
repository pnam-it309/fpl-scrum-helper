package udpm.hn.server.infrastructure.job.reportcompensation.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReportCompensationRequest {

    private int orderNumber;

    private String name;

    private String email;

    private String ngayNay;

    private String ngayMai;

    private String khoKhan;

//    private String maDa;

    private String ngayBaoCao;


}
