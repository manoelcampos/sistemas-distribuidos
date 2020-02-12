package com.manoelcampos.server.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
 * @author Manoel Campos da Silva Filho
 */
@Entity
public class Cliente implements Cadastro, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Campo utilizado para controlar alterações concorrentes de
     * diferentes usuários ao cadastro de um mesmo cliente,
     * tentando evitar assim sobreposição de dados.
     * É utilizada a anotação @{@link Version} do JPA
     * para isto. Sempre que um usuário fizer uma alteração em um registro
     * de um cliente, tal campo é automaticamente incrementado.
     * Mais detalhes do funcionamento estão disponíveis em
     * https://manoelcampos.gitbooks.io/sistemas-distribuidos/book/chapter01c-transparency.html
     */
    @Version
    private long versao;

    private String nome;
    private String cpf;
    private char sexo;
    private String endereco;
    private String telefone;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public char getSexo() {
        return sexo;
    }

    public void setSexo(char sexo) {
        this.sexo = sexo;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }


    public long getVersao() {
        return versao;
    }

    public void setVersao(long versao) {
        this.versao = versao;
    }
}
