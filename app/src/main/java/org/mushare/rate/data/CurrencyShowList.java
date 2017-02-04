package org.mushare.rate.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by dklap on 1/5/2017.
 */

public class CurrencyShowList {
    private static String baseCurrencyCid;

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

    public synchronized static void cache(SQLiteDatabase db) {
        db.execSQL("update base_currency set cid = ?", new String[]{baseCurrencyCid});
    }


    public synchronized static void reloadFromCache(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select * from base_currency", null);
        if (cursor.moveToNext()) {
            baseCurrencyCid = cursor.getString(0);
        }
        cursor.close();
    }
}
