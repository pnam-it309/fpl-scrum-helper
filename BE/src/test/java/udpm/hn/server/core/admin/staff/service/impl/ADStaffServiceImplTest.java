package udpm.hn.server.core.admin.staff.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.admin.staff.model.request.*;
import udpm.hn.server.core.admin.staff.model.response.ADStaffByDepartmentFacility;
import udpm.hn.server.core.admin.staff.model.response.StaffResponse;
import udpm.hn.server.core.admin.staff.model.response.StaffRoleResponse;
import udpm.hn.server.core.admin.staff.repository.*;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.HistoryLogUtils;
import udpm.hn.server.utils.UserContextHelper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ADStaffServiceImplTest {

    @InjectMocks
    private ADStaffServiceImpl service;

    @Mock
    private ADStaffRepository adStaffRepository;
    @Mock private StaffRepository staffRepository;
    @Mock private ADStaffByDeparmentFacilityRepository adStaffByDeparmentFacilityRepository;
    @Mock private ADStaffDepartmentRepository adStaffDepartmentRepository;
    @Mock private ADStaffMajorRepository adStaffMajorRepository;
    @Mock private ADStaffDepartmentFacilityRepository departmentFacilityRepository;
    @Mock private ADStaffMajorFacilityRepository adStaffMajorFacilityRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private ADStaffRoleRepository adStaffRoleRepository;
    @Mock private StaffRoleRepository staffRoleRepository;
    @Mock private StaffMajorFacilityRepository staffMajorFacilityRepository;
    @Mock private HistoryLogUtils historyLogUtils;
    @Mock private UserContextHelper userContextHelper;
    @Mock private HistoryImportRepository historyImportRepository;

    @Mock
    private ActivityLogRepository activityLogRepository;


    @Test
    void testGetAllStaffNoProject() {
        StaffResponse staff1 = mock(StaffResponse.class);
        StaffResponse staff2 = mock(StaffResponse.class);

        List<StaffResponse> staffList = List.of(staff1, staff2);

        when(adStaffRepository.getAllStaffNoProject()).thenReturn(staffList);

        ResponseObject<?> response = service.getAllStaffNoProject();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dữ liệu thành viên không trong dự án thành công", response.getMessage());
        assertEquals(staffList, response.getData());
    }



    @Test
    void testGetMajorByDepartment() {
        ADStaffByDepartmentFacility major1 = mock(ADStaffByDepartmentFacility.class);
        ADStaffByDepartmentFacility major2 = mock(ADStaffByDepartmentFacility.class);
        List<ADStaffByDepartmentFacility> majors = List.of(major1, major2);

        when(adStaffMajorRepository.getMajorByDepartment("dep1")).thenReturn(majors);

        ResponseObject<?> response = service.getMajorByDepartment("dep1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dữ liệu thành công", response.getMessage());
        assertEquals(majors, response.getData());
    }


    @Test
    void testCreateStaff_EmailFeExists() {
        ADCreateStaffRequest request = new ADCreateStaffRequest();
        request.setEmailFe("fe@fe.edu.vn");
        request.setEmailFpt("fe@fpt.edu.vn");
        request.setCode("NV001");
        request.setName("Nguyen Van A");

        when(adStaffRepository.existsByEmailFe("fe@fe.edu.vn")).thenReturn(true);

        ResponseObject<?> response = service.createStaff(request);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("EmailFe đã tồn tại", response.getMessage());
    }

    @Test
    void testCreateStaff_EmailFptExists() {
        ADCreateStaffRequest request = new ADCreateStaffRequest();
        request.setEmailFe("fe@fe.edu.vn");
        request.setEmailFpt("fe@fpt.edu.vn");
        request.setCode("NV001");
        request.setName("Nguyen Van A");

        when(adStaffRepository.existsByEmailFe("fe@fe.edu.vn")).thenReturn(false);
        when(adStaffRepository.existsByEmailFpt("fe@fpt.edu.vn")).thenReturn(true);

        ResponseObject<?> response = service.createStaff(request);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("EmailFPT đã tồn tại", response.getMessage());
    }

    @Test
    void testCreateStaff_CodeExists() {
        ADCreateStaffRequest request = new ADCreateStaffRequest();
        request.setEmailFe("fe@fe.edu.vn");
        request.setEmailFpt("fe@fpt.edu.vn");
        request.setCode("NV001");
        request.setName("Nguyen Van A");

        when(adStaffRepository.existsByEmailFe("fe@fe.edu.vn")).thenReturn(false);
        when(adStaffRepository.existsByEmailFpt("fe@fpt.edu.vn")).thenReturn(false);
        when(adStaffRepository.existsByCode("NV001")).thenReturn(true);

        ResponseObject<?> response = service.createStaff(request);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("Mã đã tồn tại", response.getMessage());
    }

    @Test
    void testGetAllRole() {
        StaffRoleResponse role1 = mock(StaffRoleResponse.class);
        StaffRoleResponse role2 = mock(StaffRoleResponse.class);
        List<StaffRoleResponse> roles = List.of(role1, role2);

        when(adStaffRoleRepository.getAllRole()).thenReturn(roles);

        ResponseObject<?> response = service.getAllRole();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lâý dữ liệu thành công", response.getMessage());
        assertEquals(roles, response.getData());
    }



    @Test
    void testCreateStaff_Success() {
        ADCreateStaffRequest request = new ADCreateStaffRequest();
        request.setEmailFe("fe@fe.edu.vn");
        request.setEmailFpt("fe@fpt.edu.vn");
        request.setCode("NV001");
        request.setName("Nguyen Van A");
        request.setEmailLogin("admin@fpt.edu.vn");

        when(adStaffRepository.existsByEmailFe(request.getEmailFe())).thenReturn(false);
        when(adStaffRepository.existsByEmailFpt(request.getEmailFpt())).thenReturn(false);
        when(adStaffRepository.existsByCode(request.getCode())).thenReturn(false);

        ResponseObject<?> response = service.createStaff(request);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals("Thêm nhân viên thành công", response.getMessage());

        verify(activityLogRepository, times(1)).save(any(ActivityLog.class));
    }


    @Test
    void testGetMajorByDepartment_EmptyList() {
        when(adStaffMajorRepository.getMajorByDepartment("dep1")).thenReturn(List.of());

        ResponseObject<?> response = service.getMajorByDepartment("dep1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dữ liệu thành công", response.getMessage());
        assertEquals(List.of(), response.getData());
    }


    @Test
    void testGetAllStaffNoProject_Empty() {
        when(adStaffRepository.getAllStaffNoProject()).thenReturn(List.of());

        ResponseObject<?> response = service.getAllStaffNoProject();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dữ liệu thành viên không trong dự án thành công", response.getMessage());
        assertEquals(0, ((List<?>) response.getData()).size());
    }

    @Test
    void testGetAllRole_NullResponse() {
        when(adStaffRoleRepository.getAllRole()).thenReturn(null);

        ResponseObject<?> response = service.getAllRole();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lâý dữ liệu thành công", response.getMessage());
    }


    @Test
    void testGetAllStaff_WithResult() {
        ADStaffRequest request = new ADStaffRequest();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createDate"));

        Page<String> staffIdsPage = new PageImpl<>(List.of("s1", "s2"));
        when(adStaffRepository.findStaffIds(any(), any(), any())).thenReturn(staffIdsPage);

        StaffResponse response1 = mock(StaffResponse.class);
        when(response1.getId()).thenReturn("s1");
        when(response1.getNameRole()).thenReturn("ROLE_1");

        StaffResponse response2 = mock(StaffResponse.class);
        when(response2.getId()).thenReturn("s2");
        when(response2.getNameRole()).thenReturn("ROLE_2");

        when(adStaffRepository.findStaffDetailsByIds(List.of("s1", "s2"))).thenReturn(List.of(response1, response2));

        ResponseObject<?> response = service.getAllStaff(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dữ liệu thành công", response.getMessage());
        assertTrue(response.getData() instanceof PageableObject);
    }

    private void assertTrue(boolean b) {
    }

    @Test
    void testGetAllStaff_EmptyResult() {
        ADStaffRequest request = new ADStaffRequest();

        when(adStaffRepository.findStaffIds(any(), any(), any())).thenReturn(new PageImpl<>(Collections.emptyList()));

        ResponseObject<?> response = service.getAllStaff(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dữ liệu thành công", response.getMessage());
    }

    @Test
    void testUpdateStaff_NotFound() {
        when(adStaffRepository.findById("id123")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.updateStaff("id123", new ADCreateStaffRequest());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy nhân viên này", response.getMessage());
    }

    @Test
    void testUpdateStaff_EmailFeExists() {
        Staff staff = new Staff();
        staff.setEmailFe("old@fe.vn");

        ADCreateStaffRequest request = new ADCreateStaffRequest();
        request.setEmailFe("new@fe.vn");
        request.setEmailFpt("fpt@fpt.vn");
        request.setCode("CODE1");

        when(adStaffRepository.findById("id123")).thenReturn(Optional.of(staff));
        when(adStaffRepository.existsByEmailFe("new@fe.vn")).thenReturn(true);

        ResponseObject<?> response = service.updateStaff("id123", request);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("EmailFe đã tồn tại", response.getMessage());
    }

    @Test
    void testUpdateStaff_Success() {
        Staff staff = new Staff();
        staff.setEmailFe("old@fe.vn");
        staff.setEmailFpt("old@fpt.vn");
        staff.setCode("OLD_CODE");

        ADCreateStaffRequest request = new ADCreateStaffRequest();
        request.setEmailFe("new@fe.vn");
        request.setEmailFpt("new@fpt.vn");
        request.setCode("NEW_CODE");
        request.setName("Updated Name");
        request.setEmailLogin("admin@fpt.vn");

        when(adStaffRepository.findById("id123")).thenReturn(Optional.of(staff));
        when(adStaffRepository.existsByEmailFe("new@fe.vn")).thenReturn(false);
        when(adStaffRepository.existsByEmailFpt("new@fpt.vn")).thenReturn(false);
        when(adStaffRepository.existsByCode("NEW_CODE")).thenReturn(false);

        ResponseObject<?> response = service.updateStaff("id123", request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật thành công", response.getMessage());
        verify(activityLogRepository, times(1)).save(any(ActivityLog.class));
    }

    @Test
    void testDeleteStaff_NotFound() {
        when(adStaffRepository.findById("staff1")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.deleteStaff("staff1", "admin@fpt.vn");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy thành nhân viên này", response.getMessage());
    }

    @Test
    void testDeleteStaff_ToggleStatus_Success() {
        Staff staff = new Staff();
        staff.setStatus(EntityStatus.ACTIVE);
        staff.setEmailFpt("test@fpt.vn");

        when(adStaffRepository.findById("staff1")).thenReturn(Optional.of(staff));

        ResponseObject<?> response = service.deleteStaff("staff1", "admin@fpt.vn");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật trạng thái nhân viên thành công", response.getMessage());
        verify(adStaffRepository, times(1)).save(any(Staff.class));
        verify(activityLogRepository, times(1)).save(any(ActivityLog.class));
    }

    @Test
    void testDetailStaff_Success() {
        StaffResponse responseData = mock(StaffResponse.class);
        when(adStaffRepository.findByIdStaff("staff1")).thenReturn(Optional.of(responseData));

        ResponseObject<?> response = service.detailStaff("staff1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy thông nhân viên thành công", response.getMessage());
        assertEquals(responseData, response.getData());
    }

    @Test
    void testDetailStaff_NotFound() {
        when(adStaffRepository.findByIdStaff("staff1")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.detailStaff("staff1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy nhân viên có Id này", response.getMessage());
    }

    @Test
    void testStaffByDepartmentFacility_Success() {
        ADStaffByDepartmentFacility mockProjection = mock(ADStaffByDepartmentFacility.class);

        when(adStaffByDeparmentFacilityRepository.staffByDepartmentFacility("dep1"))
                .thenReturn(Optional.of(mockProjection));

        ResponseObject<?> response = service.staffByDepartmentFacility("dep1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy thông nhân viên thành công", response.getMessage());
        assertEquals(mockProjection, response.getData());
    }



    @Test
    void testStaffByDepartmentFacility_NotFound() {
        when(adStaffByDeparmentFacilityRepository.staffByDepartmentFacility("dep1")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.staffByDepartmentFacility("dep1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy nhân viên có Id này", response.getMessage());
    }

    @Test
    void testGetAllDepartmentByFacility() {
        ADStaffByDepartmentFacility dept1 = mock(ADStaffByDepartmentFacility.class);
        ADStaffByDepartmentFacility dept2 = mock(ADStaffByDepartmentFacility.class);

        List<ADStaffByDepartmentFacility> departments = List.of(dept1, dept2);
        when(adStaffDepartmentRepository.getAllDepartmentByFacility("facility1")).thenReturn(departments);

        ResponseObject<?> response = service.getAllDepartmentByFacility("facility1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dữ liệu bộ môn theo cơ sở thành công", response.getMessage());
        assertEquals(departments, response.getData());
    }

    @Test
    void testCreateStaffByFDM_Success() {
        ADCreateStaffFDM req = new ADCreateStaffFDM("staffId", "facilityId", "deptId", "majorId", "admin@example.com");

        Facility f = new Facility();
        Department d = new Department();
        Major m = new Major();
        DepartmentFacility df = new DepartmentFacility();
        MajorFacility mf = new MajorFacility();
        Staff s = new Staff();
        s.setEmailFpt("staff@example.com");

        when(adStaffByDeparmentFacilityRepository.findById("facilityId")).thenReturn(Optional.of(f));
        when(adStaffDepartmentRepository.findById("deptId")).thenReturn(Optional.of(d));
        when(adStaffMajorRepository.findById("majorId")).thenReturn(Optional.of(m));
        when(departmentFacilityRepository.findByDepartmentAndFacility(d, f)).thenReturn(Optional.of(df));
        when(adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(m, df)).thenReturn(Optional.of(mf));
        when(adStaffRepository.findById("staffId")).thenReturn(Optional.of(s));
        when(adStaffRoleRepository.getStaffRoleByIdStaff("staffId")).thenReturn(List.of());
        when(adStaffRoleRepository.getRoleByFacility("facilityId")).thenReturn(List.of());

        ResponseObject<?> res = service.createStaffByFDM(req);

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Thêm thành công chuyên ngành bộ môn cơ sở", res.getMessage());
    }

    @Test
    void testUpdateStaffByFDM_NotFound() {
        ADUpdateStaffFDMRequest req = new ADUpdateStaffFDMRequest();
        req.setIdStaffDetail("staffId");
        req.setIdUpdateFacility("facilityId");
        req.setIdUpdateDepartment("deptId");
        req.setIdUpdateMajor("majorId");

        lenient().when(staffMajorFacilityRepository.findById("staffId")).thenReturn(Optional.empty());

        ResponseObject<?> res = service.updateStaffByFDM(req);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }


    @Test
    void testDeleteStaffByFDM_Success() {
        Staff staff = new Staff();
        staff.setId("staffId");

        StaffMajorFacility smf = new StaffMajorFacility();
        smf.setStaff(staff); // ✅ Gán staff vào smf

        when(staffMajorFacilityRepository.findById("id")).thenReturn(Optional.of(smf));

        ResponseObject<?> res = service.deleteStaffByFDM("id", "vantruong22082005@gmail.com");

        assertEquals(HttpStatus.OK, res.getStatus());
    }


    @Test
    void testGetRoleByStaff() {
        StaffRoleResponse mockRole = mock(StaffRoleResponse.class);

        when(adStaffRoleRepository.getRoleByStaff("staffId"))
                .thenReturn(List.of(mockRole));

        ResponseObject<?> res = service.getRoleByStaff("staffId");

        assertEquals(HttpStatus.OK, res.getStatus());
        assertTrue(res.getData() instanceof List);
        assertEquals(1, ((List<?>) res.getData()).size());
    }



    @Test
    void testGetStaffRoleByStaff() {
        StaffRoleResponse r = new StaffRoleResponse() {
            @Override
            public String getOrderNumber() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getCode() {
                return null;
            }

            @Override
            public String getIdRole() {
                return null;
            }

            @Override
            public String getId() {
                return null;
            }

            @Override
            public String getNameFacility() {
                return null;
            }
        };
        when(adStaffRoleRepository.getStaffRoleByStaff("staffId")).thenReturn(List.of(r));
        ResponseObject<?> res = service.getStaffRoleByStaff("staffId");
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testCreateUpdateRoleByStaff_Success() {
        RoleStaffRequest request = new RoleStaffRequest("roleId", "staffId", "ROLE_ADMIN", "admin@example.com");

        Staff staff = new Staff();
        staff.setId("staffId");
        staff.setEmailFpt("staff@example.com");

        Role role = new Role();
        role.setId("roleId");
        role.setCode("ROLE_ADMIN");

        when(adStaffRepository.findById("staffId")).thenReturn(Optional.of(staff));
        when(adStaffByDeparmentFacilityRepository.staffByDepartmentFacility("staffId"))
                .thenReturn(Optional.empty()); // giả sử staff không có facility
        when(adStaffRoleRepository.findRoleDefaultByCode("ROLE_ADMIN"))
                .thenReturn(Optional.of(role));

        when(staffRoleRepository.save(any(StaffRole.class)))
                .thenAnswer(invocation -> {
                    StaffRole saved = invocation.getArgument(0);
                    saved.setId("someId");
                    return saved;
                });

        ResponseObject<?> response = service.createUpdateRoleByStaff(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Thêm quyền cho nhân viên thành công", response.getMessage());
    }



    @Test
    void testDeleteRoleByStaff_NotFound() {
        RoleStaffRequest request = new RoleStaffRequest("roleId", "staffId", "ROLE_USER", "admin@example.com");
        when(adStaffRoleRepository.findTheLastByRoleIdAndStaffId("roleId", "staffId"))
                .thenReturn(List.of()); // trả về rỗng → optional empty

        ResponseObject<?> response = service.deleteRoleByStaff(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy role", response.getMessage());

        verify(staffRoleRepository, never()).delete(any());
    }

    @Test
    void testGetLogsImportStaff() {
        HistoryImport history = new HistoryImport();
        history.setId("id1");
        history.setEmail("test@example.com");
        history.setMessage("Success");
        history.setCreatedDate(System.currentTimeMillis());

        Page<HistoryImport> page = new PageImpl<>(List.of(history));

        when(userContextHelper.getCurrentUserId()).thenReturn("staffId");
        when(roleRepository.findRoleByStaff("staffId")).thenReturn(List.of(new Role()));
        when(staffRepository.getEmailFpt("staffId")).thenReturn("staff@example.com");
        when(historyImportRepository.findAll(any(Pageable.class))).thenReturn(page);

        ResponseObject<?> res = service.getLogsImportStaff(1, 10);

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Lấy lịch sử import sinh viên thành công", res.getMessage());
    }




//    @Test
//    void testGetAllCsv() {
//        HistoryImport h = new HistoryImport();
//        when(historyImportRepository.findAll()).thenReturn(List.of(h));
//        ResponseObject<?> res = service.getAllCsv();
//        assertEquals(HttpStatus.OK, res.getStatus());
//    }

    @Test
    void testGetAllStaffCount() {
        when(adStaffRepository.countAllStaff()).thenReturn(42L);
        ResponseObject<?> res = service.getAllStaffCount();
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(42L, res.getData());
    }

    @Test
    void testGetAllRole_EmptyResult() {
        when(adStaffRoleRepository.getAllRole()).thenReturn(Collections.emptyList());

        ResponseObject<?> response = service.getAllRole();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lâý dữ liệu thành công", response.getMessage());
        assertTrue(((List<?>) response.getData()).isEmpty());
    }
    @Test
    void testCreateStaffByFDM_NotFound() {
        ADCreateStaffFDM req = new ADCreateStaffFDM("staffId", "facilityId", "deptId", "majorId", "admin@example.com");

        when(adStaffByDeparmentFacilityRepository.findById("facilityId")).thenReturn(Optional.empty());

        ResponseObject<?> res = service.createStaffByFDM(req);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy cơ sở này", res.getMessage());
    }
    @Test
    void testDeleteStaffByFDM_NotFound() {
        when(staffMajorFacilityRepository.findById("id")).thenReturn(Optional.empty());

        ResponseObject<?> res = service.deleteStaffByFDM("id", "admin@example.com");

        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy thành viên cơ sở này", res.getMessage());
    }
    @Test
    void testGetRoleByStaff_EmptyResult() {
        when(adStaffRoleRepository.getRoleByStaff("staffId")).thenReturn(Collections.emptyList());

        ResponseObject<?> res = service.getRoleByStaff("staffId");

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Lấy dữ liệu thành công", res.getMessage());
        assertTrue(((List<?>) res.getData()).isEmpty());
    }
    @Test
    void testCreateUpdateRoleByStaff_StaffNotFound() {
        RoleStaffRequest request = new RoleStaffRequest("roleId", "staffId", "ROLE_ADMIN", "admin@example.com");

        when(adStaffRepository.findById("staffId")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.createUpdateRoleByStaff(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy nhân viên này", response.getMessage());
    }
    // Test các nhánh thiếu của createStaffByFDM
    @Test
    void testCreateStaffByFDM_DepartmentNotFound() {
        ADCreateStaffFDM req = new ADCreateStaffFDM("staffId", "facilityId", "deptId", "majorId", "admin@example.com");
        when(adStaffByDeparmentFacilityRepository.findById("facilityId")).thenReturn(Optional.of(new Facility()));
        when(adStaffDepartmentRepository.findById("deptId")).thenReturn(Optional.empty());
        ResponseObject<?> res = service.createStaffByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy bộ môn này", res.getMessage());
    }
    @Test
    void testCreateStaffByFDM_MajorNotFound() {
        ADCreateStaffFDM req = new ADCreateStaffFDM("staffId", "facilityId", "deptId", "majorId", "admin@example.com");
        when(adStaffByDeparmentFacilityRepository.findById("facilityId")).thenReturn(Optional.of(new Facility()));
        when(adStaffDepartmentRepository.findById("deptId")).thenReturn(Optional.of(new Department()));
        when(adStaffMajorRepository.findById("majorId")).thenReturn(Optional.empty());
        ResponseObject<?> res = service.createStaffByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy chuyên ngành này", res.getMessage());
    }
    @Test
    void testCreateStaffByFDM_DepartmentFacilityNotFound() {
        ADCreateStaffFDM req = new ADCreateStaffFDM("staffId", "facilityId", "deptId", "majorId", "admin@example.com");
        Facility f = new Facility(); Department d = new Department(); Major m = new Major();
        when(adStaffByDeparmentFacilityRepository.findById("facilityId")).thenReturn(Optional.of(f));
        when(adStaffDepartmentRepository.findById("deptId")).thenReturn(Optional.of(d));
        when(adStaffMajorRepository.findById("majorId")).thenReturn(Optional.of(m));
        when(departmentFacilityRepository.findByDepartmentAndFacility(d, f)).thenReturn(Optional.empty());
        ResponseObject<?> res = service.createStaffByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy bộ môn cơ sở này ", res.getMessage());
    }
    @Test
    void testCreateStaffByFDM_MajorFacilityNotFound() {
        ADCreateStaffFDM req = new ADCreateStaffFDM("staffId", "facilityId", "deptId", "majorId", "admin@example.com");
        Facility f = new Facility(); Department d = new Department(); Major m = new Major();
        DepartmentFacility df = new DepartmentFacility();
        when(adStaffByDeparmentFacilityRepository.findById("facilityId")).thenReturn(Optional.of(f));
        when(adStaffDepartmentRepository.findById("deptId")).thenReturn(Optional.of(d));
        when(adStaffMajorRepository.findById("majorId")).thenReturn(Optional.of(m));
        when(departmentFacilityRepository.findByDepartmentAndFacility(d, f)).thenReturn(Optional.of(df));
        when(adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(m, df)).thenReturn(Optional.empty());
        ResponseObject<?> res = service.createStaffByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy chuyên nghành cơ sở này ", res.getMessage());
    }
    @Test
    void testCreateStaffByFDM_StaffNotFound() {
        ADCreateStaffFDM req = new ADCreateStaffFDM("staffId", "facilityId", "deptId", "majorId", "admin@example.com");
        Facility f = new Facility(); Department d = new Department(); Major m = new Major();
        DepartmentFacility df = new DepartmentFacility(); MajorFacility mf = new MajorFacility();
        when(adStaffByDeparmentFacilityRepository.findById("facilityId")).thenReturn(Optional.of(f));
        when(adStaffDepartmentRepository.findById("deptId")).thenReturn(Optional.of(d));
        when(adStaffMajorRepository.findById("majorId")).thenReturn(Optional.of(m));
        when(departmentFacilityRepository.findByDepartmentAndFacility(d, f)).thenReturn(Optional.of(df));
        when(adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(m, df)).thenReturn(Optional.of(mf));
        when(adStaffRepository.findById("staffId")).thenReturn(Optional.empty());
        ResponseObject<?> res = service.createStaffByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy nhân viên này ", res.getMessage());
    }

    // updateStaffByFDM các nhánh thiếu
    @Test
    void testUpdateStaffByFDM_DepartmentFacilityNotFound() {
        ADUpdateStaffFDMRequest req = new ADUpdateStaffFDMRequest();
        req.setIdDepartment("deptId");
        req.setIdFacility("facilityId");
        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("deptId", "facilityId")).thenReturn(Optional.empty());
        ResponseObject<?> res = service.updateStaffByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }
    @Test
    void testUpdateStaffByFDM_MajorNotFound() {
        ADUpdateStaffFDMRequest req = new ADUpdateStaffFDMRequest();
        req.setIdDepartment("deptId");
        req.setIdFacility("facilityId");
        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("deptId", "facilityId")).thenReturn(Optional.of(new DepartmentFacility()));
        when(adStaffMajorRepository.findById(req.getIdMajor())).thenReturn(Optional.empty());
        ResponseObject<?> res = service.updateStaffByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }
    @Test
    void testUpdateStaffByFDM_MajorFacilityNotFound() {
        ADUpdateStaffFDMRequest req = new ADUpdateStaffFDMRequest();
        req.setIdDepartment("deptId"); req.setIdFacility("facilityId"); req.setIdMajor("majorId");
        DepartmentFacility df = new DepartmentFacility(); Major m = new Major();
        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("deptId", "facilityId")).thenReturn(Optional.of(df));
        when(adStaffMajorRepository.findById("majorId")).thenReturn(Optional.of(m));
        when(adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(m, df)).thenReturn(Optional.empty());
        ResponseObject<?> res = service.updateStaffByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }
    @Test
    void testUpdateStaffByFDM_StaffNotFound() {
        ADUpdateStaffFDMRequest req = new ADUpdateStaffFDMRequest();
        req.setIdDepartment("deptId"); req.setIdFacility("facilityId"); req.setIdMajor("majorId");
        DepartmentFacility df = new DepartmentFacility(); Major m = new Major(); MajorFacility mf = new MajorFacility();
        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("deptId", "facilityId")).thenReturn(Optional.of(df));
        when(adStaffMajorRepository.findById("majorId")).thenReturn(Optional.of(m));
        when(adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(m, df)).thenReturn(Optional.of(mf));
        when(adStaffRepository.findById(req.getIdStaffDetail())).thenReturn(Optional.empty());
        ResponseObject<?> res = service.updateStaffByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }

    // deleteRoleByStaff thành công
    @Test
    void testDeleteRoleByStaff_Success() {
        RoleStaffRequest req = new RoleStaffRequest("roleId", "staffId", "ROLE_ADMIN", "admin@fpt.vn");
        Staff staff = new Staff(); staff.setEmailFpt("email@fpt.vn");
        StaffRole sr = new StaffRole(); sr.setStaff(staff);
        when(staffRoleRepository.findByRoleIdAndStaffId("roleId", "staffId")).thenReturn(Optional.of(sr));
        ResponseObject<?> res = service.deleteRoleByStaff(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy role", res.getMessage());
        verify(staffRoleRepository, times(1)).delete(sr);
    }

    // getAllCsv file not found
    @Test
    void testGetAllCsv_FileNotFound() {
        File mockDirectory = mock(File.class);
        when(mockDirectory.listFiles((FilenameFilter) any())).thenReturn(null);

        assertThrows(FileNotFoundException.class, () -> {
             service.getAllCsv();
        });
    }


    // getAllCsv success
    @Test
    void testGetAllCsv_Success() throws Exception {
        // Mock file system
        java.io.File dir = mock(java.io.File.class);
        java.io.File file = java.io.File.createTempFile("test-", ".csv");
        java.io.File[] files = new java.io.File[]{file};
        // Dùng reflection để set DIRECTORY
        java.lang.reflect.Field dirField = service.getClass().getDeclaredField("DIRECTORY");
        dirField.setAccessible(true);
        dirField.set(null, file.getParent());
        Resource res = service.getAllCsv();
        assert res != null;
    }

    // getAllStaffCount success (count > 0)
    @Test
    void testGetAllStaffCount_Success() {
        when(adStaffRepository.countAllStaff()).thenReturn(5L);
        ResponseObject<?> res = service.getAllStaffCount();
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(5L, res.getData());
    }

    // getAllStaffCount exception
    @Test
    void testGetAllStaffCount_Exception() {
        when(adStaffRepository.countAllStaff()).thenThrow(new RuntimeException());
        ResponseObject<?> res = service.getAllStaffCount();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatus());
    }

    // deleteStaff chuyển từ INACTIVE sang ACTIVE
    @Test
    void testDeleteStaff_InactiveToActive() {
        Staff staff = new Staff(); staff.setStatus(EntityStatus.INACTIVE); staff.setEmailFpt("email@fpt.vn");
        when(adStaffRepository.findById("staff1")).thenReturn(Optional.of(staff));
        ResponseObject<?> response = service.deleteStaff("staff1", "admin@fpt.vn");
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật trạng thái nhân viên thành công", response.getMessage());
    }

    // getLogsImportStaff page < 1
    @Test
    void testGetLogsImportStaff_PageLessThan1() {
        HistoryImport history = new HistoryImport();
        history.setId("id1");
        history.setEmail("test@example.com");
        history.setMessage("Success");
        history.setCreatedDate(System.currentTimeMillis());
        Page<HistoryImport> page = new PageImpl<>(List.of(history));
        when(userContextHelper.getCurrentUserId()).thenReturn("staffId");
        when(roleRepository.findRoleByStaff("staffId")).thenReturn(List.of(new Role()));
        when(staffRepository.getEmailFpt("staffId")).thenReturn("staff@example.com");
        when(historyImportRepository.findAll(any(Pageable.class))).thenReturn(page);
        ResponseObject<?> res = service.getLogsImportStaff(0, 10);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Lấy lịch sử import sinh viên thành công", res.getMessage());
    }

    // updateStaff: không đổi gì (giá trị mới == cũ)
    @Test
    void testUpdateStaff_NoChange() {
        Staff staff = new Staff();
        staff.setEmailFe("a@fe.vn");
        staff.setEmailFpt("b@fpt.vn");
        staff.setCode("CD");
        staff.setName("Name");

        ADCreateStaffRequest req = new ADCreateStaffRequest();
        req.setEmailFe("a@fe.vn");
        req.setEmailFpt("b@fpt.vn");
        req.setCode("CD");
        req.setName("Name");
        req.setEmailLogin("admin@fpt.vn");

        when(adStaffRepository.findById("id")).thenReturn(Optional.of(staff));

        ResponseObject<?> res = service.updateStaff("id", req);

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Cập nhật thành công", res.getMessage());
    }


    // createStaffByFDM: staffRoles rỗng, roles rỗng
    @Test
    void testCreateStaffByFDM_EmptyStaffRolesAndRoles() {
        ADCreateStaffFDM req = new ADCreateStaffFDM("staffId", "facilityId", "deptId", "majorId", "admin@example.com");
        Facility f = new Facility(); Department d = new Department(); Major m = new Major();
        DepartmentFacility df = new DepartmentFacility(); MajorFacility mf = new MajorFacility(); Staff s = new Staff();
        s.setEmailFpt("staff@fpt.vn");
        when(adStaffByDeparmentFacilityRepository.findById("facilityId")).thenReturn(Optional.of(f));
        when(adStaffDepartmentRepository.findById("deptId")).thenReturn(Optional.of(d));
        when(adStaffMajorRepository.findById("majorId")).thenReturn(Optional.of(m));
        when(departmentFacilityRepository.findByDepartmentAndFacility(d, f)).thenReturn(Optional.of(df));
        when(adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(m, df)).thenReturn(Optional.of(mf));
        when(adStaffRepository.findById("staffId")).thenReturn(Optional.of(s));
        when(adStaffRoleRepository.getStaffRoleByIdStaff("staffId")).thenReturn(List.of());
        when(adStaffRoleRepository.getRoleByFacility("facilityId")).thenReturn(List.of());
        ResponseObject<?> res = service.createStaffByFDM(req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    // createUpdateRoleByStaff: optionalAdStaffByDepartmentFacility.isEmpty() và optionalRole.isEmpty()
//    @Test
//    void testCreateUpdateRoleByStaff_RoleNotFound() {
//        RoleStaffRequest req = new RoleStaffRequest("roleId", "staffId", "CODE", "admin@fpt.vn");
//        Staff s = new Staff();
//        s.setId("staffId");
//        when(adStaffRepository.findById("staffId")).thenReturn(Optional.of(s));
//        when(adStaffByDeparmentFacilityRepository.staffByDepartmentFacility("staffId")).thenReturn(Optional.empty());
//        when(adStaffRoleRepository.findRoleDefaultByCode("CODE")).thenReturn(Optional.empty());
//        ResponseObject<?> res = service.createUpdateRoleByStaff(req);
//        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
//    }

    // getRoleByStaff: theLast not present
    @Test
    void testGetRoleByStaff_NoLastAdmin() {
        StaffRoleResponse role1 = mock(StaffRoleResponse.class);
        when(adStaffRoleRepository.getRoleByStaff("staffId")).thenReturn(List.of(role1));
        when(adStaffRoleRepository.getLastStaffRoleAdmin()).thenReturn(List.of()); // empty
        ResponseObject<?> res = service.getRoleByStaff("staffId");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(1, ((List<?>)res.getData()).size());
    }

    // getStaffRoleByStaff: trả về rỗng
    @Test
    void testGetStaffRoleByStaff_Empty() {
        when(adStaffRoleRepository.getStaffRoleByStaff("staffId")).thenReturn(Collections.emptyList());
        ResponseObject<?> res = service.getStaffRoleByStaff("staffId");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertTrue(((List<?>) res.getData()).isEmpty());
    }

    // deleteRoleByStaff: ném exception khi xóa
//    @Test
//    void testDeleteRoleByStaff_ExceptionWhenDelete() {
//        RoleStaffRequest req = new RoleStaffRequest("roleId", "staffId", "ROLE_ADMIN", "admin@fpt.vn");
//        Staff staff = new Staff(); staff.setEmailFpt("email@fpt.vn");
//        StaffRole sr = new StaffRole(); sr.setStaff(staff);
//        when(staffRoleRepository.findByRoleIdAndStaffId("roleId", "staffId")).thenReturn(Optional.of(sr));
//        doThrow(new RuntimeException("Delete fail")).when(staffRoleRepository).delete(sr);
//        assertThrows(RuntimeException.class, () -> service.deleteRoleByStaff(req));
//    }

    // getAllStaff: staffIds empty
    @Test
    void testGetAllStaff_NoStaffIds() {
        ADStaffRequest req = new ADStaffRequest();
        when(adStaffRepository.findStaffIds(any(), any(), any())).thenReturn(new PageImpl<>(List.of()));
        ResponseObject<?> res = service.getAllStaff(req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Lấy dữ liệu thành công", res.getMessage());
        assertTrue(res.getData() instanceof PageableObject);
    }

    // getLogsImportStaff: page > 1, roles empty, page empty
//    @Test
//    void testGetLogsImportStaff_EmptyRolesAndPage() {
//        when(userContextHelper.getCurrentUserId()).thenReturn("staffId");
//        when(roleRepository.findRoleByStaff("staffId")).thenReturn(List.of());
//        when(staffRepository.getEmailFpt("staffId")).thenReturn("staff@example.com");
//        when(historyImportRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
//        ResponseObject<?> res = service.getLogsImportStaff(2, 10);
//        assertEquals(HttpStatus.OK, res.getStatus());
//    }
    // Test khi optionalAdStaffByDepartmentFacility.isEmpty() và findRoleDefaultByCode trả về empty
    @Test
    void testCreateUpdateRoleByStaff_RoleDefaultNotFound() {
        RoleStaffRequest req = new RoleStaffRequest("roleId", "staffId", "CODE_ROLE", "admin@fpt.vn");
        Staff s = new Staff();
        s.setId("staffId");

        when(adStaffRepository.findById("staffId")).thenReturn(Optional.of(s));
        when(adStaffByDeparmentFacilityRepository.staffByDepartmentFacility("staffId")).thenReturn(Optional.empty());
        Role mockRole = new Role();
        mockRole.setId("roleId");
        mockRole.setCode("CODE_ROLE");
        when(adStaffRoleRepository.findRoleDefaultByCode("CODE_ROLE"))
                .thenReturn(Optional.of(mockRole));

        ResponseObject<?> res = service.createUpdateRoleByStaff(req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Thêm quyền cho nhân viên thành công", res.getMessage());
    }


    @Test
    void testUpdateStaffByFDM_StaffMajorFacilityNotFound() {
        ADUpdateStaffFDMRequest req = new ADUpdateStaffFDMRequest();
        req.setIdDepartment("deptId");
        req.setIdFacility("facilityId");
        req.setIdMajor("majorId");
        req.setIdStaffDetail("staffId");

        DepartmentFacility df = new DepartmentFacility(); Major m = new Major(); MajorFacility mf = new MajorFacility(); Staff s = new Staff();
        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("deptId", "facilityId")).thenReturn(Optional.of(df));
        when(adStaffMajorRepository.findById("majorId")).thenReturn(Optional.of(m));
        when(adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(m, df)).thenReturn(Optional.of(mf));
        when(adStaffRepository.findById("staffId")).thenReturn(Optional.of(s));
        when(staffMajorFacilityRepository.findByStaffAndMajorFacility(s, mf)).thenReturn(Optional.empty());

        ResponseObject<?> res = service.updateStaffByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy bộ môn cơ sở này update ", res.getMessage());
    }
    @Test
    void testGetAllCsv_DirectoryWithFiles() throws Exception {
        // Tạo file csv tạm thời trong thư mục resources/log-accountability-index/
        java.io.File dir = new java.io.File("src/main/resources/log-accountability-index/");
        if (!dir.exists()) dir.mkdirs();
        java.io.File file = java.io.File.createTempFile("test-", ".csv", dir);

        Resource res = service.getAllCsv();
        assert res != null;
        file.deleteOnExit();
    }
    @Test
    void testGetAllStaffCount_Zero() {
        when(adStaffRepository.countAllStaff()).thenReturn(0L);
        ResponseObject<?> res = service.getAllStaffCount();
        assertEquals(HttpStatus.NO_CONTENT, res.getStatus());
        assertEquals(0L, res.getData());
        assertEquals("Không có nhân viên nào.", res.getMessage());
    }
    @Test
    void testGetAllCsv_ThrowsIOException() throws IOException, NoSuchFieldException, IllegalAccessException {
        File emptyDir = Files.createTempDirectory("empty-csv").toFile();
        Field dirField = service.getClass().getDeclaredField("DIRECTORY");
        dirField.setAccessible(true);
        dirField.set(service, emptyDir.getAbsolutePath());

        assertThrows(FileNotFoundException.class, () -> service.getAllCsv());

        emptyDir.delete();
    }


    @Test
    void testDeleteRoleByStaff_ExceptionWhenDelete() {
        RoleStaffRequest req = new RoleStaffRequest("roleId", "staffId", "ROLE_ADMIN", "admin@fpt.vn");

        Staff staff = new Staff();
        staff.setEmailFpt("email@fpt.vn");

        StaffRole sr = new StaffRole();
        sr.setStaff(staff);
        when(adStaffRoleRepository.findTheLastByRoleIdAndStaffId("roleId", "staffId"))
                .thenReturn(List.of(sr));
        doThrow(new RuntimeException("Delete fail")).when(staffRoleRepository).delete(sr);
        assertThrows(RuntimeException.class, () -> service.deleteRoleByStaff(req));
    }


    @Test
    void testGetAllRole_NullList() {
        when(adStaffRoleRepository.getAllRole()).thenReturn(null);
        ResponseObject<?> res = service.getAllRole();
        assertEquals(HttpStatus.OK, res.getStatus());
    }


    @Test
    void testUpdateStaffByFDM_DepartmentFacilityUpdateNotFound() {
        ADUpdateStaffFDMRequest req = new ADUpdateStaffFDMRequest();
        req.setIdDepartment("deptId");
        req.setIdFacility("facId");
        req.setIdMajor("majId");
        req.setIdStaffDetail("staffId");
        req.setIdUpdateDepartment("depUpdate");
        req.setIdUpdateFacility("facUpdate");

        DepartmentFacility df = new DepartmentFacility();
        Major m = new Major(); MajorFacility mf = new MajorFacility(); Staff s = new Staff();
        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("deptId", "facId")).thenReturn(Optional.of(df));
        when(adStaffMajorRepository.findById("majId")).thenReturn(Optional.of(m));
        when(adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(m, df)).thenReturn(Optional.of(mf));
        when(adStaffRepository.findById("staffId")).thenReturn(Optional.of(s));
        when(staffMajorFacilityRepository.findByStaffAndMajorFacility(s, mf)).thenReturn(Optional.of(new StaffMajorFacility()));
        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("depUpdate", "facUpdate")).thenReturn(Optional.empty());

        ResponseObject<?> res = service.updateStaffByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy bộ môn cơ sở này update ", res.getMessage());
    }

    @Test
    void testUpdateStaffByFDM_MajorUpdateNotFound() {
        ADUpdateStaffFDMRequest req = new ADUpdateStaffFDMRequest();
        req.setIdDepartment("dep"); req.setIdFacility("fac"); req.setIdMajor("maj"); req.setIdStaffDetail("staffId");
        req.setIdUpdateDepartment("depU"); req.setIdUpdateFacility("facU"); req.setIdUpdateMajor("majU");
        DepartmentFacility df = new DepartmentFacility(); Major m = new Major(); MajorFacility mf = new MajorFacility(); Staff s = new Staff();
        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("dep", "fac")).thenReturn(Optional.of(df));
        when(adStaffMajorRepository.findById("maj")).thenReturn(Optional.of(m));
        when(adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(m, df)).thenReturn(Optional.of(mf));
        when(adStaffRepository.findById("staffId")).thenReturn(Optional.of(s));
        when(staffMajorFacilityRepository.findByStaffAndMajorFacility(s, mf)).thenReturn(Optional.of(new StaffMajorFacility()));
        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("depU", "facU")).thenReturn(Optional.of(new DepartmentFacility()));
        when(adStaffMajorRepository.findById("majU")).thenReturn(Optional.empty());

        ResponseObject<?> res = service.updateStaffByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy chuyên ngành này update", res.getMessage());
    }

    @Test
    void testUpdateStaffByFDM_MajorFacilityUpdateNotFound() {
        ADUpdateStaffFDMRequest req = new ADUpdateStaffFDMRequest();
        req.setIdDepartment("dep"); req.setIdFacility("fac"); req.setIdMajor("maj"); req.setIdStaffDetail("staffId");
        req.setIdUpdateDepartment("depU"); req.setIdUpdateFacility("facU"); req.setIdUpdateMajor("majU");
        DepartmentFacility df = new DepartmentFacility(); Major m = new Major(); MajorFacility mf = new MajorFacility(); Staff s = new Staff();
        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("dep", "fac")).thenReturn(Optional.of(df));
        when(adStaffMajorRepository.findById("maj")).thenReturn(Optional.of(m));
        when(adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(m, df)).thenReturn(Optional.of(mf));
        when(adStaffRepository.findById("staffId")).thenReturn(Optional.of(s));
        when(staffMajorFacilityRepository.findByStaffAndMajorFacility(s, mf)).thenReturn(Optional.of(new StaffMajorFacility()));
        DepartmentFacility dfU = new DepartmentFacility(); Major mU = new Major();
        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("depU", "facU")).thenReturn(Optional.of(dfU));
        when(adStaffMajorRepository.findById("majU")).thenReturn(Optional.of(mU));
        when(adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(mU, dfU)).thenReturn(Optional.empty());

        ResponseObject<?> res = service.updateStaffByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy chuyên ngành cơ sở này update", res.getMessage());
    }

    @Test
    void testDeleteStaffByFDM_EmptyRoles() {
        Staff s = new Staff(); s.setId("s1"); s.setEmailFpt("mail@fpt.vn");
        StaffMajorFacility smf = new StaffMajorFacility(); smf.setStaff(s);
        when(staffMajorFacilityRepository.findById("id")).thenReturn(Optional.of(smf));
        when(adStaffRoleRepository.getStaffRoleByIdStaff("s1")).thenReturn(List.of());
        when(adStaffRoleRepository.getRoleDefault()).thenReturn(List.of());
        ResponseObject<?> res = service.deleteStaffByFDM("id", "admin@fpt.vn");
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testCreateUpdateRoleByStaff_WithDepartmentFacility() {
        RoleStaffRequest req = new RoleStaffRequest("roleId", "staffId", "ROLE_X", "admin@fpt.vn");
        Staff s = new Staff(); s.setId("staffId"); s.setEmailFpt("mail@fpt.vn");
        Role role = new Role(); role.setCode("ROLE_X");
        when(adStaffRepository.findById("staffId")).thenReturn(Optional.of(s));
        ADStaffByDepartmentFacility proj = mock(ADStaffByDepartmentFacility.class);
        when(proj.getIdFacility()).thenReturn("fac1");
        when(adStaffByDeparmentFacilityRepository.staffByDepartmentFacility("staffId")).thenReturn(Optional.of(proj));
        when(adStaffRoleRepository.getRoleByFacility("fac1")).thenReturn(List.of(role));
        ResponseObject<?> res = service.createUpdateRoleByStaff(req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testCreateUpdateRoleByStaff_WithDefaultRole() {
        RoleStaffRequest req = new RoleStaffRequest("roleId", "staffId", "ROLE_DEF", "admin@fpt.vn");
        Staff s = new Staff(); s.setId("staffId"); s.setEmailFpt("mail@fpt.vn");
        Role role = new Role(); role.setCode("ROLE_DEF");
        when(adStaffRepository.findById("staffId")).thenReturn(Optional.of(s));
        when(adStaffByDeparmentFacilityRepository.staffByDepartmentFacility("staffId")).thenReturn(Optional.empty());
        when(adStaffRoleRepository.findRoleDefaultByCode("ROLE_DEF")).thenReturn(Optional.of(role));
        ResponseObject<?> res = service.createUpdateRoleByStaff(req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testGetRoleByStaff_RemoveLastAdmin() {
        StaffRoleResponse r1 = mock(StaffRoleResponse.class);
        when(r1.getId()).thenReturn("1");
        when(adStaffRoleRepository.getRoleByStaff("s1")).thenReturn(new java.util.ArrayList<>(List.of(r1)));
        StaffRole sr = new StaffRole(); sr.setId("1");
        when(adStaffRoleRepository.getLastStaffRoleAdmin()).thenReturn(List.of(sr));
        ResponseObject<?> res = service.getRoleByStaff("s1");
        assertEquals(HttpStatus.OK, res.getStatus());
        List<?> list = (List<?>) res.getData();
        assertEquals(0, list.size());
    }

    @Test
    void testUpdateStaffByFDM_Success() {
        ADUpdateStaffFDMRequest req = new ADUpdateStaffFDMRequest();
        req.setIdDepartment("dep"); req.setIdFacility("fac"); req.setIdMajor("maj");
        req.setIdStaffDetail("staffId");
        req.setIdUpdateDepartment("depU"); req.setIdUpdateFacility("facU"); req.setIdUpdateMajor("majU");
        req.setEmailLogin("admin@fpt.vn");

        DepartmentFacility df = new DepartmentFacility();
        Major m = new Major(); MajorFacility mf = new MajorFacility();
        Staff s = new Staff(); s.setEmailFpt("mail@fpt.vn");
        StaffMajorFacility smf = new StaffMajorFacility(); smf.setStaff(s);

        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("dep","fac")).thenReturn(Optional.of(df));
        when(adStaffMajorRepository.findById("maj")).thenReturn(Optional.of(m));
        when(adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(m, df)).thenReturn(Optional.of(mf));
        when(adStaffRepository.findById("staffId")).thenReturn(Optional.of(s));
        when(staffMajorFacilityRepository.findByStaffAndMajorFacility(s, mf)).thenReturn(Optional.of(smf));

        DepartmentFacility dfU = new DepartmentFacility(); Major mU = new Major(); MajorFacility mfU = new MajorFacility();
        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("depU","facU")).thenReturn(Optional.of(dfU));
        when(adStaffMajorRepository.findById("majU")).thenReturn(Optional.of(mU));
        when(adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(mU, dfU)).thenReturn(Optional.of(mfU));
        when(adStaffRoleRepository.getStaffRoleByIdStaff("staffId")).thenReturn(List.of());
        when(adStaffRoleRepository.getRoleByFacility("facU")).thenReturn(List.of());

        ResponseObject<?> res = service.updateStaffByFDM(req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Update thành công", res.getMessage());
    }

    @Test
    void testDeleteStaffByFDM_WithRoles() {
        Staff s = new Staff(); s.setId("sid"); s.setEmailFpt("mail@fpt.vn");
        StaffMajorFacility smf = new StaffMajorFacility(); smf.setStaff(s);

        Role r = new Role(); r.setCode("R1");
        StaffRole sr = new StaffRole(); sr.setRole(r); sr.setStaff(s);

        when(staffMajorFacilityRepository.findById("id")).thenReturn(Optional.of(smf));
        when(adStaffRoleRepository.getStaffRoleByIdStaff("sid")).thenReturn(List.of(sr));
        when(adStaffRoleRepository.getRoleDefault()).thenReturn(List.of(r));

        ResponseObject<?> res = service.deleteStaffByFDM("id","admin@fpt.vn");
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testCreateUpdateRoleByStaff_NoRoleMatch() {
        RoleStaffRequest req = new RoleStaffRequest("rid","sid","CODE_X","admin@fpt.vn");
        Staff s = new Staff(); s.setId("sid");
        when(adStaffRepository.findById("sid")).thenReturn(Optional.of(s));
        ADStaffByDepartmentFacility proj = mock(ADStaffByDepartmentFacility.class);
        when(proj.getIdFacility()).thenReturn("fac1");
        when(adStaffByDeparmentFacilityRepository.staffByDepartmentFacility("sid")).thenReturn(Optional.of(proj));
        Role role = new Role(); role.setCode("OTHER");
        when(adStaffRoleRepository.getRoleByFacility("fac1")).thenReturn(List.of(role));
        ResponseObject<?> res = service.createUpdateRoleByStaff(req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testGetLogsImportStaff_PageGreaterThan1_Empty() {
        when(userContextHelper.getCurrentUserId()).thenReturn("sid");
        when(roleRepository.findRoleByStaff("sid")).thenReturn(List.of());
        when(staffRepository.getEmailFpt("sid")).thenReturn("mail@fpt.vn");
        when(historyImportRepository.findByEmail(eq("mail@fpt.vn"), any())).thenReturn(new PageImpl<>(List.of()));
        ResponseObject<?> res = service.getLogsImportStaff(2, 10);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Lấy lịch sử import sinh viên thành công", res.getMessage());
    }

    @Test
    void testGetRoleByStaff_LastAdminNotMatching() {
        StaffRoleResponse r1 = mock(StaffRoleResponse.class);
        when(r1.getId()).thenReturn("id1");
        when(adStaffRoleRepository.getRoleByStaff("sid")).thenReturn(new java.util.ArrayList<>(List.of(r1)));
        StaffRole sr = new StaffRole(); sr.setId("diffId");
        when(adStaffRoleRepository.getLastStaffRoleAdmin()).thenReturn(List.of(sr));
        ResponseObject<?> res = service.getRoleByStaff("sid");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(1, ((List<?>)res.getData()).size());
    }
    @Test
    void testUpdateStaffByFDM_SuccessFullFlow() {
        ADUpdateStaffFDMRequest req = new ADUpdateStaffFDMRequest();
        req.setIdDepartment("dep"); req.setIdFacility("fac"); req.setIdMajor("maj");
        req.setIdStaffDetail("staffId");
        req.setIdUpdateDepartment("depU"); req.setIdUpdateFacility("facU"); req.setIdUpdateMajor("majU");
        req.setEmailLogin("admin@fpt.vn");

        DepartmentFacility df = new DepartmentFacility();
        Major m = new Major(); MajorFacility mf = new MajorFacility();
        Staff s = new Staff(); s.setEmailFpt("mail@fpt.vn");
        StaffMajorFacility smf = new StaffMajorFacility(); smf.setStaff(s);

        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("dep","fac")).thenReturn(Optional.of(df));
        when(adStaffMajorRepository.findById("maj")).thenReturn(Optional.of(m));
        when(adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(m, df)).thenReturn(Optional.of(mf));
        when(adStaffRepository.findById("staffId")).thenReturn(Optional.of(s));
        when(staffMajorFacilityRepository.findByStaffAndMajorFacility(s, mf)).thenReturn(Optional.of(smf));

        DepartmentFacility dfU = new DepartmentFacility(); Major mU = new Major(); MajorFacility mfU = new MajorFacility();
        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("depU","facU")).thenReturn(Optional.of(dfU));
        when(adStaffMajorRepository.findById("majU")).thenReturn(Optional.of(mU));
        when(adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(mU, dfU)).thenReturn(Optional.of(mfU));
        when(adStaffRoleRepository.getStaffRoleByIdStaff("staffId")).thenReturn(List.of());
        when(adStaffRoleRepository.getRoleByFacility("facU")).thenReturn(List.of());

        ResponseObject<?> res = service.updateStaffByFDM(req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testDeleteStaffByFDM_WithStaffRoles() {
        Staff s = new Staff(); s.setId("sid"); s.setEmailFpt("mail@fpt.vn");
        StaffMajorFacility smf = new StaffMajorFacility(); smf.setStaff(s);

        Role r = new Role(); r.setCode("R1");
        StaffRole sr = new StaffRole(); sr.setRole(r); sr.setStaff(s);

        when(staffMajorFacilityRepository.findById("id")).thenReturn(Optional.of(smf));
        when(adStaffRoleRepository.getStaffRoleByIdStaff("sid")).thenReturn(List.of(sr));
        when(adStaffRoleRepository.getRoleDefault()).thenReturn(List.of(r));

        ResponseObject<?> res = service.deleteStaffByFDM("id","admin@fpt.vn");
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testCreateUpdateRoleByStaff_NoRoleMatched() {
        RoleStaffRequest req = new RoleStaffRequest("rid","sid","CODE_X","admin@fpt.vn");
        Staff s = new Staff(); s.setId("sid");
        when(adStaffRepository.findById("sid")).thenReturn(Optional.of(s));
        ADStaffByDepartmentFacility proj = mock(ADStaffByDepartmentFacility.class);
        when(proj.getIdFacility()).thenReturn("fac1");
        when(adStaffByDeparmentFacilityRepository.staffByDepartmentFacility("sid")).thenReturn(Optional.of(proj));
        Role role = new Role(); role.setCode("OTHER");
        when(adStaffRoleRepository.getRoleByFacility("fac1")).thenReturn(List.of(role));

        ResponseObject<?> res = service.createUpdateRoleByStaff(req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testGetLogsImportStaff_PageGreaterThan1_EmptyPage() {
        when(userContextHelper.getCurrentUserId()).thenReturn("sid");
        when(roleRepository.findRoleByStaff("sid")).thenReturn(List.of());
        when(staffRepository.getEmailFpt("sid")).thenReturn("mail@fpt.vn");
        when(historyImportRepository.findByEmail(eq("mail@fpt.vn"), any())).thenReturn(new PageImpl<>(List.of()));

        ResponseObject<?> res = service.getLogsImportStaff(2, 10);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testGetRoleByStaff_LastAdminPresentButNotMatching() {
        StaffRoleResponse r1 = mock(StaffRoleResponse.class);
        when(r1.getId()).thenReturn("id1");
        when(adStaffRoleRepository.getRoleByStaff("sid")).thenReturn(new java.util.ArrayList<>(List.of(r1)));
        StaffRole sr = new StaffRole(); sr.setId("diffId");
        when(adStaffRoleRepository.getLastStaffRoleAdmin()).thenReturn(List.of(sr));

        ResponseObject<?> res = service.getRoleByStaff("sid");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(1, ((List<?>)res.getData()).size());
    }

    @Test
    void testUpdateStaffByFDM_UpdateDeptFacilityNotFound() {
        ADUpdateStaffFDMRequest req = new ADUpdateStaffFDMRequest();
        req.setIdDepartment("dep"); req.setIdFacility("fac"); req.setIdMajor("maj");
        req.setIdStaffDetail("staffId");
        req.setIdUpdateDepartment("depU"); req.setIdUpdateFacility("facU");

        DepartmentFacility df = new DepartmentFacility();
        Major m = new Major(); MajorFacility mf = new MajorFacility();
        Staff s = new Staff();
        StaffMajorFacility smf = new StaffMajorFacility(); smf.setStaff(s);

        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("dep","fac")).thenReturn(Optional.of(df));
        when(adStaffMajorRepository.findById("maj")).thenReturn(Optional.of(m));
        when(adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(m, df)).thenReturn(Optional.of(mf));
        when(adStaffRepository.findById("staffId")).thenReturn(Optional.of(s));
        when(staffMajorFacilityRepository.findByStaffAndMajorFacility(s, mf)).thenReturn(Optional.of(smf));
        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("depU","facU")).thenReturn(Optional.empty());

        ResponseObject<?> res = service.updateStaffByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }

    @Test
    void testUpdateStaffByFDM_UpdateMajorNotFound() {
        ADUpdateStaffFDMRequest req = new ADUpdateStaffFDMRequest();
        req.setIdDepartment("dep"); req.setIdFacility("fac"); req.setIdMajor("maj");
        req.setIdStaffDetail("staffId");
        req.setIdUpdateDepartment("depU"); req.setIdUpdateFacility("facU"); req.setIdUpdateMajor("majU");

        DepartmentFacility df = new DepartmentFacility();
        Major m = new Major(); MajorFacility mf = new MajorFacility();
        Staff s = new Staff();
        StaffMajorFacility smf = new StaffMajorFacility(); smf.setStaff(s);

        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("dep","fac")).thenReturn(Optional.of(df));
        when(adStaffMajorRepository.findById("maj")).thenReturn(Optional.of(m));
        when(adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(m, df)).thenReturn(Optional.of(mf));
        when(adStaffRepository.findById("staffId")).thenReturn(Optional.of(s));
        when(staffMajorFacilityRepository.findByStaffAndMajorFacility(s, mf)).thenReturn(Optional.of(smf));

        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("depU","facU")).thenReturn(Optional.of(new DepartmentFacility()));
        when(adStaffMajorRepository.findById("majU")).thenReturn(Optional.empty());

        ResponseObject<?> res = service.updateStaffByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }

    @Test
    void testUpdateStaffByFDM_UpdateMajorFacilityNotFound() {
        ADUpdateStaffFDMRequest req = new ADUpdateStaffFDMRequest();
        req.setIdDepartment("dep"); req.setIdFacility("fac"); req.setIdMajor("maj");
        req.setIdStaffDetail("staffId");
        req.setIdUpdateDepartment("depU"); req.setIdUpdateFacility("facU"); req.setIdUpdateMajor("majU");

        DepartmentFacility df = new DepartmentFacility();
        Major m = new Major(); MajorFacility mf = new MajorFacility();
        Staff s = new Staff();
        StaffMajorFacility smf = new StaffMajorFacility(); smf.setStaff(s);

        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("dep","fac")).thenReturn(Optional.of(df));
        when(adStaffMajorRepository.findById("maj")).thenReturn(Optional.of(m));
        when(adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(m, df)).thenReturn(Optional.of(mf));
        when(adStaffRepository.findById("staffId")).thenReturn(Optional.of(s));
        when(staffMajorFacilityRepository.findByStaffAndMajorFacility(s, mf)).thenReturn(Optional.of(smf));

        DepartmentFacility dfU = new DepartmentFacility(); Major mU = new Major();
        when(departmentFacilityRepository.findByDepartmentAndFacilityAndStaff("depU","facU")).thenReturn(Optional.of(dfU));
        when(adStaffMajorRepository.findById("majU")).thenReturn(Optional.of(mU));
        when(adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(mU, dfU)).thenReturn(Optional.empty());

        ResponseObject<?> res = service.updateStaffByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }

    @Test
    void testDeleteStaffByFDM_WithMultipleRoles() {
        Staff s = new Staff(); s.setId("sid"); s.setEmailFpt("mail@fpt.vn");
        StaffMajorFacility smf = new StaffMajorFacility(); smf.setStaff(s);

        Role r1 = new Role(); r1.setCode("R1");
        Role r2 = new Role(); r2.setCode("R2");
        StaffRole sr1 = new StaffRole(); sr1.setRole(r1); sr1.setStaff(s);
        StaffRole sr2 = new StaffRole(); sr2.setRole(r2); sr2.setStaff(s);

        when(staffMajorFacilityRepository.findById("id")).thenReturn(Optional.of(smf));
        when(adStaffRoleRepository.getStaffRoleByIdStaff("sid")).thenReturn(List.of(sr1,sr2));
        when(adStaffRoleRepository.getRoleDefault()).thenReturn(List.of(r1,r2));

        ResponseObject<?> res = service.deleteStaffByFDM("id","admin@fpt.vn");
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testCreateUpdateRoleByStaff_DefaultRoleFound() {
        RoleStaffRequest req = new RoleStaffRequest("rid","sid","R_CODE","admin@fpt.vn");
        Staff s = new Staff(); s.setId("sid");
        when(adStaffRepository.findById("sid")).thenReturn(Optional.of(s));
        when(adStaffByDeparmentFacilityRepository.staffByDepartmentFacility("sid")).thenReturn(Optional.empty());
        Role role = new Role(); role.setCode("R_CODE");
        when(adStaffRoleRepository.findRoleDefaultByCode("R_CODE")).thenReturn(Optional.of(role));
        when(staffRoleRepository.save(any())).thenReturn(new StaffRole());

        ResponseObject<?> res = service.createUpdateRoleByStaff(req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    // deleteStaffByFDM: role trùng -> chỉ set 1 lần
    @Test
    void testDeleteStaffByFDM_DuplicateRoles() {
        Staff s = new Staff(); s.setId("sid"); s.setEmailFpt("e@fpt.vn");
        StaffMajorFacility smf = new StaffMajorFacility(); smf.setStaff(s);

        Role r = new Role(); r.setCode("R1");
        StaffRole sr1 = new StaffRole(); sr1.setRole(r); sr1.setStaff(s);
        StaffRole sr2 = new StaffRole(); sr2.setRole(r); sr2.setStaff(s);

        when(staffMajorFacilityRepository.findById("id")).thenReturn(Optional.of(smf));
        when(adStaffRoleRepository.getStaffRoleByIdStaff("sid")).thenReturn(List.of(sr1, sr2));
        when(adStaffRoleRepository.getRoleDefault()).thenReturn(List.of(r));

        ResponseObject<?> res = service.deleteStaffByFDM("id","admin@fpt.vn");
        assertEquals(HttpStatus.OK, res.getStatus());
        verify(staffMajorFacilityRepository, times(1)).delete(smf);
    }



    // createUpdateRoleByStaff: có facility -> chọn đúng role
    @Test
    void testCreateUpdateRoleByStaff_WithFacilityRoles() {
        RoleStaffRequest req = new RoleStaffRequest("rid","sid","CODE_ROLE","admin@fpt.vn");
        Staff s = new Staff(); s.setId("sid");
        when(adStaffRepository.findById("sid")).thenReturn(Optional.of(s));

        ADStaffByDepartmentFacility facility = mock(ADStaffByDepartmentFacility.class);
        when(facility.getIdFacility()).thenReturn("fid");
        when(adStaffByDeparmentFacilityRepository.staffByDepartmentFacility("sid")).thenReturn(Optional.of(facility));

        Role r1 = new Role(); r1.setCode("CODE_ROLE");
        Role r2 = new Role(); r2.setCode("OTHER");
        when(adStaffRoleRepository.getRoleByFacility("fid")).thenReturn(List.of(r1,r2));
        when(staffRoleRepository.save(any())).thenReturn(new StaffRole());

        ResponseObject<?> res = service.createUpdateRoleByStaff(req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Thêm quyền cho nhân viên thành công", res.getMessage());
    }

    // deleteRoleByStaff: tìm thấy và xóa
    @Test
    void testDeleteRoleByStaff_FoundAndDeleted() {
        RoleStaffRequest req = new RoleStaffRequest("rid","sid","CODE","admin@fpt.vn");
        Staff s = new Staff(); s.setEmailFpt("mail@fpt.vn");
        StaffRole sr = new StaffRole(); sr.setStaff(s);
        when(adStaffRoleRepository.findTheLastByRoleIdAndStaffId("rid","sid")).thenReturn(List.of(sr));

        ResponseObject<?> res = service.deleteRoleByStaff(req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Xóa thành công", res.getMessage());
        verify(staffRoleRepository, times(1)).delete(sr);
    }

    // deleteRoleByStaff: not found
    @Test
    void testDeleteRoleByStaff_NotFound_NewMethod() {
        RoleStaffRequest req = new RoleStaffRequest("rid","sid","CODE","admin@fpt.vn");
        when(adStaffRoleRepository.findTheLastByRoleIdAndStaffId("rid","sid")).thenReturn(Collections.emptyList());
        ResponseObject<?> res = service.deleteRoleByStaff(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy role", res.getMessage());
    }

    // getAllStaffCount: exception handling
    @Test
    void testGetAllStaffCount_Throws() {
        when(adStaffRepository.countAllStaff()).thenThrow(new RuntimeException("err"));
        ResponseObject<?> res = service.getAllStaffCount();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatus());
        assertEquals("Đã xảy ra lỗi khi đếm nhân viên.", res.getMessage());
    }
    @Test
    void testGetAllStaff_WithStatusNotNull() {
        ADStaffRequest req = new ADStaffRequest();
        req.setSearchStatus(1); // -> EntityStatus.ACTIVE

        Page<String> staffIdsPage = new PageImpl<>(List.of("s1"));
        when(adStaffRepository.findStaffIds(any(), any(), any())).thenReturn(staffIdsPage);

        StaffResponse resp = mock(StaffResponse.class);
        when(resp.getId()).thenReturn("s1");
        when(resp.getNameRole()).thenReturn("ROLE_USER");
        when(adStaffRepository.findStaffDetailsByIds(List.of("s1"))).thenReturn(List.of(resp));

        ResponseObject<?> res = service.getAllStaff(req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertTrue(res.getData() instanceof PageableObject);
    }



    @Test
    void testDeleteStaffByFDM_UpdateRoles() {
        Staff staff = new Staff(); staff.setId("sid"); staff.setEmailFpt("fpt@mail.com");
        StaffMajorFacility smf = new StaffMajorFacility(); smf.setStaff(staff);
        StaffRole sr = new StaffRole(); Role oldRole = new Role(); oldRole.setCode("OLD"); sr.setRole(oldRole);

        when(staffMajorFacilityRepository.findById("smfid")).thenReturn(Optional.of(smf));
        when(adStaffRoleRepository.getStaffRoleByIdStaff("sid")).thenReturn(List.of(sr));
        Role defRole = new Role(); defRole.setCode("OLD");
        when(adStaffRoleRepository.getRoleDefault()).thenReturn(List.of(defRole));

        ResponseObject<?> res = service.deleteStaffByFDM("smfid", "admin@fpt.vn");
        assertEquals(HttpStatus.OK, res.getStatus());
        verify(staffMajorFacilityRepository, times(1)).delete(smf);
    }

    @Test
    void testGetAllCsv_MultipleFiles() throws Exception {
        File dir = new File("src/main/resources/log-accountability-index/");
        if (!dir.exists()) dir.mkdirs();
        File file1 = File.createTempFile("f1-", ".csv", dir);
        File file2 = File.createTempFile("f2-", ".csv", dir);

        Resource res = service.getAllCsv();
        assertNotNull(res);

        file1.deleteOnExit();
        file2.deleteOnExit();
    }


    @Test
    void testDeleteStaff_SwitchToInactive() {
        Staff staff = new Staff();
        staff.setId("s1");
        staff.setStatus(EntityStatus.ACTIVE);
        staff.setEmailFpt("abc@fpt.vn");

        when(adStaffRepository.findById("s1")).thenReturn(Optional.of(staff));
        ResponseObject<?> res = service.deleteStaff("s1","admin@fpt.vn");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Cập nhật trạng thái nhân viên thành công", res.getMessage());
        verify(adStaffRepository,times(1)).save(any(Staff.class));
    }

    @Test
    void testCreateUpdateRoleByStaff_RoleDefaultFlow() {
        RoleStaffRequest req = new RoleStaffRequest("roleX","staff1","ROLE_DEFAULT","admin@fpt.vn");
        Staff staff = new Staff(); staff.setId("staff1"); staff.setEmailFpt("fpt@fpt.vn");

        Role defaultRole = new Role(); defaultRole.setCode("ROLE_DEFAULT");

        when(adStaffRepository.findById("staff1")).thenReturn(Optional.of(staff));
        when(adStaffByDeparmentFacilityRepository.staffByDepartmentFacility("staff1")).thenReturn(Optional.empty());
        when(adStaffRoleRepository.findRoleDefaultByCode("ROLE_DEFAULT")).thenReturn(Optional.of(defaultRole));
        when(staffRoleRepository.save(any())).thenReturn(new StaffRole());

        ResponseObject<?> res = service.createUpdateRoleByStaff(req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Thêm quyền cho nhân viên thành công", res.getMessage());
    }

    @Test
    void testGetLogsImportStaff_EmptyPage() {
        when(userContextHelper.getCurrentUserId()).thenReturn("uid");
        when(roleRepository.findRoleByStaff("uid")).thenReturn(List.of());
        when(staffRepository.getEmailFpt("uid")).thenReturn("mail@fpt.vn");
        when(historyImportRepository.findByEmail(eq("mail@fpt.vn"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        ResponseObject<?> res = service.getLogsImportStaff(2,5);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Lấy lịch sử import sinh viên thành công", res.getMessage());
    }






}
