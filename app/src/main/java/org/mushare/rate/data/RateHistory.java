package org.mushare.rate.data;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dklap on 2/7/2017.
 */

public class RateHistory {
    private List<Double> data = new LinkedList<>();
    private long time;
    private String inCurrency;
    private String outCurrency;

    public void clear() {
        data.clear();
        time = 0;
        inCurrency = null;
        outCurrency = null;
    }

    public List<Double> getData() {
        return data;
    }

    public void addData(double data) {
        this.data.add(data);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getInCurrency() {
        return inCurrency;
    }

    public String getOutCurrency() {
        return outCurrency;
    }

    public void setCurrencyPair(String inCurrency, String outCurrency) {
        this.inCurrency = inCurrency;
        this.outCurrency = outCurrency;
    }
}
