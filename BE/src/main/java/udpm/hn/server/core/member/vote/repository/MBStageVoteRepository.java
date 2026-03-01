package udpm.hn.server.core.member.vote.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.member.vote.model.response.MBStageVoteResponse;
import udpm.hn.server.repository.StageVoteRepository;
import java.util.List;
import java.util.Optional;

public interface MBStageVoteRepository  extends StageVoteRepository {

    @Query("""
    SELECT sv.id AS id, sv.startTime AS startTime, sv.endTime AS endTime, pp.id AS phaseId,
    pp.name AS namePhaseProject
    FROM StageVote sv
    JOIN sv.phaseProject pp
    JOIN pp.project p
    WHERE :currentTime BETWEEN sv.startTime AND sv.endTime
    AND p.id = :projectId
    AND sv.status = 0 
    """)
    Optional<MBStageVoteResponse> findActiveStageVoteByProject(
            @Param("currentTime") Long currentTime,
            @Param("projectId") String projectId
    );

    @Query("""
    SELECT sv.id AS id, sv.startTime AS startTime, sv.endTime AS endTime, pp.id AS phaseId,
        pp.name AS namePhaseProject
    FROM StageVote sv
    JOIN sv.phaseProject pp
    JOIN pp.project p
    WHERE sv.startTime > :currentTime
    AND p.id = :projectId
    AND p.status = 0
    """)
    List<MBStageVoteResponse> findUpcomingStageVoteByProject(
            @Param("currentTime") Long currentTime,
            @Param("projectId") String projectId
    );




}
