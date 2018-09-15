package cadastro.soapserver.dao;


import cadastro.soapserver.model.Entidade;
import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 *
 * @author Manoel Campos da Silva Filho
 * @param <T>
 */
@Transactional
@Dependent
public class JpaDAO<T extends Entidade> implements DAO<T> {
    @PersistenceContext
    private EntityManager em;
    
    private Class<T> entityClass;
    
    public JpaDAO(Class<T> entityClass){
        this.entityClass = entityClass;
    }

    @Override
    public T getById(Long id){
        return em.find(entityClass, id);
    }
    
    @Override
    public boolean persist(T entity){
        em.persist(entity);
        return true;
    }
    
    @Override
    public void remove(T entity){
        em.remove(entity);
    }
        
}
