package udpm.hn.server.core.member.projectdetails.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateOrDeleteTodoVoteRequest;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeMemberProjectResponse;
import udpm.hn.server.core.member.projectdetails.repository.MBMeAssignRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeMemberProjectRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeTodoRepository;
import udpm.hn.server.entity.Assign;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.Todo;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class MBMeMemberProjectServiceImplTest {


    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private MBMeMemberProjectRepository memberProjectRepository;

    @Mock
    private MBMeTodoRepository todoRepository;

    @Mock
    private MBMeAssignRepository assignRepository;

    @InjectMocks
    private MBMeMemberProjectServiceImpl service;

    private MBMeCreateOrDeleteTodoVoteRequest createRequest() {
        MBMeCreateOrDeleteTodoVoteRequest request = new MBMeCreateOrDeleteTodoVoteRequest();
        request.setIdMember("member1");
        request.setProjectId("project1");
        request.setIdTodo("todo1");
        return request;
    }


    @Test
    void getAllMemberProject_shouldReturnMemberListSuccessfully() {
        List<MBMeMemberProjectResponse> mockList = List.of(new MBMeMemberProjectResponse() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getEmail() {
                return null;
            }

            @Override
            public String getImage() {
                return null;
            }

            @Override
            public Boolean getIsAdded() {
                return null;
            }
        });
        when(memberProjectRepository.findAllMembersWithAddedFlag("project1", "todo1"))
                .thenReturn(mockList);

        ResponseObject<?> response = service.getAllMemberProject("project1", "todo1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("getAllMemberProject", response.getMessage());
        assertEquals(mockList, response.getData());
    }

    @Test
    void joinTodoVoteMemberProject_shouldJoinSuccessfully() {
        MBMeCreateOrDeleteTodoVoteRequest request = createRequest();

        when(memberProjectRepository.findIdByMemberIdAndProjectId("member1", "project1")).thenReturn("staff1");

        Todo todo = new Todo();
        StaffProject staff = new StaffProject();

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(memberProjectRepository.findById("staff1")).thenReturn(Optional.of(staff));
        when(assignRepository.existsByTodoAndStaffProject(todo, staff)).thenReturn(false);

        // Giả lập Assign được lưu
        Assign savedAssign = new Assign();
        savedAssign.setTodo(todo);
        savedAssign.setStaffProject(staff);

        when(assignRepository.save(any(Assign.class))).thenReturn(savedAssign);

        // Gọi service
        ResponseObject<?> response = service.joinTodoVoteMemberProject(request);

        // Kiểm tra
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("joinTodoVoteMemberProject", response.getMessage());

        // So sánh field thay vì so sánh object reference
        Assign result = (Assign) response.getData();
        assertNotNull(result);
        assertEquals(todo, result.getTodo());
        assertEquals(staff, result.getStaffProject());
    }


    @Test
    void joinTodoVoteMemberProject_shouldReturnNotFoundIfTodoOrStaffProjectMissing() {
        MBMeCreateOrDeleteTodoVoteRequest request = createRequest();

        when(memberProjectRepository.findIdByMemberIdAndProjectId("member1", "project1")).thenReturn("staff1");
        when(todoRepository.findById("todo1")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.joinTodoVoteMemberProject(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Todo hoặc StaffProject không tồn tại", response.getMessage());
    }

    @Test
    void joinTodoVoteMemberProject_shouldReturnConflictIfAlreadyVoted() {
        MBMeCreateOrDeleteTodoVoteRequest request = createRequest();
        when(memberProjectRepository.findIdByMemberIdAndProjectId("member1", "project1")).thenReturn("staff1");

        Todo todo = new Todo();
        StaffProject staff = new StaffProject();

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(memberProjectRepository.findById("staff1")).thenReturn(Optional.of(staff));
        when(assignRepository.existsByTodoAndStaffProject(todo, staff)).thenReturn(true);

        ResponseObject<?> response = service.joinTodoVoteMemberProject(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatus());
        assertEquals("Đã vote Todo này rồi", response.getMessage());
    }

    @Test
    void outTodoVoteMemberProject_shouldOutSuccessfully() {
        MBMeCreateOrDeleteTodoVoteRequest request = createRequest();

        when(memberProjectRepository.findIdByMemberIdAndProjectId("member1", "project1")).thenReturn("staff1");
        when(todoRepository.findById("todo1")).thenReturn(Optional.of(new Todo()));
        when(memberProjectRepository.findById("staff1")).thenReturn(Optional.of(new StaffProject()));
        when(assignRepository.findIdAssignByIdTodoAndIdStaffProject("todo1", "staff1")).thenReturn(Optional.of("assign1"));

        ResponseObject<?> response = service.outTodoVoteMemberProject(request);

        verify(assignRepository).deleteById("assign1");
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("outTodoVoteMemberProject", response.getMessage());
    }

    @Test
    void outTodoVoteMemberProject_shouldReturnNotFoundIfTodoOrStaffProjectMissing() {
        MBMeCreateOrDeleteTodoVoteRequest request = createRequest();

        when(memberProjectRepository.findIdByMemberIdAndProjectId("member1", "project1")).thenReturn("staff1");
        when(todoRepository.findById("todo1")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.outTodoVoteMemberProject(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Todo hoặc StaffProject không tồn tại", response.getMessage());
    }

    @Test
    void outTodoVoteMemberProject_shouldReturnNotFoundIfNotVoted() {
        MBMeCreateOrDeleteTodoVoteRequest request = createRequest();

        when(memberProjectRepository.findIdByMemberIdAndProjectId("member1", "project1")).thenReturn("staff1");
        when(todoRepository.findById("todo1")).thenReturn(Optional.of(new Todo()));
        when(memberProjectRepository.findById("staff1")).thenReturn(Optional.of(new StaffProject()));
        when(assignRepository.findIdAssignByIdTodoAndIdStaffProject("todo1", "staff1")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.outTodoVoteMemberProject(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Người dùng chưa vote nên không thể out", response.getMessage());
    }

    @Test
    void getAllFilterMemberProject_shouldReturnFilteredMemberListSuccessfully() {
        List<MBMeMemberProjectResponse> mockList = List.of(new MBMeMemberProjectResponse() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getEmail() {
                return null;
            }

            @Override
            public String getImage() {
                return null;
            }

            @Override
            public Boolean getIsAdded() {
                return null;
            }
        });
        when(memberProjectRepository.getAllFilterMemberProject("project1")).thenReturn(mockList);

        ResponseObject<?> response = service.getAllFilterMemberProject("project1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("getAllFilterMemberProject", response.getMessage());
        assertEquals(mockList, response.getData());
    }

}
