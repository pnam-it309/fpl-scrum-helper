package udpm.hn.server.core.member.report.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.report.model.request.MBTodoProjectRequest;
import udpm.hn.server.core.member.report.model.response.MBTodoProjectResponse;
import udpm.hn.server.core.member.report.repository.MBTodoProjectRepository;
import udpm.hn.server.utils.UserContextHelper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MBTodoProjectServiceImplTest {

    @InjectMocks
    private MBTodoProjectServiceImpl todoProjectService;

    @Mock
    private MBTodoProjectRepository todoProjectRepository;

    @Mock
    private UserContextHelper userContextHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTodoByProjectAndStaffProject_Success() {
        // Given
        MBTodoProjectRequest request = new MBTodoProjectRequest();
        String idProject = "project123";

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());

        MBTodoProjectResponse todo1 = mock(MBTodoProjectResponse.class);
        MBTodoProjectResponse todo2 = mock(MBTodoProjectResponse.class);
        Page<MBTodoProjectResponse> mockPage = new PageImpl<>(List.of(todo1, todo2));

        when(todoProjectRepository.getAllTodoByProjectAndStaffProject(
                any(Pageable.class), eq(idProject), eq(userContextHelper), eq(request))
        ).thenReturn(mockPage);

        // When
        ResponseObject<?> response = todoProjectService.getAllTodoByProjectAndStaffProject(request, idProject);

        // Then
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());

        PageableObject<?> resultPageable = (PageableObject<?>) response.getData();
        assertEquals(2, resultPageable.getData().size());

        verify(todoProjectRepository, times(1))
                .getAllTodoByProjectAndStaffProject(any(Pageable.class), eq(idProject), eq(userContextHelper), eq(request));
    }

    @Test
    void testGetAllTodoByProjectAndStaffProject_Exception() {
        MBTodoProjectRequest request = new MBTodoProjectRequest();
        String idProject = "project123";

        when(todoProjectRepository.getAllTodoByProjectAndStaffProject(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> {
            todoProjectService.getAllTodoByProjectAndStaffProject(request, idProject);
        });
    }
}
