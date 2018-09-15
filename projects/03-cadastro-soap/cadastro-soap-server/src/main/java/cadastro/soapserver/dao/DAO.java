package cadastro.soapserver.dao;

import cadastro.soapserver.model.Entidade;

/**
 *
 * @author Manoel Campos da Silva Filho
 * @param <T>
 */
public interface DAO<T extends Entidade> {
    T getById(Long id);
    boolean persist(T entity);
    void remove(T entity);
}
