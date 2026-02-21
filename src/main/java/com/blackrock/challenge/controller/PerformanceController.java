package com.blackrock.challenge.controller;

import com.blackrock.challenge.dto.PerformanceResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/blackrock/challenge/v1")
public class PerformanceController {

    @GetMapping(value = "performance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PerformanceResponse> performance() {
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        long heapUsed = mem.getHeapMemoryUsage().getUsed();
        double heapUsedMB = heapUsed / (1024.0 * 1024.0);
        String memory = String.format("%.2f MB", heapUsedMB);

        long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
        long hours = TimeUnit.MILLISECONDS.toHours(uptimeMs);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptimeMs) % 60;
        double seconds = (uptimeMs % 60_000) / 1000.0;
        String time = String.format("%02d:%02d:%06.3f", hours, minutes, seconds);

        int threadCount = ManagementFactory.getThreadMXBean().getThreadCount();
        return ResponseEntity.ok(new PerformanceResponse(time, memory, threadCount));
    }
}
