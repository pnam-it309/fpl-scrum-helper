package udpm.hn.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.HistoryImportStudent;

@Repository
public interface HistoryImportStudentRepository extends JpaRepository<HistoryImportStudent, String> {

    @Query("""
        SELECT h FROM HistoryImportStudent h
        WHERE h.email = :email
    """)
    Page<HistoryImportStudent> findByEmail(@Param("email") String email, Pageable pageable);
}
