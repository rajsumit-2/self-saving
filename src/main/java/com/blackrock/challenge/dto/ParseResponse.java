package com.blackrock.challenge.dto;

import java.util.List;

public record ParseResponse(
        List<TransactionDto> transactions,
        double totalInvested,
        double totalRemanent,
        double totalExpense
) {}
