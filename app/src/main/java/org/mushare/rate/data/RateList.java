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

    public synchronized static void clear() {
        rateMap.clear();
    }

    public synchronized static void put(String cid, double rate) {
        rateMap.put(cid, rate);
    }

    public synchronized static Double get(String cid) {
        return rateMap.get(cid);
    }

    public synchronized static void cache(SQLiteDatabase db) {
        db.execSQL("delete from rates");
        for (Map.Entry<String, Double> entry : rateMap.entrySet()) {
            db.execSQL("insert into rates values(?, ?)", new Object[]{entry.getKey(),
                    entry.getValue()});
        }
    }

    public synchronized static void reloadFromCache(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select * from rates", null);
        rateMap.clear();
        while (cursor.moveToNext()) {
            rateMap.put(cursor.getString(0), cursor.getDouble(1));
        }
        cursor.close();
    }
}
