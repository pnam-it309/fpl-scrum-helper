package udpm.hn.server.core.member.projectdetails.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.member.projectdetails.model.response.ProjectView;
import udpm.hn.server.repository.ProjectRepository;

import java.util.Optional;
@Repository
public interface MBMeProjectRepository extends ProjectRepository {

    @Query("""
        SELECT 
            p.id AS id,
            p.code AS code,
            p.name AS name,
            p.startTime AS startTime,
            p.endTime AS endTime,
            p.actualEndDate AS actualEndDate,
            p.descriptions AS descriptions,
            p.progress AS progress,
            p.backgroundColor AS backgroundColor,
            p.backgroundImage AS backgroundImage,
            p.statusProject AS statusProject,
            pp.name AS phaseProjectName
        FROM PhaseProject pp
        JOIN pp.project p
        WHERE pp.id = :phaseProjectId
    """)
    Optional<ProjectView> findProjectViewByPhaseProjectId(@Param("phaseProjectId") String phaseProjectId);


}
