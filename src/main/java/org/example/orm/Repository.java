package org.example.orm;

public interface Repository<T> {

    T findByPrimaryKey(Object primaryKey);

    boolean save(T entity);

}
