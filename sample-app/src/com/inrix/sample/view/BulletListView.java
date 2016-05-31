package com.inrix.sample.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inrix.sample.R;
import com.inrix.sdk.utils.CollectionUtils.IFunction;

import java.util.List;
import java.util.Locale;

import static com.inrix.sdk.utils.CollectionUtils.map;

public class BulletListView extends LinearLayout {
    private String format;

    public BulletListView(Context context) {
        super(context);
        this.init(context);
    }

    public BulletListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public BulletListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public BulletListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context);
    }

    private void init(final Context context) {
        this.setOrientation(VERTICAL);

        this.format = context.getString(R.string.bullet_list_item_format);
    }

    public void setData(final List<String> data) {
        final List<String> formatted = map(data, new IFunction<String, String>() {
            @Override
            public String apply(String s) {
                return String.format(Locale.getDefault(), format, s);
            }
        });

        this.removeAllViews();

        for (final String text : formatted) {
            final TextView textView = new TextView(this.getContext());
            textView.setTextAppearance(this.getContext(), R.style.ParkingBlock_Content_TextAppearance);
            textView.setText(text);
            this.addView(textView);
        }
    }
}
