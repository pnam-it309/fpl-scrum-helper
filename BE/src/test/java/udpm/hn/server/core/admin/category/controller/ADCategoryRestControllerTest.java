package udpm.hn.server.core.admin.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.admin.category.modal.request.ADCategoryRequest;
import udpm.hn.server.core.admin.category.modal.request.ADCreateUpdateCategoryRequest;
import udpm.hn.server.core.admin.category.service.ADCategoryService;
import udpm.hn.server.core.common.base.ResponseObject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:mysql://localhost:3306/scrum",
        "spring.datasource.username=root",
        "spring.datasource.password=123456",
        "spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver"
})
public class ADCategoryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ADCategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseObject<?> mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Success");
        mockResponse.setData(null);
    }

    @Test
    void testGetAllCategory() throws Exception {
        when(categoryService.getAllCategory(any(ADCategoryRequest.class)))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testCreateCategory() throws Exception {
        ADCreateUpdateCategoryRequest request = new ADCreateUpdateCategoryRequest();

        when(categoryService.createCategory(any()))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(post("/api/v1/admin/category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testUpdateCategory() throws Exception {
        ADCreateUpdateCategoryRequest request = new ADCreateUpdateCategoryRequest();

        when(categoryService.updateCategory(any(), any()))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(put("/api/v1/admin/category/update/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testChangeStatusCategory() throws Exception {
        when(categoryService.changeCategoryStatus("abc"))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(put("/api/v1/admin/category/abc/change-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testGetByIdCategory() throws Exception {
        when(categoryService.getCategoryById("abc"))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/category/detail/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

}
