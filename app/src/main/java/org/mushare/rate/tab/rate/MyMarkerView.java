package org.mushare.rate.tab.rate;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import org.mushare.rate.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Created by dklap on 3/2/2017.
 */

public class MyMarkerView extends MarkerView {
    private TextView tvContent;
    private long startTime;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime);
        cal.add(Calendar.DATE, (int) e.getX());
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(e.getY() < 0.001 ? 6 : 3);
        tvContent.setText(DateFormat.getDateInstance().format(cal.getTime()) + "\n" + df.format(e
                .getY()));
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {

        MPPointF offset = getOffset();
        MPPointF mOffset2 = new MPPointF();
        mOffset2.x = offset.x;
        mOffset2.y = offset.y;

        Chart chart = getChartView();

        float width = getWidth();
        float height = getHeight();

        if (posX + mOffset2.x < 0) {
            mOffset2.x = -posX;
        } else if (chart != null && posX + width + mOffset2.x > chart.getWidth()) {
            mOffset2.x = chart.getWidth() - posX - width;
        }
        mOffset2.y = -posY;
        return mOffset2;
    }
}
