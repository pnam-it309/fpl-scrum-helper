package udpm.hn.server.core.manage.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.manage.project.model.request.MaProjectSearchRequest;
import udpm.hn.server.core.manage.project.model.response.MAProjectDetailSummaryResponse;
import udpm.hn.server.core.manage.project.model.response.MaProjectResponse;
import udpm.hn.server.infrastructure.constant.StatusProject;
import udpm.hn.server.repository.ProjectRepository;
import udpm.hn.server.utils.UserContextHelper;

import java.util.Optional;

@Repository
public interface MaProjectRepository extends ProjectRepository {

    @Query(value = """
            SELECT 
                row_number() over(order by p.id desc) as orderNumber,
                p.id as id,
                p.name as nameProject,
                p.code as codeProject,
                concat(d.name, ' - ', f.name) as nameDepartment,
                p.statusProject as status,
                f.id as idFacility,
                p.backgroundColor as backgroundColorProject,
                p.backgroundImage as backgroundImageProject,
                    (
                        SELECT
                            CASE
                                WHEN COUNT(t.id) = 0 THEN 0
                                ELSE ROUND(
                                    (SUM(CASE WHEN t.statusTodo <> 0 THEN 1 ELSE 0 END) * 100.0) / COUNT(t.id),
                                    2
                                )
                            END
                        FROM Todo t
                		JOIN PhaseTodoProject ptp ON t.id = ptp.todo.id
                		JOIN PhaseProject  pp ON ptp.phaseProject.id = pp.id
                        WHERE pp.project.id = p.id
                    ) AS progressProject,
                p.startTime as startTimeProject,
                p.endTime as endTimeProject,
                p.createdDate as createdDateProject
            FROM Project p
            JOIN MajorFacility mf ON p.majorFacility.id = mf.id
            JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
            JOIN Department d ON df.department.id = d.id
            JOIN Facility f ON df.facility.id = f.id
            JOIN StaffProject sp ON p.id = sp.project.id
                WHERE p.status = 0 AND 
                 (:#{#request.search} IS NULL OR p.name LIKE %:#{#request.search}%
                    OR p.code LIKE %:#{#request.search}%)
                    AND (:#{#request.nameDepartment} IS NULL OR
                    d.name LIKE %:#{#request.nameDepartment}% OR
                    f.name LIKE %:#{#request.nameDepartment}% OR
                    CONCAT(d.name, '-', f.name) LIKE %:#{#request.nameDepartment}%)
                    AND (:#{#request.status} IS NULL OR p.statusProject = :status)  
                    AND (sp.staff.id = :#{#userContextHelper.currentUserId}
                    OR sp.student.id = :#{#userContextHelper.currentUserId})
                    And sp.status = 0 and p.statusProject = 1
        """,countQuery = """
            select count(*)
                 FROM Project p
                JOIN MajorFacility mf ON p.majorFacility.id = mf.id
                JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
                JOIN Department d ON df.department.id = d.id
                JOIN Facility f ON df.facility.id = f.id
                JOIN StaffProject sp ON p.id = sp.project.id
            WHERE  p.status = 0 AND  
                (:#{#request.search} IS NULL OR p.name LIKE %:#{#request.search}%
                OR p.code LIKE %:#{#request.search}%)
                AND (:#{#request.nameDepartment} IS NULL OR
                d.name LIKE %:#{#request.nameDepartment}% OR
                f.name LIKE %:#{#request.nameDepartment}% OR
                CONCAT(d.name, '-', f.name) LIKE %:#{#request.nameDepartment}%)
                AND (:#{#request.status} IS NULL OR p.statusProject = :status)
                AND (sp.staff.id = :#{#userContextHelper.currentUserId}
                OR sp.student.id = :#{#userContextHelper.currentUserId})
                And sp.status = 0 and p.statusProject = 1
        """)
    Page<MaProjectResponse> getAllProject(Pageable pageable, MaProjectSearchRequest request, @Param("status") StatusProject status, UserContextHelper userContextHelper);



    @Query(value = """
            SELECT 
            p.id as id,
            p.code as code,
            p.name as name,
            concat(d.name, ' - ', f.name) as nameDepartment,
            p.category.name as category,
            p.startTime as startTime,
            p.endTime as endTime,
            p.statusProject as status,
            p.createdDate as createdDateProject,
            COUNT(sp.id) as amountOfMembers,
            p.actualEndDate as actualEndTime 
            FROM Project p
            JOIN MajorFacility mf ON p.majorFacility.id = mf.id
            JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
            JOIN Department d ON df.department.id = d.id
            JOIN Facility f ON df.facility.id = f.id
            JOIN StaffProject sp on sp.project.id = p.id
            where p.id = :id
            GROUP BY p.id
            """)
    Optional<MAProjectDetailSummaryResponse> getDetailSummaryProject(@Param("id") String id);
}