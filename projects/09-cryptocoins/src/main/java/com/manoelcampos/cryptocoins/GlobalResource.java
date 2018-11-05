package com.manoelcampos.cryptocoins;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.Closeable;

/**
 * Obtém cotação atuail de uma determinada criptomoeda
 * convertido para dolar, bitcoin e outra moeda desejada.
 * Utiliza a versão 1 da API REST do <a href="https://coinmarketcap.com/api/">CoinMarketCap</a>.
 * 
 * <p>Esta classe foi gerada partir do menu File > New > RESTfull Java Client
 * no NetBeans. No entanto, seu código foi refatorado e documentado
 * para torná-lo mais claro, simples, eficiente e fácil de alterar.</p>
 * 
 * @author Manoel Campos da Silva Filho
 */
public class GlobalResource implements Closeable {
    /**
     * Símbolo da moeda ou criptomoeda para qual
     * deseja-se obter dados globais.
     */
    private static final String SIMBOLO_MOEDA = "BTC";
    
    private final WebTarget webTarget;
    private final Client client;

    /**
     * URL base para acesso ao WS.
     * Um exemplo de URI final é https://api.coinmarketcap.com/v1/global?convert=BRL
     */
    private static final String BASE_URI = "https://api.coinmarketcap.com/v1/";

    public GlobalResource() {
        client = ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("global");
    }

    /**
     * Obtém dados globais para uma determinada moeda ou criptomoeda.
     * @param simboloMoedaDestino símbolo da moeda ou criptomoeda para qual deseja-se 
     *                            obter os dados globais
     * @return um objeto {@link GlobalData} contendo os dados globais da moeda/criptomoeda
     * @throws ClientErrorException 
     */
    public GlobalData getCotacoes(String simboloMoedaDestino) throws ClientErrorException {
        WebTarget resource = webTarget.queryParam("convert", simboloMoedaDestino);
        System.out.println("URI do WS: " + resource.getUri() + "\n");

        /*GlobalData.class indica o tipo de dados para o qual o documento JSON retornado
        deve ser convertido*/        
        return resource.request(MediaType.APPLICATION_JSON).get(GlobalData.class);
    }

    @Override
    public void close() {
        client.close();
    }
    
    public static void main(String[] args) {
        try(GlobalResource resource = new GlobalResource()){
            GlobalData cotacao = resource.getCotacoes(SIMBOLO_MOEDA);
            System.out.println("Moeda                               : " + SIMBOLO_MOEDA);
            System.out.printf("Valor Total de Mercado (USD)       : %.2f\n", cotacao.getTotal_market_cap_usd());
            System.out.printf("Volume Total negociado em 24h (USD): %.2f\n", cotacao.getTotal_24h_volume_usd());
        }
    }
}
