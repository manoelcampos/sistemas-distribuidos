package com.manoelcampos.soapserver.dao;


import javax.persistence.EntityManager;
import com.manoelcampos.soapserver.model.Cadastro;

/**
 * Uma implementação da interface {@link DAO} 
 * que utiliza JPA para acesso ao banco de dados.
 * 
 * @author Manoel Campos da Silva Filho
 * @param <T> classe de negócio que será manipulada
 *            pelo objeto DAO.
 *            Tais classes percentem ao pacote {@link com.manoelcampos.soapserver.model}.
 *            Cada instância de um DAO precisa estar associada a uma classe destas.
 *            Se ela estiver associada à classe Produto, isto indica
 *            que manipulará objetos produto obtidos do banco de dados.
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
