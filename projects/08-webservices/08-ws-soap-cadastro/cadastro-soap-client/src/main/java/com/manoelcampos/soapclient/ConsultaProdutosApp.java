package com.manoelcampos.soapclient;

import com.sun.xml.internal.ws.client.ClientTransportException;
import java.util.Scanner;

/**
 * Aplicação cliente para consumo do Web Service de Produtos
 * disponibilizado no projeto cadastro-soap-server.
 * O projeto servidor informado acima deve estar em execução para
 * esta aplicação funcionar.
 * A aplicação irá acessar tal Web Service para consultar produtos
 * cujo código é digitado pelo usuário.
 * 
 * <p>Para se certificar que o servidor está em execução,
 * tente acessar <a href="http://localhost:8080/GerenciaProduto/GerenciaProduto">este link</a>.</p>
 * 
 * @author Manoel Campos da Silva Filho <http://github.com/manoelcampos>
 */
public class ConsultaProdutosApp {
    /** Objeto que representa a porta pela qual o Web Services de Produtos será acessado. */
    private final GerenciaProduto port;
    private final Scanner scanner;
    
    public ConsultaProdutosApp(){
        /*Instancia o objeto que dará acesso ao Web Service de Produtos.*/
        GerenciaProduto_Service service = new GerenciaProduto_Service();
        
        /*
         * Instancia o objeto que permitirá acessar o Web Service de Produtos. A classe
         * GerenciaProdutoService foi criada pelo NetBeans quando o arquivo WSDL que descreve
         * as funções existentes no Web Service de Produtos foi lido. 
         * A classe GerenciaProduto também foi criada pelo NetBeans neste processo.
         * No entanto, ela é uma classe local que permitirá acessar
         * a classe remota de mesmo nome. Tal classe remota é que implementa
         * de fato o Web Service de Produtos.
         */
        this.port = service.getGerenciaProdutoPort();
        this.scanner = new Scanner(System.in);
    }
    
    public void iniciar(){
        int idProduto;
        do{
            System.out.print("Digite o id do produto que deseja consultar (ou -1 pra sair): ");
            idProduto = scanner.nextInt();
            if(idProduto >= 0){
                try{
                    //Tenta acessar o Web Service de Produtos e chamar o método getById
                    Produto produto = port.getById(idProduto);
                    imprimeProduto(produto);
                } catch(ClientTransportException e){
                    System.err.println(
                        "\nErro ao tentar fazer comunicação com o Web Service de Produtos. " + 
                        "\nVerifique se o servidor está em execução em " + 
                        "http://localhost:8080/GerenciaProduto/GerenciaProduto\n");
                }
            }
        }while(idProduto >= 0);
    }

    private void imprimeProduto(Produto produto) {
        if(produto == null){
            System.err.println("\nProduto não encontrado!\n");
            return;
        } 

        System.out.println("\nProduto: " + produto.getDescricao());
        System.out.println("Marca: " + produto.getMarca().getNome());
        System.out.println();
    }
    
    public static void main(String[] args) {
        ConsultaProdutosApp app = new ConsultaProdutosApp();
        app.iniciar();
    }
}
