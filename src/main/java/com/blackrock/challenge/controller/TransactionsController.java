package com.blackrock.challenge.controller;

import com.blackrock.challenge.dto.*;
import com.blackrock.challenge.service.FilterService;
import com.blackrock.challenge.service.ParseService;
import com.blackrock.challenge.service.ValidatorService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blackrock/challenge/v1")
public class TransactionsController {

    private final ParseService parseService;
    private final ValidatorService validatorService;
    private final FilterService filterService;

    public TransactionsController(ParseService parseService, ValidatorService validatorService, FilterService filterService) {
        this.parseService = parseService;
        this.validatorService = validatorService;
        this.filterService = filterService;
    }

    @PostMapping(value = "transactions:parse", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParseResponse> parse(@RequestBody ParseRequest request) {
        if (request == null) {
            request = new ParseRequest(null);
        }
        return ResponseEntity.ok(parseService.parse(request.expenses()));
    }

    @PostMapping(value = "transactions:validator", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validator(@RequestBody ValidatorRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Request body must be an object"));
        }
        if (!Double.isFinite(request.wage()) || request.wage() < 0) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Valid wage (number >= 0) is required"));
        }
        ValidatorResponse res = validatorService.validate(request.wage(), request.maxAmountToInvest(), request.transactions());
        return ResponseEntity.ok(res);
    }

    @PostMapping(value = "transactions:filter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FilterResponse> filter(@RequestBody FilterRequest request) {
        if (request == null) {
            request = new FilterRequest(null, null, null, null);
        }
        return ResponseEntity.ok(filterService.filterByPeriods(request));
    }
}
