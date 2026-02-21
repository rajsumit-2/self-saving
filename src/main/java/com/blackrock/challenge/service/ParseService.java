package com.blackrock.challenge.service;

import com.blackrock.challenge.dto.ExpenseDto;
import com.blackrock.challenge.dto.ParseResponse;
import com.blackrock.challenge.dto.TransactionDto;
import com.blackrock.challenge.util.DatesUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParseService {

    private static final int MULTIPLE = 100;
    private static final double MAX_AMOUNT = 500_000;

    public static double ceiling100(double amount) {
        if (amount <= 0) return MULTIPLE;
        return Math.ceil(amount / MULTIPLE) * MULTIPLE;
    }

    public ParseResponse parse(List<ExpenseDto> expenses) {
        if (expenses == null) {
            return new ParseResponse(List.of(), 0, 0, 0);
        }
        List<TransactionDto> transactions = new ArrayList<>();
        double totalExpense = 0;
        double totalRemanent = 0;

        for (ExpenseDto raw : expenses) {
            String ts = raw.getDateOrTimestamp();
            if (ts == null) continue;
            Double amountBox = raw.amount();
            if (amountBox == null || !Double.isFinite(amountBox)) continue;
            double amount = amountBox;
            if (amount < 0 || amount >= MAX_AMOUNT) continue;

            var parsed = DatesUtil.parseTimestamp(ts);
            if (parsed.isEmpty()) continue;

            double ceiling = ceiling100(amount);
            double remanent = ceiling - amount;

            transactions.add(new TransactionDto(
                    DatesUtil.formatTimestamp(parsed.get()),
                    amount,
                    ceiling,
                    remanent
            ));
            totalExpense += amount;
            totalRemanent += remanent;
        }

        return new ParseResponse(transactions, totalRemanent, totalRemanent, totalExpense);
    }
}
