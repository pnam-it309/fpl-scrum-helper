package udpm.hn.server.core.member.projectdetails.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.multipart.MultipartFile;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.*;
import udpm.hn.server.core.member.projectdetails.model.response.MBBackgroundProjectResponse;
import udpm.hn.server.core.member.projectdetails.model.response.MBUpdateBackgroundProject;
import udpm.hn.server.core.member.projectdetails.repository.*;
import udpm.hn.server.entity.Activity;
import udpm.hn.server.entity.Image;
import udpm.hn.server.entity.Project;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.infrastructure.cloudinary.CloudinaryUploadImages;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.StatusImage;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MBMeImageServiceImplTest {

    @InjectMocks
    private MBMeImageServiceImpl imageService;

    @Mock private MBMeImageRepository imageRepository;
    @Mock private MBMeTodoRepository todoRepository;
    @Mock private MBMeActivityRepository activityRepository;
    @Mock private MBMeProjectRepository projectRepository;
    @Mock private CloudinaryUploadImages cloudinary;
    @Mock private SimpMessagingTemplate messagingTemplate;

    @Mock private StompHeaderAccessor headerAccessor;

    @Test
    void add_shouldAddImage() {
        MBMeCreateImageRequest request = new MBMeCreateImageRequest();
        request.setIdTodo("todo1");
        request.setNameFileOld("file.jpg");
        request.setUrlImage("url");
        request.setIdTodoList("todoList");
        request.setIdUser("user");
        request.setProjectId("project");
        request.setUrlPath("path");

        Todo todo = new Todo();
        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(imageRepository.countImageByIdTodo("todo1")).thenReturn(0);
        when(imageRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(activityRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ResponseObject<?> response = imageService.add(request, headerAccessor);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void uploadFile_shouldUploadSuccessfully() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(cloudinary.uploadImage(file)).thenReturn("uploaded_url");

        ResponseObject<?> response = imageService.uploadFile(file);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("uploaded_url", response.getData());
    }

    @Test
    void uploadFile_shouldHandleException() {
        MultipartFile file = mock(MultipartFile.class);
        when(cloudinary.uploadImage(file)).thenThrow(new RuntimeException());

        ResponseObject<?> response = imageService.uploadFile(file);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test
    void findById_shouldReturnImage() {
        Image image = new Image();
        when(imageRepository.findById("img1")).thenReturn(Optional.of(image));

        ResponseObject<?> response = imageService.findById("img1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(image, response.getData());
    }

    @Test
    void getAllByIdTodo_shouldReturnList() {
        when(imageRepository.getAllByIdTodo("todo1")).thenReturn(List.of());

        ResponseObject<?> response = imageService.getAllByIdTodo("todo1");

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void updateNameImage_shouldUpdateName() {
        MBMeUpdateNameImageRequest request = new MBMeUpdateNameImageRequest();
        request.setId("img1");
        request.setNameImage("new_name");

        Image image = new Image();
        when(imageRepository.findById("img1")).thenReturn(Optional.of(image));
        when(imageRepository.save(any())).thenReturn(image);

        ResponseObject<?> response = imageService.updateNameImage(request, headerAccessor);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void deleteImage_shouldDeleteImageSuccessfully() {
        MBMeDeleteImageRequest request = new MBMeDeleteImageRequest();
        request.setId("img1");
        request.setIdTodo("todo1");
        request.setIdUser("user");
        request.setProjectId("proj");
        request.setNameImage("img");
        request.setIdTodoList("list1");
        request.setUrlPath("path");
        request.setStatusImage("0");
        request.setUrlImage("url");

        Todo todo = new Todo();
        todo.setName("Test Todo");

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(activityRepository.findActivityByIdImage("img1")).thenReturn(new Activity());
        when(activityRepository.save(any())).thenReturn(new Activity());

        ResponseObject<?> response = imageService.deleteImage(request, headerAccessor);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void changeCoverTodo_shouldSetCoverImage() {
        MBMeChangeCoverTodoRequest request = new MBMeChangeCoverTodoRequest();
        request.setIdTodo("todo1");
        request.setIdImage("img1");
        request.setStatus("1");
        request.setUrlImage("url");

        Todo todo = new Todo();
        Image image = new Image();
        image.setStatusImage(StatusImage.NO_COVER);

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(imageRepository.findById("img1")).thenReturn(Optional.of(image));
        when(imageRepository.save(any())).thenReturn(image);
        when(todoRepository.save(any())).thenReturn(todo);

        ResponseObject<?> response = imageService.changeCoverTodo(request, headerAccessor);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void getBackgroundByIdProject_shouldReturnBackground() {
        MBBackgroundProjectResponse mockResponse = mock(MBBackgroundProjectResponse.class);
        when(mockResponse.getBackgroundImage()).thenReturn("img_url");
        when(mockResponse.getBackgroundColor()).thenReturn("blue");

        when(imageRepository.getBackgroundByIdProject("project1")).thenReturn(mockResponse);

        ResponseObject<?> response = imageService.getBackgroundByIdProject("project1");

        assertEquals(HttpStatus.OK, response.getStatus());

        MBBackgroundProjectResponse result = (MBBackgroundProjectResponse) response.getData();
        assertEquals("img_url", result.getBackgroundImage());
        assertEquals("blue", result.getBackgroundColor());
    }


    @Test
    void updateBackgroundProject_shouldUpdateSuccessfully() {
        MBUpdateBackgroundProject request = new MBUpdateBackgroundProject();
        request.setIdProject("p1");
        request.setBackgroundColor("blue");
        request.setBackgroundImage("img_url");

        Project project = new Project();
        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);

        ResponseObject<?> response = imageService.updateBackgroundProject(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(project, response.getData());
    }
}

