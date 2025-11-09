package com.finance.tracker.model.vo;

public record FinancesVO(double salary, double totalExpenseCost, double estimatedSavings, double remainingAmountToUse,
                         String preSignedUrl) {
}
