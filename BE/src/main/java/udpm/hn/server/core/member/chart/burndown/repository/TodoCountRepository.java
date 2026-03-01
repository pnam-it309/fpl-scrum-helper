package udpm.hn.server.core.member.chart.burndown.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.infrastructure.constant.StatusTodo;
import udpm.hn.server.repository.PhaseTodoProjectRepository;
import udpm.hn.server.repository.ToDoRepository;

import java.util.List;

@Repository
public interface TodoCountRepository extends PhaseTodoProjectRepository {

    @Query("SELECT COUNT(ptp) FROM PhaseTodoProject ptp WHERE ptp.phaseProject.id = :phaseProjectId")
    Long countTodosByPhaseProject(@Param("phaseProjectId") String phaseProjectId);

    @Query("""
    SELECT COUNT(ptp)
    FROM PhaseTodoProject ptp
    WHERE ptp.phaseProject.project.id = :projectId
      AND ptp.phaseProject.id = :phaseId
      AND ptp.todo.statusTodo IN (:completedStatuses)
""")
    Long countCompletedTasksByPhaseAndProject(
            @Param("projectId") String projectId,
            @Param("phaseId") String phaseId,
            @Param("completedStatuses") List<StatusTodo> completedStatuses);

    @Query("""
    SELECT COUNT(ptp)
    FROM PhaseTodoProject ptp
    WHERE ptp.phaseProject.project.id = :projectId
      AND ptp.phaseProject.id = :phaseId
      AND ptp.todo.statusTodo IN (:remainingStatuses)
""")
    Long countRemainingTasksByPhaseAndProject(
            @Param("projectId") String projectId,
            @Param("phaseId") String phaseId,
            @Param("remainingStatuses") List<StatusTodo> remainingStatuses);

    @Query("""
    SELECT 
           COUNT(t.id)
    FROM PhaseTodoProject ptp
    JOIN ptp.todo t
    WHERE ptp.phaseProject.project.id = :projectId 
      AND ptp.phaseProject.id = :phaseId
      AND t.statusTodo IN :completedStatuses
      AND t.completionTime IS NOT NULL
    GROUP BY FUNCTION('FROM_UNIXTIME', t.completionTime / 1000, '%Y-%m-%d')
    ORDER BY FUNCTION('FROM_UNIXTIME', t.completionTime / 1000, '%Y-%m-%d')
""")
    List<Long> countCompletedTasksPerDay(
            @Param("projectId") String projectId,
            @Param("phaseId") String phaseId,
            @Param("completedStatuses") List<StatusTodo> completedStatuses);


}
