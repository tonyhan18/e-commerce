package kr.hhplus.be.server.interfaces.balance;

import kr.hhplus.be.server.application.balance.BalanceFacade;
import kr.hhplus.be.server.application.balance.BalanceResult;
import kr.hhplus.be.server.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceFacade balanceFacade;

    @GetMapping("/{userId}/balance")
    public ApiResponse<BalanceResponse.Balance> getBalance(@PathVariable Long userId) {
        BalanceResult.Balance balance = balanceFacade.getBalance(userId);
        return ApiResponse.success(BalanceResponse.Balance.of(balance));
    }

    @PostMapping("/{userId}/balance/charge")
    public ApiResponse<Void> updateBalance(@PathVariable Long userId, 
                                           @Valid @RequestBody BalanceRequest.Charge request) {
        balanceFacade.chargeBalance(request.toCriteria(userId));
        return ApiResponse.success();
    }
}