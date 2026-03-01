package udpm.hn.server.core.admin.project.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.admin.project.model.response.ADProjectStaffStudentResponse;
import udpm.hn.server.entity.Student;
import udpm.hn.server.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

public interface ADProjectStudentRepository extends StudentRepository {


    @Query("""
                    select
                        s.id as id,
                        s.name as name,
                        s.code as code,
                        s.email as email,
                        s.image as image 
                    from Student s
                    join MajorFacility mf on s.majorFacility.id = mf.id
                    where mf.id = :idMajorFacility
                    and s.status = 0 
            """)

    List<ADProjectStaffStudentResponse> getAllStudent(@Param("idMajorFacility") String idMajorFacility);

    Optional<Student> findByEmail(String email);
    @Query("""
            select 
                s.id as id, 
                s.name as name,
                s.code as code,
                s.email as email,
                s.image as image,
                sp.role as role
            from StaffProject sp 
            join Student s on sp.student.id = s.id
            join Project p on sp.project.id = p.id
            where p.id = :idProject
            and sp.status = 0
        """)
    List<ADProjectStaffStudentResponse> getAllStudentByProject (@Param("idProject") String idProject);

}
