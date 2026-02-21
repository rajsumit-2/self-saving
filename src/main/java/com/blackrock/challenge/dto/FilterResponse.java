package com.blackrock.challenge.dto;

import java.util.List;

public record FilterResponse(List<TransactionDto> valid, List<InvalidTransactionDto> invalid) {}
