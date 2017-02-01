package org.mushare.rate.fragment.rate;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.mushare.rate.R;
import org.mushare.rate.data.CurrenciesList;
import org.mushare.rate.data.CurrencyRate;
import org.mushare.rate.data.MyCurrency;
import org.mushare.rate.data.RateList;
import org.mushare.rate.data.Settings;
import org.mushare.rate.url.HttpHelper;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by dklap on 12/16/2016.
 */

public class RateFragment extends Fragment {
    final static int MSG_REFRESH_FINISH = 0;
    final static int MSG_REFRESH_FAIL = 1;

    List<CurrencyRate> dataSet = new LinkedList<>();

    MaterialSearchView searchView;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText editText;
    TextView textViewBaseCurrencyName, textViewBaseCurrencyInfo;
    ImageView imageViewBaseCountryFlag;
    RateRecyclerViewAdapter adapter;

    MessageHandler handler = new MessageHandler(new WeakReference<>(this));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.search_item);
        searchView = (MaterialSearchView) view.findViewById(R.id.search_view);
        searchView.setMenuItem(toolbar.getMenu().findItem(R.id.action_search));
        searchView.setTextColor(getResources().getColor(R.color.colorTextPrimary));
        searchView.setHintTextColor(getResources().getColor(R.color.colorTextSecondary));
        searchView.setHint(getResources().getString(R.string.action_search));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
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
                double times = 1d;
                String text = s.toString();
                try {
                    times = Double.parseDouble(text.replace(",", ""));
                    if (!text.contains(".")) {
                        String formatted = String.format(Locale.getDefault(), "%1$,d", (long)
                                times);
                        if (!formatted.equals(s.toString())) {
//                            editText.removeTextChangedListener(this);
                            editText.setText(formatted);
                            times = Double.parseDouble(formatted);
//                            editText.addTextChangedListener(this);
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } finally {
                    adapter.setTimes(times);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        setBaseCurrency();

        View viewBaseCurrency = view.findViewById(R.id.baseCurrency);
        viewBaseCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.isEnabled()) {
                    editText.requestFocus();
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
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

    public boolean closeSearch() {
        if (searchView != null && searchView.isSearchOpen()) {
            searchView.closeSearch();
            return true;
        }
        return false;
    }

    void setBaseCurrency() {
        MyCurrency currency = Settings.getBaseCurrency();
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

    static class MessageHandler extends Handler {
        RateFragment fragment;

        MessageHandler(WeakReference<RateFragment> weakReference) {
            fragment = weakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_FINISH:
//                    Toast.makeText(fragment.getContext(), R.string.error_refresh_success, Toast
// .LENGTH_SHORT).show();
                    fragment.setBaseCurrency();
                    fragment.adapter.notifyDataSetChanged();
                    fragment.swipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_REFRESH_FAIL:
//                    Snackbar.make(fragment.swipeRefreshLayout, R.string.error_refresh_fail,
//                            Snackbar.LENGTH_LONG).setAction(R.string.snackbar_action, new View
//                            .OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            fragment.swipeRefreshLayout.setRefreshing(true);
//                            fragment.refresh();
//                        }
//                    }).show();
                    Toast.makeText(fragment.getContext(), R.string.error_refresh_fail, Toast
                            .LENGTH_SHORT).show();
                    fragment.swipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    }
}

