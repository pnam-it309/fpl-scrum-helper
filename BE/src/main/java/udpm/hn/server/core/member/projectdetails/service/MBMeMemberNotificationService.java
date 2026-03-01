package udpm.hn.server.core.member.projectdetails.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeFindNotificationMemberRequest;

public interface MBMeMemberNotificationService {

    ResponseObject<?> getAllNotificationMember(final MBMeFindNotificationMemberRequest request);

    ResponseObject<?> countNotificationMember(String memberId);

    ResponseObject<?> updateStatus(String idNotificationMember);

    ResponseObject<?> updateAllStatus(String memberId);

}
