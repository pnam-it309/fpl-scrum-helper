package udpm.hn.server.core.member.chart.burndown.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.PhaseProject;
import udpm.hn.server.repository.PhaseRepository;

import java.util.Optional;

@Repository
public interface PhaseBurndownRepository extends PhaseRepository {

    @Query("SELECT (p.endTime - p.startTime) / (1000 * 60 * 60 * 24) FROM PhaseProject p WHERE p.id = :phaseId and p.project.id = :idProject")
    Long calculatePhaseDurationDays(@Param("phaseId") String phaseId, @Param("idProject") String idProject);

    @Query("""
    SELECT p FROM PhaseProject p
    WHERE p.id = :phaseId AND p.project.id = :projectId
""")
    Optional<PhaseProject> findByIdAndProjectId(
            @Param("phaseId") String phaseId,
            @Param("projectId") String projectId
    );
}
