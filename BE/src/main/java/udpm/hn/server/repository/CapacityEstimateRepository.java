package udpm.hn.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.member.capacityestimate.model.response.capacityEstimateResponse;
import udpm.hn.server.entity.CapacityEstimate;

import java.util.List;
import java.util.Optional;

public interface CapacityEstimateRepository extends JpaRepository<CapacityEstimate, String> {

    @Query(value = """
            select
             ROW_NUMBER() OVER (ORDER BY ce.created_date) as orderNumber,
                ce.id as id,
                ce.workday as workday,
                ce.adjusted_story_points,
                coalesce(s.name, s2.name) as name,
                coalesce(s.id, s2.id) as idUser,
                pp.name as sprint,
                pp.id as idPhase,
                ce.describe as descriptions,
                pp.start_time as startTime,
                pp.end_time as endTime
            from
                capacity_estimate ce
            join staff_project sp on
                ce.id_staff_project = sp.id
            join phase_project pp on 
                pp.id = ce.id_phase_project
            left join student s on
                sp.id_student = s.id
            left join staff s2 on
                sp.id_staff = s2.id
            join project p on p.id  = sp.id_project 
            where p.id = :idProject and sp.id = :idStaffProject
            order by ce.created_date
              """, nativeQuery = true)
    Page<capacityEstimateResponse> getAll(Pageable pageable, @Param("idProject") String idProject, @Param("idStaffProject") String idStaffProject);

    @Query(value = """
                select 
                       ce.id                     as id,
                       ce.adjusted_story_points,
                       ce.workday                as workday,
                       ce.describe               as description,
                       coalesce(s.name, s2.name) as name,
                       coalesce(s.id, s2.id)     as idUser,
                       pp.name                   as sprint,
                       pp.id                     as idPhase,
                       pp.start_time             as startTime,
                       pp.end_time               as endTime
                from capacity_estimate ce
                         join phase_project pp on ce.id_phase_project = pp.id
                         join staff_project sp on ce.id_staff_project = sp.id
                         left join student s on
                    sp.id_student = s.id
                         left join staff s2 on
                    sp.id_staff = s2.id
                where ce.id = :idCapacityEstimate
            """, nativeQuery = true)
    Optional<capacityEstimateResponse> detailCapacityEstimate(@Param("idCapacityEstimate") String idCapacityEstimate);


    @Query(value = """
            select
             ROW_NUMBER() OVER (ORDER BY ce.created_date) as orderNumber,
                ce.id as id,
                ce.workday as workday,
                ce.describe as descriptions,
                ce.adjusted_story_points,
                coalesce(s.name, s2.name) as name,
                coalesce(s.id, s2.id) as idUser,
                pp.name as sprint,
                pp.id as idPhase,
                pp.start_time as startTime,
                pp.end_time as endTime
            from
                capacity_estimate ce
            join staff_project sp on
                ce.id_staff_project = sp.id
            join phase_project pp on 
                pp.id = ce.id_phase_project
            left join student s on
                sp.id_student = s.id
            left join staff s2 on
                sp.id_staff = s2.id
            join project p on p.id  = sp.id_project 
            where p.id = :idProject 
            order by ce.created_date
              """, nativeQuery = true)
    List<capacityEstimateResponse> capacityEstimateBySprint( @Param("idProject") String idProject);




}
