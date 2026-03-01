package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import udpm.hn.server.core.manage.project.model.response.MeDataDashboardLabelResponse;
import udpm.hn.server.core.manage.project.model.response.MeDataDashboardTodoListResponse;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.infrastructure.constant.StatusTodo;

import java.util.List;
import java.util.Optional;

public interface ToDoRepository extends JpaRepository<Todo, String> {

    @Query(value = """
            SELECT COUNT(1) FROM todo WHERE id_todo_list = :todoListId 
            """, nativeQuery = true)
    Integer countSimpleEntityByIdTodo(@Param("todoListId") String todoListId);

    @Query(value = """
                        SELECT MAX(a.index_todo) FROM todo a JOIN phase_todo_project b ON a.id = b.id_todo
                        JOIN phase_project c ON c.id = b.id_phase_project
                        WHERE a.id_todo_list = :todoListId AND c.id = :idPhaseProject
            """, nativeQuery = true)
    Short getIndexTodoMax(@Param("todoListId") String todoListId, @Param("idPhaseProject") String idPhaseProject);

    @Query(value = """
                      SELECT a.name, (SELECT COUNT(1) FROM todo b 
                      WHERE b.id_todo_list = a.id) as List
                      FROM todo_list a WHERE a.id_project = :projectId ORDER BY a.index_todo_list ASC
            """, nativeQuery = true)
    List<MeDataDashboardTodoListResponse> countTodoByTodoListAllProject(@Param("projectId") String projectId);

    @Query(value = """
                      SELECT COUNT(1) FROM todo a
                      JOIN todo_list b ON a.id_todo_list = b.id 
                      WHERE b.id_project = :projectId AND a.priority_level = :priority_level
            """, nativeQuery = true)
    Integer countTodoByPriorityLevel(@Param("projectId") String projectId, @Param("priority_level") Integer priorityLevel);

    @Query(value = """
                       SELECT COUNT(1) FROM todo a 
                       JOIN todo_list b ON a.id_todo_list = b.id
                       WHERE b.id_project = :projectId
                       AND a.todo_status = :statusTodo
            """, nativeQuery = true)
    Integer countTodoByDueDateAllProject(@Param("projectId") String projectId, @Param("statusTodo") Integer statusTodo);

    @Query(value = """
                       SELECT COUNT(1) FROM todo a 
                       JOIN todo_list b ON a.id_todo_list = b.id
                       WHERE b.id_project = :projectId
                       AND a.deadline IS NULL
            """, nativeQuery = true)
    Integer countTodoByNoDueDateAllProject(@Param("projectId") String projectId);

//    @Query(value = """
//    SELECT DISTINCT
//       SUBSTRING_INDEX(c.email, '@', 1) AS name,
//       (SELECT COUNT(1) FROM todo )
//""")

    @Query(value = """
                      SELECT DISTINCT a.name, (SELECT COUNT(1) FROM todo g 
                      JOIN label_project_todo k ON g.id = k.id_todo WHERE k.id_label_project = a.id) as label from label_project a 
                      JOIN label_project_todo b ON a.id = b.id_label_project
                      JOIN todo c ON c.id = b.id_todo
                      WHERE a.id_project = :projectId
            """, nativeQuery = true)
    List<MeDataDashboardLabelResponse> countTodoByLabelAllProject(@Param("projectId") String projectId);

    @Query(value = """
                      SELECT COUNT(1)
                      FROM todo a
                      JOIN todo_list b ON a.id_todo_list = b.id
                      LEFT JOIN label_project_todo c ON a.id = c.id_todo
                      WHERE b.id_project = :projectId
                      AND c.id IS NULL 
            """, nativeQuery = true)
    Integer countTodoByNoLabelAllProject(@Param("projectId") String projectId);


    /// //////////////////////////////////////////

    @Query(value = """
                       SELECT a.name, (SELECT COUNT(1) FROM todo b JOIN phase_todo_project i ON b.id = i.id_todo
                       WHERE b.id_todo_list = a.id AND a.id_phase_project = :phaseId) as List
                       FROM todo_list a WHERE a.id_project = :projectId and a.id_phase_project = :phaseId
                       ORDER BY a.index_todo_list ASC
            """, nativeQuery = true)
    List<MeDataDashboardTodoListResponse> countTodoByTodoListPhase(@Param("projectId") String projectId, @Param("phaseId") String phaseId);

