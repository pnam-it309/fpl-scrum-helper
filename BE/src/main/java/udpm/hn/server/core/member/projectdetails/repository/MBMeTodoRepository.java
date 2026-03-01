package udpm.hn.server.core.member.projectdetails.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeFilterTodoRequest;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeTodoChildResponse;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeTodoResponse;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.entity.TodoList;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.repository.ToDoRepository;

import java.util.List;

public interface MBMeTodoRepository extends ToDoRepository {
    @Query("""
    SELECT a.id AS id, 
           a.code AS code, 
           a.name AS name, 
           a.completionTime AS completionTime, 
           a.priorityLevel AS priorityLevel, 
           a.deadline AS deadline, 
           round( a.progress)AS progress, 
           a.indexTodo AS indexTodo, 
           a.urlImage AS urlImage,
           a.imageId AS imageId,
              t.type AS typeTodo,
           (SELECT COUNT(g) FROM Todo g WHERE g.todoList.id = a.todoList.id) AS numberTodo,
           (SELECT COUNT(g) FROM Todo g WHERE g.todoList.id = a.todoList.id AND g.statusTodo = 1) AS numberTodoComplete,
           a.todoList.id AS todoListId
    FROM Todo a
    LEFT JOIN a.typeTodo t
    JOIN PhaseTodoProject b ON a.id = b.todo.id
    JOIN PhaseProject c ON b.phaseProject.id = c.id
    WHERE a.todoList.id = :#{#req.idTodoList}
    AND c.id = :#{#req.idPhase}
    AND a.status =0 
    AND a.todoList.status =0
    ORDER BY a.indexTodo
""")
    List<MBMeTodoResponse> getToDoByPeriodAndIdTodoList(@Param("req") MBMeFilterTodoRequest req);


    @Query("""
    SELECT t
    FROM TodoList t
    WHERE t.project.id = :projectId 
      AND t.phaseProject.id = :phaseId
      AND t.status = 0
    ORDER BY t.indexTodoList
""")
    List<TodoList> findAllByProjectIdAndPhaseIdOrderByIndexTodoList(
            @Param("projectId") String projectId,
            @Param("phaseId") String phaseId
    );

    @Modifying
    @Transactional
    @Query("""
    UPDATE Todo t
    SET t.indexTodo = t.indexTodo - 1
    WHERE t.indexTodo > :indexBefore AND t.indexTodo <= :indexAfter
    AND t.todoList.id = :idTodoList
    AND t.status = 0 
    AND EXISTS (
              SELECT 1 FROM PhaseTodoProject pt
              WHERE pt.todo.id = t.id AND pt.phaseProject.id = :idPhase
    )
""")
    void updateIndexTodoDecs(@Param("indexBefore") Short indexBefore,
                             @Param("indexAfter") Short indexAfter,
                             @Param("idPhase") String idPhase,
                             @Param("idTodoList") String idTodoList);

    @Modifying
    @Transactional
    @Query("""
    UPDATE Todo t
    SET t.indexTodo = t.indexTodo + 1
    WHERE t.indexTodo < :indexBefore AND t.indexTodo >= :indexAfter
    AND t.todoList.id = :idTodoList
    AND t.status = 0
    AND EXISTS (
              SELECT 1 FROM PhaseTodoProject pt
              WHERE pt.todo.id = t.id AND pt.phaseProject.id = :idPhase
          )
""")
    void updateIndexTodoAsc(@Param("indexBefore") Short indexBefore,
                            @Param("indexAfter") Short indexAfter,
                            @Param("idPhase") String idPhase,
                            @Param("idTodoList") String idTodoList);

    @Query("""
    SELECT COUNT(t.id)
    FROM Todo t
    WHERE t.todoList.id = :idTodoList
   AND t.status = 0
   AND EXISTS (
              SELECT 1 FROM PhaseTodoProject pt
              WHERE pt.todo.id = t.id AND pt.phaseProject.id = :idPhase
    )
""")
    Short countTodoInTodoList(@Param("idTodoList") String idTodoList,
                              @Param("idPhase") String idPhase);

