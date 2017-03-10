package org.mushare.rate.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by dklap on 1/5/2017.
 */

public class CurrencyList {
    private static Map<String, MyCurrency> currencyMap = new HashMap<>();
    private static int revision;
    private static String language;
    private static boolean isChanged;

    public synchronized static void put(String cid, MyCurrency currency) {
        currencyMap.put(cid, currency);
    }

    public synchronized static MyCurrency get(String cid) {
        return currencyMap.get(cid);
    }

    public synchronized static int getRevision(String lan) {
        if (lan != null && lan.equals(language)) return revision;
        else return 0;
    }

    public synchronized static Set<String> getCidSet() {
        return currencyMap.keySet();
    }

    public synchronized static void setRevision(int revision) {
        CurrencyList.revision = revision;
        isChanged = true;
    }

    public synchronized static String getLanguage() {
        return language;
    }

    public synchronized static void setLanguage(String language) {
        CurrencyList.language = language;
        isChanged = true;
    }

    public synchronized static void cache(SQLiteDatabase db) {
        if (!isChanged) return;
        for (Map.Entry<String, MyCurrency> entry : currencyMap.entrySet()) {
            MyCurrency currency = entry.getValue();
            db.execSQL("insert or replace into currencies values(?, ?, ?, ?)", new String[]{entry
                    .getKey(), currency.getCode(), currency.getIcon(), currency.getName()});
        }
        db.execSQL("update currency_list_cache_info set rev = ?, language = ?", new
                Object[]{revision, language});
        isChanged = false;
    }

    public synchronized static boolean reloadFromCache(SQLiteDatabase db) {
        if (isChanged) return false;
        Cursor cursor = db.rawQuery("select * from currency_list_cache_info", null);
        cursor.moveToNext();
        revision = cursor.getInt(0);
        language = cursor.getString(1);
        cursor.close();
        cursor = db.rawQuery("select * from currencies", null);
        currencyMap.clear();
        while (cursor.moveToNext()) {
            currencyMap.put(cursor.getString(0), new MyCurrency(cursor.getString(1), cursor
                    .getString(2), cursor.getString(3)));
        }
        cursor.close();
        return true;
    }
}
