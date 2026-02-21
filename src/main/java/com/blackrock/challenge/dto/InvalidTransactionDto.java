package com.blackrock.challenge.dto;

public record InvalidTransactionDto(
        String date, Double amount, Double ceiling, Double remanent, String message
) {
    public static InvalidTransactionDto from(TransactionDto t, String message) {
        return new InvalidTransactionDto(
                t != null ? t.date() : null,
                t != null ? t.amount() : null,
                t != null ? t.ceiling() : null,
                t != null ? t.remanent() : null,
                message
        );
    }
}
