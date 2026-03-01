package udpm.hn.server.core.member.projectdetails.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeFindCommentRequest;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeCommentResponse;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeUserInProjectResponse;
import udpm.hn.server.repository.CommentRepository;

import java.util.List;

public interface MBMeCommentRepository extends CommentRepository {

    @Query("""
    SELECT c.content AS content,
           c.id AS id,
           CAST(c.todo.id AS string) AS todoId,
           CAST(c.staffProject.project.id AS string) AS projectId,
           CAST(c.staffProject.id AS string) AS staffProjectId,
           c.statusEdit AS statusEdit,
           c.createdDate AS createdDate,

           COALESCE(CAST(sp.staff.id AS string), CAST(sp.student.id AS string)) AS userId,
           COALESCE(sp.staff.code, sp.student.code) AS userCode,
           COALESCE(sp.staff.name, sp.student.name) AS userName,
           COALESCE(sp.staff.emailFpt, sp.student.email) AS userEmail,
           COALESCE(sp.staff.picture, sp.student.image) AS userImage

    FROM Comment c
    LEFT JOIN c.staffProject sp
    LEFT JOIN sp.staff s
    LEFT JOIN sp.student st
    WHERE c.todo.id = :#{#req.idTodo}
    AND c.status = 0
    ORDER BY c.createdDate DESC
   
""")
    Page<MBMeCommentResponse> getAllCommentByIdTodo(Pageable page, @Param("req") MBMeFindCommentRequest req);


    @Query("""
    SELECT 
        COALESCE(CAST(s.id AS string), CAST(st.id AS string)) AS userId,
        COALESCE(s.code, st.code) AS userCode,
        COALESCE(s.name, st.name) AS userName,
        COALESCE(s.emailFpt, st.email) AS userEmail,
        COALESCE(s.picture, st.image) AS userImage
    FROM StaffProject sp
    LEFT JOIN sp.staff s
    LEFT JOIN sp.student st
    WHERE sp.project.id = :idProject
    AND sp.status = 0
    AND (s.status = 0 OR st.status =0)
    AND (s.emailFpt != :email OR st.email != :email)
""")
    List<MBMeUserInProjectResponse> getUsersByProject(@Param("idProject") String idProject,@Param("email") String email);


}
