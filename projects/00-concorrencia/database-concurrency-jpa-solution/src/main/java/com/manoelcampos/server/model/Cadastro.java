package com.manoelcampos.server.model;

/**
 * Interface a ser utilizada por classe de negócio dentro deste mesmo pacote,
 * que representam as Entities da JPA, ou seja, as tabelas do banco de dados.
 *
 * @author Manoel Campos da Silva Filho
 */
public interface Cadastro {
    long getId();
    void setId(long id);
}
