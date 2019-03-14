import java.util.ArrayList;
import java.util.List;

public class MapReduceApp{
    private List<Pessoa> pessoas;

    private MapReduceApp(){
        pessoas = new ArrayList<>(10);
        pessoas.add(new Pessoa("Manoel", 'M'));
        pessoas.add(new Pessoa("João", 'M'));
        pessoas.add(new Pessoa("Andreia", 'F'));
        pessoas.add(new Pessoa("Pedro", 'M'));
        pessoas.add(new Pessoa("Maria", 'F'));
        pessoas.add(new Pessoa("Joana", 'F'));
        pessoas.add(new Pessoa("Bete", 'F'));
        pessoas.add(new Pessoa("Márcio", 'M'));
        pessoas.add(new Pessoa("Luiza", 'F'));
        pessoas.add(new Pessoa("André", 'M'));
    }

    /**
     * Conta o total de pessoas do sexo especificado, de forma paralela, utilizando múltiplos núcleos de CPU para isso.
     * Pela reduzida quantidade de pessoas e simplificidade da operação (contar),
     * não há a menor necessidade de executar tal operação em paralelo.
     * Fazendo isso para tão poucos dados e tão simples operação de contagem
     * provavelmente vai levar mais tempo que executando em uma CPU só.
     * Executar operações em paralelo adiciona uma sobrecarga adicional
     * que só é compensada se tivermos processamento muitos dados e/ou
     * executando operações complexas sobre tais dados.
     *
     * @param sexo sexo para buscar o total de pessoas
     * @return o total de pessoas do sexo indicado
     */
    public long contaPessoas(char sexo){
        return pessoas.stream().parallel().filter(pessoa -> pessoa.getSexo() == sexo).count();
    }

    public static void main(String args[]){
        MapReduceApp app = new MapReduceApp();
        System.out.println("Total de mulheres: " + app.contaPessoas('F'));
    }
}

class Pessoa{
    private String nome;
    private char sexo;

    public Pessoa(String nome, char sexo){
        setNome(nome);
        setSexo(sexo);
    }

    public String getNome(){
        return nome;
    }

    public final void setNome(String nome){
        this.nome = nome;
    }

    public char getSexo(){
        return sexo;
    }

    public final void setSexo(char sexo){
        this.sexo = sexo;
    }
}
