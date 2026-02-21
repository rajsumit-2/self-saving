package com.blackrock.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SavingsByDateDto(String start, String end, double amount, Double profits, Double taxBenefit, @JsonProperty("return") Double return_) {
    public static SavingsByDateDto nps(String start, String end, double amount, double profits, double taxBenefit) {
        return new SavingsByDateDto(start, end, amount, profits, taxBenefit, null);
    }
    public static SavingsByDateDto index(String start, String end, double amount, double return_) {
        return new SavingsByDateDto(start, end, amount, null, null, return_);
    }
}
