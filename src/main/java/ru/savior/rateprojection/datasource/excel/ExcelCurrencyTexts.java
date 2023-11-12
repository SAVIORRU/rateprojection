package ru.savior.rateprojection.datasource.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.*;

@Slf4j
public class ExcelCurrencyTexts {
    private final Map<Currency, String> customCurrencyTexts = new HashMap<>();

    public ExcelCurrencyTexts() {
        loadTextsFromResource();
    }

    private void loadTextsFromResource() {
        try {
            addCustomCurrencyTexts(readTextsFile());
        } catch (IOException | RuntimeException exception) {
            log.info("Cannot find or load custom currency texts file from resources due to reason {}",
                    exception.getMessage());
        }
    }

    private Map<String, String> readTextsFile() throws IOException {
        Map<String, String> currencyTextsRaw = new HashMap<>();
        URL resourcePath = this.getClass().getClassLoader().getResource("datasource/excel/currencyTexts.yaml");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        currencyTextsRaw = mapper.readValue(resourcePath, Map.class);
        log.info("Found currencyTexts.yaml");
        return currencyTextsRaw;
    }

    private void addCustomCurrencyTexts(Map<String, String> extractedTexts) {
        for (String currencyCode : extractedTexts.keySet()) {
            Currency currency = Currency.getInstance(currencyCode.toUpperCase());
            customCurrencyTexts.put(currency, extractedTexts.get(currencyCode));
        }
    }

    public Currency findCurrencyForText(String currencyText) {
        Currency currency = findCurrencyTextInCustomTexts(currencyText);
        if (currency == null) {
            currency = findCurrencyInStandardTexts(currencyText);
        }
        return currency;
    }

    private Currency findCurrencyTextInCustomTexts(String currencyText) {
        Optional<Map.Entry<Currency, String>> currency = customCurrencyTexts.entrySet()
                .stream()
                .filter(x -> x.getValue().equals(currencyText.toLowerCase()))
                .findAny();
        return currency.map(Map.Entry::getKey).orElse(null);
    }

    private Currency findCurrencyInStandardTexts(String currencyText) {
        Optional<Currency> currency = Currency.getAvailableCurrencies()
                .stream()
                .filter(x -> x.getDisplayName(new Locale("ru", "RU"))
                        .toLowerCase().equals(currencyText.toLowerCase()))
                .findAny();
        return currency.orElseThrow(() -> new NoSuchElementException("The following currency " +
                currencyText + " not found"));
    }
}
