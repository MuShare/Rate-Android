package org.mushare.rate.fragment.rate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.mushare.rate.MainActivity;
import org.mushare.rate.R;
import org.mushare.rate.data.CurrencyList;
import org.mushare.rate.data.CurrencyShowList;
import org.mushare.rate.data.DBOpenHelper;
import org.mushare.rate.data.MyCurrency;
import org.mushare.rate.data.MyCurrencyRate;
import org.mushare.rate.data.RateList;
import org.mushare.rate.url.HttpHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by dklap on 12/16/2016.
 */

public class RateFragment extends Fragment {
    List<MyCurrencyRate> dataSet = new LinkedList<>();

    SwipeRefreshLayout swipeRefreshLayout;
    EditText editText;
    View viewBaseCurrency;
    TextView textViewBaseCurrencyName, textViewBaseCurrencyInfo;
    ImageView imageViewBaseCountryFlag;
    RateRecyclerViewAdapter adapter;

    DBOpenHelper dbOpenHelper;
    SQLiteDatabase sqLiteDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        dbOpenHelper = new DBOpenHelper(getContext(), "db", 1);
        sqLiteDatabase = dbOpenHelper.getReadableDatabase();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.search_item);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_search) {
                    ((MainActivity) getActivity()).showSearch();
                }
                return false;
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
        CurrencyShowList.getExchangeCurrencyRateList(dataSet);
        adapter = new RateRecyclerViewAdapter(dataSet);
        recyclerView.setAdapter(adapter);

        textViewBaseCurrencyName = (TextView) view.findViewById(R.id.textViewBaseCurrencyCode);
        textViewBaseCurrencyInfo = (TextView) view.findViewById(R.id.textViewBaseCurrencyName);
        imageViewBaseCountryFlag = (ImageView) view.findViewById(R.id.imageViewBaseCountryFlag);

        editText = (EditText) view.findViewById(R.id.editTextBase);
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    editText.clearFocus();
                    return true;
                }
                return false;
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager keyboard = (InputMethodManager) getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                } else {
                    InputMethodManager keyboard = (InputMethodManager) getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                double base = 100;
                String text = s.toString();
                try {
                    base = Double.parseDouble(text.replace(",", ""));
                    if (!text.contains(".")) {
                        String formatted = String.format(Locale.getDefault(), "%1$,d", (long)
                                base);
                        if (!formatted.equals(s.toString())) {
//                            editText.removeTextChangedListener(this);
                            editText.setText(formatted);
                            base = Double.parseDouble(formatted);
//                            editText.addTextChangedListener(this);
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } finally {
                    adapter.setBase(base);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        viewBaseCurrency = view.findViewById(R.id.baseCurrency);
        viewBaseCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.isEnabled()) {
                    editText.requestFocus();
                }
            }
        });

        setBaseCurrency();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorBackground);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        swipeRefreshLayout.setRefreshing(true);
        refresh();
        return view;
    }

    void refresh() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                String lan = Locale.getDefault().getLanguage() + "-" + Locale.getDefault()
                        .getCountry();
                if (HttpHelper.getCurrencyList(lan, CurrencyList.getRevision(lan)) == 200 &&
                        HttpHelper.getCurrencyRates(CurrencyShowList.getBaseCurrencyCid()) == 200) {
                    CurrencyList.cache(sqLiteDatabase);
                    RateList.cache(sqLiteDatabase);
//                    CurrencyShowList.cache(sqLiteDatabase);
                    CurrencyShowList.getExchangeCurrencyRateList(dataSet);
                    EventBus.getDefault().post(new RefreshFinishEvent());
                } else EventBus.getDefault().post(new RefreshFailEvent());
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    void setBaseCurrency() {
        MyCurrency currency = CurrencyShowList.getBaseCurrency();
        if (currency == null) return;
        textViewBaseCurrencyName.setText(currency.getCode());
        textViewBaseCurrencyInfo.setText(currency.getName());
        editText.setEnabled(true);
        int resID = getResources().getIdentifier("ic_flag_" + currency.getIcon(),
                "drawable", getContext().getPackageName());
        if (resID != 0) {
            imageViewBaseCountryFlag.setImageResource(resID);
        }
    }

    void startBaseCurrencyViewAnimation() {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -8, getResources()
                .getDisplayMetrics());
        viewBaseCurrency.animate().translationY(px).setDuration(60).setInterpolator(new
                DecelerateInterpolator()).withLayer().withEndAction(new Runnable() {
            @Override
            public void run() {
                viewBaseCurrency.animate().translationY(0).setDuration(300).setInterpolator(new
                        OvershootInterpolator()).withLayer();
            }
        });
    }

    @Override
    public void onDestroy() {
        dbOpenHelper.close();
        sqLiteDatabase.close();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager keyboard = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BaseCurrencyChangedEvent event) {
        CurrencyShowList.cache(sqLiteDatabase);
        RateList.clear();
        CurrencyShowList.getExchangeCurrencyRateList(dataSet);
        setBaseCurrency();
        startBaseCurrencyViewAnimation();
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(true);
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshFinishEvent event) {
        setBaseCurrency();
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshFailEvent event) {
        Toast.makeText(getContext(), R.string.error_refresh_fail, Toast
                .LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
    }

    public static class BaseCurrencyChangedEvent {
    }

    public static class RefreshFinishEvent {
    }

    public static class RefreshFailEvent {
    }
}

