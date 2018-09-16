package com.manoelcampos.soapserver.dao;

import com.manoelcampos.soapserver.model.Cadastro;

/**
 * Uma interface que implementa o padrão de projetos Data Access Objects (DAO)
 * para encapsular o código de acesso ao banco de dados.
 * 
 * @author Manoel Campos da Silva Filho
 * @param <T> classe de negócio que será manipulada
 *            pelo objeto DAO.
 *            Tais classes percentem ao pacote {@link com.manoelcampos.soapserver.model}.
 *            Cada instância de um DAO precisa estar associada a uma classe destas.
 *            Se ela estiver associada à classe Produto, isto indica
 *            que manipulará objetos produto obtidos do banco de dados.
 */
public interface DAO<T extends Cadastro> {
    T getById(Long id);
    boolean persist(T entity);
    void remove(T entity);
}
