package udpm.hn.server.core.member.projectdetails.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.*;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeLabelResponse;
import udpm.hn.server.core.member.projectdetails.repository.MBMeLabelRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeProjectRepository;
import udpm.hn.server.entity.LabelProject;
import udpm.hn.server.entity.Project;
import udpm.hn.server.infrastructure.constant.EntityStatus;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MBMeLabelServiceImplTest {

    @Mock
    private MBMeLabelRepository labelRepository;

    @Mock
    private MBMeProjectRepository projectRepository;

    @InjectMocks
    private MBMeLabelServiceImpl labelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllLabelByIdTodo_shouldReturnList() {
        MBMeLabelResponse labelResponse = mock(MBMeLabelResponse.class);
        List<MBMeLabelResponse> mockList = List.of(labelResponse);

        when(labelRepository.getAllLabelByIdTodo("todo1")).thenReturn(mockList);

        ResponseObject<?> response = labelService.getAllLabelByIdTodo("todo1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(mockList, response.getData());
    }

    @Test
    void getAllByIdProject_shouldReturnList() {
        MBMeLabelResponse labelResponse = mock(MBMeLabelResponse.class);
        List<MBMeLabelResponse> mockList = List.of(labelResponse);

        when(labelRepository.getAllByIdProject("project1", "todo1")).thenReturn(mockList);

        ResponseObject<?> response = labelService.getAllByIdProject("project1", "todo1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(mockList, response.getData());
    }

    @Test
    void getAllLabelSearchByIdProject_shouldReturnList() {
        MBMeLabelResponse labelResponse = mock(MBMeLabelResponse.class);
        List<MBMeLabelResponse> mockList = List.of(labelResponse);

        when(labelRepository.getAllLabelSearchByIdProject("project1")).thenReturn(mockList);

        ResponseObject<?> response = labelService.getAllLabelSearchByIdProject("project1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(mockList, response.getData());
    }

    @Test
    void create_shouldSaveAndReturnLabel() {
        MBMeCreateLabelProjectRequest request = new MBMeCreateLabelProjectRequest();
        request.setName("Important");
        request.setColor("Red");
        request.setProjectId("project1");

        Project mockProject = new Project();
        when(projectRepository.findById("project1")).thenReturn(Optional.of(mockProject));

        LabelProject savedLabel = new LabelProject();
        savedLabel.setName("Important");

        when(labelRepository.save(any(LabelProject.class))).thenReturn(savedLabel);

        ResponseObject<?> response = labelService.create(request, null);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(savedLabel, response.getData());
    }

    @Test
    void update_shouldUpdateAndReturnLabel() {
        MBMeUpdateLabelProjectRequest request = new MBMeUpdateLabelProjectRequest();
        request.setId("label1");
        request.setName("Updated");
        request.setColor("Blue");

        LabelProject existing = new LabelProject();
        existing.setName("Old");

        when(labelRepository.findById("label1")).thenReturn(Optional.of(existing));
        when(labelRepository.save(existing)).thenReturn(existing);

        ResponseObject<?> response = labelService.update(request, null);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(existing, response.getData());
        assertEquals("Updated", ((LabelProject) response.getData()).getName());
    }

    @Test
    void update_shouldReturnNotFoundWhenLabelDoesNotExist() {
        MBMeUpdateLabelProjectRequest request = new MBMeUpdateLabelProjectRequest();
        request.setId("label1");

        when(labelRepository.findById("label1")).thenReturn(Optional.empty());

        ResponseObject<?> response = labelService.update(request, null);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNull(response.getData());
        assertEquals("Label không tồn tại", response.getMessage());
    }

    @Test
    void delete_shouldMarkLabelInactive() {
        MBMeDeleteLabelProjectRequest request = new MBMeDeleteLabelProjectRequest();
        request.setIdLabelProject("label1");

        LabelProject label = new LabelProject();
        label.setStatus(EntityStatus.ACTIVE);

        when(labelRepository.findById("label1")).thenReturn(Optional.of(label));
        when(labelRepository.save(label)).thenReturn(label);

        ResponseObject<?> response = labelService.delete(request, null);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(EntityStatus.INACTIVE, ((LabelProject) response.getData()).getStatus());
    }

    @Test
    void delete_shouldReturnNotFoundWhenLabelDoesNotExist() {
        MBMeDeleteLabelProjectRequest request = new MBMeDeleteLabelProjectRequest();
        request.setIdLabelProject("label1");

        when(labelRepository.findById("label1")).thenReturn(Optional.empty());

        ResponseObject<?> response = labelService.delete(request, null);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNull(response.getData());
        assertEquals("Label không tồn tại", response.getMessage());
    }

}