    @Modifying
    @Transactional
    @Query("""
    UPDATE Todo t
    SET t.indexTodo = t.indexTodo - 1
    WHERE t.todoList.id = :idTodoList
    AND t.indexTodo > :indexBefore
    AND t.status = 0
    AND EXISTS (
              SELECT 1 FROM PhaseTodoProject pt
              WHERE pt.todo.id = t.id AND pt.phaseProject.id = :idPhase
    )
""")
    void updateIndexTodoInTodoListOld(@Param("idTodoList") String idTodoList,
                                      @Param("idPhase") String idPhase,
                                      @Param("indexBefore") Short indexBefore);

    @Modifying
    @Transactional
    @Query("""
    UPDATE Todo t
    SET t.indexTodo = t.indexTodo + 1
    WHERE t.todoList.id = :idTodoList
    AND t.indexTodo >= :indexAfter
    AND t.id != :idTodo
    AND t.status = 0
    AND EXISTS (
              SELECT 1 FROM PhaseTodoProject pt
              WHERE pt.todo.id = t.id AND pt.phaseProject.id = :idPhase
    )
""")
    void updateIndexTodoInTodoListNew(@Param("idTodoList") String idTodoList,
                                      @Param("idPhase") String idPhase,
                                      @Param("indexAfter") Short indexAfter,
                                      @Param("idTodo") String idTodo);


    boolean existsByTodoList_Id(String todoListId);

    boolean existsByTodoList_IdAndStatus(String todoListId, EntityStatus status);

    @Query("""
    SELECT 
        t.id AS id,
        t.code AS code,
        t.name AS name,
        t.completionTime AS completionTime,
        t.priorityLevel AS priorityLevel,
        t.deadline AS deadline,
        t.progress AS progress,
        t.indexTodo AS indexTodo,
        i.id AS imageId,
        i.name AS nameFile,
        t.todoList.id AS todoListId,
        
        (SELECT COUNT(tc) FROM TodoChild tc WHERE tc.todo.id = :idTodo) AS numberTodo,
        (SELECT COUNT(tc) FROM TodoChild tc WHERE tc.todo.id = :idTodo AND tc.statusTodoChild = 0) AS numberTodoComplete
        
    FROM Todo t
    LEFT JOIN Image i ON i.todo.id = t.id
    WHERE t.id = :idTodo
""")
    MBMeTodoResponse findTodoDetailById(@Param("idTodo") String idTodo);

    @Query("""
        SELECT
            tc.id AS id,
            tc.code AS code,
            tc.name AS name,   
            tc.status AS status,
             tc.statusTodoChild as statusTodoChild      
        FROM TodoChild tc
        WHERE tc.todo.id = :todoId 
        AND tc.status = 0
    """)
    List<MBMeTodoChildResponse> getAllCheckTodoChild(@Param("todoId") String todoId);

    @Query("""
        SELECT COUNT(tc)
        FROM TodoChild tc
        WHERE tc.todo.id = :todoId
        AND tc.status = 0
    """)
    Short countByTodoChildId(@Param("todoId") String todoId);

    @Query("""
        SELECT COUNT(tc)
        FROM TodoChild tc
        WHERE tc.todo.id = :todoId 
        AND tc.statusTodoChild = 1
        AND tc.status = 0
    """)
    Short countCompletedByTodoChildId(@Param("todoId") String todoId);

