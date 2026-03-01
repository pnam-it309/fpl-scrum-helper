package udpm.hn.server.core.manage.todo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.entity.TodoList;
import udpm.hn.server.infrastructure.constant.StatusListTodo;
import udpm.hn.server.repository.TodoListRepository;

import java.util.List;

public interface MAListTodoProjectRepository extends TodoListRepository {


    @Query("""
        select tl from TodoList tl 
        join Project p on tl.project.id = p.id
        join Todo  t on tl.id = t.todoList.id
        left join PhaseTodoProject  ptp on t.id = ptp.todo.id
        where p.id = :idProject and ptp.todo.id is null 
""")
    List<TodoList> findByProject(@Param("idProject") String idProject);

    @Query("""
        select tl from TodoList tl 
        join Project p on tl.project.id = p.id
        join Todo  t on tl.id = t.todoList.id
        left join PhaseTodoProject  ptp on t.id = ptp.todo.id
        left join PhaseProject  pp on ptp.phaseProject.id = pp.id
        where p.id = :idProject and pp.id = :idPhase 
""")
    List<TodoList> findByProjectAndPhase(@Param("idProject") String idProject,@Param("idPhase") String idPhase);

    @Query("""
        select tl from TodoList tl 
        join Project p on tl.project.id = p.id
        left join PhaseProject  pp on tl.phaseProject.id = pp.id
        where p.id = :idProject and pp.id = :idPhase and tl.StatusListTodo = :statusListTodo and tl.status = 0
""")
    List<TodoList> findTodoListNotStratedByProjectAndPhase(@Param("idProject") String idProject, @Param("idPhase") String idPhase,@Param("statusListTodo") StatusListTodo statusListTodo);
//

    @Query("""
    SELECT tl FROM TodoList tl
    JOIN tl.project p
    WHERE p.id = :id AND tl.status = 0
""")
    List<TodoList> findTodoListByProject_Id(@Param("id") String id);

}
