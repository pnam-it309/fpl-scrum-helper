package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.MemberNotification;

@Repository
public interface MemberNotificationRepository extends JpaRepository<MemberNotification,String> {
}
