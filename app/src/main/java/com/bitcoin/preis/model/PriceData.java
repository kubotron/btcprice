package com.bitcoin.preis.model;

public class PriceData {

    public PriceData(String base, String currency, float amount) {
        this.base = base;
        this.currency = currency;
        this.amount = amount;
    }

    public final String base;

    public final String currency;

    public final float amount;
}
