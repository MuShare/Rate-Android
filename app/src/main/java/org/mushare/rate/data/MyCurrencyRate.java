package org.mushare.rate.data;

/**
 * Created by dklap on 1/5/2017.
 */

public class MyCurrencyRate {
    private MyCurrency currency;
    private Double rate;

    public MyCurrencyRate(MyCurrency currency, Double rate) {
        this.currency = currency;
        this.rate = rate;
    }

    public MyCurrency getCurrency() {
        return currency;
    }

    public Double getRate() {
        return rate;
    }
}
