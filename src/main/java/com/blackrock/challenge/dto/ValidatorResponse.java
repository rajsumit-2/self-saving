package com.blackrock.challenge.dto;

import java.util.List;

public record ValidatorResponse(
        List<TransactionDto> valid,
        List<InvalidTransactionDto> invalid,
        List<InvalidTransactionDto> duplicate
) {}
