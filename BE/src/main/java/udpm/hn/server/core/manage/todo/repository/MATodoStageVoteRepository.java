package udpm.hn.server.core.manage.todo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.repository.StageVoteRepository;

import java.util.Optional;

public interface MATodoStageVoteRepository extends StageVoteRepository {

    @Query("""
    SELECT sv.id FROM StageVote sv
    JOIN sv.phaseProject pp
    JOIN pp.project p
    WHERE (:currentTime BETWEEN sv.startTime AND sv.endTime) and sv.status = 0
    AND p.id = :projectId
    """)
    Optional<String> findActiveStageVoteIdByProject(@Param("currentTime") Long currentTime,
                                                    @Param("projectId") String projectId);



}
