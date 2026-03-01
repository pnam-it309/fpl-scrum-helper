package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import udpm.hn.server.entity.Project;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.Student;
import udpm.hn.server.utils.UserContextHelper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface StaffProjectRepository extends JpaRepository<StaffProject,String> {

    Optional<StaffProject> findByStaffAndProject(Staff staff, Project project);

    Optional<StaffProject> findByStaff(Staff staff);
    Optional<StaffProject> findByStudent(Student student);
    Optional<StaffProject> findByStudentAndProject(Student student, Project project);


    List<StaffProject> findByProject(Project project);

    @Query(value = """
    select sp from StaffProject sp
    left join Staff s on sp.staff.id = s.id
    left join Student st on sp.student.id = st.id
    where (s.id = :#{#userContextHelper.currentUserId} OR st.id = :#{#userContextHelper.currentUserId})
    order by sp.id desc limit 1
""")
    Optional<StaffProject> getStaffProjectByStaff(UserContextHelper userContextHelper);

    @Query(value = """
    select sp from StaffProject sp
    left join Staff s on sp.staff.id = s.id
    left join Student st on sp.student.id = st.id
    left join Project p on sp.project.id = p.id
    where 
    (sp.staff.id = :#{#userContextHelper.currentUserId}
             OR sp.student.id = :#{#userContextHelper.currentUserId})
    and sp.project.id = :idProject
    order by sp.id desc limit 1
""")
    Optional<StaffProject> getStaffProjectByStaffAndProject(UserContextHelper userContextHelper, String idProject);
}
