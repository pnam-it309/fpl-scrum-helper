package udpm.hn.server.infrastructure.config.websocket.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.utils.Helper;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TestSocketController {
    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public ResponseObject<?> sendMessage(Map<String, String> message) {
        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        List<String> list = new ArrayList<>();
        // Đảm bảo trả về JSON hợp lệ
        Map<String, String> response = new HashMap<>();
        response.put("sender", message.get("sender"));
        response.put("content", message.get("content"));
        response.put("timestamp", timestamp);

        return new ResponseObject(response, HttpStatus.OK,"đã nhận");
    }
}
