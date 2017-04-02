package org.mushare.rate.tab.rate;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;

/**
 * Created by dklap on 4/2/2017.
 */

public class HTMLTextView extends AppCompatTextView {
    public HTMLTextView(Context context) {
        this(context, null);
    }

    public HTMLTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public HTMLTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMovementMethod(new LinkMovementMethod());
    }

    @Override
    public void setText(CharSequence html, BufferType type) {
        if (!(html instanceof Spanned)) html = Html.fromHtml(html.toString());
        super.setText(html, type);

    }
}
