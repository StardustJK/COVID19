package com.bupt.sse.group7.covid19.fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class StatisticGridView extends GridView {
    public StatisticGridView(Context context) {
        super(context);
    }

    public StatisticGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatisticGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StatisticGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
