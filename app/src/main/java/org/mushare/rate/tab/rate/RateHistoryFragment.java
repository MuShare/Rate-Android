package org.mushare.rate.tab.rate;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.mushare.rate.R;
import org.mushare.rate.data.CurrencyList;
import org.mushare.rate.data.MyCurrency;
import org.mushare.rate.data.RateHistory;
import org.mushare.rate.data.RateList;
import org.mushare.rate.url.HttpHelper;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by dklap on 2/14/2017.
 */

public class RateHistoryFragment extends Fragment {
    private String cid1;
    private String cid2;
    private boolean swap = false;

    private ImageView flagBase;
    private ImageView flag;
    private TextView currencyCodeBase;
    private TextView currencyCode;
    private TextView rate;
    private LineChart chart;
    private View progressBar;
    private MyMarkerView mv;
    private View pair;
    private MyTabLayout tabLayout;

    private RateHistory rateHistory = new RateHistory();
    private int timeOffset;
    private boolean refreshing = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate_history, container, false);
//        view.findViewById(R.id.appbar_layout).setPadding(0, getStatusBarHeight(), 0, 0);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.appbar);

        toolbar.setTitle(R.string.timeline);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        toolbar.inflateMenu(R.menu.add_reminder_item);

        flagBase = (ImageView) view.findViewById(R.id.imageViewCountryFlagBase);
        flag = (ImageView) view.findViewById(R.id.imageViewCountryFlag);
        currencyCodeBase = (TextView) view.findViewById(R.id.textViewCurrencyBase);
        currencyCode = (TextView) view.findViewById(R.id.textViewCurrency);
        rate = (TextView) view.findViewById(R.id.textViewRate);

        tabLayout = (MyTabLayout) view.findViewById(R.id.tabs_time_range);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int size = rateHistory.getData().size();
                switch (tab.getPosition()) {
                    case 0:
                        timeOffset = size - 30;
                        break;
                    case 1:
                        timeOffset = size - 90;
                        break;
                    case 2:
                        timeOffset = size - 180;
                        break;
                    case 3:
                        timeOffset = size - 365;
                        break;
                    case 4:
                        timeOffset = 0;
                        break;
                }
                refreshDataSet();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        progressBar = view.findViewById(R.id.progressBar);
        chart = (LineChart) view.findViewById(R.id.chart);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        mv = new MyMarkerView(getContext(), R.layout.chart_marker);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart

//        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
//        chart.setScaleYEnabled(false);
        chart.setNoDataText(getString(R.string.error_refresh_fail));
        chart.setNoDataTextColor(getResources().getColor(R.color.colorTextPrimary));

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(3);
        xAxis.setGranularity(1);
//        xAxis.setCenterAxisLabels(true);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(getResources().getColor(R.color.colorTextPrimary));
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(rateHistory.getTime());
                cal.add(Calendar.DATE, (int) value + timeOffset);
                return DateFormat.getDateInstance().format(cal.getTime());
            }
        });

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setTextColor(getResources().getColor(R.color.colorTextPrimary));
        yAxis.setSpaceTop(15);
        yAxis.setSpaceBottom(15);
//        yAxis.setAxisMinimum(0);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

