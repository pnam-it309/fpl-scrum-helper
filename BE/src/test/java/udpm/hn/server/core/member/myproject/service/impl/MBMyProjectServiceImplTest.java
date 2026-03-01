package udpm.hn.server.core.member.myproject.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.request.MaProjectSearchRequest;
import udpm.hn.server.core.member.myproject.model.response.MBMyProjectResponse;
import udpm.hn.server.core.member.myproject.repository.MBMyProjectRepository;
import udpm.hn.server.infrastructure.constant.StatusProject;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MBMyProjectServiceImplTest {
    @InjectMocks
    private MBMyProjectServiceImpl service;

    @Mock
    private MBMyProjectRepository repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMyProject_Success() {
        // Given
        MaProjectSearchRequest request = new MaProjectSearchRequest();
        request.setSearch("AI");
        request.setNameDepartment("CNTT");
        request.setStatus(StatusProject.DANG_DIEN_RA.ordinal());

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());

        MBMyProjectResponse mockProject1 = mock(MBMyProjectResponse.class);
        MBMyProjectResponse mockProject2 = mock(MBMyProjectResponse.class);
        Page<MBMyProjectResponse> mockPage = new PageImpl<>(List.of(mockProject1, mockProject2));

        when(repository.getAllMyProject(eq(request), any(Pageable.class), eq(StatusProject.DANG_DIEN_RA)))
                .thenReturn(mockPage);

        // When
        ResponseObject<?> response = service.getAllMyProject(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());

        Page<?> page = (Page<?>) response.getData();
        assertEquals(2, page.getContent().size());

        verify(repository, times(1))
                .getAllMyProject(eq(request), any(Pageable.class), eq(StatusProject.DANG_DIEN_RA));
    }

    @Test
    void testGetAllMyProject_StatusNull() {
        // Given
        MaProjectSearchRequest request = new MaProjectSearchRequest(); // no status
        request.setSearch("web");

        Page<MBMyProjectResponse> emptyPage = new PageImpl<>(List.of());

        when(repository.getAllMyProject(eq(request), any(Pageable.class), isNull()))
                .thenReturn(emptyPage);

        // When
        ResponseObject<?> response = service.getAllMyProject(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());

        Page<?> page = (Page<?>) response.getData();
        assertEquals(0, page.getContent().size());

        verify(repository, times(1)).getAllMyProject(eq(request), any(Pageable.class), isNull());
    }

    @Test
    void testGetAllMyProject_RepositoryThrowsException() {
        // Given
        MaProjectSearchRequest request = new MaProjectSearchRequest();
        request.setStatus(StatusProject.CHUA_DIEN_RA.ordinal());

        when(repository.getAllMyProject(any(), any(), any()))
                .thenThrow(new RuntimeException("Database error"));

        // Then
        assertThrows(RuntimeException.class, () -> {
            service.getAllMyProject(request);
        });

        verify(repository, times(1)).getAllMyProject(any(), any(), any());
    }

}
