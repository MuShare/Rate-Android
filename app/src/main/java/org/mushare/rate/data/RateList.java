package org.mushare.rate.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dklap on 1/5/2017.
 */

public class RateList {
    private static Map<String, Double> rateMap = new HashMap<>();
    private static long updateTime = 0;
    private static boolean isChanged;

    public synchronized static long getUpdateTime() {
        return updateTime;
    }

    public synchronized static void setUpdateTime(long updateTime) {
        RateList.updateTime = updateTime;
        isChanged = true;
    }

    public synchronized static void clear() {
        rateMap.clear();
        updateTime = 0;
    }

    public synchronized static void put(String cid, double rate) {
        rateMap.put(cid, rate);
    }

    public synchronized static Double get(String cid, String base) {
        Double a = rateMap.get(cid);
        Double b = rateMap.get(base);
        return (a == null || b == null) ? null : a / b;
    }

    public synchronized static void cache(SQLiteDatabase db) {
        if (!isChanged) return;
        db.execSQL("delete from rates");
        for (Map.Entry<String, Double> entry : rateMap.entrySet()) {
            db.execSQL("insert into rates values(?, ?)", new Object[]{entry.getKey(),
                    entry.getValue()});
        }
        db.execSQL("update rate_list_cache_info set time = ?", new Long[]{updateTime});
        isChanged = false;
    }

    public synchronized static void reloadFromCache(SQLiteDatabase db) {
        if (isChanged) return;
        Cursor cursor = db.rawQuery("select * from rates", null);
        rateMap.clear();
        while (cursor.moveToNext()) {
            rateMap.put(cursor.getString(0), cursor.getDouble(1));
        }
        cursor.close();
        cursor = db.rawQuery("select * from rate_list_cache_info", null);
        if (cursor.moveToNext())
            updateTime = cursor.getLong(0);
        cursor.close();
    }
}
