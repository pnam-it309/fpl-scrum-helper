package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.Report;
import udpm.hn.server.entity.ReportSetting;

@Repository
public interface ReportSettingRepository extends JpaRepository<ReportSetting, String> {
}
