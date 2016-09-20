package com.elephant.multiple.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ListView;

/**
 * @class MaxLineListView
 * @description 
 * @author Elephant
 * @time 16/9/18 下午4:26
 */

public class MaxLineListView extends ListView {

    private int maxLines = Integer.MAX_VALUE;
    private int lineHeightInDp = 0;

    public MaxLineListView(Context context) {
        super(context);
    }

    public MaxLineListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxLineListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaxLineListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setMaxLines(int maxLines, int lineHeightInDp) {
        this.maxLines = maxLines;
        this.lineHeightInDp = lineHeightInDp;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getAdapter() != null && maxLines != Integer.MAX_VALUE) {
            if (getChildAt(0) != null) {
                View view = getChildAt(0);
                int maxHeight = maxLines * view.getMeasuredHeight();
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
            } else {
                DisplayMetrics dm = getResources().getDisplayMetrics();
                int lineHeight = (int) (lineHeightInDp * dm.density);
                int maxHeight = maxLines * lineHeight;
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
