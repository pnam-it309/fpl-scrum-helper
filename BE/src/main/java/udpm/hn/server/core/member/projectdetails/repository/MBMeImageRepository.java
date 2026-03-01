package udpm.hn.server.core.member.projectdetails.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.member.projectdetails.model.response.MBBackgroundProjectResponse;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeImageResponse;
import udpm.hn.server.repository.ImageRepository;

import java.util.List;

public interface MBMeImageRepository extends ImageRepository {

    @Query("SELECT COUNT(i) FROM Image i WHERE i.todo.id = :idTodo")
    Integer countImageByIdTodo(@Param("idTodo") String idTodo);


    @Query("SELECT i.id AS id, i.urlImage AS urlImage, i.name AS nameImage, " +
            "i.statusImage AS statusImage, i.todo.id AS todoId, i.createdDate AS createdDate " +
            "FROM Image i WHERE i.todo.id = :idTodo ORDER BY i.createdDate DESC")
    List<MBMeImageResponse> getAllByIdTodo(@Param("idTodo") String idTodo);

    @Modifying
    @Transactional
    @Query("""
       UPDATE Image i 
       SET i.statusImage = 1 
       WHERE i.statusImage = 0 AND i.todo.id = :idTodo
       """)
    void updateCoverOld(@Param("idTodo") String idTodo);

    @Query("""
select p.backgroundImage as backgroundImage , 
p.backgroundColor as  backgroundColor 
from 
Project p where p.id = :idProject
""")
    MBBackgroundProjectResponse getBackgroundByIdProject(@Param("idProject") String idProject);
}
