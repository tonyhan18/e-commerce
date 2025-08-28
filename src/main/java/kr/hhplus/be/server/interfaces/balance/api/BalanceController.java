package kr.hhplus.be.server.interfaces.balance.api;

import kr.hhplus.be.server.application.balance.BalanceResult;
import kr.hhplus.be.server.interfaces.ApiResponse;
import kr.hhplus.be.server.interfaces.balance.api.BalanceResponse;
import kr.hhplus.be.server.interfaces.balance.api.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @GetMapping("/{userId}/balance")
    public ApiResponse<BalanceResponse.Balance> getBalance(@PathVariable Long userId) {
        BalanceResult.Balance balance = balanceService.getBalance(userId);
        return ApiResponse.success(BalanceResponse.Balance.of(balance));
    }

    @PostMapping("/{userId}/balance/charge")
    public ApiResponse<Void> chargeBalance(@PathVariable Long userId, 
                                           @Valid @RequestBody BalanceRequest.Charge request) {
        balanceService.chargeBalance(request.toCriteria(userId));
        return ApiResponse.success();
    }
}