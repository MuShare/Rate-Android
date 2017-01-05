package mushare.org.rate.url;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import mushare.org.rate.data.CurrenciesList;
import mushare.org.rate.data.Currency;
import mushare.org.rate.data.RateList;
import mushare.org.rate.data.Settings;

/**
 * Created by dklap on 1/3/2017.
 */

public class HttpHelper {
    private final static String host = "http://rate.mushare.cn/";

    public static int getCurrencyList(String lan, int oldRev) {
        StringBuilder stream = new StringBuilder();
        int responseCode = HttpDataHandler.sendGet(host + "api/currencies", "lan=" + lan + "&rev=" + oldRev, stream);

        if (responseCode == 200) {
            try {

                JSONObject reader = new JSONObject(stream.toString());
                JSONObject result = reader.getJSONObject("result");
                JSONArray currencies = result.getJSONArray("currencies");
                if (Settings.getBaseCurrency() == null && currencies.length() > 0)
                    Settings.setBaseCurrencyCid(currencies.getJSONObject(0).getString("cid"));
                for (int i = 0; i < currencies.length(); i++) {
                    JSONObject currency = currencies.getJSONObject(i);
                    CurrenciesList.put(currency.getString("cid"), new Currency(currency.getString("code"), currency.getString("icon"), currency.getString("name")));
                }
                CurrenciesList.setRevision(result.getInt("revision"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return responseCode;
    }

    public static double getCurrencyRate(String from, String to) {
        StringBuilder stream = new StringBuilder();
        int responseCode = HttpDataHandler.sendGet(host + "api/rate/current", "from=" + from + "&to=" + to, stream);

        if (responseCode == 200) {
            try {
                JSONObject reader = new JSONObject(stream.toString());
                JSONObject result = reader.getJSONObject("result");
                return result.getDouble("rate");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return -responseCode;
    }

    public static int getCurrencyRates(String token, String from, boolean favorite) {
        StringBuilder stream = new StringBuilder();
        int responseCode = HttpDataHandler.sendGet(host + "api/rate/current", "from=" + from + "&favorite=" + favorite, new RequestHeader[]{new RequestHeader("token", token)}, stream);

        if (responseCode == 200) {
            try {
                JSONObject reader = new JSONObject(stream.toString());
                JSONObject result = reader.getJSONObject("result");
                JSONObject rates = result.getJSONObject("rates");
                Iterator<String> keys = rates.keys();
                RateList.clear();
                while (keys.hasNext()) {
                    String cid = keys.next();
                    RateList.put(cid, rates.getDouble(cid));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return responseCode;
    }

    public static int getCurrencyRates(String from) {
        StringBuilder stream = new StringBuilder();
        int responseCode = HttpDataHandler.sendGet(host + "api/rate/current", "from=" + from, stream);

        if (responseCode == 200) {
            try {
                JSONObject reader = new JSONObject(stream.toString());
                JSONObject result = reader.getJSONObject("result");
                JSONObject rates = result.getJSONObject("rates");
                Iterator<String> keys = rates.keys();
                RateList.clear();
                while (keys.hasNext()) {
                    String cid = keys.next();
                    RateList.put(cid, rates.getDouble(cid));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return responseCode;
    }
}
