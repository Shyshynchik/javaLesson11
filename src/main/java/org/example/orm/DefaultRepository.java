package org.example.orm;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class DefaultRepository<T> implements Repository<T> {

    private final Class<T> clazz;

    public DefaultRepository(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new EntityNotFoundException();
        }
        this.clazz = clazz;
    }

    @Override
    public T findByPrimaryKey(Object primaryKey) {
        var name = getPrimaryKeyNameFromClass();
        var tableName = getTableName();

        try(var connection = SQLConnection.createConnection()) {
            PreparedStatement statement = connection.prepareStatement("select * from " + tableName + " where " + name + " = ?");
            var typeCaster = SpecialTypeCaster.findCasterByType(primaryKey.getClass().getSimpleName());
            typeCaster.setStatement(statement, primaryKey.toString(), 1);
            statement.setFetchSize(1);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return createObject(resultSet);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private String getPrimaryKeyNameFromClass() {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                var columnAnnotation = field.getAnnotation(Column.class);
                return columnAnnotation.name();
            }
        }
        return null;
    }

    private String getTableName() {
        if (clazz.isAnnotationPresent(Table.class)) {
            return clazz.getDeclaredAnnotation(Table.class).name();
        }

        return getTableNameByClassName();
    }

    private String getTableNameByClassName() {
        String className = clazz.getSimpleName();
        return className.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase() + "s";
    }

    @SneakyThrows
    private T createObject(ResultSet resultSet) {
        T newEntity = clazz.getConstructor().newInstance();

        for (Field field: newEntity.getClass().getDeclaredFields()) {
            var entityField = newEntity.getClass().getDeclaredField(field.getName());
            entityField.setAccessible(true);

            var parseEntity = ParseEntity.findCasterByType(field);

            Object resultSetFieldValue = parseEntity.getValue(resultSet, getColumnName(field));
            entityField.set(newEntity, parseEntity.parseEntity(resultSetFieldValue, entityField));
        }

        return newEntity;
    }

    private enum ParseEntity {
        DEFAULT{

        },
        SIMPLE_TYPE {
            @Override
            @SneakyThrows
            Object parseEntity(Object value, Field field) {
                return (new ObjectMapper()).readValue(value.toString(), field.getType());
            }
        },
        DATETIME {
            @Override
            Object parseEntity(Object value, Field field) {
                Timestamp timestamp = (Timestamp) value;

                return timestamp.toLocalDateTime();
            }

            @Override
            @SneakyThrows
            Object getValue(ResultSet resultSet, String columnName) {
                return resultSet.getTimestamp(columnName);
            }
        },
        LIST_REFERENCE{

            @SneakyThrows
            Object getValue(ResultSet resultSet, String columnName) {
                return resultSet.getObject(columnName);
            }

            @Override
            @SuppressWarnings("rawtypes, unchecked")
            Object parseEntity(Object value, Field field) {
                var clazz = field.getAnnotation(OneToMany.class).targetEntity();
                var columnName = field.getAnnotation(OneToMany.class).mappedBy();
                Repository repository = new DefaultRepository(clazz);

                return repository.findByColumn(columnName, value);
            }

        },
        SIMPLE_REFERENCE{

            @SneakyThrows
            Object getValue(ResultSet resultSet, String columnName) {
                return resultSet.getObject(columnName);
            }

            @Override
            @SuppressWarnings("rawtypes, unchecked")
            Object parseEntity(Object value, Field field) {
                Repository repository = new DefaultRepository(field.getType());

                return repository.findByPrimaryKey(value);
            }
        };

        static ParseEntity findCasterByType(Field field) {
            if (field.getType().equals(LocalDateTime.class)) {
                return DATETIME;
            }
            if (field.isAnnotationPresent(OneToOne.class) || field.isAnnotationPresent(ManyToOne.class)) {
                return SIMPLE_REFERENCE;
            }
            if (field.isAnnotationPresent(OneToMany.class)) {
                return LIST_REFERENCE;
            }
            if (!field.getType().equals(String.class)) {
                return SIMPLE_TYPE;
            }

            return DEFAULT;
        }

        Object parseEntity(Object value, Field field) {
            return value;
        }

        @SneakyThrows
        Object getValue(ResultSet resultSet, String columnName) {
            return resultSet.getString(columnName);
        }
    }

    private String getColumnName(Field field) {
        if (field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).name().isBlank()) {
            return field.getAnnotation(Column.class).name();
        }
        if (field.isAnnotationPresent(JoinColumn.class) && !field.getAnnotation(JoinColumn.class).name().isBlank()) {
            return field.getAnnotation(JoinColumn.class).name();
        }
        if (field.isAnnotationPresent(OneToMany.class)) {
            return field.getAnnotation(OneToMany.class).mappedBy();
        }

        return getColumnNameByFieldName(field);
    }

    private String getColumnNameByFieldName(Field field) {
        String fieldName = field.getName();
        return fieldName.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
    }

    @Override
    @SneakyThrows
    public boolean save(T entity) {
        String sql = createInsertSqlQueryString(entity);

        try (var connection = SQLConnection.createConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);

            int i = 1;
            for (Field field: entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Column.class)) {
                    var value = field.get(entity).toString();

                    var type = field.isAnnotationPresent(Type.class)
                            ? field.getAnnotation(Type.class).type()
                            : "";

                    var typeCaster = SpecialTypeCaster.findCasterByType(type);

                    i = typeCaster.setStatement(statement, value, i);
                }
            }

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private String createInsertSqlQueryString(T entity) {
        List<String> insertPart = new ArrayList<>();
        List<String> valuesQuestion = new ArrayList<>();

        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            var name = getColumnName(field);

            insertPart.add(name);

            var type = TypeCaster.castType(field);

            valuesQuestion.add(type);
        }

        var insert = String.join(", ", insertPart);
        var question = String.join(", ", valuesQuestion);

        return "insert into " + getTableName() + "(" + insert + ")" + " values(" + question + ")";
    }

    @Override
    public List<T> findByColumn(String column, Object value) {
        var tableName = getTableName();
        List<T> resultList = new ArrayList<>();

        try(var connection = SQLConnection.createConnection()) {
            PreparedStatement statement = connection.prepareStatement("select * from " + tableName + " where " + column + " = ?");
            var typeCaster = SpecialTypeCaster.findCasterByType(value.getClass().getSimpleName());
            typeCaster.setStatement(statement, value.toString(), 1);
            statement.setFetchSize(1);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                resultList.add(createObject(resultSet));
            }

            return resultList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
