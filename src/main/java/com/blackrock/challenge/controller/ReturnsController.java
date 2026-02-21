package com.blackrock.challenge.controller;

import com.blackrock.challenge.dto.ReturnsRequestDto;
import com.blackrock.challenge.dto.ReturnsResponse;
import com.blackrock.challenge.service.ReturnsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/blackrock/challenge/v1")
public class ReturnsController {

    private final ReturnsService returnsService;

    public ReturnsController(ReturnsService returnsService) {
        this.returnsService = returnsService;
    }

    @PostMapping(value = "returns:nps", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> nps(@RequestBody ReturnsRequestDto body) {
        if (body == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Request body must be an object"));
        }
        if (body.age() == null || body.age() < 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Valid age (non-negative integer) is required"));
        }
        if (body.wage() == null || !Double.isFinite(body.wage()) || body.wage() < 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Valid wage is required"));
        }
        ReturnsResponse res = returnsService.computeNpsReturns(
                body.age(), body.wage(), body.inflation(),
                body.q(), body.p(), body.k(), body.transactions()
        );
        return ResponseEntity.ok(res);
    }

    @PostMapping(value = "returns:index", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> index(@RequestBody ReturnsRequestDto body) {
        if (body == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Request body must be an object"));
        }
        if (body.age() == null || body.age() < 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Valid age (non-negative integer) is required"));
        }
        ReturnsResponse res = returnsService.computeIndexReturns(
                body.age(), body.inflation(),
                body.q(), body.p(), body.k(), body.transactions()
        );
        return ResponseEntity.ok(res);
    }
}
