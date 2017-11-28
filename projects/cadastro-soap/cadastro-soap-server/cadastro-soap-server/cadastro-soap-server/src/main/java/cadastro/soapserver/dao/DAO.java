package cadastro.soapserver.dao;


import cadastro.soapserver.model.Entidade;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;


/**
 *
 * @author Manoel Campos da Silva Filho
 * @param <T>
 */
@Transactional()
public class DAO<T extends Entidade> {
    @PersistenceContext(unitName = "default")
    private EntityManager em;
    
    private final Class<T> entityClass;
    
    public DAO(Class<T> entityClass){
        this.entityClass = entityClass;
    }

    public EntityManager getEm() {
        return em;
    }
    
    public T getById(Long id){
        return em.find(entityClass, id);
    }
    
    public boolean persist(T entity){
        em.persist(entity);
        return true;
    }
    
    public void remove(T entity){
        em.remove(entity);
    }
        
}
