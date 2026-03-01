package udpm.hn.server.core.member.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.member.report.model.request.MBTodoProjectRequest;
import udpm.hn.server.core.member.report.model.response.MBTodoProjectResponse;
import udpm.hn.server.repository.ToDoRepository;
import udpm.hn.server.utils.UserContextHelper;

@Repository
public interface MBTodoProjectRepository extends ToDoRepository {

    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER(ORDER BY t.id DESC) AS orderNumber, 
            t.id AS idTodo,
            t.name AS nameTodo,
            t.createdDate AS createdDate,
            sp.project.id AS projectId,
            t.priorityLevel AS priorityLevel
        FROM 
            Todo t
            JOIN Assign tv on t.id = tv.todo.id
            join StaffProject sp on tv.staffProject.id  = sp.id
        WHERE 
            (sp.staff.id = :#{#userContextHelper.currentUserId}
             OR sp.student.id = :#{#userContextHelper.currentUserId})
        and sp.project.id = :idProject
        """, countQuery = """
        SELECT 
            COUNT(t.id) 
        FROM 
            Todo t
        JOIN Assign tv on t.id = tv.todo.id
        join StaffProject sp on tv.staffProject.id  = sp.id
        WHERE 
            (sp.staff.id = :#{#userContextHelper.currentUserId}
             OR sp.student.id = :#{#userContextHelper.currentUserId})
        and sp.project.id = :idProject
        """)
    Page<MBTodoProjectResponse> getAllTodoByProjectAndStaffProject(Pageable pageable, String idProject, UserContextHelper userContextHelper, @Param("req")MBTodoProjectRequest req);
}
