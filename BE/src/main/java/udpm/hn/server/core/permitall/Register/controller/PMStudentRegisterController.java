package udpm.hn.server.core.permitall.Register.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.permitall.Register.model.request.PMCreateStudentRegisterRequest;
import udpm.hn.server.core.permitall.Register.service.PMLoginFacilityService;
import udpm.hn.server.core.permitall.Register.service.PMStudentRegisterService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@Controller
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_AUTH_PREFIX)
@Slf4j
public class PMStudentRegisterController {

    private final PMStudentRegisterService studentRegisterService;

    private final PMLoginFacilityService loginFacilityService;

    @PostMapping("/register-student")
    public ResponseEntity<?> registerStudent(@ModelAttribute PMCreateStudentRegisterRequest request){
        log.info("registerStudent"+request);
        return Helper.createResponseEntity(studentRegisterService.registerStudent(request));
    }

    @GetMapping("/facility-login")
    public ResponseEntity<?> getAllFacilityLogin(){
        return Helper.createResponseEntity(loginFacilityService.getAllFacility());
    }

}
