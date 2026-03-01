package udpm.hn.server.core.member.projectdetails.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeLabelResponse;
import udpm.hn.server.repository.LabelProjectRepository;

import java.util.List;

public interface MBMeLabelRepository extends LabelProjectRepository {

    @Query("""
    SELECT c.id AS id, c.code AS code, c.name AS name, c.color AS colorLabel
    FROM LabelProjectTodo b
    JOIN b.todo a
    JOIN b.labelProject c
    WHERE a.id = :idTodo
    AND b.status = 0
    AND c.status= 0
    """)
    List<MBMeLabelResponse> getAllLabelByIdTodo(@Param("idTodo") String idTodo);

    @Query("""
    SELECT 
        a.id AS id, 
        a.code AS code, 
        a.name AS name, 
        a.color AS colorLabel,
        CASE WHEN lpt.id IS NOT NULL THEN true ELSE false END AS isChecked
    FROM LabelProject a
    LEFT JOIN LabelProjectTodo lpt 
        ON lpt.labelProject.id = a.id AND lpt.todo.id = :idTodo
    WHERE a.project.id = :idProject
      AND a.status = 0
""")
    List<MBMeLabelResponse> getAllByIdProject(@Param("idProject") String idProject, @Param("idTodo") String idTodo);

    String id(String id);

    @Query("""
    SELECT 
        a.id AS id, 
        a.code AS code, 
        a.name AS name, 
        a.color AS colorLabel
    FROM LabelProject a
    WHERE a.project.id = :idProject
      AND a.status = 0
""")
    List<MBMeLabelResponse> getAllLabelSearchByIdProject(@Param("idProject") String idProject);

}
