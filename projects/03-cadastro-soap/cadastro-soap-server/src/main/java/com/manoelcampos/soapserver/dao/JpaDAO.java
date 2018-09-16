package com.manoelcampos.soapserver.dao;


import javax.persistence.EntityManager;
import com.manoelcampos.soapserver.model.Cadastro;

/**
 *
 * @author Manoel Campos da Silva Filho
 * @param <T>
 */
public class JpaDAO<T extends Cadastro> implements DAO<T> {
    private final EntityManager em;
    private final Class<T> entityClass;
    
    public JpaDAO(EntityManager em, Class<T> entityClass){
        this.em = em;
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
