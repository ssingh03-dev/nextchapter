package io.github.ssingh03_dev.nextchapter.converter;

import io.github.ssingh03_dev.nextchapter.enums.DeliveryDay;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class DeliveryDaySetConverter implements AttributeConverter<Set<DeliveryDay>, String> {

    @Override
    public String convertToDatabaseColumn(Set<DeliveryDay> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        return attribute.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }

    @Override
    public Set<DeliveryDay> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return EnumSet.noneOf(DeliveryDay.class);
        }

        return Arrays.stream(dbData.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(DeliveryDay::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(DeliveryDay.class)));
    }


}
