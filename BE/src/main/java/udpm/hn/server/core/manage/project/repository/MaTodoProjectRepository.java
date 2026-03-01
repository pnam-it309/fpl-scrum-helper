package udpm.hn.server.core.manage.project.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.manage.project.model.response.MaTodoByPhaseSummaryResponse;
import udpm.hn.server.core.manage.project.model.response.MaTodoSummaryResponse;
import udpm.hn.server.repository.ToDoRepository;

import java.util.List;

@Repository
public interface MaTodoProjectRepository extends ToDoRepository {
    @Query(value = """
        select 
            count(1) as amount
            ,t.statusTodo as todoStatus
        from Todo t
        join TodoList tl on t.todoList.id = tl.id
        where tl.project.id = :id and t.status = 0
        group by t.statusTodo
        """)
    List<MaTodoSummaryResponse> countTodoByTodoStatus(@Param("id") String id);

    @Query(value = """
        select count(1) as amount
            , pp.name as namePhase 
            from PhaseProject pp
        join TodoList tl on tl.phaseProject.id = pp.id
        join Todo t on t.todoList.id = tl.id
        where pp.project.id = :idProject and tl.status = 0
        group by pp.id, pp.createdDate
        ORDER BY pp.createdDate ASC
        """)
    List<MaTodoByPhaseSummaryResponse> getAmountOfTodoByPhase(@Param("idProject") String idProject);

}
