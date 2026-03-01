package udpm.hn.server.core.manage.phase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.manage.phase.model.response.MAPhaseResponse;
import udpm.hn.server.entity.PhaseProject;
import udpm.hn.server.repository.PhaseRepository;
import udpm.hn.server.utils.StatusPhase;

import java.util.List;
import java.util.Optional;

public interface MAPhaseRepository extends PhaseRepository {

    @Query("""
                    select 
                        pp.name as name,
                        pp.id as id,
                        pp.code as code,
                        pp.createdDate as createDate,
                        pp.startTime as startTime,
                        pp.endTime as endTime,
                        pp.descriptions as descriptions,
                        pp.statusPhase as statusPhase
                     from PhaseProject  pp 
                     join Project p on pp.project.id = p.id
                     where pp.status = 0 and   (pp.statusPhase = 0 OR pp.statusPhase = 1)  and p.id = :idProject
                     order by pp.createdDate ASC 
            """)
    Page<MAPhaseResponse> getAllPhaseProject(Pageable pageable, @Param("idProject") String idProject);



    @Query("""
                    select 
                        pp.name as name,
                        pp.id as id,
                        pp.code as code,
                        pp.createdDate as createDate,
                        pp.startTime as startTime,
                        pp.endTime as endTime,
                        pp.descriptions as descriptions,
                        pp.statusPhase as statusPhase
                     from PhaseProject  pp 
                     join Project p on pp.project.id = p.id
                     where pp.status = 0  and p.id = :idProject
            """)
    List<MAPhaseResponse> getAllPhaseProjectStatistics(@Param("idProject") String idProject);


    @Query("""
    select 
        pp.name as name,
        pp.id as id,
        pp.code as code,
        pp.createdDate as createDate,
        pp.startTime as startTime,
        pp.endTime as endTime,
        pp.descriptions as descriptions,
        pp.statusPhase as statusPhase
    from PhaseProject pp 
    join Project p on pp.project.id = p.id
    where pp.status = 0 and pp.statusPhase = 2 and p.id = :idProject 
    and (:statusPhase IS NULL OR pp.statusPhase = :statusPhase)
AND (:startOfDay IS NULL OR pp.startTime <= :endOfDay)
AND (:endOfDay IS NULL OR pp.endTime >= :startOfDay)
    AND (:search IS NULL OR pp.name LIKE CONCAT('%', :search, '%'))
""")
    Page<MAPhaseResponse> getAllSprintProject(Pageable pageable,
                                              @Param("idProject") String idProject,
                                              @Param("statusPhase") StatusPhase statusPhase,
                                              @Param("startOfDay") Long startOfDay,
                                              @Param("endOfDay") Long endOfDay,
                                              @Param("search") String search);


    @Query("""
                    select 
                        pp.name as name,
                        pp.id as id,
                        pp.code as code,
                        pp.descriptions as descriptions,
                        pp.statusPhase as statusPhase
                     from PhaseProject  pp 
                     join Project p on pp.project.id = p.id
                     where pp.status = 0 and p.id = :idProject
            """)
    List<MAPhaseResponse> getAllPhaseByProjectId(String idProject);

    @Query("""
           SELECT count(pp.id)
           FROM PhaseProject pp 
           where pp.project.id = :idProject
                and pp.statusPhase = :statusPhase
                and pp.status = 0
           """)
    int countPhaseProjectByIdProject(@Param("idProject") String idProject, @Param("statusPhase") StatusPhase statusPhase);

    Optional<PhaseProject> findByStatusPhase(StatusPhase statusPhase);


}
