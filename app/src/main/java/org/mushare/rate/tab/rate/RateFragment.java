package org.mushare.rate.tab.rate;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
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

import com.daimajia.swipe.SwipeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.mushare.rate.R;
import org.mushare.rate.data.CurrencyList;
import org.mushare.rate.data.CurrencyShowList;
import org.mushare.rate.data.MyCurrency;
import org.mushare.rate.data.MyCurrencyRate;
import org.mushare.rate.data.RateList;
import org.mushare.rate.url.HttpHelper;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

//import android.text.format.DateFormat;

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
    RateRecyclerView recyclerView;
    Toolbar toolbar;

    private boolean refreshing = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        recyclerView = (RateRecyclerView) view.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView
                .getContext(),
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.list_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
        // specify an adapter (see also next example)
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

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
//        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorBackground);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        toolbar.inflateMenu(R.menu.refresh_item);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_refresh) {
                    refresh();
                }
                return false;
            }
        });

        setBaseCurrency();
        CurrencyShowList.getExchangeCurrencyRateList(dataSet);
        adapter.notifyDataSetChanged();

//        toolbar.setSubtitle(getResources().getString(R.string.update_time_prefix) + DateFormat
// .getDateTimeInstance().format(new Date()));

        if (RateList.getUpdateTime() != 0)
            toolbar.setSubtitle(getResources().getString(R.string.update_time_prefix) +
                    DateFormat.getDateTimeInstance().format(new Date(RateList.getUpdateTime())));

        return view;
    }

    void refresh() {
        refreshing = true;
        swipeRefreshLayout.setRefreshing(true);
        recyclerView.setTouchEnabled(false);
        Thread thread = new Thread() {
            @Override
            public void run() {
                String lan = Locale.getDefault().getLanguage() + "-" + Locale.getDefault()
                        .getCountry();
                if (HttpHelper.getCurrencyList(lan, CurrencyList.getRevision(lan)) == 200 &&
                        HttpHelper.getCurrencyRates("ff808181568824b701568825c7680000") == 200) {
                    RateList.setUpdateTime(System.currentTimeMillis());
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
    public void onPause() {
        InputMethodManager keyboard = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        for (SwipeLayout s : adapter.mItemManger.getOpenLayouts()) {
            ((MySwipeLayout) s).setTouchEnabled(true);
        }
        EventBus.getDefault().register(this);
        if (refreshing) {
            refresh();
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("refreshing", refreshing);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null)
            refreshing = savedInstanceState.getBoolean("refreshing", true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BaseCurrencyChangedEvent event) {
//        RateList.clear();
        CurrencyShowList.getExchangeCurrencyRateList(dataSet);
        setBaseCurrency();
        startBaseCurrencyViewAnimation();
        adapter.notifyDataSetChanged();

//        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshFinishEvent event) {
        setBaseCurrency();
        adapter.notifyDataSetChanged();
        toolbar.setSubtitle(getResources().getString(R.string.update_time_prefix) + DateFormat
                .getDateTimeInstance().format(new Date(RateList.getUpdateTime())));
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setTouchEnabled(true);
        refreshing = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshFailEvent event) {
        Toast.makeText(getContext(), R.string.error_refresh_fail, Toast
                .LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setTouchEnabled(true);
        refreshing = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LaunchHistoryActivity event) {
//        Intent intent = new Intent(getContext(), RateHistoryActivity.class);
//        intent.putExtra("in_currency", CurrencyShowList.getBaseCurrencyCid());
//        intent.putExtra("out_currency", CurrencyShowList.getExchangeCurrencyCid
//                (event.getIndex()));
//        startActivityForResult(intent, 0);
        RateHistoryFragment fragment = new RateHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("cid1", CurrencyShowList.getBaseCurrencyCid());
        bundle.putString("cid2", CurrencyShowList.getExchangeCurrencyCid(event.getIndex()));
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.main_container, fragment)
                .addToBackStack(null).commit();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // Check which request we're responding to
//        if (requestCode == 0) {
//            // Make sure the request was successful
//            adapter.closeAllItems();
//        }
//    }

    public static class BaseCurrencyChangedEvent {
    }

    public static class RefreshFinishEvent {
    }

    public static class RefreshFailEvent {
    }

    public static class LaunchHistoryActivity {
        int index;

        public LaunchHistoryActivity(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }
}