    @Query(value = """
                      SELECT COUNT(1) FROM todo a 
                      JOIN todo_list b ON a.id_todo_list = b.id
                      JOIN phase_todo_project c ON a.id = c.id_todo
                      WHERE b.id_peoject = :projectId
                      AND a.status_todo = :statusTodo
                      AND c.id_phase = :phaseId
            """, nativeQuery = true)
    Integer countTodoByDueDatePhase(@Param("projectId") String projectId, @Param("phaseId") String phaseId, @Param("statusTodo") Integer statusTodo);

    @Query(value = """
                      SELECT COUNT(1) FROM todo a
                      JOIN todo_list b ON a.id_todo_list = b.id
                      JOIN phase_todo_project c ON a.id = c.id_todo 
                      WHERE b.id_project = :projectId
                      AND a.deadline IS NULL
                      AND c.id_phase = :phaseId
            """, nativeQuery = true)
    Integer countTodoByNoDueDatePhase(@Param("projectId") String projectId, @Param("phaseId") String phaseId);

    Optional<Todo> findByCode(String code);

    @Query("SELECT MAX(t.indexTodo) FROM Todo t WHERE t.todoList.id = :todoListId")
    Short findMaxIndexByTodoList(@Param("todoListId") String todoListId);

    @Query(value = """
            SELECT td FROM Todo td WHERE td.deliveryDate IS NOT NULL AND td.statusReminder = 0 
            """)
    List<Todo> getAllTodoReminder();

    @Query(value = """
            SELECT b.phaseProject.id FROM Todo a 
            JOIN PhaseTodoProject b ON a.id = b.todo.id
            WHERE a.id = :todoId
            """)
    String getPhaseByIdTodo(@Param("todoId") String todoId);


    @Query(value = """
            SELECT c.id FROM Todo a 
            JOIN TodoList b ON a.todoList.id = b.id
            JOIN Project c ON b.project.id = c.id
            WHERE a.id = :todoId
            """)
    String getProjectByIdTodo(@Param("todoId") String todoId);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE Todo td
            SET td.statusReminder = 1
            WHERE td.id = :todoId
            """)
    void updateStatusReminder(@Param("todoId") String todoId);

    @Query("""
        SELECT 
            FUNCTION('DATE', FUNCTION('FROM_UNIXTIME', t.completionTime / 1000)),
            SUM(t.storyPoint)
        FROM PhaseTodoProject ptp
        JOIN ptp.todo t
        WHERE ptp.phaseProject.id = :phaseId
          AND ptp.phaseProject.project.id = :projectId
          AND t.statusTodo IN :statuses
          AND t.completionTime BETWEEN :startTime AND :endTime
        GROUP BY FUNCTION('DATE', FUNCTION('FROM_UNIXTIME', t.completionTime / 1000))
        ORDER BY FUNCTION('DATE', FUNCTION('FROM_UNIXTIME', t.completionTime / 1000))
    """)
    List<Object[]> getDonePointsByDateAndProject(@Param("projectId") String projectId,
                                                 @Param("phaseId") String phaseId,
                                                 @Param("startTime") Long startTime,
                                                 @Param("endTime") Long endTime,
                                                 @Param("statuses") List<StatusTodo> statuses);

    @Query("""
        SELECT SUM(t.storyPoint)
        FROM PhaseTodoProject ptp
        JOIN ptp.todo t
        WHERE ptp.phaseProject.id = :phaseId
          AND ptp.phaseProject.project.id = :projectId
    """)
    Integer getTotalStoryPointByPhaseAndProject(@Param("projectId") String projectId,
                                                @Param("phaseId") String phaseId);



    @Query("""
    SELECT 
        FUNCTION('DATE', FUNCTION('FROM_UNIXTIME', t.completionTime / 1000)),
        SUM(t.storyPoint)
    FROM PhaseTodoProject ptp
    JOIN ptp.todo t
    WHERE ptp.phaseProject.project.id = :projectId
      AND t.statusTodo IN :statuses
      AND t.completionTime BETWEEN :startTime AND :endTime
    GROUP BY FUNCTION('DATE', FUNCTION('FROM_UNIXTIME', t.completionTime / 1000))
    ORDER BY FUNCTION('DATE', FUNCTION('FROM_UNIXTIME', t.completionTime / 1000))
""")
    List<Object[]> getDonePointsByDateAndProjectOnly(@Param("projectId") String projectId,
                                                     @Param("startTime") Long startTime,
                                                     @Param("endTime") Long endTime,
                                                     @Param("statuses") List<StatusTodo> statuses);

    @Query("""
    SELECT SUM(t.storyPoint)
    FROM PhaseTodoProject ptp
    JOIN ptp.todo t
    WHERE ptp.phaseProject.project.id = :projectId
""")
    Integer getTotalStoryPointByProject(@Param("projectId") String projectId);

}
