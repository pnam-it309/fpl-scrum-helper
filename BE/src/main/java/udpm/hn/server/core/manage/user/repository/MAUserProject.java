package udpm.hn.server.core.manage.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.manage.user.model.request.MAUserRequest;
import udpm.hn.server.core.manage.user.model.response.MAUserProjectResponse;
import udpm.hn.server.core.manage.user.model.response.MAUserResponse;
import udpm.hn.server.repository.StaffProjectRepository;

import java.util.List;
import java.util.Optional;

public interface MAUserProject extends StaffProjectRepository {

    @Query("""
                    select s.id as idStaff,
                    s.code as codeStaff,
                    s.name as nameStaff,
                    s2.id as idStudent,
                    s2.code as codeStudent,
                    s2.name as nameStudent
                    from StaffProject sp 
                    left join Staff s on sp.staff.id = s.id
                    left join Student s2 on sp.student.id = s2.id
                    join Project p on sp.project.id = p.id
                    where sp.status = 0 and p.id = :idProject
            """)
    Page<MAUserProjectResponse> listUserProject(Pageable pageable, @Param("idProject") String idProject);

    @Query("""
                    select s.id as idStaff,
                    s.code as codeStaff,
                    s.name as nameStaff,
                    s2.id as idStudent,
                    s2.code as codeStudent,
                    s2.name as nameStudent,
                    p.code as codeProject,
                    p.name as nameProject,
                    sp.id as idStaffProject
                    from StaffProject sp 
                    left join Staff s on sp.staff.id = s.id
                    left join Student s2 on sp.student.id = s2.id
                    join Project p on sp.project.id = p.id
                    where sp.status = 0 and p.id = :idProject and sp.id = :idStaffProject
            """)
    Optional<MAUserProjectResponse> listUser(@Param("idProject") String idProject, @Param("idStaffProject") String idStaffProject);

    @Query("""
        select
            case
                when s.id is not null then s.name
                else s2.name
            end as name,
            case
                when s.id is not null then s.code
                else s2.code
            end as code,
            cast(sp.role as string) as role,
            case
                when s.id is not null then s.emailFpt
                else s2.email
            end as email,
            (select count(t.id)
             from Assign a
             join Todo t on a.todo.id = t.id
             where a.staffProject.id = sp.id
               and t.statusTodo in (
                   udpm.hn.server.infrastructure.constant.StatusTodo.DA_HOAN_THANH,
                   udpm.hn.server.infrastructure.constant.StatusTodo.HOAN_THANH_SOM,
                   udpm.hn.server.infrastructure.constant.StatusTodo.QUA_HAN
               )
            ) as countTodo,
            (select count(t.id)
             from Assign a
             join PhaseTodoProject ptp on a.todo.id = ptp.todo.id
             join Todo t on a.todo.id = t.id
             where a.staffProject.id = sp.id and ptp.phaseProject.id = :idPhase
             and (t.statusTodo = udpm.hn.server.infrastructure.constant.StatusTodo.DA_HOAN_THANH OR t.statusTodo = udpm.hn.server.infrastructure.constant.StatusTodo.HOAN_THANH_SOM)) as countCompletedTodo,
            (select count(a.id)
             from Assign a
             join PhaseTodoProject ptp on a.todo.id = ptp.todo.id
             where a.staffProject.id = sp.id and ptp.phaseProject.id = :idPhase) as countTodoByPhase,
            p.code as codeProject,
            sp.id as idStaffProject
        from StaffProject sp
        left join Staff s on sp.staff.id = s.id
        left join Student s2 on sp.student.id = s2.id
        join Project p on sp.project.id = p.id
        where sp.status = 0 and p.id = :idProject
          AND (
                :#{#req.search} IS NULL OR
                (
                  (s.id IS NOT NULL AND (s.name LIKE CONCAT('%', :#{#req.search}, '%') OR s.code LIKE CONCAT('%', :#{#req.search}, '%') OR s.emailFpt LIKE CONCAT('%', :#{#req.search}, '%') OR s.emailFe LIKE CONCAT('%', :#{#req.search}, '%')) OR
                  (s2.id IS NOT NULL AND (s2.name LIKE CONCAT('%', :#{#req.search}, '%') OR s2.code LIKE CONCAT('%', :#{#req.search}, '%') OR s2.email LIKE CONCAT('%', :#{#req.search}, '%'))
                )
            )))
            
    """)
    Page<MAUserResponse> getListUser(Pageable pageable,
                                     @Param("idProject") String idProject,
                                     @Param("idPhase") String idPhase,
                                     @Param("req") MAUserRequest req);

}
