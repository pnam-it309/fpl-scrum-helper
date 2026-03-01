package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.entity.VelocityRecord;

import java.util.List;

public interface VelocityRecordRepository extends JpaRepository<VelocityRecord, String> {

    @Query(value = """
                select avg(vr.actual_point)
                from velocity_record vr
                         join phase_project pp on vr.id_phase_project = pp.id
                         join project p on pp.id_project = p.id
                where p.id = :idProject
            """, nativeQuery = true)
    Float avgActualPoint(@Param("idProject") String idProject);

    @Query(value = """
                    select avg(vr.actual_working_day)  
                from velocity_record vr
                         join phase_project pp on vr.id_phase_project = pp.id
                         join project p on pp.id_project = p.id
                where p.id = :idProject
            """, nativeQuery = true)
    Float avgActualWorkingDay(@Param("idProject") String idProject);

    @Query("SELECT v FROM VelocityRecord v WHERE v.phaseProject.project.id = :projectId")
    List<VelocityRecord> findAllByProjectId(String projectId);

}
