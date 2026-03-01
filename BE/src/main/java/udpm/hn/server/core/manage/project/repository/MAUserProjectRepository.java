package udpm.hn.server.core.manage.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.manage.project.model.response.MAUserProjectResponse;
import udpm.hn.server.repository.StaffProjectRepository;

public interface MAUserProjectRepository extends StaffProjectRepository {

    @Query("""
        select s.name as staffName,
        s.code as codeStaff,
        s.id as idStaff,
        s2.name as studentName,
        s2.code as codeStudent,
        s2.email as emailStudent,
        s2.id as idStudent,
        s.emailFpt as emailStaff
         from StaffProject sp 
        left join Project p on sp.project.id = p.id
        left join Staff s on sp.staff.id = s.id
        left join Student s2 on sp.student.id = s2.id 
        where p.id = :idProject
""")
    Page<MAUserProjectResponse> getAllUserByProject(Pageable pageable, @Param("idProject") String idProject);




    @Query(value = """
        SELECT count(1)
        FROM StaffProject sp
        where sp.project.id = :idProject
        and sp.student is null 
        """)
    Integer getAmountStaff(@Param("idProject") String idProject);

    @Query(value = """
        SELECT count(1)
        FROM StaffProject sp
        where sp.project.id = :idProject
        and sp.staff is null 
        """)
    Integer getAmountStudent(@Param("idProject") String idProject);
}
