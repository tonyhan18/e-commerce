package kr.hhplus.be.server.api.controller.rank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rank")
public class RankController {
    @GetMapping("/top")
    public ResponseEntity<RankTopResponse> getTopRank() {
        RankTopResponse.Data r1 = new RankTopResponse.Data(1001L, "아메리카노", 257);
        RankTopResponse.Data r2 = new RankTopResponse.Data(1002L, "카페라떼", 110);
        RankTopResponse response = new RankTopResponse(200, "OK", List.of(r1, r2));
        return ResponseEntity.ok(response);
    }
} 