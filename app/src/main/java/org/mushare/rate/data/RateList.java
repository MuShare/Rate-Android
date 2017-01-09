package org.mushare.rate.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dklap on 1/5/2017.
 */

public class RateList {
    private static Map<String, Double> rateMap = new HashMap<>();

    public synchronized static void clear() {
        rateMap.clear();
    }

    public synchronized static void put(String cid, double rate) {
        rateMap.put(cid, rate);
    }

    public synchronized static double get(String cid) {
        return rateMap.get(cid);
    }

    public synchronized static List<CurrencyRate> getList(List<CurrencyRate> list) {
        list.clear();
        for (String cid : rateMap.keySet()) {
            list.add(new CurrencyRate(CurrenciesList.get(cid), get(cid)));
        }
        return list;
    }
}
