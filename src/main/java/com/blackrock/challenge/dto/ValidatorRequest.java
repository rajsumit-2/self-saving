package com.blackrock.challenge.dto;

import java.util.List;

public record ValidatorRequest(double wage, Double maxAmountToInvest, List<TransactionDto> transactions) {
    public List<TransactionDto> transactions() {
        return transactions != null ? transactions : List.of();
    }
}
