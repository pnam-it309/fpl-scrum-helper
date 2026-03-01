package udpm.hn.server.core.manage.stage.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.manage.stage.model.response.StageResponse;
import udpm.hn.server.core.manage.stage.model.response.TodoVoteStageResponse;
import udpm.hn.server.entity.StageVote;
import udpm.hn.server.repository.StageVoteRepository;

import java.util.List;
import java.util.Optional;

public interface MAStageVoteRepository extends StageVoteRepository {


    @Query("""
            SELECT s FROM StageVote s
                join PhaseProject pp on s.phaseProject.id = pp.id
                join Project p on pp.project.id = p.id
            WHERE s.status = 0 and ( s.endTime > :time  and s.startTime <= :time) and p.id = :idProject
            """)
    Optional<StageVote> findCustom(@Param("idProject") String idProject, @Param("time") Long time);

    @Query("""
            SELECT   s.startTime as startTime,
                     s.endTime as endTime,
                     pp.name as name,
                     pp.id as id
                     FROM StageVote s  
            JOIN PhaseProject pp ON s.phaseProject.id = pp.id
            JOIN Project p on pp.project.id = p.id
            WHERE s.status = 0 and  p.id =:idProject and (s.endTime > :time and s.startTime <:time)
            """)
    Optional<StageResponse> findByStageTakePlace(@Param("idProject") String idProject, @Param("time") Long time);


    @Query("""
            SELECT   
            row_number() over(order by s.createdDate desc) as orderNumber,
            s.id as idStage,
            s.startTime as startTime,
                     s.endTime as endTime,
                     pp.name as name,
                     pp.id as id
                     FROM StageVote s  
                JOIN PhaseProject pp ON s.phaseProject.id = pp.id
                 JOIN Project p on pp.project.id = p.id
                 where s.status = 0 and p.id= :idProject
                 ORDER BY s.createdDate DESC 
                 """)
    Page<StageResponse> getAllStage(Pageable pageable, @Param("idProject") String idProject);


    @Query("""
                    select  t.name as name ,
                             t.priorityLevel as level
                       from TodoVote tv 
                        join StageVote sv on tv.stageVote.id = sv.id
                        join Todo t on tv.todo.id = t.id
                        where sv.id = :idStageVote and t.status = 0
            """)
    List<TodoVoteStageResponse> findALlTodoVoteStage(@Param("idStageVote") String idStageVote);

    @Query("""
             SELECT s FROM StageVote s
                            join PhaseProject pp on s.phaseProject.id = pp.id
                            join Project p on pp.project.id = p.id
                        WHERE s.status = 0 and p.id = :idProject
            """)
    List<StageVote> findAllByIdProject(@Param("idProject") String idProject);


}
