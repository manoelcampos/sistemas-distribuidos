package com.manoelcampos.server.config;

import com.manoelcampos.server.dao.DAO;
import com.manoelcampos.server.dao.JpaDAO;
import com.manoelcampos.server.model.Cadastro;
import java.lang.reflect.ParameterizedType;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Classe responsável pela produção (criação/instanciação)
 * de objetos complexos como o {@link EntityManager} para 
 * fazer a persistência de dados em um banco
 * e objetos {@link DAO} que geram as instruções para realização
 * das operações básicas de seleção, inclusão, exclusão e atualização de dados no banco.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class Producers {
    /**
     * Devido ao uso da GraalVM para geração de aplicações nativas (que não existem
     * a JVM para executar), a recomendação do Quarkus é declarar atributos
     * injetados com visibilidade package.
     */
    @Produces 
    @PersistenceContext
    EntityManager em;
    
    @Produces
    public <T extends Cadastro> DAO<T> getDao(InjectionPoint ip){
        ParameterizedType t = (ParameterizedType) ip.getType();
        Class classe = (Class) t.getActualTypeArguments()[0];
        return new JpaDAO<>(em, classe);
    }        
}
