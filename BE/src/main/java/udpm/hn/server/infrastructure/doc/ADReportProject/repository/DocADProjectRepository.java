package udpm.hn.server.infrastructure.doc.ADReportProject.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.infrastructure.doc.ADReportProject.model.request.ProjectNameStartTimeProjection;
import udpm.hn.server.infrastructure.doc.ADReportProject.model.response.DocADProjectMemberResponse;
import udpm.hn.server.repository.ProjectRepository;

import java.util.List;

public interface DocADProjectRepository extends ProjectRepository {

    @Query("""
    SELECT 
        p.id as projectId,
        p.name AS projectName,
        p.code as projectCode,
        sp.id AS staffProjectId,
        CASE WHEN sp.staff IS NOT NULL THEN st.name ELSE su.name END AS memberName,
        CASE WHEN sp.staff IS NOT NULL THEN st.code ELSE su.code END AS userCode,
        CASE WHEN sp.staff IS NOT NULL THEN st.emailFpt ELSE su.email END AS userEmail
    FROM StaffProject sp
    JOIN sp.project p
    LEFT JOIN sp.staff st
    LEFT JOIN sp.student su
    WHERE p.id = :projectId
""")
    List<DocADProjectMemberResponse> findProjectAndMemberNamesByProjectId(@Param("projectId") String projectId);

    @Query("SELECT p.name AS name, p.startTime AS startTime FROM Project p WHERE p.id = :id")
    ProjectNameStartTimeProjection findProjectStartTimeById(@Param("id") String id);



}
