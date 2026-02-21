package com.blackrock.challenge.service;

import com.blackrock.challenge.dto.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReturnsService {

    private static final double NPS_RATE = 0.0711;
    private static final double INDEX_RATE = 0.1449;
    private static final double NPS_TAX_CAP = 200_000;
    private static final double INFLATION_DEFAULT = 0.055;

    public ReturnsResponse computeNpsReturns(int age, double wage, Double inflation,
                                            List<QPeriodDto> q, List<PPeriodDto> p, List<KPeriodDto> k,
                                            List<TransactionDto> transactions) {
        if (inflation == null) inflation = INFLATION_DEFAULT;
        FilterRequest req = new FilterRequest(q, p, k, transactions);
        FilterResponse filtered = filterService.filterByPeriods(req);
        List<TransactionDto> valid = filtered.valid();

        double totalAmount = valid.stream().mapToDouble(TransactionDto::amount).sum();
        double totalCeiling = valid.stream().mapToDouble(TransactionDto::ceiling).sum();
        List<SavingsByDateDto> savingsRaw = filterService.savingsByKPeriods(valid, k);

        double annualIncome = wage * 12;
        int t = yearsToRetirement(age);

        List<SavingsByDateDto> savingsByDates = new ArrayList<>();
        for (SavingsByDateDto sav : savingsRaw) {
            double amount = sav.amount();
            double future = compound(amount, NPS_RATE, t);
            double deduction = npsDeduction(amount, annualIncome);
            double taxBenefit = tax(annualIncome) - tax(annualIncome - deduction);
            double realValue = future / Math.pow(1 + inflation, t);
            double profits = Math.round((realValue - amount) * 100) / 100.0;
            savingsByDates.add(SavingsByDateDto.nps(sav.start(), sav.end(), amount, profits, taxBenefit));
        }
        return new ReturnsResponse(totalAmount, totalCeiling, savingsByDates);
    }

    public ReturnsResponse computeIndexReturns(int age, Double inflation,
                                               List<QPeriodDto> q, List<PPeriodDto> p, List<KPeriodDto> k,
                                               List<TransactionDto> transactions) {
        if (inflation == null) inflation = INFLATION_DEFAULT;
        FilterRequest req = new FilterRequest(q, p, k, transactions);
        FilterResponse filtered = filterService.filterByPeriods(req);
        List<TransactionDto> valid = filtered.valid();

        double totalAmount = valid.stream().mapToDouble(TransactionDto::amount).sum();
        double totalCeiling = valid.stream().mapToDouble(TransactionDto::ceiling).sum();
        List<SavingsByDateDto> savingsRaw = filterService.savingsByKPeriods(valid, k);

        int t = yearsToRetirement(age);

        List<SavingsByDateDto> savingsByDates = new ArrayList<>();
        for (SavingsByDateDto sav : savingsRaw) {
            double amount = sav.amount();
            double future = compound(amount, INDEX_RATE, t);
            double realValue = future / Math.pow(1 + inflation, t);
            double ret = Math.round(realValue * 100) / 100.0;
            savingsByDates.add(SavingsByDateDto.index(sav.start(), sav.end(), amount, ret));
        }
        return new ReturnsResponse(totalAmount, totalCeiling, savingsByDates);
    }

    private final FilterService filterService;

    public ReturnsService(FilterService filterService) {
        this.filterService = filterService;
    }

    private static double tax(double income) {
        if (income <= 700_000) return 0;
        if (income <= 1_000_000) return (income - 700_000) * 0.1;
        if (income <= 1_200_000) return 30_000 + (income - 1_000_000) * 0.15;
        if (income <= 1_500_000) return 60_000 + (income - 1_200_000) * 0.2;
        return 120_000 + (income - 1_500_000) * 0.3;
    }

    private static double npsDeduction(double invested, double annualIncome) {
        double cap = Math.min(annualIncome * 0.1, NPS_TAX_CAP);
        return Math.min(invested, cap);
    }

    private static double compound(double P, double r, int t) {
        return P * Math.pow(1 + r, t);
    }

    private static int yearsToRetirement(int age) {
        return age < 60 ? 60 - age : 5;
    }
}
