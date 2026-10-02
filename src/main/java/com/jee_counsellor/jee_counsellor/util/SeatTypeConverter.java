package com.jee_counsellor.jee_counsellor.util;

import com.jee_counsellor.jee_counsellor.model.SeatType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SeatTypeConverter implements AttributeConverter<SeatType, String> {

    @Override
    public String convertToDatabaseColumn(SeatType attribute) {
        return attribute != null ? attribute.getDisplayName() : null;
    }

    @Override
    public SeatType convertToEntityAttribute(String dbData) {
        return dbData != null ? SeatType.fromString(dbData) : null;
    }
}
