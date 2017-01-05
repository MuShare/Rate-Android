package mushare.org.rate.fragment.rate;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import mushare.org.rate.R;
import mushare.org.rate.data.CurrenciesList;
import mushare.org.rate.data.Currency;
import mushare.org.rate.data.CurrencyRate;
import mushare.org.rate.data.RateList;
import mushare.org.rate.data.Settings;
import mushare.org.rate.url.HttpHelper;

/**
 * Created by dklap on 12/16/2016.
 */

public class RateFragment extends Fragment {
    final static int MSG_REFRESH_FINISH = 0;
    final static int MSG_REFRESH_FAIL = 1;

    List<CurrencyRate> dataSet = new LinkedList<>();

    SwipeRefreshLayout swipeRefreshLayout;
    EditText editText;
    TextView textViewBaseCurrencyName, textViewBaseCurrencyInfo;
    RecyclerView.Adapter adapter;

    MessageHandler handler = new MessageHandler(new WeakReference<>(this));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("rate", "start");
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        if (HttpHelper.getCurrencyList("", CurrenciesList.getRevision()) == 200 &&
                                HttpHelper.getCurrencyRates(Settings.getBaseCurrencyCid()) == 200) {
                            RateList.getList(dataSet);
                            handler.sendEmptyMessage(MSG_REFRESH_FINISH);
                        } else handler.sendEmptyMessage(MSG_REFRESH_FAIL);
                    }
                };
                thread.setDaemon(true);
                thread.start();
            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        RateList.getList(dataSet);
        adapter = new RateRecyclerViewAdapter(dataSet);
        recyclerView.setAdapter(adapter);

        textViewBaseCurrencyName = (TextView) view.findViewById(R.id.textViewBaseCurrencyName);
        textViewBaseCurrencyInfo = (TextView) view.findViewById(R.id.textViewBaseCurrencyInfo);
        setBaseCurrency();

        editText = (EditText) view.findViewById(R.id.editTextBase);
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager keyboard = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    void setBaseCurrency() {
        Currency currency = Settings.getBaseCurrency();
        if (currency == null) return;
        textViewBaseCurrencyName.setText(currency.getCode());
        textViewBaseCurrencyInfo.setText(currency.getName());
    }

    static class MessageHandler extends Handler {
        RateFragment fragment;

        MessageHandler(WeakReference<RateFragment> weakReference) {
            fragment = weakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_FINISH:
                    Toast.makeText(fragment.getContext(), R.string.error_refresh_success, Toast.LENGTH_SHORT).show();
                    fragment.setBaseCurrency();
                    fragment.adapter.notifyDataSetChanged();
                    fragment.swipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_REFRESH_FAIL:
                    Toast.makeText(fragment.getContext(), R.string.error_refresh_fail, Toast.LENGTH_SHORT).show();
                    fragment.swipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    }
}

