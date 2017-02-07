package org.mushare.rate.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dklap on 1/5/2017.
 */

public class CurrencyShowList {
    private static String baseCurrencyCid;
    private static List<String> exchangeCurrencyCids = new ArrayList<>();
    private static boolean needRefresh = true;

    public static boolean isNeedRefresh() {
        return needRefresh;
    }

    public static void setNeedRefresh(boolean needRefresh) {
        CurrencyShowList.needRefresh = needRefresh;
    }

    public synchronized static String getBaseCurrencyCid() {
        return baseCurrencyCid;
    }

    public synchronized static void setBaseCurrencyCid(String baseCurrencyCid) {
        CurrencyShowList.baseCurrencyCid = baseCurrencyCid;
    }

    public synchronized static MyCurrency getBaseCurrency() {
        if (baseCurrencyCid == null) return null;
        else return CurrencyList.get(baseCurrencyCid);
    }

    public synchronized static String getExchangeCurrencyCid(int index) {
        if (exchangeCurrencyCids.size() > index && index > -1) {
            return exchangeCurrencyCids.get(index);
        } else return null;
    }

    public synchronized static boolean swapBaseCurrencyCid(int index) {
        if (exchangeCurrencyCids.size() > index && index > -1) {
            baseCurrencyCid = exchangeCurrencyCids.set(index, baseCurrencyCid);
            return true;
        } else return false;
    }

    public synchronized static List<MyCurrencyRate> getExchangeCurrencyRateList
            (List<MyCurrencyRate> list) {
        list.clear();
        for (String cid : exchangeCurrencyCids) {
            MyCurrency currency = CurrencyList.get(cid);
            if (currency != null)
                list.add(new MyCurrencyRate(currency, RateList.get(cid)));
        }
        return list;
    }

    public synchronized static void cache(SQLiteDatabase db) {
        db.execSQL("update base_currency set cid = ?", new String[]{baseCurrencyCid});
        db.execSQL("delete from exchange_currencies");
        for (String cid : exchangeCurrencyCids) {
            db.execSQL("insert into exchange_currencies values(?)", new String[]{cid});
        }
    }


    public synchronized static void reloadFromCache(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select * from base_currency", null);
        if (cursor.moveToNext()) {
            baseCurrencyCid = cursor.getString(0);
        }
        cursor.close();
        exchangeCurrencyCids.clear();
        cursor = db.rawQuery("select * from exchange_currencies", null);
        while (cursor.moveToNext()) {
            exchangeCurrencyCids.add(cursor.getString(0));
        }
        cursor.close();
    }
}
