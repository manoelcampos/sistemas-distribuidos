package com.manoelcampos.soapserver.dao;

import java.lang.reflect.ParameterizedType;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import com.manoelcampos.soapserver.model.Cadastro;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Classe que produz objetos complexos
 * como {@link EntityManager} e
 * {@link DAO}. Tais objetos não podem ser instanciados
 * por injeção de dependência,
 * usando a anotação {@link javax.inject.Inject} pois seus construtores requerem
 * alguns parâmetros.
 * 
 * Sempre que uma instância desses objetos for solicitada
 * via anotação {@link javax.inject.Inject}, os produtores
 * nesta classe são automaticamente executados
 * para adequadamente criar e retornar tais objetos.
 * Se você olhar o construtor da classe {@link com.manoelcampos.soapserver.dao.JpaDAO}
 * verá que ela é complexa de ser criada uma vez que não tem um construtor padrão.
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
