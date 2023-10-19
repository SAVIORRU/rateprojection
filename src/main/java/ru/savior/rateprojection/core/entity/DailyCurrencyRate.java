package ru.savior.rateprojection.core.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class DailyCurrencyRate {

    final Currency  currencyType;

    final LocalDateTime rateDate;

    final Double rate;

    public DailyCurrencyRate(@NonNull Currency currencyType,  @NonNull LocalDateTime rateDate, @NonNull Double rate) {
        if (rate <= 0) {
            throw new IllegalArgumentException("Currency rate cannot be below or equal zero");
        }
        this.currencyType = currencyType;
        this.rateDate = rateDate;
        this.rate = rate;
    }
}
