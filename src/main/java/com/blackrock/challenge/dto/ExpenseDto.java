package com.blackrock.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExpenseDto(String timestamp, String date, Double amount) {
    public String getDateOrTimestamp() {
        return timestamp != null ? timestamp : date;
    }
}
