package udpm.hn.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.HistoryImportReportCompensation;

@Repository
public interface HistoryImportReportCompensationRepository extends JpaRepository<HistoryImportReportCompensation, String> {

    @Query("""
        SELECT h FROM HistoryImportReportCompensation h
        WHERE h.email = :email
    """)
    Page<HistoryImportReportCompensation> findByEmail(@Param("email") String email, Pageable pageable);

}
