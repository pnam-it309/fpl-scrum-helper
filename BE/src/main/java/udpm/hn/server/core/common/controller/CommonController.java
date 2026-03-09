package udpm.hn.server.core.common.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.infrastructure.constant.MappingConstants;

import java.util.Map;

@RestController
@RequestMapping(MappingConstants.API_COMMON)
@RequiredArgsConstructor
public class CommonController {


    private final JdbcTemplate jdbcTemplate;


    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Server is awake!");
    }

    @GetMapping("/ping-db")
    public ResponseEntity<String> pingDb() {
        try {
            jdbcTemplate.execute("SELECT 1");
            return ResponseEntity.ok("Database is connected!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Database connection failed: " + e.getMessage());
        }
    }

    @GetMapping("/keep-alive")
    public ResponseEntity<Map<String, Object>> keepAlive() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "Server is staying awake",
                "timestamp", System.currentTimeMillis()
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
