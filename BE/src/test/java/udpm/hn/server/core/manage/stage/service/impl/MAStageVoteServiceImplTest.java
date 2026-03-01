package udpm.hn.server.core.manage.stage.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.phase.repository.MAPhaseRepository;
import udpm.hn.server.core.manage.stage.repository.MAStageVoteRepository;
import udpm.hn.server.core.manage.stage.service.impl.MAStageVoteServiceImpl;
import udpm.hn.server.core.manage.stage.model.request.StageRequest;
import udpm.hn.server.core.manage.stage.model.request.StageVoteRequest;
import udpm.hn.server.core.manage.stage.model.response.StageResponse;
import udpm.hn.server.core.manage.todo.repository.MAStaffProjectRepository;
import udpm.hn.server.entity.PhaseProject;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.StageVote;
import udpm.hn.server.infrastructure.configemail.EmailSender;
import udpm.hn.server.infrastructure.constant.EntityStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
public class MAStageVoteServiceImplTest {

    @Mock private MAStageVoteRepository stageVoteRepository;
    @Mock private MAPhaseRepository phaseRepository;
    @Mock private MAStaffProjectRepository staffProjectRepository;
    @Mock private EmailSender emailSender;

    @InjectMocks private MAStageVoteServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateStageVote_Success() {
        StageRequest request = new StageRequest();
        request.setIdProject("project-1");
        request.setIdPhase("phase-1");
        request.setStartTime(System.currentTimeMillis() + 10000);
        request.setEndTime(System.currentTimeMillis() + 20000);

        when(phaseRepository.findById("phase-1")).thenReturn(Optional.of(new PhaseProject()));
        when(stageVoteRepository.findAllByIdProject("project-1")).thenReturn(Collections.emptyList());

        ResponseObject<?> response = service.createUpdate(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Tạo thành công", response.getMessage());
    }

    @Test
    void testCreateStageVote_PhaseNotFound() {
        StageRequest request = new StageRequest();
        request.setIdPhase("not-exist");

        when(phaseRepository.findById("not-exist")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.createUpdate(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void testCreateStageVote_TimeOverlap() {
        StageRequest request = new StageRequest();
        request.setIdProject("project-1");
        request.setIdPhase("phase-1");
        request.setStartTime(10000L);
        request.setEndTime(20000L);

        StageVote existing = StageVote.builder().startTime(15000L).endTime(25000L).build();

        when(phaseRepository.findById("phase-1")).thenReturn(Optional.of(new PhaseProject()));
        when(stageVoteRepository.findAllByIdProject("project-1")).thenReturn(Collections.singletonList(existing));

        ResponseObject<?> response = service.createUpdate(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }

    @Test
    void testUpdateStageVote_Success() {
        StageRequest request = new StageRequest();
        request.setIdProject("project-1");
        request.setIdPhase("phase-1");
        request.setIdStage("stage-1");
        request.setStartTime(System.currentTimeMillis() + 10000);
        request.setEndTime(System.currentTimeMillis() + 20000);

        StageVote vote = StageVote.builder()
                .startTime(0L)
                .endTime(0L)
                .build();
        vote.setId("stage-1");  // Sửa tại đây

        when(phaseRepository.findById("phase-1")).thenReturn(Optional.of(new PhaseProject()));
        when(stageVoteRepository.findById("stage-1")).thenReturn(Optional.of(vote));
        when(stageVoteRepository.findAllByIdProject("project-1")).thenReturn(Collections.singletonList(vote));

        ResponseObject<?> response = service.createUpdate(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật thành công", response.getMessage());
    }

    @Test
    void testDetailStageVote() {
        when(stageVoteRepository.findById("stage-id")).thenReturn(Optional.of(StageVote.builder().build()));

        ResponseObject<?> response = service.detailStageVote("stage-id");

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteStage_Success() {
        StageVote vote = new StageVote();
        vote.setStatus(EntityStatus.ACTIVE);

        when(stageVoteRepository.findById("stage-id")).thenReturn(Optional.of(vote));

        ResponseObject<?> response = service.deleteStage("stage-id");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(EntityStatus.INACTIVE, vote.getStatus());
    }

    @Test
    void testDeleteStage_NotFound() {
        when(stageVoteRepository.findById("missing")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.deleteStage("missing");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void testStageTakePlace_Found() {
        StageResponse responseMock = mock(StageResponse.class);
        when(stageVoteRepository.findByStageTakePlace(eq("project-1"), anyLong())).thenReturn(Optional.of(responseMock));

        ResponseObject<?> response = service.stageTakePlace("project-1");

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testStageTakePlace_NotFound() {
        when(stageVoteRepository.findByStageTakePlace(anyString(), anyLong())).thenReturn(Optional.empty());

        ResponseObject<?> response = service.stageTakePlace("project-1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Không có cuộc bình chọn nào diễn ra", response.getMessage());
    }

    @Test
    void testFindAllTodoVoteStage() {
        when(stageVoteRepository.findALlTodoVoteStage("stage-id")).thenReturn(Collections.emptyList());

        ResponseObject<?> response = service.findALlTodoVoteStage("stage-id");

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testGetAllStage() {
        StageVoteRequest request = new StageVoteRequest();
        request.setIdProject("project-id");

        when(stageVoteRepository.getAllStage(any(Pageable.class), eq("project-id")))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        ResponseObject<?> response = service.getAllStage(request);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testCreateStageVote_StartTimeNull() {
        StageRequest request = new StageRequest();
        request.setIdProject("project-1");
        request.setIdPhase("phase-1");
        request.setStartTime(null); // deliberately set null
        request.setEndTime(System.currentTimeMillis() + 20000);

        when(phaseRepository.findById("phase-1")).thenReturn(Optional.of(new PhaseProject()));

        ResponseObject<?> response = service.createUpdate(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("Thời gian bắt đầu và kết thúc không được để trống.", response.getMessage());
    }
    @Test
    void testCreateStageVote_StartTimeAfterEndTime() {
        StageRequest request = new StageRequest();
        request.setIdProject("project-1");
        request.setIdPhase("phase-1");
        long now = System.currentTimeMillis();
        request.setStartTime(now + 20000);
        request.setEndTime(now + 10000); // start > end

        when(phaseRepository.findById("phase-1")).thenReturn(Optional.of(new PhaseProject()));

        ResponseObject<?> response = service.createUpdate(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("Thời gian bắt đầu phải nhỏ hơn thời gian kết thúc.", response.getMessage());
    }
    @Test
    void testCreateStageVote_TimeInPast() {
        StageRequest request = new StageRequest();
        request.setIdProject("project-1");
        request.setIdPhase("phase-1");
        long now = System.currentTimeMillis();
        request.setStartTime(now - 10000); // quá khứ
        request.setEndTime(now + 10000);

        when(phaseRepository.findById("phase-1")).thenReturn(Optional.of(new PhaseProject()));
        when(stageVoteRepository.findAllByIdProject("project-1")).thenReturn(Collections.emptyList());

        ResponseObject<?> response = service.createUpdate(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("Không thể chọn thời gian trong quá khứ.", response.getMessage());
    }

    @Test
    void testUpdateStageVote_NotFound() {
        StageRequest request = new StageRequest();
        request.setIdProject("project-1");
        request.setIdPhase("phase-1");
        request.setIdStage("stage-missing");
        request.setStartTime(System.currentTimeMillis() + 10000);
        request.setEndTime(System.currentTimeMillis() + 20000);

        when(phaseRepository.findById("phase-1")).thenReturn(Optional.of(new PhaseProject()));
        when(stageVoteRepository.findAllByIdProject("project-1")).thenReturn(Collections.emptyList());
        when(stageVoteRepository.findById("stage-missing")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.createUpdate(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy stageVote", response.getMessage());
    }

    @Test
    void testSendEmailToProjectMembers_NoEmails() {
        when(staffProjectRepository.getAllUserByProject("proj1")).thenReturn(Collections.emptyList());
        StageVote vote = StageVote.builder()
                .startTime(System.currentTimeMillis())
                .endTime(System.currentTimeMillis() + 10000)
                .build();

        service.sendEmailToProjectMembers("proj1", vote, true);
        // Không exception, không gọi emailSender
        verify(emailSender, never()).sendEmail(any(), any(), any(), any());
    }

    @Test
    void testSendEmailToProjectMembers_WithEmails() {
        StaffProject sp = new StaffProject();
        udpm.hn.server.entity.Staff staff = new udpm.hn.server.entity.Staff();
        staff.setEmailFpt("test@example.com");
        sp.setStaff(staff);

        when(staffProjectRepository.getAllUserByProject("proj1")).thenReturn(List.of(sp));
        StageVote vote = StageVote.builder()
                .startTime(System.currentTimeMillis())
                .endTime(System.currentTimeMillis() + 10000)
                .build();

        service.sendEmailToProjectMembers("proj1", vote, false);

        verify(emailSender, atLeastOnce()).sendEmail(any(), any(), any(), any());
    }
    @Test
    void testUpdateStageVote_NotFoundStageVote() {
        StageRequest request = new StageRequest();
        request.setIdProject("project-1");
        request.setIdPhase("phase-1");
        request.setIdStage("missing-stage");
        request.setStartTime(System.currentTimeMillis() + 10000);
        request.setEndTime(System.currentTimeMillis() + 20000);

        when(phaseRepository.findById("phase-1")).thenReturn(Optional.of(new PhaseProject()));
        when(stageVoteRepository.findAllByIdProject("project-1")).thenReturn(Collections.emptyList());
        when(stageVoteRepository.findById("missing-stage")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.createUpdate(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy stageVote", response.getMessage());
    }

    @Test
    void testSendEmailToProjectMembers_EmptyEmails() {
        when(staffProjectRepository.getAllUserByProject("proj1")).thenReturn(Collections.emptyList());
        StageVote vote = StageVote.builder()
                .startTime(System.currentTimeMillis())
                .endTime(System.currentTimeMillis() + 10000)
                .build();

        service.sendEmailToProjectMembers("proj1", vote, true);

        verify(emailSender, never()).sendEmail(any(), any(), any(), any());
    }

    @Test
    void testSendEmailToProjectMembers_StudentEmail() {
        StaffProject sp = new StaffProject();
        udpm.hn.server.entity.Student st = new udpm.hn.server.entity.Student();
        st.setEmail("student@example.com");
        sp.setStudent(st);

        when(staffProjectRepository.getAllUserByProject("proj2")).thenReturn(List.of(sp));
        StageVote vote = StageVote.builder()
                .startTime(System.currentTimeMillis())
                .endTime(System.currentTimeMillis() + 10000)
                .build();

        service.sendEmailToProjectMembers("proj2", vote, true);

        verify(emailSender, atLeastOnce()).sendEmail(any(), any(), any(), any());
    }

    @Test
    void testSendEmailToProjectMembers_StaffEmailFe() {
        StaffProject sp = new StaffProject();
        udpm.hn.server.entity.Staff staff = new udpm.hn.server.entity.Staff();
        staff.setEmailFe("stafffe@example.com");
        sp.setStaff(staff);

        when(staffProjectRepository.getAllUserByProject("proj3")).thenReturn(List.of(sp));
        StageVote vote = StageVote.builder()
                .startTime(System.currentTimeMillis())
                .endTime(System.currentTimeMillis() + 10000)
                .build();

        service.sendEmailToProjectMembers("proj3", vote, false);

        verify(emailSender, atLeastOnce()).sendEmail(any(), any(), any(), any());
    }

    @Test
    void testSendEmailToProjectMembers_MixedEmailsBatch() {
        List<StaffProject> staffProjects = new ArrayList<>();
        for (int i = 0; i < 55; i++) {
            StaffProject sp = new StaffProject();
            udpm.hn.server.entity.Staff staff = new udpm.hn.server.entity.Staff();
            staff.setEmailFpt("staff" + i + "@example.com");
            sp.setStaff(staff);
            staffProjects.add(sp);
        }

        when(staffProjectRepository.getAllUserByProject("proj4")).thenReturn(staffProjects);
        StageVote vote = StageVote.builder()
                .startTime(System.currentTimeMillis())
                .endTime(System.currentTimeMillis() + 10000)
                .build();

        service.sendEmailToProjectMembers("proj4", vote, true);

        verify(emailSender, atLeast(2)).sendEmail(any(), any(), any(), any());
    }

    @Test
    void testCreateUpdate_UpdateStageVote_PhaseFoundButStageVoteMissing() {
        StageRequest request = new StageRequest();
        request.setIdProject("project-1");
        request.setIdPhase("phase-1");
        request.setIdStage("stage-missing");
        request.setStartTime(System.currentTimeMillis() + 5000);
        request.setEndTime(System.currentTimeMillis() + 10000);

        when(phaseRepository.findById("phase-1")).thenReturn(Optional.of(new PhaseProject()));
        when(stageVoteRepository.findAllByIdProject("project-1")).thenReturn(Collections.emptyList());
        when(stageVoteRepository.findById("stage-missing")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.createUpdate(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy stageVote", response.getMessage());
    }

    @Test
    void testDeleteStage_SetsInactiveAndPersists() {
        StageVote vote = StageVote.builder()
                .startTime(System.currentTimeMillis())
                .endTime(System.currentTimeMillis() + 1000)
                .build();
        vote.setId("to-delete");

        when(stageVoteRepository.findById("to-delete")).thenReturn(Optional.of(vote));

        ResponseObject<?> response = service.deleteStage("to-delete");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Xóa cuộc bình chọn thành công", response.getMessage());
        verify(stageVoteRepository).save(vote);
        assertEquals(EntityStatus.INACTIVE, vote.getStatus());
    }

    @Test
    void testStageTakePlace_ReturnsSuccessMessageWhenEmpty() {
        when(stageVoteRepository.findByStageTakePlace(anyString(), anyLong())).thenReturn(Optional.empty());

        ResponseObject<?> response = service.stageTakePlace("proj5");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Không có cuộc bình chọn nào diễn ra", response.getMessage());
    }
    @Test
    void testDetailStageVote_ReturnsData() {
        StageVote vote = StageVote.builder()
                .startTime(System.currentTimeMillis())
                .endTime(System.currentTimeMillis() + 5000)
                .build();
        when(stageVoteRepository.findById("detail-1")).thenReturn(Optional.of(vote));

        ResponseObject<?> response = service.detailStageVote("detail-1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getData() instanceof Optional);
        assertTrue(((Optional<?>) response.getData()).isPresent());
    }

    @Test
    void testDetailStageVote_NotFound() {
        when(stageVoteRepository.findById("detail-missing")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.detailStageVote("detail-missing");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getData() instanceof Optional);
        assertTrue(((Optional<?>) response.getData()).isEmpty());
    }

    @Test
    void testCreateUpdate_UpdateExistingStageVote_Success() {
        StageRequest request = new StageRequest();
        request.setIdProject("proj-update");
        request.setIdPhase("phase-1");
        request.setIdStage("stage-1");
        request.setStartTime(System.currentTimeMillis() + 5000);
        request.setEndTime(System.currentTimeMillis() + 10000);

        StageVote existingVote = StageVote.builder()
                .startTime(System.currentTimeMillis())
                .endTime(System.currentTimeMillis() + 2000)
                .build();

        when(phaseRepository.findById("phase-1")).thenReturn(Optional.of(new PhaseProject()));
        when(stageVoteRepository.findAllByIdProject("proj-update")).thenReturn(Collections.singletonList(existingVote));
        when(stageVoteRepository.findById("stage-1")).thenReturn(Optional.of(existingVote));

        ResponseObject<?> response = service.createUpdate(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật thành công", response.getMessage());
        verify(stageVoteRepository).save(existingVote);
    }

    @Test
    void testCreateUpdate_OverlapWithSameStageId_ShouldSkipAndUpdate() {
        StageRequest request = new StageRequest();
        request.setIdProject("proj-overlap");
        request.setIdPhase("phase-1");
        request.setIdStage("stage-2");
        long now = System.currentTimeMillis();
        request.setStartTime(now + 5000);
        request.setEndTime(now + 10000);

        StageVote vote = StageVote.builder()
                .startTime(now + 2000)
                .endTime(now + 7000)
                .build();

        when(phaseRepository.findById("phase-1")).thenReturn(Optional.of(new PhaseProject()));
        when(stageVoteRepository.findAllByIdProject("proj-overlap")).thenReturn(Collections.singletonList(vote));
        when(stageVoteRepository.findById("stage-2")).thenReturn(Optional.of(vote));

        ResponseObject<?> response = service.createUpdate(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật thành công", response.getMessage());
    }

    @Test
    void testCreateUpdate_NewStageVote_TimeInFuture_Success() {
        StageRequest request = new StageRequest();
        request.setIdProject("proj-new");
        request.setIdPhase("phase-1");
        request.setStartTime(System.currentTimeMillis() + 50000);
        request.setEndTime(System.currentTimeMillis() + 100000);

        when(phaseRepository.findById("phase-1")).thenReturn(Optional.of(new PhaseProject()));
        when(stageVoteRepository.findAllByIdProject("proj-new")).thenReturn(Collections.emptyList());

        ResponseObject<?> response = service.createUpdate(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Tạo thành công", response.getMessage());
    }

    @Test
    void testCreateUpdate_InvalidTimes_StartEqualsEnd() {
        StageRequest request = new StageRequest();
        request.setIdProject("proj-invalid");
        request.setIdPhase("phase-1");
        long now = System.currentTimeMillis();
        request.setStartTime(now);
        request.setEndTime(now); // start == end

        when(phaseRepository.findById("phase-1")).thenReturn(Optional.of(new PhaseProject()));

        ResponseObject<?> response = service.createUpdate(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("Thời gian bắt đầu phải nhỏ hơn thời gian kết thúc.", response.getMessage());
    }

    @Test
    void testDeleteStage_AlreadyInactive_ShouldStillReturnOk() {
        StageVote vote = new StageVote();
        vote.setStatus(EntityStatus.INACTIVE); // already inactive

        when(stageVoteRepository.findById("stage-inactive")).thenReturn(Optional.of(vote));

        ResponseObject<?> response = service.deleteStage("stage-inactive");

        assertEquals(HttpStatus.OK, response.getStatus());
        verify(stageVoteRepository).save(vote);
    }

    @Test
    void testStageTakePlace_FoundAndReturnStageResponse() {
        StageResponse stageResponse = mock(StageResponse.class); // dùng mock thay vì new
        when(stageVoteRepository.findByStageTakePlace(eq("proj-stage"), anyLong()))
                .thenReturn(Optional.of(stageResponse));

        ResponseObject<?> response = service.stageTakePlace("proj-stage");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(stageResponse, response.getData());
    }





}
