package org.mushare.rate.tab.rate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

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

    private RateHistory rateHistory = new RateHistory();

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

        chart = (LineChart) view.findViewById(R.id.chart);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        mv = new MyMarkerView(getContext(), R.layout.chart_marker);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart

        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(4);
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
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture
                    lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture
                    lastPerformedGesture) {
                // un-highlight values after the gesture is finished and no single-tap
                if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
                    chart.highlightValues(null); // or highlightTouch(null) for callback to
                // onNothingSelected(...)
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {

            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float
                    velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });

        Bundle bundle = getArguments();
        cid1 = bundle.getString("cid1");
        cid2 = bundle.getString("cid2");

        setCurrencyPair();
        requestHistoryData();

        View pair = view.findViewById(R.id.currency_pair);
        pair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swap = !swap;
                setCurrencyPair();
                requestHistoryData();
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
        dataSet.setHighlightLineWidth(1.5f);
        dataSet.enableDashedHighlightLine(10, 8, 1);
        dataSet.setHighLightColor(getResources().getColor(R.color.colorChartHighLightLine));
        dataSet.setDrawValues(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(getResources().getColor(R.color.colorAccent));
        dataSet.setFillAlpha(10);

//        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        mv.setStartTime(rateHistory.getTime());
        chart.invalidate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshFailEvent event) {
        Toast.makeText(getContext(), R.string.error_refresh_fail, Toast
                .LENGTH_SHORT).show();
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

