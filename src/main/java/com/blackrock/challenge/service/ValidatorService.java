package com.blackrock.challenge.service;

import com.blackrock.challenge.dto.InvalidTransactionDto;
import com.blackrock.challenge.dto.TransactionDto;
import com.blackrock.challenge.dto.ValidatorResponse;
import com.blackrock.challenge.util.DatesUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ValidatorService {

    private static final double MAX_AMOUNT = 500_000;

    public ValidatorResponse validate(double wage, Double maxAmountToInvest, List<TransactionDto> transactions) {
        if (transactions == null) {
            return new ValidatorResponse(List.of(), List.of(), List.of());
        }
        List<TransactionDto> valid = new ArrayList<>();
        List<InvalidTransactionDto> invalid = new ArrayList<>();
        List<InvalidTransactionDto> duplicate = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        double annualIncome = wage * 12;
        double maxInvest = (maxAmountToInvest != null && Double.isFinite(maxAmountToInvest))
                ? maxAmountToInvest
                : Math.min(annualIncome * 0.1, 200_000);

        for (TransactionDto t : transactions) {
            String dateStr = t.date();
            double amount = t.amount();
            double ceiling = t.ceiling();
            double remanent = t.remanent();

            if (seen.contains(dateStr)) {
                duplicate.add(InvalidTransactionDto.from(t, "Duplicate date"));
                continue;
            }
            seen.add(dateStr);

            if (DatesUtil.parseTimestamp(dateStr).isEmpty()) {
                invalid.add(InvalidTransactionDto.from(t, "Invalid date"));
                continue;
            }

            double expectedCeiling = ParseService.ceiling100(amount);
            double expectedRemanent = expectedCeiling - amount;
            if (ceiling != expectedCeiling || remanent != expectedRemanent) {
                invalid.add(InvalidTransactionDto.from(t,
                        "Ceiling/remanent mismatch: expected ceiling=" + expectedCeiling + ", remanent=" + expectedRemanent));
                continue;
            }
            if (!Double.isFinite(amount) || amount < 0 || amount >= MAX_AMOUNT) {
                invalid.add(InvalidTransactionDto.from(t, "Amount out of range [0, " + MAX_AMOUNT + ")"));
                continue;
            }
            valid.add(t);
        }

        double totalRemanent = valid.stream().mapToDouble(TransactionDto::remanent).sum();
        if (totalRemanent > maxInvest) {
            for (TransactionDto t : valid) {
                invalid.add(InvalidTransactionDto.from(t,
                        "Total investment " + totalRemanent + " exceeds max " + maxInvest));
            }
            return new ValidatorResponse(List.of(), invalid, duplicate);
        }
        return new ValidatorResponse(valid, invalid, duplicate);
    }
}
