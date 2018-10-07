package com.manoelcampos.cryptocoins;

/**
 * Representa os 
 * <a href="https://coinmarketcap.com/api/#endpoint_global_data">dados globais</a> 
 * de uma determinada moeda ou criptomoeda.
 *  
 * @author Manoel Campos da Silva Filho
 */
public class GlobalData {
    private double total_market_cap_usd;
    private double total_24h_volume_usd;
    private double bitcoin_percentage_of_market_cap;
    private double active_currencies;
    private double active_assets;
    private double active_markets;
    private double last_updated;

    /**
     * @return the total_market_cap_usd
     */
    public double getTotal_market_cap_usd() {
        return total_market_cap_usd;
    }

    /**
     * @param total_market_cap_usd the total_market_cap_usd to set
     */
    public void setTotal_market_cap_usd(double total_market_cap_usd) {
        this.total_market_cap_usd = total_market_cap_usd;
    }

    /**
     * @return the total_24h_volume_usd
     */
    public double getTotal_24h_volume_usd() {
        return total_24h_volume_usd;
    }

    /**
     * @param total_24h_volume_usd the total_24h_volume_usd to set
     */
    public void setTotal_24h_volume_usd(double total_24h_volume_usd) {
        this.total_24h_volume_usd = total_24h_volume_usd;
    }

    /**
     * @return the bitcoin_percentage_of_market_cap
     */
    public double getBitcoin_percentage_of_market_cap() {
        return bitcoin_percentage_of_market_cap;
    }

    /**
     * @param bitcoin_percentage_of_market_cap the bitcoin_percentage_of_market_cap to set
     */
    public void setBitcoin_percentage_of_market_cap(double bitcoin_percentage_of_market_cap) {
        this.bitcoin_percentage_of_market_cap = bitcoin_percentage_of_market_cap;
    }

    /**
     * @return the active_currencies
     */
    public double getActive_currencies() {
        return active_currencies;
    }

    /**
     * @param active_currencies the active_currencies to set
     */
    public void setActive_currencies(double active_currencies) {
        this.active_currencies = active_currencies;
    }

    /**
     * @return the active_assets
     */
    public double getActive_assets() {
        return active_assets;
    }

    /**
     * @param active_assets the active_assets to set
     */
    public void setActive_assets(double active_assets) {
        this.active_assets = active_assets;
    }

    /**
     * @return the active_markets
     */
    public double getActive_markets() {
        return active_markets;
    }

    /**
     * @param active_markets the active_markets to set
     */
    public void setActive_markets(double active_markets) {
        this.active_markets = active_markets;
    }

    /**
     * @return the last_updated
     */
    public double getLast_updated() {
        return last_updated;
    }

    /**
     * @param last_updated the last_updated to set
     */
    public void setLast_updated(double last_updated) {
        this.last_updated = last_updated;
    }
}                       
