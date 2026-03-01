package udpm.hn.server.core.member.projectdetails.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeFindActivityRequest;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeActivityConvertResponse;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeActivityResponse;
import udpm.hn.server.core.member.projectdetails.repository.MBMeActivityRepository;
import udpm.hn.server.core.member.projectdetails.service.MBMeActivityService;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.Student;
import udpm.hn.server.repository.StaffRepository;
import udpm.hn.server.repository.StudentRepository;
import udpm.hn.server.utils.Helper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MBMeActivityServiceImpl implements MBMeActivityService {

    private final MBMeActivityRepository mbMeActivityRepository;

    private final StaffRepository staffRepository;

    private final StudentRepository studentRepository;

    @Override
    public ResponseObject<?> getAllActivityWhereIdTodo(MBMeFindActivityRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        Page<MBMeActivityResponse> activities = mbMeActivityRepository.getAllActivityWhereIdTodo(pageable, request);
        List<MBMeActivityConvertResponse> result = activities.stream().map(activity -> {
            MBMeActivityConvertResponse dto = new MBMeActivityConvertResponse();
            dto.setMemberCreatedId(activity.getMemberCreatedId());
            dto.setMemberId(activity.getMemberId());
            dto.setProjectId(activity.getProjectId());
            dto.setTodoId(activity.getTodoId());
            dto.setTodoListId(activity.getTodoListId());
            dto.setContentAction(activity.getContentAction());
            dto.setUrlImage(activity.getUrlImage());
            dto.setCreatedDate(activity.getCreatedDate());
            dto.setUrlPath(activity.getUrlPath());
            // Tìm thông tin thành viên
            String memberCreatedId = activity.getMemberCreatedId();
            Staff staff = staffRepository.findById(memberCreatedId).orElse(null);
            if (staff != null) {
                dto.setMemberName(staff.getName());
                dto.setMemberEmail(staff.getEmailFpt());
                dto.setMemberImage(staff.getPicture());
            } else {
                Student student = studentRepository.findById(memberCreatedId).orElse(null);
                if (student != null) {
                    dto.setMemberName(student.getName());
                    dto.setMemberEmail(student.getEmail());
                    dto.setMemberImage(student.getImage());
                }
            }
            return dto;
        }).collect(Collectors.toList());

        return new ResponseObject<>(
                PageableObject.of(new PageImpl<>(result, pageable, activities.getTotalElements())),
                HttpStatus.OK,
                "Get all activity successfully"
        );

    }

    @Override
    public ResponseObject<?> getAllActivityWhereIdProject(MBMeFindActivityRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        Page<MBMeActivityResponse> activities = mbMeActivityRepository.getAllActivityWhereIdProject(pageable, request);
        List<MBMeActivityConvertResponse> result = activities.stream().map(activity -> {
            MBMeActivityConvertResponse dto = new MBMeActivityConvertResponse();
            dto.setMemberCreatedId(activity.getMemberCreatedId());
            dto.setMemberId(activity.getMemberId());
            dto.setProjectId(activity.getProjectId());
            dto.setTodoId(activity.getTodoId());
            dto.setTodoListId(activity.getTodoListId());
            dto.setContentAction(activity.getContentAction());
            dto.setUrlImage(activity.getUrlImage());
            dto.setCreatedDate(activity.getCreatedDate());
            dto.setContentActionProject(activity.getContentActionProject());
            dto.setUrlPath(activity.getUrlPath());

            // Tìm thông tin thành viên
            String memberCreatedId = activity.getMemberCreatedId();
            Staff staff = staffRepository.findById(memberCreatedId).orElse(null);
            if (staff != null) {
                dto.setMemberName(staff.getName());
                dto.setMemberEmail(staff.getEmailFpt());
                dto.setMemberImage(staff.getPicture());
            } else {
                Student student = studentRepository.findById(memberCreatedId).orElse(null);
                if (student != null) {
                    dto.setMemberName(student.getName());
                    dto.setMemberEmail(student.getEmail());
                    dto.setMemberImage(student.getImage());
                }
            }
            return dto;
        }).collect(Collectors.toList());

        return new ResponseObject<>(
                PageableObject.of(new PageImpl<>(result, pageable, activities.getTotalElements())),
                HttpStatus.OK,
                "Get all activity successfully"
        );
    }

    @Override
    public ResponseObject<?> countTotalActivitiesWhereIdProject(MBMeFindActivityRequest request) {
        return new ResponseObject<>( mbMeActivityRepository.countTotalActivitiesWhereIdProject(request),
                HttpStatus.OK,"Lấy thành công tổng hoạt động của du an ");
    }

}
