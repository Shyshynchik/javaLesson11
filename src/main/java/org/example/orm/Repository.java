package org.example.orm;

public interface Repository<T, K> {

    T findByPrimaryKey(K primaryKey);

}
