package udpm.hn.server.core.member.projectdetails.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeNotificationMemberResponse;
import udpm.hn.server.repository.MemberNotificationRepository;

public interface MBMeNotificationMemberRepository extends MemberNotificationRepository {

    @Query("""
    SELECT mn.id AS id, 
           n.id AS notificationId, 
           n.name AS content, 
           n.todo.id AS todoId, 
           n.url AS url,
           mn.status AS status,
           mn.createdDate AS createdDate
    FROM MemberNotification mn
    JOIN mn.notification n
    WHERE mn.memberId = :memberId
    ORDER BY mn.createdDate DESC
""")
    Page<MBMeNotificationMemberResponse> getAllNotificationMember(
            Pageable pageable,
            @Param("memberId") String memberId
    );

}