    @Query("SELECT COUNT(t) FROM Todo t WHERE t.id = :todoId")
    Short countTodoInCheckList(@Param("todoId") String todoId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.todo.id = :todoId AND c.status = 0")
    Integer countCommentByIdTodo(@Param("todoId") String todoId);

    @Query("SELECT COUNT(r) FROM Resource r WHERE r.todo.id = :todoId AND r.status =0")
    Integer countResourceByIdTodo(@Param("todoId") String todoId);


    @Query("""
    SELECT t 
    FROM Todo t 
    WHERE t.todoList = :todoList 
      AND t.todoList.phaseProject.id = :phaseProjectId 
    ORDER BY t.priorityLevel ASC
""")
    List<Todo> findTodoSortTodoPriorityASC(
            @Param("todoList") TodoList todoList,
            @Param("phaseProjectId") String phaseProjectId
    );

    @Query("""
    SELECT t 
    FROM Todo t 
    WHERE t.todoList = :todoList 
      AND t.todoList.phaseProject.id = :phaseProjectId 
    ORDER BY t.priorityLevel DESC
""")
    List<Todo> findTodoSortTodoPriorityDESC(
            @Param("todoList") TodoList todoList,
            @Param("phaseProjectId") String phaseProjectId
    );


    @Query("""
    SELECT t 
    FROM Todo t 
    WHERE t.todoList = :todoList 
      AND t.todoList.phaseProject.id = :phaseProjectId 
    ORDER BY t.deadline ASC
""")
    List<Todo> findTodoSortTodoDeadlineASC(
            @Param("todoList") TodoList todoList,
            @Param("phaseProjectId") String phaseProjectId
    );

    @Query("""
    SELECT t 
    FROM Todo t 
    WHERE t.todoList = :todoList 
      AND t.todoList.phaseProject.id = :phaseProjectId 
    ORDER BY t.deadline DESC
""")
    List<Todo> findTodoSortTodoDeadlineDESC(
            @Param("todoList") TodoList todoList,
            @Param("phaseProjectId") String phaseProjectId
    );

    @Query("""
    SELECT t 
    FROM Todo t 
    WHERE t.todoList = :todoList 
      AND t.todoList.phaseProject.id = :phaseProjectId 
    ORDER BY t.createdDate ASC
""")
    List<Todo> findTodoSortTodoCreateDateASC(
            @Param("todoList") TodoList todoList,
            @Param("phaseProjectId") String phaseProjectId
    );

    @Query("""
    SELECT t 
    FROM Todo t 
    WHERE t.todoList = :todoList 
      AND t.todoList.phaseProject.id = :phaseProjectId 
    ORDER BY t.createdDate DESC
""")
    List<Todo> findTodoSortTodoCreateDateDESC(
            @Param("todoList") TodoList todoList,
            @Param("phaseProjectId") String phaseProjectId
    );

    @Query("""
    SELECT t 
    FROM Todo t 
    WHERE t.todoList = :todoList 
      AND t.todoList.phaseProject.id = :phaseProjectId 
    ORDER BY t.name ASC
""")
    List<Todo> findTodoSortTodoNameASC(
            @Param("todoList") TodoList todoList,
            @Param("phaseProjectId") String phaseProjectId
    );

    @Query("""
    SELECT t 
    FROM Todo t 
    WHERE t.todoList = :todoList 
      AND t.todoList.phaseProject.id = :phaseProjectId 
    ORDER BY t.name DESC
""")
    List<Todo> findTodoSortTodoNameDESC(
            @Param("todoList") TodoList todoList,
            @Param("phaseProjectId") String phaseProjectId
    );

    @Query("""
    SELECT t 
    FROM Todo t 
    WHERE t.todoList = :todoList 
      AND t.todoList.phaseProject.id = :phaseProjectId 
    ORDER BY t.progress ASC
""")
    List<Todo> findTodoSortTodoProgressASC(
            @Param("todoList") TodoList todoList,
            @Param("phaseProjectId") String phaseProjectId
    );

    @Query("""
    SELECT t 
    FROM Todo t 
    WHERE t.todoList = :todoList 
      AND t.todoList.phaseProject.id = :phaseProjectId 
    ORDER BY t.progress DESC
""")
    List<Todo> findTodoSortTodoProgressDESC(
            @Param("todoList") TodoList todoList,
            @Param("phaseProjectId") String phaseProjectId
    );

}
