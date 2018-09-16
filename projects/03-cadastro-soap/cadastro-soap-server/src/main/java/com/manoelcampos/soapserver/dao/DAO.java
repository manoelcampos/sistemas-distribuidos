package com.manoelcampos.soapserver.dao;

import com.manoelcampos.soapserver.model.Cadastro;

/**
 *
 * @author Manoel Campos da Silva Filho
 * @param <T>
 */
public interface DAO<T extends Cadastro> {
    T getById(Long id);
    boolean persist(T entity);
    void remove(T entity);
}
