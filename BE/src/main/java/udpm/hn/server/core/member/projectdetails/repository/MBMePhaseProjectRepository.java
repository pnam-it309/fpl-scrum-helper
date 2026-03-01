package udpm.hn.server.core.member.projectdetails.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.PhaseProject;
import udpm.hn.server.utils.StatusPhase;

import java.util.List;
import java.util.Optional;

@Repository
public interface MBMePhaseProjectRepository extends JpaRepository<PhaseProject, String> {


    @Query("SELECT p.statusPhase FROM PhaseProject p WHERE p.id = :id")
    Optional<StatusPhase> findStatusPhaseById(@Param("id") String id);


}
