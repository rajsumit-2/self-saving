package com.blackrock.challenge.service;

import com.blackrock.challenge.dto.*;
import com.blackrock.challenge.util.DatesUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FilterService {

    public FilterResponse filterByPeriods(FilterRequest request) {
        List<QPeriodDto> q = request.q();
        List<PPeriodDto> p = request.p();
        List<TransactionDto> transactions = request.transactions();

        List<TransactionDto> valid = new ArrayList<>();
        List<InvalidTransactionDto> invalid = new ArrayList<>();

        for (TransactionDto tx : transactions) {
            var parsed = DatesUtil.parseTimestamp(tx.date());
            if (parsed.isEmpty()) {
                invalid.add(InvalidTransactionDto.from(tx, "Invalid date"));
                continue;
            }
            valid.add(effectiveRemanent(tx, q, p));
        }
        return new FilterResponse(valid, invalid);
    }

    public TransactionDto effectiveRemanent(TransactionDto tx, List<QPeriodDto> q, List<PPeriodDto> p) {
        var parsed = DatesUtil.parseTimestamp(tx.date());
        if (parsed.isEmpty()) {
            return new TransactionDto(tx.date(), tx.amount(), tx.ceiling(), 0);
        }
        var txDate = parsed.get();
        Optional<Double> qFixed = applyQ(txDate, q);
        double remanent = qFixed.orElse(tx.remanent());
        remanent += applyP(txDate, p);
        return new TransactionDto(tx.date(), tx.amount(), tx.ceiling(), remanent);
    }

    private Optional<Double> applyQ(java.time.LocalDateTime txDate, List<QPeriodDto> qPeriods) {
        if (qPeriods == null || qPeriods.isEmpty()) return Optional.empty();
        List<QPeriodDto> matching = new ArrayList<>();
        for (QPeriodDto q : qPeriods) {
            var startR = DatesUtil.parseTimestamp(q.start());
            var endR = DatesUtil.parseTimestamp(q.end());
            if (startR.isEmpty() || endR.isEmpty()) continue;
            if (DatesUtil.inRangeInclusive(txDate, startR.get(), endR.get())) {
                matching.add(new QPeriodDto(q.fixed(), q.start(), q.end()));
            }
        }
        if (matching.isEmpty()) return Optional.empty();
        matching.sort((a, b) -> {
            var ta = DatesUtil.parseTimestamp(a.start()).orElse(java.time.LocalDateTime.MIN);
            var tb = DatesUtil.parseTimestamp(b.start()).orElse(java.time.LocalDateTime.MIN);
            return tb.compareTo(ta); // latest start first
        });
        return Optional.of(matching.get(0).fixed());
    }

    private double applyP(java.time.LocalDateTime txDate, List<PPeriodDto> pPeriods) {
        if (pPeriods == null || pPeriods.isEmpty()) return 0;
        double extra = 0;
        for (PPeriodDto p : pPeriods) {
            var startR = DatesUtil.parseTimestamp(p.start());
            var endR = DatesUtil.parseTimestamp(p.end());
            if (startR.isEmpty() || endR.isEmpty()) continue;
            if (DatesUtil.inRangeInclusive(txDate, startR.get(), endR.get())) {
                extra += p.extra();
            }
        }
        return extra;
    }

    public List<SavingsByDateDto> savingsByKPeriods(List<TransactionDto> transactionsWithRemanent, List<KPeriodDto> kPeriods) {
        if (kPeriods == null) return List.of();
        List<SavingsByDateDto> result = new ArrayList<>();
        for (KPeriodDto k : kPeriods) {
            double amount = sumInRange(transactionsWithRemanent, k.start(), k.end());
            result.add(new SavingsByDateDto(k.start(), k.end(), amount, null, null, null));
        }
        return result;
    }

    private double sumInRange(List<TransactionDto> transactions, String startStr, String endStr) {
        var startR = DatesUtil.parseTimestamp(startStr);
        var endR = DatesUtil.parseTimestamp(endStr);
        if (startR.isEmpty() || endR.isEmpty()) return 0;
        var start = startR.get();
        var end = endR.get();
        double sum = 0;
        for (TransactionDto t : transactions) {
            var r = DatesUtil.parseTimestamp(t.date());
            if (r.isEmpty()) continue;
            if (DatesUtil.inRangeInclusive(r.get(), start, end)) {
                sum += t.remanent();
            }
        }
        return sum;
    }
}
