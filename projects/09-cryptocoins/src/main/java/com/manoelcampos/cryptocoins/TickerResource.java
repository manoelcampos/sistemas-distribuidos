package com.manoelcampos.cryptocoins;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.Closeable;
import java.util.HashMap;
import java.util.List;

/**
 * Obtém cotação atual de uma determinada criptomoeda
 * convertido para dolar, bitcoin e outra moeda desejada.
 * Utiliza a versão 1 da API REST do <a href="https://coinmarketcap.com/api/">CoinMarketCap</a>.
 * 
 * <p>Esta classe foi gerada partir do menu File > New > RESTfull Java Client
 * no NetBeans. No entanto, seu código foi refatorado e documentado
 * para torná-lo mais claro, simples, eficiente e fácil de alterar.</p>
 * 
 * @author Manoel Campos da Silva Filho
 */
public class TickerResource implements Closeable {
    /**
     * Nome da criptomoeda para obter a cotação.
     * O nome das moedas suportadas pode ser obtido na tabela
     * da <a href="https://coinmarketcap.com">página inicial</a>.
     */
    private static final String NOME_CRIPTOMOEDA = "Litecoin";

    /**
     * Símbolo da moeda adicional para converter a {@link #NOME_CRIPTOMOEDA}. 
     * O símbolo das moedas suportadas pode ser obtido 
     * <a href="https://coinmarketcap.com/api/#endpoint_ticker_specific_cryptocurrency">aqui</a>.
     */
    private static final String SIMBOLO_MOEDA_CONVERSAO = "BRL";
    
    private final WebTarget webTarget;
    private final Client client;
    
    /**
     * URL base para acesso ao WS.
     * Um exemplo de URI final é https://api.coinmarketcap.com/v1/ticker/bitcoin/?convert=brl
     */
    private static final String BASE_URI = "https://api.coinmarketcap.com/v1/";

    public TickerResource() {
        client = ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("ticker");
    }

    /**
     * Obtém a cotação de uma determinada criptomoeda.
     * Observe que o método retorna uma lista de {@link HashMap}
     * no lugar de uma lista de objetos {@link Cotacao}.
     * Isto se deve ao fato de os campos da cotação mudam
     * de acordo com a moeda de destino para qual a cotação
     * será convertida.
     * 
     * @param nomeCriptomoedaOrigem nome da criptomoeda que deseja-se obter a cotação.
     * @param simboloMoedaDestino símbolo da moeda ou criptomoeda para qual deseja-se converter
     * @return uma lista de {@link HashMap} com as cotações obtidas,
     * onde cada chave no HashMap representa o valor de um campo
     * com dados da cotação.
     * @throws ClientErrorException 
     */
    public List<HashMap> getCotacoes(String nomeCriptomoedaOrigem, String simboloMoedaDestino) throws ClientErrorException {
        WebTarget resource = webTarget
                                .path(nomeCriptomoedaOrigem)
                                .queryParam("convert", simboloMoedaDestino);
        System.out.println("URI do WS: " + resource.getUri() + "\n");
        
        /*List.class indica o tipo de dados para o qual o documento JSON retornado
        deve ser convertido*/
        return resource.request(MediaType.APPLICATION_JSON).get(List.class);
    }

    @Override
    public void close() {
        client.close();
    }
    
    public static void main(String[] args) {
        try(TickerResource resource = new TickerResource()){
            List<HashMap> cotacoes = resource.getCotacoes(NOME_CRIPTOMOEDA, SIMBOLO_MOEDA_CONVERSAO);
            if(cotacoes.isEmpty()){
                System.err.println("Nenhuma cotação retornada para a criptomoeda " + NOME_CRIPTOMOEDA);
                return;
            }
            
            HashMap cotacao = cotacoes.get(0);
            System.out.println("Moeda    : " + cotacao.get("name") + " (" + cotacao.get("symbol")+ ")");
            if(!NOME_CRIPTOMOEDA.equalsIgnoreCase("bitcoin")){
                System.out.println("Valor BTC: " + cotacao.get("price_btc"));
            }
            System.out.println("Valor US$: " + cotacao.get("price_usd"));
            System.out.println("Valor  R$: " + cotacao.get("price_brl"));
        }
    }
    
}
