package udpm.hn.server.core.member.projectdetails.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeFindNotificationMemberRequest;
import udpm.hn.server.core.member.projectdetails.repository.MBMeNotificationMemberRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeNotificationRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeStaffProjectRepository;
import udpm.hn.server.core.member.projectdetails.service.MBMeMemberNotificationService;
import udpm.hn.server.entity.MemberNotification;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.utils.Helper;

import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MBMeMemberNotificationServiceImpl implements MBMeMemberNotificationService {

    private final MBMeNotificationRepository mbMeNotificationRepository;

    private final MBMeNotificationMemberRepository mbMeNotificationMemberRepository;

    private final MBMeStaffProjectRepository mbMeStaffProjectRepository;

    @Override
    public ResponseObject<?> getAllNotificationMember(MBMeFindNotificationMemberRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(mbMeNotificationMemberRepository.getAllNotificationMember(pageable,request.getIdMember())),
                HttpStatus.OK,
                "Get all notification successfully "
        );
    }

    @Override
    public ResponseObject<?> countNotificationMember(String memberId) {
        return null;
    }

    @Override
    public ResponseObject<?> updateStatus(String idNotificationMember) {
        Optional<MemberNotification> notificationMemberFind = mbMeNotificationMemberRepository.findById(idNotificationMember);
        if (!notificationMemberFind.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Notification member not found");
        }
        notificationMemberFind.get().setStatus(EntityStatus.INACTIVE);
        mbMeNotificationMemberRepository.save(notificationMemberFind.get());
        return null;
    }

    @Override
    public ResponseObject<?> updateAllStatus(String memberId) {
        return null;
    }
}
