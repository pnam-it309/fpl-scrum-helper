package udpm.hn.server.core.member.projectdetails.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.*;
import udpm.hn.server.core.member.projectdetails.service.MBMeCommentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")

public class MBMeCommentControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MBMeCommentService mbMeCommentService;

    private ResponseObject<Object> mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Success");
        mockResponse.setData(null);
    }

    @Test
    void testGetAllCommentByIdTodo() throws Exception {
        when(mbMeCommentService.getAllCommentByIdTodo(any(MBMeFindCommentRequest.class)))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/comment")
                        .param("idTodo", "todo123"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllMemberInProject() throws Exception {
        when(mbMeCommentService.getAllMemberInProject(any(MBMeFindTagMemberRequest.class)))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/comment/tag-member")
                        .param("idProject", "project123"))
                .andExpect(status().isOk());
    }



}
