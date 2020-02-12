package com.manoelcampos.server.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * Representa um Cliente do sistema.
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

    public static boolean update(Cliente cliente) {
        Cliente existente = Cliente.findById(cliente.id);
        if(existente != null){
            existente.nome = cliente.nome;
            existente.cpf = cliente.cpf;
            existente.sexo = cliente.sexo;
            existente.endereco = cliente.endereco;
            existente.telefone = cliente.telefone;
            existente.persist();
            return true;
        }

        return false;
    }
}
