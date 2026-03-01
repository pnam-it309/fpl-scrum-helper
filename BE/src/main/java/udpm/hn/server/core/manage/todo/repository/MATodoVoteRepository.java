package udpm.hn.server.core.manage.todo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.manage.todo.model.response.MATodoVoteResponse;
import udpm.hn.server.core.manage.todo.model.response.TodoVoteLevelStatistics;
import udpm.hn.server.core.manage.todo.model.response.WorkVoteResponse;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.entity.TodoVote;
import udpm.hn.server.repository.TodoVoteRepository;

import java.util.List;
import java.util.Optional;

public interface MATodoVoteRepository extends TodoVoteRepository {

    @Query("""
    select 
        tv.todo.name as nameTodo,
        tv.staffProject.student.name as nameStudent,
        tv.staffProject.staff.name as  nameStaff,
        tv.id as id 
    from TodoVote tv
""")
    List<MATodoVoteResponse>  getAllVote();

    @Query(value = """
       SELECT
            td.priority_Level as level,
            COUNT(DISTINCT td.id) as total
        FROM Todo td
        JOIN Todo_Vote tv ON td.id = tv.id_todo
        JOIN stage_vote sv on sv.id = tv.id_stage_vote
        JOIN Todo_List tl ON td.id_todo_list = tl.id
        JOIN Project p ON tl.id_project = p.id
        WHERE td.status = 0
        AND p.id = :idProject
        AND (:time between sv.start_time and sv.end_time )
        GROUP BY td.priority_Level
""", nativeQuery = true)
    List<TodoVoteLevelStatistics> countTodoVotedByPriorityLevel(@Param("idProject") String idProject, @Param("time") Long time);


    Optional<TodoVote> findByTodoAndStaffProjectAndStageVoteId(Todo todo, StaffProject staffProject, String stageVote);

    @Query(value = """
    select
        td.name as name,
        coalesce(st.name, s.name) as memberName,
        coalesce(st.image, s.picture) as memberImage,
        td.priority_Level as level
    from
        Todo td
    join Todo_Vote tv on
        td.id = tv.id_todo
    join stage_vote sv on
        tv.id_stage_vote = sv.id
    left join Staff_Project sf on
        tv.id_staff_Project = sf.id
    left join Student st on
        st.id = sf.id_student
    left join Staff s on
        s.id = sf.id_staff
    WHERE td.status = 0 
      AND td.priority_Level IS NOT NULL
      AND sf.id_project = :idProject
""", nativeQuery = true)
    List<WorkVoteResponse> findAllVotedTodos(@Param("idProject") String idProject);


}