//        chart.setAutoScaleMinMaxEnabled(true);
        Bundle bundle = getArguments();
        cid1 = bundle.getString("cid1");
        cid2 = bundle.getString("cid2");

        setCurrencyPair();
        chart.invalidate();

        pair = view.findViewById(R.id.currency_pair);
        pair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pair.setClickable(false);
                pair.setLongClickable(false);
                tabLayout.setTouchEnabled(false);
                swap = !swap;
                setCurrencyPair();
                pair.animate().rotationY(15).setDuration(60).setInterpolator(new
                        DecelerateInterpolator()).withLayer().withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        pair.animate().rotationY(0).setDuration(300).setInterpolator(new
                                OvershootInterpolator()).withLayer();
                    }
                });
                progressBar.setVisibility(View.VISIBLE);
                chart.setVisibility(View.GONE);
                chart.clear();
                requestHistoryData();
            }
        });
        pair.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        return view;
    }

    void requestHistoryData() {
        refreshing = true;
        final String from, to;
        if (!swap) {
            from = cid1;
            to = cid2;
        } else {
            from = cid2;
            to = cid1;
        }
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -3);
        Thread thread = new Thread() {
            @Override
            public void run() {
                if (HttpHelper.getRateHistory(from, to, cal.getTimeInMillis(),
                        Calendar.getInstance().getTimeInMillis(), rateHistory) == 200) {
                    EventBus.getDefault().post(new RefreshFinishEvent());
                } else EventBus.getDefault().post(new RefreshFailEvent());
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    void setCurrencyPair() {
        final String from, to;
        if (!swap) {
            from = cid1;
            to = cid2;
        } else {
            from = cid2;
            to = cid1;
        }
        MyCurrency currencyBase = CurrencyList.get(from);
        MyCurrency currency = CurrencyList.get(to);
        int resID = getContext().getResources().getIdentifier
                ("ic_flag_" + currencyBase.getIcon(), "drawable", getContext().getPackageName());
        if (resID != 0) {
            flagBase.setImageResource(resID);
        }
        resID = getContext().getResources().getIdentifier
                ("ic_flag_" + currency.getIcon(), "drawable", getContext().getPackageName());
        if (resID != 0) {
            flag.setImageResource(resID);
        }
        currencyCodeBase.setText(currencyBase.getCode());
        currencyCode.setText(currency.getCode());
        Double exchangeRate;
        if ((exchangeRate = RateList.get(to, from)) == null)
            rate.setText(getString(R.string.unknown));
        else {
            DecimalFormat df = new DecimalFormat();
            if (exchangeRate >= 1) df.setMaximumFractionDigits(2);
            else if (exchangeRate >= 0.1) df.setMaximumFractionDigits(3);
            else if (exchangeRate >= 0.01) df.setMaximumFractionDigits(4);
            else if (exchangeRate >= 0.001) df.setMaximumFractionDigits(5);
            else if (exchangeRate >= 0.0001) df.setMaximumFractionDigits(6);
            rate.setText(df.format(exchangeRate));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        if (refreshing) {
            requestHistoryData();
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
        outState.putBoolean("swap", swap);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            refreshing = savedInstanceState.getBoolean("refreshing", true);
            swap = savedInstanceState.getBoolean("swap", false);
        }
    }

    void refreshDataSet() {
        List<Entry> entries = new ArrayList<>();
        List<Double> rates = rateHistory.getData();
        for (int i = timeOffset; i < rates.size(); i++) {
            entries.add(new Entry(i - timeOffset, rates.get(i).floatValue()));
        }
        LineDataSet dataSet;
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            dataSet = (LineDataSet) chart.getData().getDataSetByIndex(0);
            dataSet.setValues(entries);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            dataSet = new LineDataSet(entries, "rates"); // add entries to dataset
            dataSet.setDrawCircles(false);
            dataSet.setLineWidth(1f);
            dataSet.setColor(getResources().getColor(R.color.colorChartLine));
            dataSet.setDrawHorizontalHighlightIndicator(false);
            dataSet.setHighlightLineWidth(1);
            dataSet.enableDashedHighlightLine(10, 8, 0);
            dataSet.setHighLightColor(getResources().getColor(R.color.colorChartHighLightLine));
            dataSet.setDrawValues(false);
            dataSet.setDrawFilled(true);
            dataSet.setFillColor(Color.GRAY);
            dataSet.setFillAlpha(10);
            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
        }
        mv.setStartTime(rateHistory.getTime(), timeOffset);
        chart.highlightValues(null);
        chart.invalidate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshFinishEvent event) {
        int size = rateHistory.getData().size();
        switch (tabLayout.getSelectedTabPosition()) {
            case 0:
                timeOffset = size - 30;
                break;
            case 1:
                timeOffset = size - 90;
                break;
            case 2:
                timeOffset = size - 180;
                break;
            case 3:
                timeOffset = size - 365;
                break;
            case 4:
                timeOffset = 0;
                break;
        }
        YAxis yAxis = chart.getAxisLeft();
        float min = Float.MAX_VALUE, max = Float.MIN_VALUE;
        for (Double value :
                rateHistory.getData()) {
            if (min > value.floatValue()) min = value.floatValue();
            if (max < value.floatValue()) max = value.floatValue();
        }
        // temporary range (before calculations)
        float range = Math.abs(max - min);

        // in case all values are equal
        if (range == 0f) {
            max = max + 1f;
            min = min - 1f;
        }
        float bottomSpace = range / 100f * yAxis.getSpaceBottom();
        yAxis.setAxisMinimum(min - bottomSpace);
        float topSpace = range / 100f * yAxis.getSpaceTop();
        yAxis.setAxisMaximum(max + topSpace);
        refreshDataSet();
        progressBar.setVisibility(View.GONE);
        chart.setVisibility(View.VISIBLE);
        chart.animateX(600);
        pair.setClickable(true);
        pair.setLongClickable(true);
        tabLayout.setTouchEnabled(true);
        refreshing = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshFailEvent event) {
        progressBar.setVisibility(View.GONE);
        chart.setVisibility(View.VISIBLE);
        pair.setClickable(true);
        pair.setLongClickable(true);
        refreshing = false;
    }

    //    public int getStatusBarHeight() {
//        int result = 0;
//        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            result = getResources().getDimensionPixelSize(resourceId);
//        }
//        return result;
//    }
    public static class RefreshFinishEvent {
    }

    public static class RefreshFailEvent {
    }
}

