package udpm.hn.server.core.manage.todo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.manage.todo.model.response.MATodoResponse;
import udpm.hn.server.core.manage.todo.model.response.MATodoStatistics;
import udpm.hn.server.infrastructure.constant.PriorityLevel;
import udpm.hn.server.repository.ToDoRepository;

import java.util.List;

public interface MATodoRepository extends ToDoRepository {

    @Query(value = """
            select 
                td.name as name,
                td.code as code,
                td.createdDate as date,
                td.priorityLevel as priorityLevel,
                td.id as id,
                sf.id as idStaffProject,
                s.id as idStaff,
                s.name as nameStaff,
                s.picture as imageStaff,
                st.id as idStudent,
                st.name as nameStudent,
                st.image as imageStudent,
                td.progress as progress,
                tt.id as idType,
                tt.type as type,
                td.storyPoint as point
            from Todo td
                 left join TodoList tl on td.todoList.id = tl.id
                 left join Project p on tl.project.id = p.id
                 left join TodoVote tv on td.id = tv.todo.id
                 left join StaffProject sf on tv.staffProject.id = sf.id
                 left join TypeTodo tt on td.typeTodo.id = tt.id
                 left join  Staff s on s.id = sf.staff.id
                 left join PhaseTodoProject ptp on td.id = ptp.todo.id
                 left join Student st on st.id = sf.student.id
                 where 
                 td.status = 0
                 and ptp.todo.id is null
                 and p.id = :idProject
                 and (:level IS NULL OR td.priorityLevel = :level)
                 and (:search IS NULL OR td.name LIKE CONCAT('%', TRIM(:search), '%') OR td.code LIKE CONCAT('%', TRIM(:search), '%'))
                 order by td.createdDate DESC 

            """)
    Page<MATodoResponse> getAllTodo(Pageable pageable, @Param("idProject") String idProject, @Param("level") PriorityLevel level, @Param("search") String search);


    @Query(value = """
            select 
                td.name as name,
                td.code as code,
                td.createdDate as date,
                td.priorityLevel as priorityLevel,
                td.id as id,
                td.statusTodo as statusTodo,
                sf.id as idStaffProject,
                s.id as idStaff,
                s.name as nameStaff,
                s.picture as imageStaff,
                st.id as idStudent,
                st.name as nameStudent,
                st.image as imageStudent,
                td.progress as progress,
                ptp.id as idPhaseTodo
            from Todo td
                 left join TodoList tl on td.todoList.id = tl.id
                 left join Project p on tl.project.id = p.id
                 left join TodoVote tv on td.id = tv.todo.id
                 left join StaffProject sf on tv.staffProject.id = sf.id
                 left join  Staff s on s.id = sf.staff.id
                 left join PhaseTodoProject ptp on td.id = ptp.todo.id
                 left join Student st on st.id = sf.student.id
                 where 
                 td.status = 0
                 and p.id = :idProject
               
            """)
    List<MATodoResponse> findAllTodoByProject(@Param("idProject") String idProject);


    @Query("""
                    select td.name as name,
                    td.code as code,
                    td.createdDate as date,
                    td.priorityLevel as priorityLevel,
                    td.id as id,
                    sf.id as idStaffProject,
                    s.id as idStaff,
                    s.name as nameStaff,
                    s.picture as imageStaff,
                    st.id as idStudent,
                    st.name as nameStudent,
                    st.image as imageStudent
                 from Todo td
                 join TodoVote tv on td.id = tv.todo.id
                 left join StaffProject sf on tv.staffProject.id = sf.id
                 left join  Staff s on s.id = sf.staff.id
                 left join Student st on st.id = sf.student.id
                 left join StageVote sv on tv.stageVote.id = sv.id
                 where td.status = 0   and sv.status  = 0  and td.id = :idTodo and 
                 (:currentTime between sv.startTime and sv.endTime)
            """)
    Page<MATodoResponse> getAllStaffByTodo(Pageable pageable, @Param("idTodo") String idTodo, @Param("currentTime") long currentTime);


    @Query("""
                    select td.name as name,
                    td.code as code,
                    td.createdDate as date,
                    td.priorityLevel as priorityLevel,
                    td.id as id,
                    sf.id as idStaffProject,
                    s.id as idStaff,
                    s.name as nameStaff,
                    s.picture as imageStaff,
                    st.id as idStudent,
                    td.deadline as deadline,
                    st.name as nameStudent,
                    st.image as imageStudent,
                    td.statusTodo as statusTodo,
                    tt.type as type,
                    td.storyPoint as point,
                    round(td.progress,0) as progress
                 from Todo td
                 left join TodoVote tv on td.id = tv.todo.id
                 left join StaffProject sf on tv.staffProject.id = sf.id
                 left join  Staff s on s.id = sf.staff.id
                 left join Student st on st.id = sf.student.id
                 left join TypeTodo tt on td.typeTodo.id = tt.id
                 join PhaseTodoProject ptp on td.id = ptp.todo.id
                 join PhaseProject pp on ptp.phaseProject.id = pp.id
                 where td.status = 0 and pp.id = :idPhase 
                    AND (:search IS NULL OR td.name LIKE CONCAT('%', :search, '%') OR td.code LIKE CONCAT('%', :search, '%')) 
                 
            """)
    Page<MATodoResponse> getAllTodoByPhaseProject(Pageable pageable, @Param("idPhase") String idPhase, @Param("search") String search);


    @Query("""
                    SELECT 
                        t.name as name,
                        t.code as code,
                        t.statusTodo as status,
                        t.priorityLevel as level
                    from Todo t 
                    left join TodoList tl on t.todoList.id = tl.id
                    left join Project p on tl.project.id = p.id 
                    left join PhaseProject pp on tl.phaseProject.id = pp.id
                    where t.status = 0 and p.id = :idProject 
                    and (:idPhase IS NULL OR pp.id = :idPhase)
            """)
    List<MATodoStatistics> AllTodoStatistics(@Param("idProject") String idProject, @Param("idPhase") String idPhase);
}
