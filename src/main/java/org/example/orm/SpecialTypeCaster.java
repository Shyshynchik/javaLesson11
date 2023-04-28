package org.example.orm;


import lombok.SneakyThrows;

import java.sql.PreparedStatement;

public enum SpecialTypeCaster {

    JSON("json") {
        @Override
        String castType() {
            return "to_json(?::json)";
        }
    },
    DEFAULT("") {
        @Override
        String castType() {
            return "?";
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
        return "";
    }

    @SneakyThrows
    int setStatement(PreparedStatement statement, String value, int i) {
        statement.setString(i++, value);
        return i;
    }

}
