package udpm.hn.server.core.manage.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.manage.project.model.request.MaTodoListProjectRequest;
import udpm.hn.server.core.manage.project.model.response.DetailTodoList;
import udpm.hn.server.core.manage.project.model.response.MATodoProjectResponse;
import udpm.hn.server.core.manage.project.model.response.MaTodoListProjectResponse;
import udpm.hn.server.repository.TodoListRepository;

@Repository
public interface MaTodoListProjectRepository extends TodoListRepository {

    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER(ORDER BY tl.id DESC) AS orderNumber, 
            tl.id AS todoListId,
            tl.code AS codeTodoList,
            tl.name AS nameTodoList,
            tl.status AS statusTodoList,
            tl.describe AS describeTodoList,
            tl.createdDate AS createdDate,
            p.name AS nameProject
        FROM 
            TodoList tl
        JOIN 
            Project p ON p.id = tl.project.id
        WHERE 
            (tl.project.id = :id)
            AND ( :#{#req.search} IS NULL 
            OR tl.name LIKE CONCAT('%', :#{#req.search} ,'%') 
            OR tl.code LIKE CONCAT('%', :#{#req.search} ,'%'))
         ORDER BY tl.createdDate DESC
        """, countQuery = """
        SELECT 
            COUNT(tl.id) 
        FROM 
            TodoList tl
        JOIN 
            Project p ON p.id = tl.project.id
        WHERE 
            (tl.project.id = :id)
            AND ( :#{#req.search} IS NULL 
            OR tl.name LIKE CONCAT('%', :#{#req.search} ,'%') 
            OR tl.code LIKE CONCAT('%', :#{#req.search} ,'%'))
        """
    )
    Page<MaTodoListProjectResponse> getAllTodo(Pageable pageable, String id, @Param("req")MaTodoListProjectRequest req);

    @Query(value = """
            SELECT
            	tl.id AS id,
            	tl.code AS codeTodoList,
            	tl.name AS nameTodoList,
            	tl.status AS statusTodoList,
            	tl.describe AS describeTodoList,
            	tl.indexTodoList AS indexTodoList
            FROM
            	TodoList tl
            WHERE
                tl.id = :id
            """, countQuery = """
            SELECT
                COUNT(tl.id)
            FROM
                TodoList tl
            WHERE
                 tl.id = :id
            """
    )
    DetailTodoList getDetailTodoListProject(String id);

    @Query(value = """
SELECT DISTINCT
    ROW_NUMBER() OVER(ORDER BY t.createdDate DESC) AS orderNumber,
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
    LEFT JOIN PhaseTodoProject ptp ON t.id = ptp.todo.id
    LEFT JOIN PhaseProject pp ON ptp.phaseProject.id = pp.id
    LEFT JOIN TodoList tl ON t.todoList.id = tl.id
    WHERE 
        (tl.project.id = :id)
        AND (t.statusTodo = :#{#req.status} OR :#{#req.status} is null)
        AND ( :#{#req.search} IS NULL OR t.name LIKE CONCAT('%', :#{#req.search} ,'%'))
        AND ( :#{#req.level} IS NULL OR t.priorityLevel = :#{#req.level})
        AND t.status = 0
    ORDER BY t.createdDate DESC
    """, countQuery = """
    SELECT 
        COUNT(DISTINCT t.id) 
    FROM 
        Todo t
    LEFT JOIN PhaseTodoProject ptp ON t.id = ptp.todo.id
    LEFT JOIN PhaseProject pp ON ptp.phaseProject.id = pp.id
    LEFT JOIN TodoList tl ON t.todoList.id = tl.id
    WHERE 
        (tl.project.id = :id)
        AND (t.statusTodo = :#{#req.status} OR :#{#req.status} is null)
        AND ( :#{#req.search} IS NULL OR t.name LIKE CONCAT('%', :#{#req.search} ,'%'))
        AND ( :#{#req.level} IS NULL OR t.priorityLevel = :#{#req.level})
        AND t.status = 0
    """
    )
    Page<MATodoProjectResponse> getAllTodoProject(Pageable pageable, String id, @Param("req") MaTodoListProjectRequest req);

}