package udpm.hn.server.core.common.base;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import udpm.hn.server.utils.UserContextHelper;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class TestController {


    private final UserContextHelper userContextHelper;

    public TestController(UserContextHelper userContextHelper) {
        this.userContextHelper = userContextHelper;
    }

    @GetMapping("/member/ping")
    public Map<String, String> member() {
        log.info("IDFACILITY test: "+userContextHelper.getCurrentIdFacility());
        Map<String, String> response = new HashMap<>();
        response.put("message", "BE đang chạy OK!");
        response.put("status", "success");
        return response;
    }

    @GetMapping("/admin/ping")
    public Map<String, String> admin() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "BE đang chạy OK!");
        response.put("status", "success");
        return response;
    }

    @GetMapping("/manage/ping")
    public Map<String, String> manage() {

        Map<String, String> response = new HashMap<>();
        response.put("message", "BE đang chạy OK!");
        response.put("status", "success");
        return response;
    }
}
