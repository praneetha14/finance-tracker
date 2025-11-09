package com.finance.tracker.model.enums;

import lombok.Getter;

@Getter
public enum MonthEnum {
    JANUARY(1, "january"),
    FEBRUARY(2, "february"),
    MARCH(3, "march"),
    APRIL(4, "april"),
    MAY(5, "may"),
    JUNE(6, "june"),
    JULY(7, "july"),
    AUGUST(8, "august"),
    SEPTEMBER(9, "september"),
    OCTOBER(10, "october"),
    NOVEMBER(11, "november"),
    DECEMBER(12, "december");

    private final int month;
    private final String value;

    MonthEnum(int month, String value) {
        this.month = month;
        this.value = value;
    }

    public static MonthEnum fromNumber(int monthNumber) {
        for (MonthEnum monthEnum : MonthEnum.values()) {
            if (monthEnum.getMonth() == monthNumber) {
                return monthEnum;
            }
        }
        throw new IllegalArgumentException("Invalid month number: " + monthNumber);
    }
}
