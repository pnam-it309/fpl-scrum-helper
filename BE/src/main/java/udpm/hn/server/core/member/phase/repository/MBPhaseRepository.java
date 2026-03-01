package udpm.hn.server.core.member.phase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.manage.project.model.request.MaTodoListProjectRequest;
import udpm.hn.server.core.manage.project.model.response.MATodoProjectResponse;
import udpm.hn.server.core.member.phase.model.request.MBPhaseRequest;
import udpm.hn.server.core.member.phase.model.response.MBDetailPhase;
import udpm.hn.server.core.member.phase.model.response.MBPhaseResponse;
import udpm.hn.server.repository.PhaseRepository;
import udpm.hn.server.utils.UserContextHelper;

public interface MBPhaseRepository extends PhaseRepository {

    @Query("""
        select 
            ROW_NUMBER() OVER (ORDER BY pp.id DESC ) AS orderNumber,
            pp.name as name,
            pp.id as id,
            pp.code as code,
            pp.createdDate as createDate,
            pp.startTime as startTime,
            pp.endTime as endTime,
            pp.descriptions as descriptions,
            pp.statusPhase as statusPhase
         from PhaseProject  pp 
         join Project p on pp.project.id = p.id
         where pp.status = 0 and (pp.statusPhase = 0 OR pp.statusPhase = 1) 
          and p.id = :idProject
          and (:#{#req.search} IS NULL OR
            pp.name LIKE CONCAT('%', :#{#req.search}, '%') )
""")
    Page<MBPhaseResponse> getAllPhaseProject(Pageable pageable,String idProject,@Param("req") MBPhaseRequest req);

    @Query("""
        select 
            pp.id as id
         from PhaseProject  pp 
         join Project p on pp.project.id = p.id
         where pp.status = 0 and pp.statusPhase = 1
          and p.id = :idProject
""")
    MBDetailPhase getPhaseId(@Param("idProject") String idProject);

    @Query(value = """
    SELECT DISTINCT
        ROW_NUMBER() OVER(ORDER BY t.id DESC) AS orderNumber,\s
        t.id AS idTodo,
        t.name AS nameTodo,
        t.descriptions AS descriptionsTodo,
        t.statusTodo AS statusTodo,
        t.deadline AS deadline,
        COALESCE(pp.name, '') AS namePhaseProject,
        t.priorityLevel AS priorityLevel,
        t.createdDate AS createdDate,
        t.lastModifiedDate AS lastModifiedDate,
        tl.project.id AS projectId,
        pp.id AS phaseId,
        (
            SELECT JSON_ARRAYAGG(JSON_OBJECT(
                'id', sp_student.id,
                'name', sp_student.name,
                'image', sp_student.image,
                'email', sp_student.email
            ))
            FROM Assign tv_sub
            JOIN StaffProject sp ON tv_sub.staffProject.id = sp.id
            JOIN Student sp_student ON sp.student.id = sp_student.id
            WHERE tv_sub.todo.id = t.id 
        ) AS students,
        (
            SELECT JSON_ARRAYAGG(JSON_OBJECT(
                'id', sp_staff.id,
                'name', sp_staff.name,
                'picture', sp_staff.picture,
                'email', sp_staff.emailFpt
            ))
            FROM Assign tv_sub
            JOIN StaffProject sp ON tv_sub.staffProject.id = sp.id
            JOIN Staff sp_staff ON sp.staff.id = sp_staff.id
            WHERE tv_sub.todo.id = t.id 
        ) AS staff
    FROM
        Todo t  
        LEFT JOIN PhaseTodoProject ptp ON t.id = ptp.todo.id\s
        LEFT JOIN Assign tv ON tv.todo.id = t.id
        LEFT JOIN StaffProject sp ON tv.staffProject.id = sp.id
        LEFT JOIN Staff s ON sp.staff.id = s.id
        LEFT JOIN Student s2 ON sp.student.id = s2.id
        LEFT JOIN TodoList tl ON t.todoList.id = tl.id
        LEFT JOIN PhaseProject pp ON ptp.phaseProject.id = pp.id
    WHERE 
        (tl.project.id = :id)
         AND ((sp.student.id = :#{#userContextHelper.currentUserId}) 
            or (sp.staff.id = :#{#userContextHelper.currentUserId}))
        AND ( :#{#req.search} IS NULL OR t.name LIKE CONCAT('%', :#{#req.search} ,'%'))
    """, countQuery = """
    SELECT 
        COUNT(DISTINCT t.id) 
    FROM 
        Todo t
        LEFT JOIN Assign tv ON tv.todo.id = t.id
        LEFT JOIN StaffProject sp ON tv.staffProject.id = sp.id
        LEFT JOIN Staff s ON sp.staff.id = s.id
        LEFT JOIN Student s2 ON sp.student.id = s2.id
        LEFT JOIN TodoList tl ON t.todoList.id = tl.id
        LEFT JOIN PhaseProject pp ON tl.phaseProject.id = pp.id
    WHERE 
        (tl.project.id = :id)
        AND ((sp.student.id = :#{#userContextHelper.currentUserId}) 
            or (sp.staff.id = :#{#userContextHelper.currentUserId}))
        AND ( :#{#req.search} IS NULL OR t.name LIKE CONCAT('%', :#{#req.search} ,'%'))
    """
    )
    Page<MATodoProjectResponse> getAllTodoProjectMemBer(Pageable pageable, String id, @Param("req") MBPhaseRequest req, UserContextHelper userContextHelper);

}
