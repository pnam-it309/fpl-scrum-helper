package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.manage.report.model.response.MaHolidayResponse;
import udpm.hn.server.core.member.report.model.response.MBReportResponse;
import udpm.hn.server.entity.Holiday;
import udpm.hn.server.entity.Report;

import java.util.List;
import java.util.Optional;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, String> {
    boolean existsByDateAndProject_Id(Long date, String projectId);

    boolean existsByCode(String code);

    @Query("""
    SELECT new udpm.hn.server.core.member.report.model.response.MBReportResponse(
        h.date,
        h.statusReport,
        h.project.id,
        'HOLIDAY'
    )
    FROM Holiday h
    WHERE h.project.id = :projectId and h.status = 0
""")
    List<MBReportResponse> findHolidayReports(@Param("projectId") String projectId);

    @Query("""
    SELECT COUNT(h)
    FROM Holiday h
    WHERE h.project.id = :projectId
      AND h.date BETWEEN :start AND :end
""")
    Long countByProjectIdAndDateBetween(@Param("projectId") String projectId,
                                        @Param("start") Long start,
                                        @Param("end") Long end);

    List<Holiday> findAllByProjectId(String projectId);

    @Query("""
    SELECT 
    h.date as date
    FROM Holiday h
    WHERE h.project.id = :projectId AND h.status = 0
""")
    List<MaHolidayResponse> getAllHoliday(@Param("projectId") String projectId);

    @Query("SELECT h FROM Holiday h WHERE h.date BETWEEN :startOfDay AND :endOfDay AND h.project.id = :projectId")
    List<Holiday> findByDateInDayAndProjectId(@Param("startOfDay") Long startOfDay,
                                                  @Param("endOfDay") Long endOfDay,
                                                  @Param("projectId") String projectId);


}


