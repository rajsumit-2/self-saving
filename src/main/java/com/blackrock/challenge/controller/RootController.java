package com.blackrock.challenge.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> root() {
        return ResponseEntity.ok(Map.of(
                "name", "Blackrock Challenge API",
                "basePath", "/blackrock/challenge/v1",
                "health", "/health",
                "endpoints", Map.of(
                        "POST /blackrock/challenge/v1/transactions:parse", "Parse expenses to transactions",
                        "POST /blackrock/challenge/v1/transactions:validator", "Validate transactions",
                        "POST /blackrock/challenge/v1/transactions:filter", "Filter by q/p/k periods",
                        "POST /blackrock/challenge/v1/returns:nps", "NPS returns",
                        "POST /blackrock/challenge/v1/returns:index", "Index fund returns",
                        "GET /blackrock/challenge/v1/performance", "Performance stats",
                        "GET /health", "Liveness check"
                )
        ));
    }

    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
