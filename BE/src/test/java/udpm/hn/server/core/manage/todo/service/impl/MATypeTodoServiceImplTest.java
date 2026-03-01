package udpm.hn.server.core.manage.todo.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.todo.repository.MATypeTodoRepository;
import udpm.hn.server.entity.TypeTodo;
import udpm.hn.server.infrastructure.constant.EntityStatus;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class MATypeTodoServiceImplTest {

    @InjectMocks
    private MATypeTodoServiceImpl service;

    @Mock
    private MATypeTodoRepository maTypeTodoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listTyTodo_shouldReturnOnlyActiveItems() {
        // Arrange
        TypeTodo active1 = new TypeTodo();
        active1.setId("1");
        active1.setStatus(EntityStatus.ACTIVE);

        TypeTodo active2 = new TypeTodo();
        active2.setId("2");
        active2.setStatus(EntityStatus.ACTIVE);

        TypeTodo inactive = new TypeTodo();
        inactive.setId("3");
        inactive.setStatus(EntityStatus.INACTIVE);

        List<TypeTodo> mockData = Arrays.asList(active1, inactive, active2);
        when(maTypeTodoRepository.findAll()).thenReturn(mockData);

        // Act
        ResponseObject<?> response = service.listTyTodo();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dữ liệu thành công", response.getMessage());

        List<?> result = (List<?>) response.getData();
        assertEquals(2, result.size()); // only 2 ACTIVE items
        assertTrue(result.contains(active1));
        assertTrue(result.contains(active2));
        assertFalse(result.contains(inactive));
    }
}
