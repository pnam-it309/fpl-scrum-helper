package udpm.hn.server.core.manage.project.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.manage.project.model.response.MaPhaseProjectResponse;
import udpm.hn.server.core.manage.project.model.response.MaPhaseSummaryResponse;
import udpm.hn.server.repository.PhaseRepository;
import udpm.hn.server.utils.StatusPhase;

import java.util.List;

@Repository
public interface MaPhaseProjectRepository extends PhaseRepository {
    @Query(value = """
        SELECT
         pp.id AS idPhase ,
         pp.name AS namePhaseProject
         FROM PhaseProject pp
         join Project p on pp.project.id = p.id
         where p.id = :id and  pp.statusPhase NOT IN (:excludedStatuses)
""")
    List<MaPhaseProjectResponse> getALl(
            @Param("id") String id,
            @Param("excludedStatuses") List<StatusPhase> excludedStatuses
    );
    @Query(value= """
        SELECT count(1) as amount, pp.statusPhase as status
        FROM PhaseProject pp
        where pp.project.id = :idProject and pp.status = 0
        GROUP BY pp.statusPhase
    """)
    List<MaPhaseSummaryResponse> getAmountOfPhaseByStatus(@Param("idProject") String idproject);
}
