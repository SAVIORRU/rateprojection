package ru.savior.rateprojection.core.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Currency;
import java.util.Locale;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Slf4j
public class DailyCurrencyRate {
    final Currency currency;

    final LocalDateTime rateDate;

    final BigDecimal rate;

    public DailyCurrencyRate(@NonNull Currency currency, @NonNull LocalDateTime rateDate, @NonNull BigDecimal rate) {
        if (rate.compareTo(new BigDecimal("0")) < 0 || rate.compareTo(new BigDecimal("0")) == 0) {
            log.error("Invalid currency rate {} in DailyCurrencyRate constructor", rate);
            throw new IllegalArgumentException("Currency rate cannot be below or equal zero");
        }
        this.currency = currency;
        this.rateDate = rateDate;
        this.rate = rate.setScale(2, RoundingMode.UP);
    }

    public String format() {
        String rateDate = this.rateDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String dayOfWeek = this.rateDate.getDayOfWeek().getDisplayName(TextStyle.SHORT,
                new Locale("ru", "RU"));
        String rate = new DecimalFormat("#.00").format(this.rate);

        return String.format("%s %s - %s", dayOfWeek, rateDate, rate);
    }
}
