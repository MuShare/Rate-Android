package org.mushare.rate.url;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mushare.rate.data.CurrencyList;
import org.mushare.rate.data.MyCurrency;
import org.mushare.rate.data.RateHistory;
import org.mushare.rate.data.RateList;

import java.util.Iterator;

/**
 * Created by dklap on 1/3/2017.
 */

public class HttpHelper {
    private final static String host = "http://rate.mushare.cn/";

    static public int getCurrencyList(String lan, int oldRev) {
        StringBuilder stream = new StringBuilder();
        int responseCode = HttpDataHandler.sendGet(host + "api/currencies/list", "lan=" + lan +
                "&rev=" + oldRev, stream);

        if (responseCode == 200) {
            try {

                JSONObject reader = new JSONObject(stream.toString());
                JSONObject result = reader.getJSONObject("result");
                if (oldRev == result.getInt("revision")) return responseCode;
                JSONArray currencies = result.getJSONArray("currencies");
                for (int i = 0; i < currencies.length(); i++) {
                    JSONObject currency = currencies.getJSONObject(i);
                    CurrencyList.put(currency.getString("cid"), new MyCurrency(currency
                            .getString("code"), currency.getString("icon"), currency.getString
                            ("name")));
                }
                CurrencyList.setRevision(result.getInt("revision"));
                CurrencyList.setLanguage(lan);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return responseCode;
    }

//    public double getCurrencyRate(String from, String to) {
//        StringBuilder stream = new StringBuilder();
//        int responseCode = HttpDataHandler.sendGet(host + "api/rate/current", "from=" + from +
//                "&to=" + to, stream);
//
//        if (responseCode == 200) {
//            try {
//                JSONObject reader = new JSONObject(stream.toString());
//                JSONObject result = reader.getJSONObject("result");
//                return result.getDouble("rate");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return -responseCode;
//    }
//
//    public int getCurrencyRates(String token, String from, boolean favorite) {
//        StringBuilder stream = new StringBuilder();
//        int responseCode = HttpDataHandler.sendGet(host + "api/rate/current", "from=" + from +
//                        "&favorite=" + favorite, new RequestHeader[]{new RequestHeader("token",
//                        token)},
//                stream);
//
//        if (responseCode == 200) {
//            try {
//                JSONObject reader = new JSONObject(stream.toString());
//                JSONObject result = reader.getJSONObject("result");
//                JSONObject rates = result.getJSONObject("rates");
//                Iterator<String> keys = rates.keys();
//                RateList.clear();
//                while (keys.hasNext()) {
//                    String cid = keys.next();
//                    RateList.put(cid, rates.getDouble(cid));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return responseCode;
//    }

    static public int getCurrencyRates(String from) {
        StringBuilder stream = new StringBuilder();
        int responseCode = HttpDataHandler.sendGet(host + "api/rate/current", "from=" + from,
                stream);

        if (responseCode == 200) {
            try {
                JSONObject reader = new JSONObject(stream.toString());
                JSONObject result = reader.getJSONObject("result");
                JSONObject rates = result.getJSONObject("rates");
                Iterator<String> keys = rates.keys();
                RateList.clear();
                RateList.put(from, 1);
                while (keys.hasNext()) {
                    String cid = keys.next();
                    RateList.put(cid, rates.getDouble(cid));
                }
                RateList.setUpdateTime(System.currentTimeMillis());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return responseCode;
    }

    static public int getRateHistory(String from, String to, Long startTime, Long endTime,
                                     RateHistory rateHistoryResult) {
        StringBuilder stream = new StringBuilder();
        int responseCode = HttpDataHandler.sendGet(host + "api/rate/history", "from=" +
                from + "&to=" + to + "&start=" + startTime + "&end=" + endTime, stream);
//        Log.i("c", stream.toString());
        if (responseCode == 200) {
            try {
                rateHistoryResult.clear();
                JSONObject reader = new JSONObject(stream.toString());
                JSONObject result = reader.getJSONObject("result");
                JSONArray rates = result.getJSONArray("data");
                for (int i = 0; i < rates.length(); i++) {
                    rateHistoryResult.addData(rates.getDouble(i));
                }
                rateHistoryResult.setTime(result.getLong("time"));
                rateHistoryResult.setCurrencyPair(result.getString("inCurrency"), result
                        .getString("outCurrency"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return responseCode;
    }
}
