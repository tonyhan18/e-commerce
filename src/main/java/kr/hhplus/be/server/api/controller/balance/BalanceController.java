package kr.hhplus.be.server.api.controller.balance;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class BalanceController {
    @GetMapping("/{id}/balance")
    public ResponseEntity<?> getBalance(@PathVariable Long id) {
        if (id == 404) {
            return ResponseEntity.status(404).body(Map.of("code", 404, "message", "Not Found"));
        }
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "OK",
                "data", Map.of("amount", 1000000)
        ));
    }

    @PostMapping("/{userId}/balance/charge")
    public ResponseEntity<?> chargeBalance(@PathVariable Long userId, @RequestBody Map<String, Object> body) {
        if (userId == 404) {
            return ResponseEntity.status(404).body(Map.of("code", 404, "message", "Not Found"));
        }
        int amount = (int) body.getOrDefault("amount", 0);
        if (amount > 10000000) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", "최대 잔액 한도를 초과할 수 없습니다."));
        }
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "잔액 충전이 성공적으로 완료되었습니다.",
                "data", Map.of("amount", 1010000)
        ));
    }
}