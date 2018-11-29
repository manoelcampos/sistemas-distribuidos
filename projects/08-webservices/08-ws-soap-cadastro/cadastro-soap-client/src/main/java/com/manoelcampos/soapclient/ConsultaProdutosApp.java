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
 * tente acessar <a href="http://localhost:8080/ProdutoWS/ProdutoWS">este link</a>.</p>
 * 
 * @author Manoel Campos da Silva Filho <http://github.com/manoelcampos>
 */
public class ConsultaProdutosApp {
    private final ProdutoWS produtoWS;
    private final Scanner scanner;
    
    public ConsultaProdutosApp(){
        this.produtoWS = new ProdutoWS_Service().getProdutoWSPort();
        scanner = new Scanner(System.in);
    }
    
    private void start(){
        int idProduto;
        do{
            System.out.print("Digite o id do produto que deseja consultar (ou -1 pra sair): ");
            idProduto = scanner.nextInt();
            if(idProduto >= 0){
                try{
                    Produto produto = produtoWS.getById(idProduto);
                    imprimeProduto(produto);
                } catch(ClientTransportException e){
                    System.err.println(
                        "\nErro ao tentar fazer comunicação com o Web Service de Produtos. " + 
                        "\nVerifique se o servidor está em execução em " + 
                        "http://localhost:8080/ProdutoWS/ProdutoWS\n");
                }
                
            }
        }while(idProduto >= 0);
    }

    private void imprimeProduto(Produto produto) {
        if(produto == null){
            System.err.println("\nProduto não encontrado!\n");
        } else {
            System.out.println("\nProduto: " + produto.getDescricao());
            System.out.println("Marca: " + produto.getMarca().getNome());
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
        ConsultaProdutosApp app = new ConsultaProdutosApp();
        app.start();
    }
    
}
