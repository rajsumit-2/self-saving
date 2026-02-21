package com.blackrock.challenge.dto;

import java.util.List;

public record ParseRequest(List<ExpenseDto> expenses) {
    public List<ExpenseDto> expenses() {
        return expenses != null ? expenses : List.of();
    }
}
