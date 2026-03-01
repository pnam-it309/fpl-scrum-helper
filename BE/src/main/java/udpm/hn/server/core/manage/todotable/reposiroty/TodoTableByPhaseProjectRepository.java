package udpm.hn.server.core.manage.todotable.reposiroty;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.manage.todotable.model.response.TodoByPhaseProjectResponse;
import udpm.hn.server.core.manage.todotable.model.response.TodoListIndexTodoResponse;
import udpm.hn.server.repository.ToDoRepository;

import java.util.List;

@Repository
public interface TodoTableByPhaseProjectRepository extends ToDoRepository {

    @Query(
            value = """
    select 
        ROW_NUMBER() OVER(ORDER BY t.id DESC) AS orderNumber,
        t.id AS idTodo,
        t.createdDate AS createdDate,
        t.name AS todoName, 
        t.completionTime AS completionTime,
        t.statusTodo AS statusTodo,
        t.priorityLevel AS priorityLevel,
        t.indexTodo AS indexTodo,
        t.deadline AS deadlineTodo, 
        tl.name AS todoListName, 
        tl.id AS todoListId,
        tt.type AS typeTodo,
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
                'picture', sp_staff.picture
            ))
            FROM Assign tv_sub
            JOIN StaffProject sp ON tv_sub.staffProject.id = sp.id
            JOIN Staff sp_staff ON sp.staff.id = sp_staff.id
            WHERE tv_sub.todo.id = t.id 
        ) AS staff,
        (
            SELECT JSON_ARRAYAGG(JSON_OBJECT(
                'id', lp.id,
                'name', lp.name,
                'color', lp.color
            ))
            FROM LabelProject lp
            JOIN LabelProjectTodo lpt ON lp.id = lpt.labelProject.id
            WHERE lpt.todo.id = t.id and lp.status =0
        ) as label
    from Todo t
    join PhaseTodoProject ptp ON t.id  = ptp.todo.id
    join PhaseProject pp on ptp.phaseProject.id = pp.id
    join TodoList tl on t.todoList.id = tl.id
     LEFT JOIN TypeTodo tt ON t.typeTodo.id = tt.id
    where pp.project.id = :idProject and pp.id = :idPhase
    order by t.indexTodo
""", countQuery = """
    select 
        COUNT(t.id) 
    from Todo t
    join PhaseTodoProject ptp ON t.id  = ptp.todo.id
    join PhaseProject pp on ptp.phaseProject.id = pp.id
    join TodoList tl on t.todoList.id = tl.id
    where pp.project.id = :idProject and pp.id = :idPhase
"""
    )
    Page<TodoByPhaseProjectResponse> getAllTodoByPhaseProject(Pageable pageable, String idProject, String idPhase);

    @Query(value = """
    select t.indexTodo as indexTodo from Todo t
    join TodoList tl on t.todoList.id = tl.id
    where t.todoList.id = :id
    """)
    List<TodoListIndexTodoResponse> getIndexTodo(String id);

}
