package ru.savior.rateprojection.core.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class DailyCurrencyRate {

    final Currency currencyType;

    final LocalDateTime rateDate;

    final BigDecimal rate;

    public DailyCurrencyRate(@NonNull Currency currencyType, @NonNull LocalDateTime rateDate, @NonNull BigDecimal rate) {
        if (rate.compareTo(new BigDecimal("0")) < 0 || rate.compareTo(new BigDecimal("0")) == 0) {
            throw new IllegalArgumentException("Currency rate cannot be below or equal zero");
        }
        this.currencyType = currencyType;
        this.rateDate = rateDate;
        this.rate = rate.setScale(2, RoundingMode.UP);
    }
}
