package udpm.hn.server.core.member.projectdetails.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeFindActivityRequest;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeActivityResponse;
import udpm.hn.server.entity.Activity;
import udpm.hn.server.repository.ActivityRepository;

public interface MBMeActivityRepository extends ActivityRepository {

    @Query("""
    SELECT a.id AS id, a.memberCreatedId AS memberCreatedId, 
           a.memberId AS memberId, a.projectId AS projectId, 
           a.todoId AS todoId, a.todoListId AS todoListId, 
           a.contentAction AS contentAction, a.urlImage AS urlImage, 
           a.createdDate AS createdDate, 
           a.url AS urlPath
    FROM Activity a 
    WHERE a.todoId = :#{#req.idTodo} 
    ORDER BY a.createdDate DESC
""")
    Page<MBMeActivityResponse> getAllActivityWhereIdTodo(Pageable pageable, @Param("req") MBMeFindActivityRequest req);

    @Query("""
    SELECT a.id AS id, a.memberCreatedId AS memberCreatedId, 
           a.memberId AS memberId, a.projectId AS projectId, 
           a.todoId AS todoId, a.todoListId AS todoListId, 
           a.contentAction AS contentAction, 
           a.urlImage AS urlImage, 
           a.contentActionProject AS contentActionProject,
           a.createdDate AS createdDate,
           a.url AS urlPath
    FROM Activity a 
    WHERE a.projectId = :#{#req.idProject} 
    ORDER BY a.createdDate DESC
""")
    Page<MBMeActivityResponse> getAllActivityWhereIdProject(Pageable pageable, @Param("req") MBMeFindActivityRequest req);

    @Query("""
    SELECT COUNT(a) 
    FROM Activity a 
    WHERE a.projectId = :#{#req.idProject}
""")
    Integer countTotalActivitiesWhereIdProject(@Param("req") MBMeFindActivityRequest req);


    @Query("""
    SELECT a FROM Activity a
    WHERE a.imageId = :idImage
""")
    Activity findActivityByIdImage(@Param("idImage") String idImage);

    @Query("""
    SELECT a FROM Activity a
    WHERE a.todoId = :idTodo AND a.contentAction LIKE :action
""")
    Activity findActivityByAction(@Param("idTodo") String idTodo, @Param("action") String action);

}
