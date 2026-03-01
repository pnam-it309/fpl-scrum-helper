package udpm.hn.server.core.admin.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.admin.project.model.request.ADProjectSearchRequest;
import udpm.hn.server.core.admin.project.model.response.*;
import udpm.hn.server.infrastructure.constant.StatusProject;
import udpm.hn.server.repository.ProjectRepository;

import java.util.List;

public interface ADProjectRepository extends ProjectRepository {

    @Query(value = """
            SELECT 
                row_number() over(order by p.id desc) as orderNumber,
                p.id as id,
                p.name as nameProject,
                p.code as codeProject,
                concat(d.name, ' - ', f.name) as nameDepartment,
                p.statusProject as status,
                f.id as idFacility,
                p.startTime as startTime,
                p.endTime as endTime
            FROM Project p
            JOIN MajorFacility mf ON p.majorFacility.id = mf.id
            JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
            JOIN Department d ON df.department.id = d.id
            JOIN Facility f ON df.facility.id = f.id
                WHERE p.status = 0 AND 
                 (:#{#request.search} IS NULL OR p.name LIKE %:#{#request.search}%
                    OR p.code LIKE %:#{#request.search}%)
                    AND (:#{#request.nameDepartment} IS NULL OR
                    d.name LIKE %:#{#request.nameDepartment}% OR
                    f.name LIKE %:#{#request.nameDepartment}% OR
                    CONCAT(d.name, '-', f.name) LIKE %:#{#request.nameDepartment}%)
                    AND (:#{#request.status} IS NULL OR p.statusProject = :status)
    
        """,countQuery = """
            select count(p.id)
                 FROM Project p
                JOIN MajorFacility mf ON p.majorFacility.id = mf.id
                JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
                JOIN Department d ON df.department.id = d.id
                JOIN Facility f ON df.facility.id = f.id
            WHERE  p.status = 0 AND  
                (:#{#request.search} IS NULL OR p.name LIKE %:#{#request.search}%
                OR p.code LIKE %:#{#request.search}%)
                AND (:#{#request.nameDepartment} IS NULL OR
                d.name LIKE %:#{#request.nameDepartment}% OR
                f.name LIKE %:#{#request.nameDepartment}% OR
                CONCAT(d.name, '-', f.name) LIKE %:#{#request.nameDepartment}%)
                AND (:#{#request.status} IS NULL OR p.statusProject = :status)
        """)
        Page<ADProjectResponse> getAllProject(Pageable pageable, ADProjectSearchRequest request,@Param("status") StatusProject status);

    @Query(value = """
        SELECT c.name AS projectName, COUNT(1) AS totalTodo
                FROM todo a
                        JOIN todo_list b on a.id_todo_list = b.id
                        JOIN project c ON b.id_project = c.id
                        WHERE  c.status = 0
                        GROUP BY c.name
                """, nativeQuery = true)
    List<ADProjectTodoCountResponse> countTodoByProjectName();

    //Đếm tổng số dự án còn active (status = 0)
    @Query(value = "SELECT COUNT(*) FROM project WHERE status = 0", nativeQuery = true)
    Long countTotalProjects();

    // Đếm số dự án theo từng trạng thái cụ thể
    @Query(value = """
    SELECT project_status AS status, COUNT(*) AS count
    FROM project
    WHERE status = 0
    GROUP BY project_status
""", nativeQuery = true)
    List<Object[]> countProjectsGroupedByStatus();

    // Đếm số dự án trễ hạn
    @Query(value = """
    SELECT COUNT(*)
    FROM project
    WHERE status = 0 AND project_status <> :doneStatus AND project_status <> 0 AND end_time < CURRENT_TIMESTAMP
""", nativeQuery = true)
    Long countOverdueProjects(@Param("doneStatus") Integer doneStatus);

    @Query("""
    SELECT 
        f.name AS facilityName,
        COUNT(p) AS total
    FROM Project p
    JOIN MajorFacility mf ON p.majorFacility.id = mf.id
    JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
    JOIN Facility f ON df.facility.id = f.id
    WHERE p.status = 0
    GROUP BY f.name
""")
    List<FacilityProjectCountResponse> countProjectsByFacility();

    @Query("""
    SELECT 
        d.name AS departmentName,
        COUNT(p) AS total
    FROM Project p
    JOIN MajorFacility mf ON p.majorFacility.id = mf.id
    JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
    JOIN Department d ON df.department.id = d.id
    WHERE p.status = 0
    GROUP BY d.name
""")
    List<DepartmentProjectCountResponse> countProjectsByDepartment();


    @Query(value = """
        SELECT 
            p.name AS projectName,
            t.status AS projectStatus,
            COUNT(*) AS totalTodo
        FROM todo t
        JOIN todo_list tl ON t.id_todo_list = tl.id
        JOIN project p ON tl.id_project = p.id
        WHERE p.status = 0
        GROUP BY p.name, t.status
        ORDER BY p.name, t.status
    """, nativeQuery = true)
    List<TodoStatusByProject> countTaskByStatusAndProject();
}
