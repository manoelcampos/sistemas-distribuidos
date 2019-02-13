package com.manoelcampos.server.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Representa um endereço de um cliente.
 * 
 * <p>Como existe um relacionamento 1..N entre Cliente e Endereco,
 * ao acessar o serviço que obtém um cliente em formato JSON
 * ocorrer o erro StackOverflow, pois o relacionamento entre estas duas
 * classes é bi-direcional: o cliente tem uma lista de endereços e cada endereço tem
 * um objeto indicando quem é o cliente para aquele endereço.
 * Com isto, a biblioteca Jackson (implementação da API Json-P)
 * entra em loop infinito ao tentar gerar o JSON para um cliente que possui endereços.
 * Para evitar tal erro, era suposto usar a anotação @JsonIgnore do Jackson
 * no método getCliente() na classe Endereco. No entanto, isto não funcionou.
 * A única maneira foi simplesmente remover tal método e deixar apenas o setter.</p>
 * 
 * @author Manoel Campos da Silva Filho
 */
@Entity
public class Endereco implements Cadastro, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String logradouro;
    
    @ManyToOne()
    private Cliente cliente;
    
    @ManyToOne
    private Cidade cidade;
    
    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the logradouro
     */
    public String getLogradouro() {
        return logradouro;
    }

    /**
     * @param logradouro the logradouro to set
     */
    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    /**
     * @return the cidade
     */
    public Cidade getCidade() {
        return cidade;
    }

    /**
     * @param cidade the cidade to set
     */
    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    /**
     * @param cliente the cliente to set
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

}
