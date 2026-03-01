package udpm.hn.server.core.member.vote.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.member.vote.model.response.StaffStudentVoteResponse;
import udpm.hn.server.repository.StaffProjectRepository;

@Repository
public interface StaffStudentVoteRepository extends StaffProjectRepository {
    @Query(value = """
        SELECT
            ROW_NUMBER() OVER(ORDER BY sp.id DESC) AS orderNumber,
            sp.id AS staffProjectId,
            COALESCE(s.name, st.name) AS nameStaff,
            sp.role AS role,
            CASE
                WHEN EXISTS (
                    SELECT 1
                    FROM TodoVote tv2
                    JOIN StageVote sv2 ON sv2.id = tv2.stageVote.id
                    WHERE tv2.staffProject.id = sp.id AND tv2.statusVote = 0
                    AND :currentTime BETWEEN sv2.startTime AND sv2.endTime
                ) THEN TRUE
                ELSE FALSE
            END AS voteStatus
        FROM StaffProject sp
        LEFT JOIN Staff s ON sp.staff.id = s.id
        LEFT JOIN Student st ON sp.student.id = st.id
        LEFT JOIN TodoVote tv ON tv.staffProject.id = sp.id AND tv.statusVote = 0
         LEFT JOIN StageVote sv ON sv.id = tv.stageVote.id AND :currentTime BETWEEN sv.startTime AND sv.endTime
        WHERE sp.project.id = :idProject
        GROUP BY sp.id
        """,
            countQuery = """
        SELECT COUNT(sp.id)
        FROM StaffProject sp
        LEFT JOIN Student st ON sp.student.id = st.id
         LEFT JOIN TodoVote tv ON tv.staffProject.id = sp.id AND tv.statusVote = 0
         LEFT JOIN StageVote sv ON sv.id = tv.stageVote.id AND :currentTime BETWEEN sv.startTime AND sv.endTime
        WHERE sp.project.id = :idProject
        """
    )
    Page<StaffStudentVoteResponse> getStaffStudentVote(Pageable pageable, String idProject, @Param("currentTime") Long currentTime);
}
