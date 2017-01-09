package org.mushare.rate.data;

/**
 * Created by dklap on 1/5/2017.
 */

public class Settings {
    private static String baseCurrencyCid;

    public static String getBaseCurrencyCid() {
        return baseCurrencyCid;
    }

    public static void setBaseCurrencyCid(String baseCurrencyCid) {
        Settings.baseCurrencyCid = baseCurrencyCid;
    }

    public static MyCurrency getBaseCurrency() {
        if (baseCurrencyCid == null) return null;
        else return CurrenciesList.get(baseCurrencyCid);
    }
}
