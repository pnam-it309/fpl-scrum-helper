package udpm.hn.server.core.manage.todo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.manage.todo.model.response.MACountTodoByStaffProject;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.repository.StaffProjectRepository;

import java.util.List;

public interface MAStaffProjectRepository extends StaffProjectRepository {

    @Query("""
                    select st from StaffProject st 
                    join Project p on st.project.id = p.id
                    where p.id  = :idProject and st.status = 0
            """)
    List<StaffProject> getAllUserByProject(@Param("idProject") String idProject);


    @Query("""
                    select 
                        s.name as nameStaff,
                        s2.name as nameStudent,
                        s.picture as imageStaff,
                        s2.image as imageStudent,
                        count(t.name) as countTodo
                        from StaffProject sp 
                    left join Staff s on sp.staff.id = s.id
                    left join Student s2 on sp.student.id = s2.id
                    join TodoVote  tv on sp.id = tv.staffProject.id 
                    join Todo t on tv.todo.id = t.id
                    join Project p on sp.project.id = p.id
                    join TodoList tl on t.todoList.id = tl.id
                    join PhaseProject pp on tl.phaseProject.id = pp.id
                    where  p.id like :idProject
                     and (:idPhase IS NULL OR pp.id like :idPhase)
                    group by s.name, s2.name, s.picture,s2.image
            """)
    List<MACountTodoByStaffProject> countTodoByStaffProject(@Param("idProject") String idProject, @Param("idPhase") String idPhase);
}
