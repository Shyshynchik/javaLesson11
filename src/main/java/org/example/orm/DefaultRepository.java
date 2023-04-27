package org.example.orm;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DefaultRepository<T, V> implements Repository<T, V> {

    private final Class<T> clazz;

    public DefaultRepository(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new EntityNotFoundException();
        }
        this.clazz = clazz;
    }

    @Override
    public T findByPrimaryKey(V primaryKey) {
        var name = getPrimaryKeyNameFromClass();
        var tableName = getTableName();

        try(var connection = SQLConnection.createConnection()) {
            PreparedStatement statement = connection.prepareStatement("select * from " + tableName + " where " + name + " = ?");
            statement.setString(1, primaryKey.toString());
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
            String resultSetFieldValue = resultSet.getString(getColumnName(field));
            var entityField = newEntity.getClass().getDeclaredField(field.getName());
            entityField.setAccessible(true);

            if (!entityField.getType().equals(String.class)) {
                entityField.set(newEntity, (new ObjectMapper()).readValue(resultSetFieldValue, entityField.getType()));
                continue;
            }

            entityField.set(newEntity, resultSetFieldValue);
        }

        return newEntity;
    }

    private String getColumnName(Field field) {
        if (!field.getAnnotation(Column.class).name().isBlank()) {
            return field.getAnnotation(Column.class).name();
        }

        return getColumnNameByFieldName(field);
    }

    private String getColumnNameByFieldName(Field field) {
        String fieldName = field.getName();
        return fieldName.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
    }

    @Override
    public boolean save(T entity) {
        createInsertSqlQueryString(entity);
        return false;
    }

    @SneakyThrows
    private String createInsertSqlQueryString(T entity) {
        List<String> insertPart = new ArrayList<>();
        List<String> valuesQuestion = new ArrayList<>();
        List<Object> valuesPart = new ArrayList<>();

        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            var value = field.get(entity);
            var name = getColumnName(field);

            insertPart.add(name);
            valuesPart.add(value);
            var type = "?";
            if (field.getType() != String.class) {
                type = "?::" + field.getType().getSimpleName().toLowerCase();
            }
            if (field.isAnnotationPresent(Type.class)) {
                type = "to_" + field.getAnnotation(Type.class).type() + "(?::" + field.getAnnotation(Type.class).type() + ")";
            }
            valuesQuestion.add(type);
        }

        var insert = String.join(", ", insertPart);
        var question = String.join(", ", valuesQuestion);

        var sql = "insert into " + getTableName() + "(" + insert + ")" + " values(" + question + ")";

        try (var connection = SQLConnection.createConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);

            for (int i = 0; i < valuesPart.size(); i++) {
                statement.setString(i+1, valuesPart.get(i).toString());
            }

            int value = statement.executeUpdate();
            return "";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
