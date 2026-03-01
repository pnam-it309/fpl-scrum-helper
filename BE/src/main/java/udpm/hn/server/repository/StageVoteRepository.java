package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import udpm.hn.server.entity.StageVote;

public interface StageVoteRepository extends JpaRepository<StageVote,String> {
}
