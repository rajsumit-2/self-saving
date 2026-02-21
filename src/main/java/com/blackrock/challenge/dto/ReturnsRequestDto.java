package com.blackrock.challenge.dto;

import java.util.List;

public record ReturnsRequestDto(
        Integer age,
        Double wage,
        Double inflation,
        List<QPeriodDto> q,
        List<PPeriodDto> p,
        List<KPeriodDto> k,
        List<TransactionDto> transactions
) {
    public List<QPeriodDto> q() { return q != null ? q : List.of(); }
    public List<PPeriodDto> p() { return p != null ? p : List.of(); }
    public List<KPeriodDto> k() { return k != null ? k : List.of(); }
    public List<TransactionDto> transactions() { return transactions != null ? transactions : List.of(); }
}
