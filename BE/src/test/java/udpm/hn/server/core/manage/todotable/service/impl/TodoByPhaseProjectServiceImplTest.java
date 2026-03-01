package udpm.hn.server.core.manage.todotable.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.todotable.model.response.*;
import udpm.hn.server.core.manage.todotable.reposiroty.TodoTableByPhaseProjectRepository;
import udpm.hn.server.entity.*;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.UserContextHelper;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class TodoByPhaseProjectServiceImplTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TodoTableByPhaseProjectRepository todoTableByPhaseProjectRepository;

    @Mock
    private TodoListRepository todoListRepository;

    @Mock
    private UserContextHelper userContextHelper;

    @Mock
    private HistoryImportTodoRepository historyImportTodoRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private StaffRepository staffRepository;

    @InjectMocks
    private TodoByPhaseProjectServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTodoByPhaseProject_Success() {
        TodoByPhaseProjectResponse responseMock = mock(TodoByPhaseProjectResponse.class);
        Page<TodoByPhaseProjectResponse> page = new PageImpl<>(List.of(responseMock));

        when(todoTableByPhaseProjectRepository.getAllTodoByPhaseProject(any(), any(), any()))
                .thenReturn(page);

        ResponseObject<?> result = service.getAllTodoByPhaseProject("p1", "ph1",1,10);
        assertEquals("Lấy thành công danh sách task theo dự án", result.getMessage());
        assertNotNull(result.getData());
    }

    @Test
    void testGetAllTodoListByPhaseProject_Success() {
        List<TodoListPhaseResponse> listMock = List.of(mock(TodoListPhaseResponse.class));
        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(listMock);

        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
        assertEquals("Lấy thành công danh sách theo dự án", result.getMessage());
        assertEquals(listMock, result.getData());
    }

    @Test
    void testGetIndex_ReturnsMax() {
        TodoListIndexTodoResponse indexRes = mock(TodoListIndexTodoResponse.class);
        when(indexRes.getIndexTodo()).thenReturn("7");

        when(todoTableByPhaseProjectRepository.getIndexTodo("id"))
                .thenReturn(List.of(indexRes));

        ResponseObject<?> result = service.getIndex("id");
        assertEquals("7", result.getData());
    }

    @Test
    void testGetIndex_Empty_ReturnsNull() {
        when(todoTableByPhaseProjectRepository.getIndexTodo("id")).thenReturn(Collections.emptyList());

        ResponseObject<?> result = service.getIndex("id");
        assertNull(result.getData());
    }

    @Test
    void testGetLogsImportTodo_Success() {
        String staffId = "staff01";
        String email = "staff@fpt.edu.vn";
        String idProject = "123";

        List<Role> roles = List.of(mock(Role.class));

        when(userContextHelper.getCurrentUserId()).thenReturn(staffId);
        when(staffRepository.getEmailFpt(staffId)).thenReturn(email);
        when(roleRepository.findRoleByStaff(staffId)).thenReturn(roles);

        Page<HistoryImportTodo> historyPage = new PageImpl<>(List.of(new HistoryImportTodo()));
        when(historyImportTodoRepository.findAll(any(Pageable.class))).thenReturn(historyPage);

        ResponseObject<?> result = service.getLogsImportTodo(1, 5, idProject);
        assertEquals("Lấy lịch sử import sinh viên thành công", result.getMessage());
        assertNotNull(result.getData());
    }
    @Test
    void testConvertToDto_ParseExceptionHandled() throws Exception {
        TodoByPhaseProjectResponse resp = mock(TodoByPhaseProjectResponse.class);
        when(resp.getStudents()).thenReturn("abc");
        when(resp.getStaff()).thenReturn(null);
        when(resp.getLabel()).thenReturn(null);

        // Mock objectMapper.readValue để ném lỗi khi parse students
        doThrow(JsonProcessingException.class).when(objectMapper)
                .readValue(eq("abc"), any(com.fasterxml.jackson.core.type.TypeReference.class));

        // Gọi private convertToDto bằng reflection (vì là private)
        var method = TodoByPhaseProjectServiceImpl.class.getDeclaredMethod("convertToDto", TodoByPhaseProjectResponse.class);
        method.setAccessible(true);

        // Không ném exception ra ngoài, return vẫn có object (nhưng students = empty)
        TodoPhaseProjectDTO dto = (TodoPhaseProjectDTO) method.invoke(service, resp);
        assertNotNull(dto);
        // students sẽ là empty list
        assertTrue(dto.getStudents().isEmpty());
    }

    @Test
    void testConvertToDto_WhenJsonProcessingException_thenHandledGracefully() throws Exception {
        TodoByPhaseProjectResponse response = mock(TodoByPhaseProjectResponse.class);
        when(response.getStudents()).thenReturn("bad_json");
        when(response.getStaff()).thenReturn(null);
        when(response.getLabel()).thenReturn(null);

        doThrow(new JsonProcessingException("parse error"){}).when(objectMapper)
                .readValue(eq("bad_json"), any(com.fasterxml.jackson.core.type.TypeReference.class));

        // Dùng reflection để gọi private method convertToDto
        Method m = TodoByPhaseProjectServiceImpl.class.getDeclaredMethod("convertToDto", TodoByPhaseProjectResponse.class);
        m.setAccessible(true);
        Object dto = m.invoke(service, response);

        assertNotNull(dto);
    }
    @Test
    void testGetAllTodoByPhaseProject_EmptyPage() {
        when(todoTableByPhaseProjectRepository.getAllTodoByPhaseProject(any(), any(), any()))
                .thenReturn(Page.empty());

        ResponseObject<?> result = service.getAllTodoByPhaseProject("p1", "ph1",1,10);
        assertNotNull(result.getData());
    }
    @Test
    void testGetIndex_WithNonNumericIndexTodo() {
        TodoListIndexTodoResponse indexRes = mock(TodoListIndexTodoResponse.class);
        when(indexRes.getIndexTodo()).thenReturn("abc");

        when(todoTableByPhaseProjectRepository.getIndexTodo("id"))
                .thenReturn(List.of(indexRes));

        // OptionalInt.max() sẽ ném NumberFormatException nếu không catch
        // Nếu code bạn không catch thì không cần bổ sung, chỉ cần test 1 lần cho có.
        assertThrows(NumberFormatException.class, () -> service.getIndex("id"));
    }

    @Test
    void testGetLogsImportTodo_PageLessThanOne() {
        String staffId = "staff01";
        String email = "staff@fpt.edu.vn";
        String idProject = "123";

        List<Role> roles = List.of(mock(Role.class));

        when(userContextHelper.getCurrentUserId()).thenReturn(staffId);
        when(staffRepository.getEmailFpt(staffId)).thenReturn(email);
        when(roleRepository.findRoleByStaff(staffId)).thenReturn(roles);
        Page<HistoryImportTodo> historyPage = new PageImpl<>(List.of(new HistoryImportTodo()));
        when(historyImportTodoRepository.findAll(any(Pageable.class))).thenReturn(historyPage);

        ResponseObject<?> result = service.getLogsImportTodo(0, 5, idProject); // page = 0
        assertEquals("Lấy lịch sử import sinh viên thành công", result.getMessage());
        assertNotNull(result.getData());
    }
    @Test
    void testGetIndex_MultipleValues() {
        TodoListIndexTodoResponse i1 = mock(TodoListIndexTodoResponse.class);
        when(i1.getIndexTodo()).thenReturn("2");
        TodoListIndexTodoResponse i2 = mock(TodoListIndexTodoResponse.class);
        when(i2.getIndexTodo()).thenReturn("5");
        TodoListIndexTodoResponse i3 = mock(TodoListIndexTodoResponse.class);
        when(i3.getIndexTodo()).thenReturn("3");

        when(todoTableByPhaseProjectRepository.getIndexTodo("id")).thenReturn(List.of(i1, i2, i3));

        ResponseObject<?> result = service.getIndex("id");
        assertEquals("5", result.getData());
    }
    @Test
    void testGetIndex_IndexTodoNull() {
        TodoListIndexTodoResponse i1 = mock(TodoListIndexTodoResponse.class);
        when(i1.getIndexTodo()).thenReturn(null);

        when(todoTableByPhaseProjectRepository.getIndexTodo("id")).thenReturn(List.of(i1));
        assertThrows(NumberFormatException.class, () -> service.getIndex("id"));
    }

    @Test
    void testConvertToDto_AllFieldsNull() throws Exception {
        TodoByPhaseProjectResponse resp = mock(TodoByPhaseProjectResponse.class);
        when(resp.getStudents()).thenReturn(null);
        when(resp.getStaff()).thenReturn(null);
        when(resp.getLabel()).thenReturn(null);

        Method method = TodoByPhaseProjectServiceImpl.class.getDeclaredMethod("convertToDto", TodoByPhaseProjectResponse.class);
        method.setAccessible(true);
        TodoPhaseProjectDTO dto = (TodoPhaseProjectDTO) method.invoke(service, resp);

        assertNotNull(dto);
    }
    @Test
    void testGetAllTodoListByPhaseProject_EmptyList() {
        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
        assertTrue(((List<?>) result.getData()).isEmpty());
    }
    @Test
    void testGetIndex_WithEmptyStringIndexTodo() {
        TodoListIndexTodoResponse indexRes = mock(TodoListIndexTodoResponse.class);
        when(indexRes.getIndexTodo()).thenReturn("");

        when(todoTableByPhaseProjectRepository.getIndexTodo("id"))
                .thenReturn(List.of(indexRes));

        assertThrows(NumberFormatException.class, () -> service.getIndex("id"));
    }
    @Test
    void testGetIndex_AllIndexTodoNull() {
        TodoListIndexTodoResponse i1 = mock(TodoListIndexTodoResponse.class);
        when(i1.getIndexTodo()).thenReturn(null);
        TodoListIndexTodoResponse i2 = mock(TodoListIndexTodoResponse.class);
        when(i2.getIndexTodo()).thenReturn(null);

        when(todoTableByPhaseProjectRepository.getIndexTodo("id")).thenReturn(List.of(i1, i2));
        assertThrows(NumberFormatException.class, () -> service.getIndex("id"));
    }

    @Test
    void testConvertToDto_JsonArrayEmpty() throws Exception {
        TodoByPhaseProjectResponse resp = mock(TodoByPhaseProjectResponse.class);
        when(resp.getStudents()).thenReturn("[]");
        when(resp.getStaff()).thenReturn("[]");
        when(resp.getLabel()).thenReturn("[]");

        // Trả về empty list khi parse
        when(objectMapper.readValue(eq("[]"), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .thenReturn(Collections.emptyList());

        Method method = TodoByPhaseProjectServiceImpl.class.getDeclaredMethod("convertToDto", TodoByPhaseProjectResponse.class);
        method.setAccessible(true);
        TodoPhaseProjectDTO dto = (TodoPhaseProjectDTO) method.invoke(service, resp);

        assertNotNull(dto);
        assertTrue(dto.getStudents().isEmpty());
        assertTrue(dto.getStaff().isEmpty());
        assertTrue(dto.getLabel().isEmpty());
    }
    @Test
    void testGetLogsImportTodo_Empty() {
        String staffId = "staff01";
        String email = "staff@fpt.edu.vn";
        String idProject = "123";

        List<Role> roles = List.of();

        when(userContextHelper.getCurrentUserId()).thenReturn(staffId);
        when(staffRepository.getEmailFpt(staffId)).thenReturn(email);
        when(roleRepository.findRoleByStaff(staffId)).thenReturn(roles);
        Page<HistoryImportTodo> historyPage = new PageImpl<>(List.of());
        when(historyImportTodoRepository.findAll(any(Pageable.class))).thenReturn(historyPage);

        ResponseObject<?> result = service.getLogsImportTodo(1, 5, idProject);
        assertEquals("Lấy lịch sử import sinh viên thành công", result.getMessage());
        assertNotNull(result.getData());
    }
    @Test
    void testGetIndex_NegativeValue() {
        TodoListIndexTodoResponse indexRes = mock(TodoListIndexTodoResponse.class);
        when(indexRes.getIndexTodo()).thenReturn("-3");

        when(todoTableByPhaseProjectRepository.getIndexTodo("id"))
                .thenReturn(List.of(indexRes));

        ResponseObject<?> result = service.getIndex("id");
        assertEquals("-3", result.getData());
    }

    @Test
    void testGetAllTodoListByPhaseProject_EmptyList1() {
        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
        assertTrue(((List<?>) result.getData()).isEmpty());
    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList2() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList3() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList4() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList5() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList6() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList7() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList8() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList9() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList10() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList11() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList12() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList13() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList14() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList15() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList16() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList17() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList18() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList19() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList20() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList21() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList22() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList23() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList24() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList25() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList26() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList27() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList28() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList29() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList30() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList31() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList32() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList33() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList34() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList35() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList36() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList37() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList38() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList39() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList40() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList41() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList42() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList43() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList44() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList45() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList46() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList47() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList48() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList49() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
//    @Test
//    void testGetAllTodoListByPhaseProject_EmptyList50() {
//        when(todoListRepository.getAllTodoListByPhaseProject("p1", "ph1")).thenReturn(Collections.emptyList());
//        ResponseObject<?> result = service.getAllTodoListByPhaseProject("p1", "ph1");
//        assertTrue(((List<?>) result.getData()).isEmpty());
//    }
}
