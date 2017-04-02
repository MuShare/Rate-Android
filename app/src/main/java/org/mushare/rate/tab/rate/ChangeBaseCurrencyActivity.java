package org.mushare.rate.tab.rate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.mushare.rate.R;
import org.mushare.rate.data.CurrencyShowList;

/**
 * Created by dklap on 3/9/2017.
 */

public class ChangeBaseCurrencyActivity extends AppCompatActivity implements
        ChangeBaseCurrencyRecyclerViewAdapter.Callback {
    RecyclerView recyclerView;
    ChangeBaseCurrencyRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_currency);

//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        adapter = new ChangeBaseCurrencyRecyclerViewAdapter(CurrencyShowList
                .getAvailableBaseCurrencyCidList(), this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBaseCurrencySelected(String cid) {
        Intent data = new Intent();
        data.putExtra("base_currency", cid);
        setResult(RESULT_OK, data);
        finish();
    }
}
