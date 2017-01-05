package mushare.org.rate.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dklap on 1/5/2017.
 */

public class CurrenciesList {
    private static Map<String, Currency> currenciesMap = new HashMap<>();
    private static int revision;


    public synchronized static void put(String cid, Currency currency) {
        currenciesMap.put(cid, currency);
    }

    public synchronized static Currency get(String cid) {
        return currenciesMap.get(cid);
    }

    public synchronized static void setRevision(int revision) {
        CurrenciesList.revision = revision;
    }

    public synchronized static int getRevision() {
        return revision;
    }
}
