package udpm.hn.server.core.manage.report.model.response;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


@Getter
@ToString
public class MAReportDTO {

    private String workPlanTomorrow;

    private String obstacles;

    private String workDoneToday;

    public MAReportDTO(String workPlanTomorrow, String obstacles, String workDoneToday) {
        this.workPlanTomorrow = workPlanTomorrow;
        this.obstacles = obstacles;
        this.workDoneToday = workDoneToday;
    }

    private String formatDate(Long timestamp) {
        if (timestamp == null) return "";
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

}
