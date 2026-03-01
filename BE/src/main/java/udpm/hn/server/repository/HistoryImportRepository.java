package udpm.hn.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.HistoryImport;
import udpm.hn.server.infrastructure.constant.LogFileType;

import java.util.List;

@Repository
public interface HistoryImportRepository extends JpaRepository<HistoryImport, String> {

    @Query("""
        SELECT h FROM HistoryImport h
        WHERE h.email = :email
    """)
    Page<HistoryImport> findByEmail(@Param("email") String email, Pageable pageable);

}
