package com.manoelcampos.client;

/**
 *
 * @author manoelcampos
 */
public class Usuario {
    private long id;
    private String nome;
    private String cpf;
    private Cidade cidade;
    
    public Usuario(){
        this.id = 1;
        this.nome = "";
        this.cpf = "";
    }
    
    public Usuario(String nome, String cpf){
        this.nome = nome;
        this.cpf = cpf;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return the cpf
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * @param cpf the cpf to set
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

}
