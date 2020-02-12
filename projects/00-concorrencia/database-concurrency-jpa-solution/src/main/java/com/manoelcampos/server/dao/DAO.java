package com.manoelcampos.server.dao;

import com.manoelcampos.server.model.Cadastro;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 *
 * @author manoelcampos
 * @param <T>
 */
public interface DAO<T extends Cadastro> {
    /**
     * Cria um objeto para executar uma consulta JPQL.
     * @param jpql consulta JPQL para localizar um objeto
     * @return o objeto query criado
     */
    TypedQuery<T> createQuery(String jpql);

    /**
     * Busca um objeto a partir do seu id
     * @param id id do objeto a ser localizado
     * @return o objeto localizado ou null caso não seja encontrado
     */
    T findById(long id);

    /**
     * Seleciona todos os cadastros existentes no banco.
     * @return
     */
    List<T> all();

    /**
     * Busca um objeto a partir de um determinado campo
     * @param fieldName nome do atributo do objeto a ser utilizado para busca
     * @param value valor do atributo a ser utilizado na busca
     * @return o objeto localizado ou null caso não seja encontrado
     */
    T findByField(String fieldName, Object value);

    /**
     * Remove um objeto do banco a partir de uma instância do próprio objeto.
     * @param entity objeto a ser removido
     * @return true se o objeto foi localizado e removido com sucesso, false caso contrário
     */
    boolean delete(T entity);
    
    /**
     * Salva um objeto no banco,
     * que pode ser tanto uma inclusão ou alteração.
     * Se o objeto já existir, será atualizado.
     * Senão, será incluído.
     * @param entity objeto a ser salvo
     * @return id do objeto salvo
     */
    long save(T entity);
}
