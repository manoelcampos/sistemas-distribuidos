package com.manoelcampos.server.dao;

import com.manoelcampos.server.model.Cadastro;

/**
 *
 * @author manoelcampos
 * @param <T>
 */
public interface DAO<T extends Cadastro> {
    T findById(long id);
    T findByField(String fieldName, Object value);
    boolean remove(T entity);
    void save(T entity);
}
