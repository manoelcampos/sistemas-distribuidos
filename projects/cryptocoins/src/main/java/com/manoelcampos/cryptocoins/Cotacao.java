package com.manoelcampos.cryptocoins;

import com.owlike.genson.annotation.JsonProperty;

/**
 * Representa a 
 * <a href="https://coinmarketcap.com/api/#endpoint_ticker_specific_cryptocurrency">cotação</a> 
 * de uma determinada criptomoeda.
 * 
 * Apesar de tal classe ter sido criada
 * objetivando retornar objetos dela
 * a partir do WS {@link TickerResource},
 * isto não é possível, uma vez que,
 * para cada criptomoeda escolhida,
 * os campos retornandos pelo WS mudam.
 * Assim, o WS retorna um {@link java.util.HashMap}
 * no lugar de um objeto desta classe.
 *  
 * @author Manoel Campos da Silva Filho
 */
public class Cotacao {
    private String id;
    private String name;
    private String symbol;
    private int rank;
    private double price_usd;
    private double price_btc;
    
    /**
     * Em Java, variáveis não podem começar com número,
     * assim, o atributo foi alterado incluíndo-se um underline.
     * A anotação {@link JsonProperty} é usada
     * para indicar o nome real no campo no JSON retornado pelo WS.
     * 
     */
    @JsonProperty("24h_volume_usd")
    private double _24h_volume_usd;
    
    private double market_cap_usd;
    private double available_supply;
    private double total_supply;
    private double max_supply;
    private double percent_change_1h;
    private double percent_change_24h;
    private double percent_change_7d;
    private long last_updated;
    private double price_brl;
    
    @JsonProperty("24h_volume_brl")
    private double _24h_volume_brl;
    
    private double market_cap_brl;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * @param symbol the symbol to set
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * @return the rank
     */
    public int getRank() {
        return rank;
    }

    /**
     * @param rank the rank to set
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * @return the price_usd
     */
    public double getPrice_usd() {
        return price_usd;
    }

    /**
     * @param price_usd the price_usd to set
     */
    public void setPrice_usd(double price_usd) {
        this.price_usd = price_usd;
    }

    /**
     * @return the price_btc
     */
    public double getPrice_btc() {
        return price_btc;
    }

    /**
     * @param price_btc the price_btc to set
     */
    public void setPrice_btc(double price_btc) {
        this.price_btc = price_btc;
    }

    /**
     * @return the _24h_volume_usd
     */
    public double get24h_volume_usd() {
        return _24h_volume_usd;
    }

    /**
     * @param _24h_volume_usd the _24h_volume_usd to set
     */
    public void set24h_volume_usd(double _24h_volume_usd) {
        this._24h_volume_usd = _24h_volume_usd;
    }

    /**
     * @return the market_cap_usd
     */
    public double getMarket_cap_usd() {
        return market_cap_usd;
    }

    /**
     * @param market_cap_usd the market_cap_usd to set
     */
    public void setMarket_cap_usd(double market_cap_usd) {
        this.market_cap_usd = market_cap_usd;
    }

    /**
     * @return the available_supply
     */
    public double getAvailable_supply() {
        return available_supply;
    }

    /**
     * @param available_supply the available_supply to set
     */
    public void setAvailable_supply(double available_supply) {
        this.available_supply = available_supply;
    }

    /**
     * @return the total_supply
     */
    public double getTotal_supply() {
        return total_supply;
    }

    /**
     * @param total_supply the total_supply to set
     */
    public void setTotal_supply(double total_supply) {
        this.total_supply = total_supply;
    }

    /**
     * @return the max_supply
     */
    public double getMax_supply() {
        return max_supply;
    }

    /**
     * @param max_supply the max_supply to set
     */
    public void setMax_supply(double max_supply) {
        this.max_supply = max_supply;
    }

    /**
     * @return the percent_change_1h
     */
    public double getPercent_change_1h() {
        return percent_change_1h;
    }

    /**
     * @param percent_change_1h the percent_change_1h to set
     */
    public void setPercent_change_1h(double percent_change_1h) {
        this.percent_change_1h = percent_change_1h;
    }

    /**
     * @return the percent_change_24h
     */
    public double getPercent_change_24h() {
        return percent_change_24h;
    }

    /**
     * @param percent_change_24h the percent_change_24h to set
     */
    public void setPercent_change_24h(double percent_change_24h) {
        this.percent_change_24h = percent_change_24h;
    }

    /**
     * @return the percent_change_7d
     */
    public double getPercent_change_7d() {
        return percent_change_7d;
    }

    /**
     * @param percent_change_7d the percent_change_7d to set
     */
    public void setPercent_change_7d(double percent_change_7d) {
        this.percent_change_7d = percent_change_7d;
    }

    /**
     * @return the last_updated
     */
    public long getLast_updated() {
        return last_updated;
    }

    /**
     * @param last_updated the last_updated to set
     */
    public void setLast_updated(long last_updated) {
        this.last_updated = last_updated;
    }

    /**
     * @return the price_brl
     */
    public double getPrice_brl() {
        return price_brl;
    }

    /**
     * @param price_brl the price_brl to set
     */
    public void setPrice_brl(double price_brl) {
        this.price_brl = price_brl;
    }

    /**
     * @return the _24h_volume_brl
     */
    public double get24h_volume_brl() {
        return _24h_volume_brl;
    }

    /**
     * @param _24h_volume_brl the _24h_volume_brl to set
     */
    public void set24h_volume_brl(double _24h_volume_brl) {
        this._24h_volume_brl = _24h_volume_brl;
    }

    /**
     * @return the market_cap_brl
     */
    public double getMarket_cap_brl() {
        return market_cap_brl;
    }

    /**
     * @param market_cap_brl the market_cap_brl to set
     */
    public void setMarket_cap_brl(double market_cap_brl) {
        this.market_cap_brl = market_cap_brl;
    }
}
