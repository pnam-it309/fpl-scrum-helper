//package udpm.hn.server.core.admin.staff.service.impl;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import udpm.hn.server.core.admin.staff.model.response.ADStaffByDepartmentFacility;
//import udpm.hn.server.core.admin.staff.repository.ADStaffFacilityRepository;
//import udpm.hn.server.core.common.base.ResponseObject;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class ADStaffFacilityServiceImplTest {
//
//
//    @Mock
//    private ADStaffFacilityRepository adStaffFacilityRepository;
//
//    @InjectMocks
//    private ADStaffFacilityServiceImpl service;
//
//    private ADStaffByDepartmentFacility mockFacility(String id, String name) {
//        return new ADStaffByDepartmentFacility() {
//            public String getId() { return id; }
//
//            @Override
//            public String getIdDepartment() {
//                return null;
//            }
//
//            @Override
//            public String getIdMajor() {
//                return null;
//            }
//
//            @Override
//            public String getIdFacility() {
//                return null;
//            }
//
//            public String getNameFacility() { return name; }
//
//            @Override
//            public String getNameDepartment() {
//                return null;
//            }
//
//            @Override
//            public String getNameMajor() {
//                return null;
//            }
//        };
//    }
//
//    private List<ADStaffByDepartmentFacility> mockData;
//
//    @BeforeEach
//    void setUp() {
//        mockData = List.of(
//                mockFacility("f001", "Cơ sở A"),
//                mockFacility("f002", "Cơ sở B")
//        );
//    }
//
//    @Test
//    void testGetAllFacility_success() {
//        // Arrange
//        when(adStaffFacilityRepository.getAll()).thenReturn(mockData);
//
//        // Act
//        ResponseObject<?> response = service.getAllFacility();
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatus());
//        assertEquals("Lấy dữ liệu cơ sở thành công", response.getMessage());
//        assertEquals(mockData, response.getData());
//
//        verify(adStaffFacilityRepository, times(1)).getAll();
//    }
//
//}
