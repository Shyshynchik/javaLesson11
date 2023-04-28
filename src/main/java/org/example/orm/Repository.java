package org.example.orm;

import java.util.List;

public interface Repository<T> {

    T findByPrimaryKey(Object primaryKey);

    boolean save(T entity);

    List<T> findByColumn(String column, Object value);

}
