package cadastro.soapserver.dao;

import cadastro.soapserver.model.Entidade;
import javax.persistence.EntityManager;

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
