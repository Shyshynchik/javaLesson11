package org.example.orm;


import lombok.SneakyThrows;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Formatter;

public enum SpecialTypeCaster {

    JSON("json") {
        @Override
        String castType() {
            return "to_json(?::json)";
        }
    },
    DEFAULT("") {
    },
    INTEGER("Integer") {
        @Override
        @SneakyThrows
        int setStatement(PreparedStatement statement, String value, int i) {
            statement.setInt(i++, Integer.parseInt(value));
            return i;
        }
    },
    POINT("point") {
        @Override
        String castType() {
            return "point(?::float, ?::float)";
        }

        @Override
        @SneakyThrows
        int setStatement(PreparedStatement statement, String value, int i) {
            String[] point = value.split(",");
            statement.setString(i++, point[0]);
            statement.setString(i++, point[1]);

            return i;
        }
    },
    DATETIME("datetime") {
        @Override
        @SneakyThrows
        int setStatement(PreparedStatement statement, String value, int i) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime dateTime = LocalDateTime.parse(value, formatter);
            statement.setTimestamp(i++, new Timestamp(dateTime.toInstant(ZoneOffset.UTC).toEpochMilli()));

            return i;
        }
    };

    private String type;

    SpecialTypeCaster (String type) {
        this.type = type;
    }

    static SpecialTypeCaster findCasterByType(String type) {
        for (SpecialTypeCaster specialTypeCaster: values()) {
            if (type.equals(specialTypeCaster.type)) {
                return specialTypeCaster;
            }
        }

        return DEFAULT;
    }

    String castType() {
        return "?";
    }

    @SneakyThrows
    int setStatement(PreparedStatement statement, String value, int i) {
        statement.setString(i++, value);
        return i;
    }

}
