package com.manoelcampos.soapserver.dao;

import java.lang.reflect.ParameterizedType;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import com.manoelcampos.soapserver.model.Cadastro;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class Producers {
    @Produces
    @PersistenceContext()
    private EntityManager em;

    /**
     * Instancia um objeto JpaDAO por meio de injeção de dependência CDI.
     *
     * @param <T> Classe do objeto de negócio a ser manipulado pelo JpaDAO a ser
     * instanciado por injeção
     * @param ip Parâmetros passados (no ponto de injeção) ao objeto
     * sendo injetado, como tipos genéricos a serem associados à instância
     * criada.
     * @return Uma instância do JpaDAO solicitado no ponto de injeção.
     */
    @Produces
    public <T extends Cadastro> DAO<T> getDao(InjectionPoint ip){
        ParameterizedType t = (ParameterizedType) ip.getType();
        Class classe = (Class) t.getActualTypeArguments()[0];
        return new JpaDAO(em, classe);
    }    
}
