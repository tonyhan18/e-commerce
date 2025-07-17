package kr.hhplus.be.server.api.controller.balance;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class BalanceController {
    @GetMapping("/{userId}/balance")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable Long userId) {
        if (userId == 404) {
            return ResponseEntity.status(404).body(new BalanceResponse(404, "Not Found", null));
        }
        BalanceResponse.Data data = new BalanceResponse.Data(1000000);
        BalanceResponse response = new BalanceResponse(200, "OK", data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/balance/charge")
    public ResponseEntity<BalanceResponse> chargeBalance(@PathVariable Long userId, @RequestBody BalanceRequest request) {
        if (userId == 404) {
            return ResponseEntity.status(404).body(new BalanceResponse(404, "Not Found", null));
        }
        int amount = request.getAmount();
        if (amount > 10000000) {
            return ResponseEntity.badRequest().body(new BalanceResponse(400, "최대 잔액 한도를 초과할 수 없습니다.", null));
        }
        BalanceResponse.Data data = new BalanceResponse.Data(1010000);
        BalanceResponse response = new BalanceResponse(200, "잔액 충전이 성공적으로 완료되었습니다.", data);
        return ResponseEntity.ok(response);
    }
} 