package udpm.hn.server.core.admin.staff.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.admin.staff.model.request.*;
import udpm.hn.server.core.admin.staff.service.ADStaffFacilityService;
import udpm.hn.server.core.admin.staff.service.ADStaffService;
import udpm.hn.server.core.common.base.ResponseObject;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ADStaffRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ADStaffService adStaffService;

    @MockitoBean
    private ADStaffFacilityService adStaffFacilityService;

    private ResponseObject<Object> response;

    @BeforeEach
    void setUp() {
        response = new ResponseObject<>();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Success");
        response.setData(null);
    }

    @Test
    void testGetAllStaff() throws Exception {
        when(adStaffService.getAllStaff(any())).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/admin/staff"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRoleByStaff() throws Exception {
        when(adStaffService.getRoleByStaff("1")).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/admin/staff/role-by-staff/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllRole() throws Exception {
        when(adStaffService.getAllRole()).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/admin/staff/role-staff"))
                .andExpect(status().isOk());
    }

    @Test
    void testDetailStaff() throws Exception {
        when(adStaffService.detailStaff("123")).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/admin/staff/detailStaff/123"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetStaffNoProject() throws Exception {
        when(adStaffService.getAllStaffNoProject()).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/admin/staff/staff-no-project"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllFacility() throws Exception {
        when(adStaffFacilityService.getAllFacility()).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/admin/staff/facility"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetDepartmentByFacility() throws Exception {
        when(adStaffService.getAllDepartmentByFacility("1")).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/admin/staff/department/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetMajorByDepartment() throws Exception {
        when(adStaffService.getMajorByDepartment("1")).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/admin/staff/major-department/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetStaffRoleByStaff() throws Exception {
        when(adStaffService.getStaffRoleByStaff("1")).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/admin/staff/staff-role-by-staff/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateStaff() throws Exception {
        ADCreateStaffRequest request = new ADCreateStaffRequest();
        when(adStaffService.createStaff(any())).thenReturn((ResponseObject) response);
        mockMvc.perform(post("/api/v1/admin/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateStaffFDM() throws Exception {
        ADCreateStaffFDM request = new ADCreateStaffFDM();
        when(adStaffService.createStaffByFDM(any())).thenReturn((ResponseObject) response);
        mockMvc.perform(post("/api/v1/admin/staff/major-department-facility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateStaffRole() throws Exception {
        RoleStaffRequest request = new RoleStaffRequest();
        when(adStaffService.createUpdateRoleByStaff(any())).thenReturn((ResponseObject) response);
        mockMvc.perform(post("/api/v1/admin/staff/create-staff-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateStaff() throws Exception {
        ADCreateStaffRequest request = new ADCreateStaffRequest();
        when(adStaffService.updateStaff(any(), any())).thenReturn((ResponseObject) response);
        mockMvc.perform(put("/api/v1/admin/staff/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateMajorDepartmentFacility() throws Exception {
        ADUpdateStaffFDMRequest request = new ADUpdateStaffFDMRequest();
        when(adStaffService.updateStaffByFDM(any())).thenReturn((ResponseObject) response);
        mockMvc.perform(put("/api/v1/admin/staff/major-department-facility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteStaff() throws Exception {
        when(adStaffService.deleteStaff("1", "admin@example.com")).thenReturn((ResponseObject) response);
        mockMvc.perform(delete("/api/v1/admin/staff/1/admin@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteStaffFacility() throws Exception {
        when(adStaffService.deleteStaffByFDM("1", "admin@example.com")).thenReturn((ResponseObject) response);
        mockMvc.perform(delete("/api/v1/admin/staff/staff-facility/1/admin@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteStaffRole() throws Exception {
        RoleStaffRequest request = new RoleStaffRequest();
        when(adStaffService.deleteRoleByStaff(any())).thenReturn((ResponseObject) response);
        mockMvc.perform(delete("/api/v1/admin/staff/delete-role-staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetLogsImportStaff() throws Exception {
        when(adStaffService.getLogsImportStaff(anyInt(), anyInt())).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/admin/staff/history?page=0&size=50"))
                .andExpect(status().isOk());
    }

    @Test
    void testDownloadCsvFile() throws Exception {
        ByteArrayResource resource = new ByteArrayResource("test,data".getBytes());
        when(adStaffService.getAllCsv()).thenReturn(resource);
        mockMvc.perform(get("/api/v1/admin/staff/csv-file"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=staff_data.csv"))
                .andExpect(content().contentType("text/csv"));
    }

    @Test
    void testGetAllStaffCount() throws Exception {
        when(adStaffService.getAllStaffCount()).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/admin/staff/count-staff"))
                .andExpect(status().isOk());
    }
    @Test
    void testStaffByDepartmentFacility_NotFound() throws Exception {
        when(adStaffService.staffByDepartmentFacility("dep1")).thenReturn(new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên có Id này"));

        mockMvc.perform(get("/api/v1/admin/staff/detailStaff/department-major/dep1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Không tìm thấy nhân viên có Id này"));
    }
    @Test
    void testDownloadCsvFile_FileNotFound() throws Exception {
        when(adStaffService.getAllCsv()).thenThrow(new IOException("Không tìm thấy file CSV"));

        mockMvc.perform(get("/api/v1/admin/staff/csv-file"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(""));  // Bạn có thể thay đổi đây nếu muốn hiển thị thông báo chi tiết lỗi
    }
    @Test
    void testDeleteStaffRole_NotFound() throws Exception {
        RoleStaffRequest request = new RoleStaffRequest();
        when(adStaffService.deleteRoleByStaff(any())).thenReturn(new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy role"));

        mockMvc.perform(delete("/api/v1/admin/staff/delete-role-staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Không tìm thấy role"));
    }



}

