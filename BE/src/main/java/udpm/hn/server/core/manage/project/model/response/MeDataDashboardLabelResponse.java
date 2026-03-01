package udpm.hn.server.core.manage.project.model.response;
import org.springframework.beans.factory.annotation.Value;
public interface MeDataDashboardLabelResponse {
    @Value("#{target.name}")
    String getName();

    @Value("#{target.label}")
    Integer getLabel();
}
