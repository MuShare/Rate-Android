package org.mushare.rate.tab.rate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by dklap on 2/7/2017.
 */

public class RateHistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String inCurrencyCid = intent.getStringExtra("in_currency");
        String outCurrencyCid = intent.getStringExtra("out_currency");
        Log.i("cscs", inCurrencyCid + ", " + outCurrencyCid);
    }
}
