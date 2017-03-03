package org.mushare.rate.tab.rate;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.github.mikephil.charting.highlight.Highlight;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.mushare.rate.R;
import org.mushare.rate.data.CurrencyList;
import org.mushare.rate.data.MyCurrency;
import org.mushare.rate.data.RateHistory;
import org.mushare.rate.url.HttpHelper;

import java.text.DateFormat;
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
    private LineChart chart;
    private MyMarkerView mv;
    private View pair;
    private MyTabLayout tabLayout;

    private RateHistory rateHistory = new RateHistory();
    private Highlight lastHighlighted;

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

        tabLayout = (MyTabLayout) view.findViewById(R.id.tabs_time_range);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int size = rateHistory.getData().size();
                switch (tab.getPosition()) {
                    case 0:
                        chart.setVisibleXRange(30, 30);
                        chart.moveViewToX(size - 30);
                        break;
                    case 1:
                        chart.setVisibleXRange(90, 90);
                        chart.moveViewToX(size - 90);
                        break;
                    case 2:
                        chart.setVisibleXRange(180, 180);
                        chart.moveViewToX(size - 180);
                        break;
                    case 3:
                        chart.setVisibleXRange(365, 365);
                        chart.moveViewToX(size - 365);
                        break;
                    case 4:
                        chart.setVisibleXRange(size, size);
                        chart.moveViewToX(0);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        chart = (LineChart) view.findViewById(R.id.chart);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        mv = new MyMarkerView(getContext(), R.layout.chart_marker);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart

        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
//        chart.setScaleYEnabled(false);
        chart.setNoDataText(getString(R.string.chart_no_data));
        chart.setNoDataTextColor(getResources().getColor(R.color.colorTextPrimary));

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(3, true);
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
                cal.add(Calendar.DATE, (int) value);
                return DateFormat.getDateInstance().format(cal.getTime());
            }
        });

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setTextColor(getResources().getColor(R.color.colorTextPrimary));
//        yAxis.setAxisMinimum(0);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

//        chart.setAutoScaleMinMaxEnabled(true);
        chart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_DOWN:
                        Highlight h = chart.getHighlightByTouchPoint(event.getX(), event.getY());
                        if (h != null && !h.equalTo(lastHighlighted)) {
                            lastHighlighted = h;
                            chart.highlightValue(h, false);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        chart.highlightValues(null);
                        break;
                }
                return true;
            }
        });
        Bundle bundle = getArguments();
        cid1 = bundle.getString("cid1");
        cid2 = bundle.getString("cid2");

        setCurrencyPair();
        chart.invalidate();
        requestHistoryData();

        pair = view.findViewById(R.id.currency_pair);
        pair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pair.animate().rotationY(15).setDuration(60).setInterpolator(new
                        DecelerateInterpolator()).withLayer().withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        pair.animate().rotationY(0).setDuration(300).setInterpolator(new
                                OvershootInterpolator()).withLayer();
                    }
                });
                pair.setClickable(false);
                tabLayout.setTouchEnabled(false);
                swap = !swap;
                setCurrencyPair();
                chart.setNoDataText(getString(R.string.chart_no_data));
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
        MyCurrency currencyBase;
        MyCurrency currency;
        if (!swap) {
            currencyBase = CurrencyList.get(cid1);
            currency = CurrencyList.get(cid2);
        } else {
            currencyBase = CurrencyList.get(cid2);
            currency = CurrencyList.get(cid1);
        }
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
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshFinishEvent event) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(rateHistory.getTime());
//        Toast.makeText(getContext(), rateHistory.getData().size() + ", " + cal.get(Calendar.DATE)
//                + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR), Toast
//                .LENGTH_LONG).show();
        List<Entry> entries = new ArrayList<>();
        int i = 0;
        List<Double> rates = rateHistory.getData();
        for (double data : rates) {
            // turn your data into Entry objects
            entries.add(new Entry(i++, (float) data));
        }
        LineDataSet dataSet = new LineDataSet(entries, "rates"); // add entries to dataset
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
        chart.setVisibleXRange(30, 30);
        chart.moveViewToX(rateHistory.getData().size() - 30);
        chart.animateX(600);
        mv.setStartTime(rateHistory.getTime());
        chart.invalidate();
        pair.setClickable(true);
        tabLayout.setTouchEnabled(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshFailEvent event) {
        chart.setNoDataText(getString(R.string.error_refresh_fail));
        chart.invalidate();
        pair.setClickable(true);
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

