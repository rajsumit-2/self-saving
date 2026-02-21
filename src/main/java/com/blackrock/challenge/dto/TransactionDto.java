package com.blackrock.challenge.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TransactionDto(
        @JsonProperty("date") @JsonAlias("timestamp") String date,
        double amount,
        double ceiling,
        double remanent
) {}
