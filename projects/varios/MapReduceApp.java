import java.util.ArrayList;
import java.util.List;

/**
 * Mostra como é extremamente simples implementar o modelo de programação
 * <a href="https://pt.wikipedia.org/wiki/MapReduce">MapReduce</a> 
 * utilizando recursos de <a href="https://www.oracle.com/technetwork/pt/articles/java/streams-api-java-8-3410098-ptb.html">Streams e Expressões Lambda do Java 8</a>
 * para processar dados em paralelo (utilizando múltiplas CPUs).
 * 
 * <p>Diferentemente de ferramentas como o Apache Hadoop, utilizadas
 * para processamento distribuído de dados (em vários computadores),
 * Streams do Java 8 só permitem dividir o processamento de dados
 * entre as diferentes CPUs de uma mesma máquina.
 * </p>
 * 
 * @author Manoel Campos da Silva Filho
 */
public class MapReduceApp{
    private List<Pessoa> pessoas;

    public static void main(String args[]){
        MapReduceApp app = new MapReduceApp();
        System.out.println("Total de mulheres: " + app.contaPessoas('F'));
    }

    private MapReduceApp(){
        pessoas = new ArrayList<>(10);
        pessoas.add(new Pessoa("Manoel",  'M'));
        pessoas.add(new Pessoa("João",    'M'));
        pessoas.add(new Pessoa("Andréia", 'F'));
        pessoas.add(new Pessoa("Pedro",   'M'));
        pessoas.add(new Pessoa("Sara",    'F'));
        pessoas.add(new Pessoa("Tereza",  'F'));
        pessoas.add(new Pessoa("Bete",    'F'));
        pessoas.add(new Pessoa("Roberto", 'M'));
        pessoas.add(new Pessoa("Luiza",   'F'));
        pessoas.add(new Pessoa("Cláudia", 'F'));
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
     * @param sexo sexo das pessoas que deseja contar
     * @return o total de pessoas do sexo indicado
     */
    public long contaPessoas(char sexo){
        /* 
           A linha abaixo, apesar de conter várias chamadas de métodos, é uma única instrução
           encadeada (após a chamada de uma função, colocamos ponto para chamar outra).
           Veja que há apenas um ponto-e-vírgula.
           Indo de ponto em ponto, temos:
           - pessoas: obtém a lista de pessoas.
           - stream(): acessa tal lista como um objeto Stream do Java 8 para permitir processamento dos dados.
           - parallel(): permitir que seja feita a divisão automática dos dados da lista para processamento 
                         em várias CPUs. Se removido, executará tudo em uma única CPU.
           - filter(): filtra a lista de pessoas. Para isto, estamos utilizando o recurso
                       de Expressões Lambda do Java 8 (identificado pelo uso da seta ->)
                       para fornecer uma função anônima que define como as pessoas da lista
                       serão filtradas. Neste caso, pessoa é uma variável que implicitamente
                       será do tipo Pessoa (o tipo dos elementos da lista pessoas).
                       Depois da seta temos o código da nossa função anônima (neste caso uma única
                       linha), que define que queremos obter apenas as pessoas do sexo indicado 
                       no parâmetro sexo
            - count(): esta é uma operação de redução, que vai contar o total de pessoas do sexo
                       indicado e retornar tal número.
        */
        return pessoas.stream().parallel().filter(pessoa -> pessoa.getSexo() == sexo).count();
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
