package com.manoelcampos.soapserver.model;

import java.io.Serializable;


/**
 * Interface a ser utilizada por classe de negócio dentro
 * deste mesmo pacote, que representam as Entities da JPA,
 * ou seja, as tabelas do banco de dados.
 * 
 * @author Manoel Campos da Silva Filho
 */
public interface Cadastro extends Serializable {
    Long getId();
    void setId(Long id);
}
