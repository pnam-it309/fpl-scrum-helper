package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.manage.todotable.model.response.TodoListPhaseResponse;
import udpm.hn.server.entity.TodoList;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoListRepository  extends JpaRepository<TodoList, String> {

    @Query("SELECT COUNT(t) FROM TodoList t WHERE t.project.id = :projectId and t.status =0")
    Integer countSimpleEntityByIdProject(@Param("projectId") String projectId);

    @Query("SELECT MAX(t.indexTodoList) FROM TodoList t WHERE t.project.id = :idProject AND t.status =0")
    Byte getIndexTodoListMax(@Param("idProject") String idProject);


    @Query("""
    SELECT MAX(t.indexTodoList) 
    FROM TodoList t 
    WHERE t.project.id = :idProject 
      AND t.phaseProject.id = :idPhase
      AND t.status = 0
""")
    Byte getIndexTodoListMaxWhereIDPHASE(
            @Param("idProject") String idProject,
            @Param("idPhase") String idPhase
    );

    @Query(value = """
        select tl from TodoList tl
        where tl.code = :nameTodoList and tl.StatusListTodo = 0
""")
    Optional<TodoList> findByCode(String nameTodoList);

    @Query(value = """
    select tl from TodoList tl
    join PhaseProject pp on tl.phaseProject.id = pp.id
    where tl.StatusListTodo = 0 and pp.id = :id and tl.status = 0
""")
    Optional<TodoList> findByPhaseProjectId(String id);

    @Query(value = """
        select 
            tl.id AS todoListId,
            tl.name AS todoListName
        from TodoList tl
        join PhaseProject pp on tl.phaseProject.id = pp.id
        join Project p on tl.project.id = p.id
        where tl.project.id = :idProject
        and tl.phaseProject.id = :idPhase
        and tl.status = 0
        order by tl.indexTodoList
    """)
    List<TodoListPhaseResponse> getAllTodoListByPhaseProject(String idProject, String idPhase);


}
