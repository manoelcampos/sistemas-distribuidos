package com.manoelcampos.server.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.runtime.JpaOperations;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Representa um Cliente do sistema.
 * A classe utiliza recursos do framework JPA
 * para evitar problemas de sobreposição de dados
 * quando há acesso concorrente ao cadastro de um mesmo cliente,
 * onde diferentes usuários tentam fazer alterações
 * em um mesmo cadastro simultanemanente.
 *
 * <p>É feito um controle de concorrência com lock otimista,
 * simplesmente usando a anotação @{@link Version} do JPA.</p>
 *
 * <p>Observe que todos os atributos são públicos,
 * indo totalmente contra tudo o que é ensinado sobre encapsulamento
 * em POO.
 * No entanto, como estamos usando a biblioteca
 * <a href="https://quarkus.io/guides/hibernate-orm-panache">Quarkus Panache</a>,
 * ele "automagicamente" inclui getters e setters para todos os atributos.
 * </p>
 *
 * <p>
 * É feita uma bruxaria (usando recursos avançados da linguagem) toda vez que
 * tentarmos: (i) obter o valor de um atributo, o getter é chamado automaticamente,
 * ou (ii) alterar o valor de um atributo, o setter é chamado automaticamente.
 * Se precisarmos programar qualquer lógica em algum getter e setter,
 * basta incluí-los manualmente como fazemos convencionalmente.
 * Assim, os atributos são públicos, parecem não estarem encapsulados,
 * mas por conta da magia negra realizada pelo Panache, eles estão :D
 * </p>
 *
 * <p>Adicionalmente, também não precisamos incluir um atributo id
 * para a classe, pois ao extender {@link PanacheEntity},
 * um id é fornecido, juntamente comm todas as funcionalidades
 * necessárias para manipular o cadastro dos clientes no banco de dados.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
@Entity
public class Cliente extends PanacheEntity implements Serializable {
    public String nome;
    public String cpf;
    public char sexo;
    public String endereco;
    public String telefone;

    /**
     * Campo utilizado para controlar alterações concorrentes de
     * diferentes usuários ao cadastro de um mesmo cliente,
     * tentando evitar assim sobreposição de dados.
     * É utilizada a anotação @{@link Version} do JPA
     * para isto. Sempre que um usuário fizer uma alteração em um registro
     * de um cliente, tal campo é automaticamente incrementado.
     * Mais detalhes do funcionamento estão disponíveis em
     * https://manoelcampos.com/sistemas-distribuidos/book/chapter01c-transparency.html
     */
    @Version
    public long versao;

    public static boolean update(Cliente cliente) {
        final EntityManager em = JpaOperations.getEntityManager();
        cliente = em.merge(cliente);
        cliente.flush();
        return true;
    }
}
