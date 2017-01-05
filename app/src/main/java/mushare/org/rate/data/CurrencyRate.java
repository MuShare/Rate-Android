package mushare.org.rate.data;

import mushare.org.rate.data.Currency;

/**
 * Created by dklap on 1/5/2017.
 */

public class CurrencyRate {
    private Currency currency;
    private double rate;

    public CurrencyRate(Currency currency, double rate) {
        this.currency = currency;
        this.rate = rate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public double getRate() {
        return rate;
    }
}
