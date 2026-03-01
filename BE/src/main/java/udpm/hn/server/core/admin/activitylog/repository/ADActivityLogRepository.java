package udpm.hn.server.core.admin.activitylog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import udpm.hn.server.core.admin.activitylog.modal.response.ADActivityLogResponse;
import udpm.hn.server.repository.ActivityLogRepository;

public interface ADActivityLogRepository extends ActivityLogRepository {

    @Query(
            value = """
            SELECT a.id AS id, 
                   a.createdDate AS createDate, 
                   a.executorEmail AS executorEmail, 
                   a.content AS content, 
                   a.role AS role 
            FROM ActivityLog a
                    order by a.createdDate desc
        """
    )
    Page<ADActivityLogResponse> getAllActivityLogs(Pageable pageable);

}
