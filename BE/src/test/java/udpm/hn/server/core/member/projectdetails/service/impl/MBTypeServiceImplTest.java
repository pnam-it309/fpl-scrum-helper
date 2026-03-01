package udpm.hn.server.core.member.projectdetails.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.response.TypeTodoResponse;
import udpm.hn.server.core.member.projectdetails.repository.MBMeTypeRepository;
import udpm.hn.server.core.member.projectdetails.service.MBTypeService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.http.HttpStatus;

public class MBTypeServiceImplTest {


    private MBMeTypeRepository mbMeTypeRepository;
    private MBTypeService mbTypeService;

    @BeforeEach
    void setUp() {
        mbMeTypeRepository = mock(MBMeTypeRepository.class);
        mbTypeService = new MBTypeServiceImpl(mbMeTypeRepository);
    }

    @Test
    void testGetAllType_success() {
        TypeTodoResponse type1 = mock(TypeTodoResponse.class);
        when(type1.getId()).thenReturn("1");
        when(type1.getType()).thenReturn("Công việc");

        TypeTodoResponse type2 = mock(TypeTodoResponse.class);
        when(type2.getId()).thenReturn("2");
        when(type2.getType()).thenReturn("Giải trí");

        List<TypeTodoResponse> mockTypeList = List.of(type1, type2);

        when(mbMeTypeRepository.getAllTypeTodoResponse()).thenReturn(mockTypeList);

        ResponseObject<?> response = mbTypeService.getAllType();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy Thành công type", response.getMessage());
        assertEquals(mockTypeList, response.getData());

        verify(mbMeTypeRepository, times(1)).getAllTypeTodoResponse();
    }

}
