package org.mushare.rate.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dklap on 1/5/2017.
 */

public class CurrencyShowList {
    private static List<String> currencyShowList = new ArrayList<>();
    private static boolean isChanged;

    public synchronized static String getBaseCurrencyCid() {
        return currencyShowList.isEmpty() ? null : currencyShowList.get(0);
    }

    public synchronized static MyCurrency getBaseCurrency() {
        String cid = getBaseCurrencyCid();
        return cid == null ? null : CurrencyList.get(cid);
    }

    public synchronized static String getExchangeCurrencyCid(int index) {
        index++;
        if (currencyShowList.size() > index && index > 0) {
            return currencyShowList.get(index);
        } else return null;
    }

    public synchronized static void addExchangeCurrencies(List<String> currencyCids) {
        currencyShowList.addAll(currencyCids);
        isChanged = true;
    }

    public synchronized static String removeExchangeCurrencyCid(int index) {
        isChanged = true;
        return currencyShowList.remove(index + 1);
    }

    public synchronized static void insertExchangeCurrencyCid(int index, String cid) {
        currencyShowList.add(index + 1, cid);
        isChanged = true;
    }

    public synchronized static void swapBaseCurrencyCid(int index) {
        Collections.swap(currencyShowList, 0, index + 1);
        isChanged = true;
    }

    public synchronized static void swapExchangeCurrencyCid(int index1, int index2) {
        Collections.swap(currencyShowList, index1 + 1, index2 + 1);
        isChanged = true;
    }

    public synchronized static List<MyCurrencyRate> getExchangeCurrencyRateList
            (List<MyCurrencyRate> list) {
        list.clear();
        String baseCurrencyCid = getBaseCurrencyCid();
        for (int i = 1; i < currencyShowList.size(); i++) {
            String cid = currencyShowList.get(i);
            list.add(new MyCurrencyRate(CurrencyList.get(cid), RateList.get(cid, baseCurrencyCid)));
        }
        return list;
    }

    public synchronized static int getInvisibleCurrencyNumber() {
        return CurrencyList.getCidSet().size() - currencyShowList.size();
    }

    public synchronized static List<MyCurrencyWrapper> getInvisibleCurrencyCidList() {
        List<String> cidList = new LinkedList<>(CurrencyList.getCidSet());
        cidList.removeAll(currencyShowList);
        List<MyCurrencyWrapper> list = new LinkedList<>();
        for (String cid : cidList) {
            list.add(new MyCurrencyWrapper(cid, CurrencyList.get(cid)));
        }
        Collections.sort(list);
        char character = '-';
        for (MyCurrencyWrapper myCurrencyWrapper : list) {
            char c = myCurrencyWrapper.getCurrency().getCode().charAt(0);
            if (c != character) {
                myCurrencyWrapper.setCharacter(c);
                character = c;
            }
        }
        return list;
    }

    public synchronized static void cache(SQLiteDatabase db) {
        if (!isChanged) return;
        db.execSQL("delete from show_currencies");
        for (String cid : currencyShowList) {
            db.execSQL("insert into show_currencies values(?)", new String[]{cid});
        }
        isChanged = false;
    }


    public synchronized static void reloadFromCache(SQLiteDatabase db) {
        if (isChanged) return;
        currencyShowList.clear();
        Cursor cursor = db.rawQuery("select * from show_currencies", null);
        while (cursor.moveToNext()) {
            currencyShowList.add(cursor.getString(0));
        }
        cursor.close();
    }
}
