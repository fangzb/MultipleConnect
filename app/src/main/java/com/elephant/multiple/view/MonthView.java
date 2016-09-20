package com.elephant.multiple.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.Calendar;

/**
 * @class MonthView
 * @description 
 * @author Elephant
 * @time 16/9/18 下午4:26
 */
public class MonthView extends View {

    public static final float CELL_WH_RATIO = 1.0f;
    public static final int CELL_PADDING_DP = 10;
    public static final int TITLE_TEXT_COLOR = 0xFF424242;
    public static final int TITLE_TEXT_SIZE_DP = 13;
    public static final int HEADER_TEXT_COLOR = 0XFF9e9e9e;
    public static final int HEADER_TEXT_SIZE_DP = 13;

    public float mTitleTextSizePx;
    public float mHeaderTextSizePx;

    public static final String[] HEADER_TEXT = {"日", "一", "二", "三", "四", "五", "六"};


    CalendarTime mCalendarTime;

    float mCellWidth, mCellHeight;

    float mCellPaddingPx;

    Calendar mMonthLastDay;

    int mDayCount, mFirstDayOffset;

    DayCellProcessor mDayCellProcessor;

    Paint mPaint;

    String mTitle;
    float mTitleW, mHeaderW;
    Paint.FontMetricsInt mTitleFontMetrics, mHeaderFontMetrics;

    SparseIntArray mDayStatus;
//    int[] mDayStatus;


    public MonthView(Context context) {
        super(context);
        init();
    }

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MonthView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    ViewConfiguration configuration;

    private void init() {
        setClickable(false);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mCellPaddingPx = dm.density * CELL_PADDING_DP;

        mTitleTextSizePx = dm.density * TITLE_TEXT_SIZE_DP;
        mHeaderTextSizePx = dm.density * HEADER_TEXT_SIZE_DP;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSkewX(0);

        mPaint.setTextSize(mTitleTextSizePx);
        configuration = ViewConfiguration.get(getContext());
    }

    public void init(CalendarTime calendarTime, SparseIntArray dayStatus, DayCellProcessor dayCellProcessor) {
        this.mCalendarTime = calendarTime;
        this.mDayCellProcessor = dayCellProcessor;
        this.mDayStatus = dayStatus;

        mMonthLastDay = Calendar.getInstance();
        mMonthLastDay.setFirstDayOfWeek(Calendar.SUNDAY);
        mMonthLastDay.set(Calendar.YEAR, calendarTime.year);
        mMonthLastDay.set(Calendar.MONTH, calendarTime.month + 1);
        mMonthLastDay.set(Calendar.DAY_OF_MONTH, 1);
        mMonthLastDay.add(Calendar.DAY_OF_MONTH, -1);

        mDayCount = mMonthLastDay.get(Calendar.DAY_OF_MONTH);
        Calendar calendar = (Calendar) mMonthLastDay.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        mFirstDayOffset = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        requestLayout();

        initTitle();
    }

    private void initTitle() {
        mTitle = String.format("%d年%d月", mCalendarTime.year, mCalendarTime.month + 1);
        mPaint.setTextSize(mTitleTextSizePx);
        mTitleW = mPaint.measureText(mTitle);
        mTitleFontMetrics = mPaint.getFontMetricsInt();

        mPaint.setTextSize(mHeaderTextSizePx);
        mHeaderW = mPaint.measureText("日");
        mHeaderFontMetrics = mPaint.getFontMetricsInt();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mCalendarTime == null) {
            return;
        }
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new RuntimeException("view width must EXACTLY set");
        }

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int width = widthSize - getPaddingLeft() - getPaddingRight();

        mCellWidth = (width - 6f * mCellPaddingPx) / 7f;
        mCellHeight = mCellWidth / CELL_WH_RATIO;

        int weekCount = mMonthLastDay.get(Calendar.WEEK_OF_MONTH);
        int heightSize = (int) (mCellHeight * (weekCount + 2));

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        int paddingLeft = getPaddingLeft();

        // title
        mPaint.setTextSize(mTitleTextSizePx);
        mPaint.setColor(TITLE_TEXT_COLOR);
        canvas.drawText(mTitle, (getWidth() - mTitleW) / 2, (mCellHeight - mTitleFontMetrics.bottom - mTitleFontMetrics.top) / 2, mPaint);

        // header
        Calendar.getInstance().setFirstDayOfWeek(Calendar.SUNDAY);
        mPaint.setColor(HEADER_TEXT_COLOR);
        float baseY = mCellHeight + (mCellHeight - mHeaderFontMetrics.bottom - mHeaderFontMetrics.top) / 2;
        for (int i = 0; i < HEADER_TEXT.length; i++) {
            float x = paddingLeft + i * (mCellPaddingPx + mCellWidth) + (mCellWidth - mHeaderW) / 2;
            canvas.drawText(HEADER_TEXT[i], x, baseY, mPaint);
        }

        // day
        int monthId = mCalendarTime.year * 10000 + mCalendarTime.month * 100;
        for (int i = 0; i < mDayCount; i++) {
            int tableIndex = mFirstDayOffset + i;
            int col = tableIndex % 7;
            int row = tableIndex / 7;
            int x = (int) (paddingLeft + col * (mCellPaddingPx + mCellWidth));
            int y = (int) (mCellHeight * (row + 2));
            int status = mDayStatus.get(monthId + i + 1);
            mDayCellProcessor.draw(canvas, i + 1, (int) mCellPaddingPx, (int) mCellWidth, (int) mCellHeight, x, y, col, row, status);
        }
    }

    PointF mDownPoint = new PointF();
    PointF mMovePoint = new PointF();
    boolean press = false;
    long pressTime;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownPoint.set(event.getX(), event.getY());
                press = true;
                pressTime = System.currentTimeMillis();
                break;
//            case MotionEvent.ACTION_MOVE:
//                if ((mDownPoint.x - mMovePoint.x) * (mDownPoint.x - mMovePoint.x) +
//                        (mDownPoint.y - mMovePoint.y) * (mDownPoint.y - mMovePoint.y)
//                        > 4 * configuration.getScaledTouchSlop() * configuration.getScaledTouchSlop()) {
//                    press = false;
//                }
//
//                break;
            case MotionEvent.ACTION_UP:
                if (press && (System.currentTimeMillis() - pressTime) < configuration.getLongPressTimeout()) {
                    click(event.getX(), event.getY());
                    press = false;
                }
                break;
        }

        return true;
    }

    private void click(float x, float y) {
        if (y < mCellHeight) {
            // title click
            mDayCellProcessor.clickTitle(mCalendarTime.year, mCalendarTime.month);
            return;
        }
        if (y < mCellHeight * 2) {
            // header click
            return;
        }
//        boolean isPressInPadding = (x - getPaddingLeft()) % (mCellWidth + mCellPaddingPx) > mCellWidth;
//        if (isPressInPadding) {
//            return;
//        }

        int col = (int) ((x - getPaddingLeft()) / (mCellWidth + mCellPaddingPx));
        int row = (int) ((y - mCellHeight * 2) / mCellHeight);
        int tableIndex = col + row * 7;
        int dayIndex = tableIndex - mFirstDayOffset;

        if (dayIndex < 0 || dayIndex > mDayCount) {
            return;
        }

        mDayCellProcessor.click(dayIndex + 1, mCalendarTime.month, mCalendarTime.year,
                mDayStatus.get(CalendarTime.generateId(mCalendarTime.year, mCalendarTime.month, dayIndex + 1)));
    }
}
