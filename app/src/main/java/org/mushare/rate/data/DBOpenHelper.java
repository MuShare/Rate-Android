package org.mushare.rate.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dklap on 2/4/2017.
 */

public class DBOpenHelper extends SQLiteOpenHelper {
    public DBOpenHelper(Context context, String name, int
            version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table currencies(cid primary key, code, icon, name)");
        db.execSQL("create table rates(cid primary key, rate)");
        db.execSQL("create table base_currency(cid)");
        db.execSQL("create table exchange_currencies(cid)");
        db.execSQL("create table currency_list_cache_info(rev, language)");
        db.execSQL("insert into currencies values('ff80818156b14e160156b1542a9f2ec3', 'BGN', " +
                "'bg', 'Bulgarian Lev'), \n" +
                "('ff80818156b14e160156b155edb95d86', 'NZD', 'nu', 'New Zealand Dollar'), \n" +
                "('ff808181568824b701568825c7680000', 'USD', 'us', 'US Dollar'), \n" +
                "('ff80818156b14e160156b154fca0438c', 'DKK', 'dk', 'Danish Krone'), \n" +
                "('ff80818156b14e160156b15496cd3928', 'COP', 'co', 'Colombian Peso'), \n" +
                "('ff80818156b14e160156b152c9070532', 'GBP', 'gb', 'British Pound Sterling'), \n" +
                "('ff80818156b14e160156b155ac045854', 'KRW', 'kr', 'South Korean Won'), \n" +
                "('ff80818156b14e160156b150a3210000', 'CNY', 'cn', 'Chinese Yuan'), \n" +
                "('ff80818156b14c280156b14cca430000', 'JPY', 'jp', 'Japanese Yen'), \n" +
                "('ff80818156b14e160156b15325930f96', 'CAD', 'ca', 'Canadian Dollar'), \n" +
                "('ff80818156b14e160156b15572005321', 'IDR', 'id', 'Indonesian Rupiah'), \n" +
                "('ff80818156b143ff0156b144f9350000', 'EUR', 'eu', 'Euro'), \n" +
                "('ff80818156b169360156b16ace9b0f93', 'BHD', 'bh', 'Bahraini Dinar'), \n" +
                "('ff80818156b14e160156b15386ec1f2d', 'TWD', 'tw', 'New Taiwan Dollar'), \n" +
                "('ff80818156b169360156b16a96520a61', 'ARS', 'ar', 'Argentine Peso'), \n" +
                "('ff80818156b14e160156b1536b6319fb', 'INR', 'in', 'Indian Rupee'), \n" +
                "('ff80818156b14e160156b1552a7d48bf', 'FJD', 'fj', 'Fijian Dollar'), \n" +
                "('ff80818156b14e160156b1568f547250', 'SEK', 'se', 'Swedish Krona'), \n" +
                "('ff80818156b14e160156b153077c0a64', 'HKD', 'hk', 'Hong Kong Dollar'), \n" +
                "('ff80818156b14e160156b154686e33f6', 'CHF', 'ch', 'Swiss Franc'), \n" +
                "('ff80818156b14e160156b156569667ec', 'PLN', 'pl', 'Polish Zloty'), \n" +
                "('ff80818156b14e160156b1566fe96d1e', 'RUB', 'ru', 'Russian Ruble'), \n" +
                "('ff80818156b169360156b16a60d8052f', 'SGD', 'sg', 'Singapore Dollar'), \n" +
                "('ff80818156b14e160156b15342f714c8', 'AUD', 'au', 'Australian Dollar'), \n" +
                "('ff80818156b14e160156b153ac9f245f', 'AED', 'ae', 'United Arab Emirates Dirham')" +
                ", \n" +
                "('ff80818156b14e160156b156119562b8', 'PHP', 'ph', 'Philippine Peso')");
        db.execSQL("insert into currency_list_cache_info values(?, ?)", new Object[]{29,
                "en"});
        db.execSQL("insert into base_currency values('ff808181568824b701568825c7680000')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
