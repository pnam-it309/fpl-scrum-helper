package udpm.hn.server.core.member.projectdetails.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.response.MBUpdateBackgroundProject;
import udpm.hn.server.core.member.projectdetails.service.MBMeImageService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")

public class MBMeImageControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MBMeImageService mbMeImageService;

    private ResponseObject<Object> mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Success");
        mockResponse.setData(null);
    }

    @Test
    void testGetBackgroundByIdProject() throws Exception {
        when(mbMeImageService.getBackgroundByIdProject("project123")).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/image/detail/background-image/project123"))
                .andExpect(status().isOk());
    }

    @Test
    void testFindById() throws Exception {
        when(mbMeImageService.findById("img123")).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/image/detail/img123"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllByIdTodo() throws Exception {
        when(mbMeImageService.getAllByIdTodo("todo123")).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/image/todo123"))
                .andExpect(status().isOk());
    }

    @Test
    void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image data".getBytes());
        when(mbMeImageService.uploadFile(any())).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(multipart("/api/v1/project-details/image/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }



}
