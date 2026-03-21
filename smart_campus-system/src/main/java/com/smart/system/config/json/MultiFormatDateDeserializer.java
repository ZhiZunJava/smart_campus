package com.smart.system.config.json;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.smart.common.utils.DateUtils;

/**
 * 兼容多种前端日期字符串格式：
 * 1. yyyy-MM-dd HH:mm:ss
 * 2. ISO-8601，如 2026-03-21T00:00:00.000+08:00
 */
public class MultiFormatDateDeserializer extends JsonDeserializer<Date> {
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Date deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getValueAsString();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String text = value.trim();

        Date parsedDate = DateUtils.parseDate(text);
        if (parsedDate != null) {
            return parsedDate;
        }
        try {
            return Date.from(Instant.parse(text));
        } catch (Exception ignored) {
        }
        try {
            return Date.from(OffsetDateTime.parse(text).toInstant());
        } catch (Exception ignored) {
        }
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(text, DEFAULT_DATE_TIME_FORMATTER);
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception ignored) {
        }
        return (Date) context.handleWeirdStringValue(Date.class, text,
                "支持格式：yyyy-MM-dd HH:mm:ss 或 ISO-8601");
    }
}
