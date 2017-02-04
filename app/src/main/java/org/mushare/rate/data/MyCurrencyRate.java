package org.mushare.rate.data;

/**
 * Created by dklap on 1/5/2017.
 */

public class MyCurrencyRate {
    private MyCurrency currency;
    private double rate;

    public MyCurrencyRate(MyCurrency currency, double rate) {
        this.currency = currency;
        this.rate = rate;
    }

    public MyCurrency getCurrency() {
        return currency;
    }

    public double getRate() {
        return rate;
    }
}
