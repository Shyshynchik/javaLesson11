package org.example.orm;

public interface Repository<T, V> {

    T findByPrimaryKey(V primaryKey);

    boolean save(T entity);

}
