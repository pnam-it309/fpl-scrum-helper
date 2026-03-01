package udpm.hn.server.core.manage.stage.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.phase.repository.MAPhaseRepository;
import udpm.hn.server.core.manage.stage.model.request.StageRequest;
import udpm.hn.server.core.manage.stage.model.request.StageVoteRequest;
import udpm.hn.server.core.manage.stage.model.response.StageResponse;
import udpm.hn.server.core.manage.stage.repository.MAStageVoteRepository;
import udpm.hn.server.core.manage.stage.service.MAStageVoteService;
import udpm.hn.server.core.manage.todo.repository.MAStaffProjectRepository;
import udpm.hn.server.entity.PhaseProject;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.StageVote;
import udpm.hn.server.infrastructure.configemail.EmailSender;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.utils.Helper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class MAStageVoteServiceImpl implements MAStageVoteService {

    private final MAStageVoteRepository maStageVoteRepository;

    private final MAPhaseRepository maPhaseRepository;

    private final MAStaffProjectRepository staffProjectRepository;

    private final EmailSender emailService;
    @Override
    public ResponseObject<?> createUpdate(StageRequest request) {
        log.info("request tạo giai đoạn bình chọn:{}",request);

        StageVote stageVote;

        // Check phaseProject tồn tại
        Optional<PhaseProject> phaseProject = maPhaseRepository.findById(request.getIdPhase());
        if (phaseProject.isEmpty()) {
            return new ResponseObject<>().error(HttpStatus.NOT_FOUND, "Không tìm thấy giai đoạn");
        }

        // Kiểm tra dữ liệu đầu vào
        if (request.getStartTime() == null || request.getEndTime() == null) {
            return new ResponseObject<>().error(HttpStatus.BAD_REQUEST, "Thời gian bắt đầu và kết thúc không được để trống.");
        }

        long now = System.currentTimeMillis();

        if (request.getStartTime() >= request.getEndTime()) {
            return new ResponseObject<>().error(HttpStatus.BAD_REQUEST, "Thời gian bắt đầu phải nhỏ hơn thời gian kết thúc.");
        }

        // Kiểm tra trùng lặp thời gian với các cuộc bầu chọn khác trong cùng project
        List<StageVote> existingVotes = maStageVoteRepository.findAllByIdProject(request.getIdProject());
        for (StageVote vote : existingVotes) {
            // Bỏ qua chính nó khi đang cập nhật
            if (request.getIdStage() != null && request.getIdStage().equals(vote.getId())) {
                continue;
            }

            long existingStart = vote.getStartTime();
            long existingEnd = vote.getEndTime();

            boolean isOverlapping = request.getStartTime() < existingEnd && request.getEndTime() > existingStart;
            if (isOverlapping) {
                return new ResponseObject<>().error(HttpStatus.BAD_REQUEST,
                        "Thời gian bình chọn bị trùng với một cuộc bình chọn đã tồn tại.");
            }
        }

        // Tạo mới
        if (request.getIdStage() == null || request.getIdStage().isEmpty()) {

            if (request.getStartTime() < now || request.getEndTime() < now) {
                return new ResponseObject<>().error(HttpStatus.BAD_REQUEST, "Không thể chọn thời gian trong quá khứ.");
            }
            stageVote = StageVote.builder()
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .phaseProject(phaseProject.get())
                    .build();
            maStageVoteRepository.save(stageVote);
//            sendEmailToProjectMembers( request.getIdProject(),stageVote, true);
            return new ResponseObject<>().success("Tạo thành công");
        }

        // Cập nhật
        Optional<StageVote> optionalStageVote = maStageVoteRepository.findById(request.getIdStage());
        if (optionalStageVote.isEmpty()) {
            return new ResponseObject<>().error(HttpStatus.NOT_FOUND, "Không tìm thấy stageVote");
        }

        stageVote = optionalStageVote.get();
        stageVote.setStartTime(request.getStartTime());
        stageVote.setEndTime(request.getEndTime());
        stageVote.setPhaseProject(phaseProject.get());
        maStageVoteRepository.save(stageVote);
//        sendEmailToProjectMembers( request.getIdProject(),stageVote, false);
        return new ResponseObject<>().success("Cập nhật thành công");
    }

    public void sendEmailToProjectMembers(String projectId, StageVote stageVote, boolean isCreated) {
        List<StaffProject> staffProjects = staffProjectRepository.getAllUserByProject(projectId);
        List<String> emails = new ArrayList<>();

        for (StaffProject sp : staffProjects) {
            if (sp.getStudent() != null && sp.getStudent().getEmail() != null) {
                emails.add(sp.getStudent().getEmail());
            } else if (sp.getStaff() != null) {
                if (sp.getStaff().getEmailFpt() != null) {
                    emails.add(sp.getStaff().getEmailFpt());
                } else if (sp.getStaff().getEmailFe() != null) {
                    emails.add(sp.getStaff().getEmailFe());
                }
            }
        }
        for (String email : emails) {
            System.out.println("Danh sách email: "+email);
        }

        if (emails.isEmpty()) {
            log.warn("Không tìm thấy email nào trong dự án {}", projectId);
            return;
        }

        // Chuẩn bị nội dung email
        String subject = isCreated ? "Thông báo tạo mới giai đoạn bình chọn" : "Thông báo cập nhật giai đoạn bình chọn";
        String title = isCreated ? "Bạn được mời tham gia một cuộc bình chọn mới!" : "Thông tin cuộc bình chọn đã được cập nhật.";

        String timeStart = Instant.ofEpochMilli(stageVote.getStartTime())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        String timeEnd = Instant.ofEpochMilli(stageVote.getEndTime())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        String content = String.format("""
            <p>Thời gian bình chọn:</p>
            <ul>
                <li>Bắt đầu: <strong>%s</strong></li>
                <li>Kết thúc: <strong>%s</strong></li>
            </ul>
            <p>Vui lòng đăng nhập để thực hiện bình chọn trong thời gian quy định.</p>
            """, timeStart, timeEnd);

        // Chia batch và gửi email
        int batchSize = 50;
        for (int i = 0; i < emails.size(); i += batchSize) {
            List<String> batch = emails.subList(i, Math.min(i + batchSize, emails.size()));
            emailService.sendEmail(batch.toArray(new String[0]), subject, title, content);

            try {
                Thread.sleep(1000); // nghỉ 1 giây giữa các batch nếu cần thiết
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        log.info("Đã gửi email đến {} người trong dự án {}", emails.size(), projectId);
    }
    @Override
    public ResponseObject<?> detailStageVote(String idStage) {
        return new ResponseObject<>(maStageVoteRepository.findById(idStage), HttpStatus.OK,"Lấy dữ liệu thành công");
    }

    @Override
    public ResponseObject<?> deleteStage(String idStage) {

        Optional<StageVote> optionalStageVote = maStageVoteRepository.findById(idStage);
        if(optionalStageVote.isEmpty()){
            return new ResponseObject<>().error(HttpStatus.NOT_FOUND,"Không tìm thấy cuộc bình chọn");
        }
        StageVote stageVote = optionalStageVote.get();
        stageVote.setStatus(EntityStatus.INACTIVE);
        maStageVoteRepository.save(stageVote);

        return new ResponseObject<>(null,HttpStatus.OK,"Xóa cuộc bình chọn thành công");
    }

    @Override
    public ResponseObject<?> stageTakePlace(String idProject) {
        long currentTimeMillis = System.currentTimeMillis();
        Optional<StageResponse> optionalStageVote = maStageVoteRepository.findByStageTakePlace(idProject, currentTimeMillis);
        if (optionalStageVote.isEmpty()) {
            return new ResponseObject<>().success("Không có cuộc bình chọn nào diễn ra");
        }

        return new ResponseObject<>(optionalStageVote.get(), HttpStatus.OK, "Lấy dữ liệu thành công");
    }

    @Override
    public ResponseObject<?> findALlTodoVoteStage(String idStageVote) {
        return new ResponseObject<>(maStageVoteRepository.findALlTodoVoteStage(idStageVote), HttpStatus.OK, "Láy dữ liệu thành công");
    }


    @Override
    public ResponseObject<?> getAllStage(StageVoteRequest request) {
        Pageable pageable = Helper.createPageable(request, "created_date");
        return new ResponseObject<>(maStageVoteRepository.getAllStage(pageable, request.getIdProject()), HttpStatus.OK, "Lấy dự liệu giai đoạn thành công");
    }
}
