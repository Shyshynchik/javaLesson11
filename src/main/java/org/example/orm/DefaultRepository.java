package org.example.orm;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new EntityNotFoundException();
        }
        var name = getPrimaryKeyNameFromClass();
        var tableName = clazz.getDeclaredAnnotation(Table.class).name();
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

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private T createObject(ResultSet resultSet) {
        T newEntity = (T) clazz.getConstructor().newInstance();

        for (Field field: newEntity.getClass().getDeclaredFields()) {
            String resultSetFieldValue = resultSet.getString(field.getAnnotation(Column.class).name());
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

}
