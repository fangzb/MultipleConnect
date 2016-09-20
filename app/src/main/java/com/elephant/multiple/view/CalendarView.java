package com.elephant.multiple.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Elephant
 * @class CalendarView
 * @description 日历控件
 * @time 16/9/18 下午4:24
 */
public class CalendarView extends ListView {

    public interface OnCalendarClickListener {

        // 月份点击
        void clickTitle(int year, int month);

        // 日期点击
        void click(int day, int month, int year, int status);
    }

    public interface OnDateSelectStateChangeListener {
        void onSelectChange(int year, int month, int day, int status);
    }

    public static final int TEXT_COLOR_DISABLE = 0xFFc9c9c9;
    public static final int TEXT_COLOR_ENABLE = 0xFF737779;
    public static final int TEXT_COLOR_HIGHLIGHT = 0xFFec6c46;
    public static final int TEXT_COLOR_SELECTED = 0xFFffffff;
    public static final int BACK_COLOR_SELECTED = 0xFFec6c46;
    public static final int TEXT_SIZE_DP = 13;
    public static final int SELECT_BACK_PADDING_DP = 3;
    public static final int MONTH_VIEW_PADDING_DP = 24;

    int mTextSizePx, mSelectBgPaddingPx;

    SparseIntArray mDayStatus = new SparseIntArray();
    ArrayList<Integer> mDayIdList = new ArrayList<>();

    ArrayList<CalendarTime> mMonths = new ArrayList<>();
    DisplayMetrics mDm;
    Paint.FontMetricsInt mFontMetricsInt;

    Paint mPaint = new Paint();

    OnCalendarClickListener calendarClickListener;
    OnDateSelectStateChangeListener dateSelectStateChangeListener;

    public CalendarView(Context context) {
        super(context);
        init();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setCalendarClickListener(OnCalendarClickListener calendarClickListener) {
        this.calendarClickListener = calendarClickListener;
    }

    public void setDateSelectStateChangeListener(OnDateSelectStateChangeListener dateSelectStateChangeListener) {
        this.dateSelectStateChangeListener = dateSelectStateChangeListener;
    }

    private void init() {
        setSelector(new ColorDrawable());
        mDm = getResources().getDisplayMetrics();

        mTextSizePx = (int) (mDm.density * TEXT_SIZE_DP);
        mSelectBgPaddingPx = (int) (mDm.density * SELECT_BACK_PADDING_DP);
        mPaint.setTextSize(mTextSizePx);
        mFontMetricsInt = mPaint.getFontMetricsInt();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    /**
     * @return 日期和状态表，key为dayid(20160003) value见StatusUtils
     */
    public SparseIntArray getDayStatus() {
        return mDayStatus;
    }

    /**
     * @return 日历中所有日期的顺序列表
     */
    public ArrayList<Integer> getDayIdList() {
        return mDayIdList;
    }

    /**
     * @param start 开始月份
     * @param end   结束月份
     */
    public void init(CalendarTime start, CalendarTime end) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.set(Calendar.YEAR, start.year);
        calendar.set(Calendar.MONTH, start.month);

        while (calendar.get(Calendar.YEAR) < end.year ||
                (calendar.get(Calendar.YEAR) == end.year && calendar.get(Calendar.MONTH) < end.month)) {
            initDayStatus(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
            mMonths.add(CalendarTime.create(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1));
            calendar.add(Calendar.MONTH, 1);
        }
        generateDayIds();
        setAdapter(adapter);
    }

    void initDayStatus(int year, int month) {
        CalendarTime calendarTime = CalendarTime.create(year, month, 1);
        Calendar mMonthLastDay = Calendar.getInstance();
        mMonthLastDay.setFirstDayOfWeek(Calendar.SUNDAY);
        mMonthLastDay.set(Calendar.YEAR, calendarTime.year);
        mMonthLastDay.set(Calendar.MONTH, calendarTime.month + 1);
        mMonthLastDay.set(Calendar.DAY_OF_MONTH, 1);
        mMonthLastDay.add(Calendar.DAY_OF_MONTH, -1);

        int mDayCount = mMonthLastDay.get(Calendar.DAY_OF_MONTH);
        // init day status
        Calendar today = Calendar.getInstance();
        today.setFirstDayOfWeek(Calendar.SUNDAY);
        CalendarTime todayTime = CalendarTime.create(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        for (int i = 0; i < mDayCount; i++) {
            CalendarTime current = CalendarTime.create(calendarTime.year, calendarTime.month, 1 + i);
            int compareResult = todayTime.compareToDay(current);
            if (compareResult > 0) {
                // past disable
                mDayStatus.put(current.dateId, StatusUtils.disable());
            } else if (compareResult < 0) {
                // future enable
                mDayStatus.put(current.dateId, StatusUtils.enable());
            } else {
                // today
                mDayStatus.put(current.dateId, StatusUtils.enable());
            }
        }
    }

    void generateDayIds() {
        mDayIdList.clear();
        int size = mDayStatus.size();
        for (int i = 0; i < size; i++) {
            mDayIdList.add(mDayStatus.keyAt(i));
        }

        Collections.sort(mDayIdList, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return lhs - rhs;
            }
        });
    }

    /**
     * 设置某一天高亮显示
     *
     * @param highlight
     */
    public void setHighLight(CalendarTime highlight) {
        int dayStatus = mDayStatus.get(highlight.dateId, -1);
        if (dayStatus == -1) {
            return;
        }
        synchronized (mDayStatus) {
            mDayStatus.put(highlight.dateId, StatusUtils.highLight(dayStatus));
        }

        invalidateChild();
    }

    /**
     * 设置集合日期高亮显示
     *
     * @param highLights
     */
    public void setHighLight(ArrayList<CalendarTime> highLights) {
        synchronized (mDayStatus) {
            for (CalendarTime highlight : highLights) {
                int dayStatus = mDayStatus.get(highlight.dateId, -1);
                if (dayStatus != -1) {
                    mDayStatus.put(highlight.dateId, StatusUtils.highLight(dayStatus));
                }
            }
        }
        invalidateChild();
    }

    /**
     * 清除所有高亮日期
     */
    public void clearAllHighLight() {
        synchronized (mDayStatus) {
            int size = mDayStatus.size();
            for (int i = 0; i < size; i++) {
                int key = mDayStatus.keyAt(i);
                int value = mDayStatus.valueAt(i);
                mDayStatus.put(key, StatusUtils.clearHighLight(value));
            }
        }
        invalidateChild();
    }

    /**
     * 清除指定日期的高亮
     *
     * @param highLights
     */
    public void clearHighLight(ArrayList<CalendarTime> highLights) {
        synchronized (mDayStatus) {
            for (CalendarTime highlight : highLights) {
                int dayStatus = mDayStatus.get(highlight.dateId, -1);
                if (dayStatus != -1) {
                    mDayStatus.put(highlight.dateId, StatusUtils.clearHighLight(dayStatus));
                }
            }
        }
        invalidateChild();
    }

    /**
     * 清除指定日期的高亮
     *
     * @param highlight
     */
    public void clearHighLight(CalendarTime highlight) {
        int dayStatus = mDayStatus.get(highlight.dateId, -1);
        if (dayStatus == -1) {
            return;
        }
        synchronized (mDayStatus) {
            mDayStatus.put(highlight.dateId, StatusUtils.clearHighLight(dayStatus));
        }
        invalidateChild();
    }

    /**
     * @return 日历中所有高亮的日期
     */
    public ArrayList<CalendarTime> getAllHighLightDates() {
        ArrayList<CalendarTime> highlights = new ArrayList<>();
        synchronized (mDayStatus) {
            int size = mDayStatus.size();
            for (int i = 0; i < size; i++) {
                int key = mDayStatus.keyAt(i);
                int value = mDayStatus.valueAt(i);
                if (StatusUtils.isHighLight(value)) {
                    highlights.add(CalendarTime.create(key));
                }
            }
        }
        return highlights;
    }

    /**
     * 选中指定日期
     *
     * @param selectedDate
     */
    public void select(CalendarTime selectedDate) {
        int dayStatus = mDayStatus.get(selectedDate.dateId, -1);
        if (dayStatus == -1) {
            return;
        }
        synchronized (mDayStatus) {
            selectImm(selectedDate);
        }
        invalidateChild();
    }

    /**
     * 选中指定日期
     *
     * @param selectedDates
     */
    public void select(ArrayList<CalendarTime> selectedDates) {
        synchronized (mDayStatus) {
            for (CalendarTime selectedDate : selectedDates) {
                int dayStatus = mDayStatus.get(selectedDate.dateId, -1);
                if (dayStatus == -1) {
                    continue;
                }
                selectImm(selectedDate);
            }
        }
        invalidateChild();
    }

    /**
     * 选定指定月份
     *
     * @param year
     * @param month
     */
    public void selectMonth(int year, int month, boolean ignoreDisable) {
        CalendarTime calendarTime = CalendarTime.create(year, month, 1);
        synchronized (mDayStatus) {
            int size = mDayStatus.size();
            if (ignoreDisable) {
                for (int i = 0; i < size; i++) {
                    int key = mDayStatus.keyAt(i);
                    int value = mDayStatus.valueAt(i);
                    CalendarTime time = CalendarTime.create(key);
                    if (calendarTime.isSameMonth(time) && StatusUtils.isEnable(value)) {
                        selectImm(time);
                    }
                }
            } else {
                for (int i = 0; i < size; i++) {
                    int key = mDayStatus.keyAt(i);
                    CalendarTime time = CalendarTime.create(key);
                    if (calendarTime.isSameMonth(time)) {
                        selectImm(time);
                    }
                }
            }
        }
        invalidateChild();
    }

    /**
     * 清除指定月份的选择状态
     *
     * @param year
     * @param month
     */
    public void clearSelectMonth(int year, int month) {
        CalendarTime calendarTime = CalendarTime.create(year, month, 1);
        synchronized (mDayStatus) {
            int size = mDayStatus.size();
            for (int i = 0; i < size; i++) {
                int key = mDayStatus.keyAt(i);
                int value = mDayStatus.valueAt(i);
                CalendarTime time = CalendarTime.create(key);
                if (calendarTime.isSameMonth(time)) {
                    unSelectImm(time);
                }
            }
        }
        invalidateChild();
    }

    /**
     * @param year
     * @param month
     * @return 返回指定月份是否每天都被选择了
     */
    public boolean isMonthAllSelected(int year, int month, boolean ignoreDisable) {
        CalendarTime calendarTime = CalendarTime.create(year, month, 1);
        synchronized (mDayStatus) {
            int size = mDayStatus.size();
            if (ignoreDisable) {
                for (int i = 0; i < size; i++) {
                    int key = mDayStatus.keyAt(i);
                    int value = mDayStatus.valueAt(i);
                    CalendarTime time = CalendarTime.create(key);
                    if (calendarTime.isSameMonth(time)) {
                        if (!StatusUtils.isSelected(value) && StatusUtils.isEnable(value)) {
                            return false;
                        }
                    }
                }
            } else {
                for (int i = 0; i < size; i++) {
                    int key = mDayStatus.keyAt(i);
                    int value = mDayStatus.valueAt(i);
                    CalendarTime time = CalendarTime.create(key);
                    if (calendarTime.isSameMonth(time)) {
                        if (!StatusUtils.isSelected(value)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 取消指定日期的选择
     *
     * @param selectedDate
     */
    public void unSelect(CalendarTime selectedDate) {
        int dayStatus = mDayStatus.get(selectedDate.dateId, -1);
        if (dayStatus == -1) {
            return;
        }
        synchronized (mDayStatus) {
            unSelectImm(selectedDate);
        }
        invalidateChild();
    }

    /**
     * 取消选择所有日期
     */
    public void clearAllSelect() {
        synchronized (mDayStatus) {
            int size = mDayStatus.size();
            for (int i = 0; i < size; i++) {
                int key = mDayStatus.keyAt(i);
                int value = mDayStatus.valueAt(i);
                mDayStatus.put(key, value & 0xFF000);
            }
        }
        invalidateChild();
    }

    /**
     * @return 当前所有选择了的日期
     */
    public ArrayList<CalendarTime> getAllSelectDates() {
        ArrayList<CalendarTime> selects = new ArrayList<>();
        synchronized (mDayStatus) {
            int size = mDayStatus.size();
            for (int i = 0; i < size; i++) {
                int key = mDayStatus.keyAt(i);
                int value = mDayStatus.valueAt(i);
                if (StatusUtils.isSelected(value)) {
                    selects.add(CalendarTime.create(key));
                }
            }
        }
        return selects;
    }

    // 更新字界面
    private void invalidateChild() {
        for (int i = 0; i < getChildCount(); i++) {
            try {
                getChildAt(i).postInvalidate();
            } finally {
            }
        }
    }

    // 处理选中
    void selectImm(CalendarTime selectedDate) {
        int dayStatus = mDayStatus.get(selectedDate.dateId, -1);
        int preDayId = processor.getPreDayId(selectedDate.dateId);
        int preStatus = mDayStatus.get(preDayId);
        mDayStatus.put(preDayId, preStatus | 0x00001); // 将"前一天"的最后一位置为选中

        int nextDayId = processor.getNextDayId(selectedDate.dateId);
        int nextStatus = mDayStatus.get(nextDayId);
        mDayStatus.put(nextDayId, nextStatus | 0x00100); // 将"后一天"的第三位置为选择中

        int current = 0x00010; // 自身置为选中
        if (StatusUtils.isSelected(preStatus)) {
            current += 0x00100; // 合并前一天的选择状态
        }
        if (StatusUtils.isSelected(nextStatus)) {
            current += 0x00001; // 合并后一天的选择状态
        }
        mDayStatus.put(selectedDate.dateId, dayStatus | current); // 设置当天的选择状态
        if (dateSelectStateChangeListener != null) {
            dateSelectStateChangeListener.onSelectChange(selectedDate.year, selectedDate.month, selectedDate.day, dayStatus | current);
        }
    }

    // 处理取消选择
    void unSelectImm(CalendarTime selectedDate) {
        int dayStatus = mDayStatus.get(selectedDate.dateId, -1);

        int preDayId = processor.getPreDayId(selectedDate.dateId);
        int preStatus = mDayStatus.get(preDayId);
        mDayStatus.put(preDayId, preStatus & 0xFFFF0);// 将"前一天"的最后一位置为未选择

        int nextDayId = processor.getNextDayId(selectedDate.dateId);
        int nextStatus = mDayStatus.get(nextDayId);
        mDayStatus.put(nextDayId, nextStatus & 0xFF0FF); // 将"后一天"的第三位置为未选择

        mDayStatus.put(selectedDate.dateId, dayStatus & 0xFFF0F); // 将"当前天"的第四位置为未选择

        if (dateSelectStateChangeListener != null) {
            dateSelectStateChangeListener.onSelectChange(selectedDate.year, selectedDate.month, selectedDate.day, dayStatus & 0xFFF0F);
        }
    }

    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mMonths.size();
        }

        @Override
        public CalendarTime getItem(int position) {
            return mMonths.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mMonths.get(position).dateId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MonthView monthView;
            if (convertView == null) {
                monthView = new MonthView(getContext());
                LayoutParams alp = new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);
                monthView.setLayoutParams(alp);
                monthView.setPadding((int) (MONTH_VIEW_PADDING_DP * mDm.density), 0,
                        (int) (MONTH_VIEW_PADDING_DP * mDm.density), 0); // 设置左右边距为24dp
                convertView = monthView;
            } else {
                monthView = (MonthView) convertView;
            }
            monthView.init(getItem(position), mDayStatus, processor);
            return convertView;
        }
    };

    DayCellProcessor processor = new DayCellProcessor() {
        // 绘制逻辑，
        @Override
        public void draw(Canvas canvas,
                         int day, int cellPadding,
                         int cellW, int cellH, int cellLeft, int cellTop,
                         int col, int row,
                         int status) {
            // background
            int cx = cellLeft + cellW / 2;
            int cy = cellTop + cellH / 2;
            int radius = Math.min(cellW, cellH) / 2 - mSelectBgPaddingPx;

            if (StatusUtils.isSelected(status)) {
                mPaint.setColor(BACK_COLOR_SELECTED);
                int drawStatus = status & 0x00FFF;
                int halfPadding = cellPadding / 2 + 1;
                switch (drawStatus) {
                    case 0x110: // 前一天和当天被选中，后一天没有被选中。    二)
                        canvas.drawArc(new RectF(cx - radius, cy - radius, cx + radius, cy + radius), -90, 180, true, mPaint);
                        if (col == 0) { // first day of week
                            canvas.drawRect(cx - radius, cy - radius, cx, cy + radius, mPaint);
                        } else {
                            canvas.drawRect(cellLeft - halfPadding, cy - radius, cx, cy + radius, mPaint);
                        }
                        break;
                    case 0x011: // 当天和后一天被选中，前一天没有被选中     (二
                        canvas.drawArc(new RectF(cx - radius, cy - radius, cx + radius, cy + radius), 90, 180, true, mPaint);
                        if (col == 6) { // last day of week
                            canvas.drawRect(cx, cy - radius, cx + radius, cy + radius, mPaint);
                        } else {
                            canvas.drawRect(cx, cy - radius, cellLeft + cellW + halfPadding, cy + radius, mPaint);
                        }
                        break;
                    case 0x111: // 前一天后一天和当天都被选中              二
                        if (col == 0) { // first day of week
                            canvas.drawRect(cx - radius, cy - radius, cellLeft + cellW + halfPadding, cy + radius, mPaint);
                        } else if (col == 6) {
                            canvas.drawRect(cellLeft - halfPadding, cy - radius, cx + radius, cy + radius, mPaint);
                        } else {
                            canvas.drawRect(cellLeft - halfPadding, cy - radius, cellLeft + cellW + halfPadding, cy + radius, mPaint);
                        }
                        break;
                    case 0x010: // 当天被选中，前后天都没被选中            ()
                        canvas.drawCircle(cx, cy, radius, mPaint);
                        break;
                    default:
                        break;
                }
            }


            // text
            String text = day + "";
            float textW = mPaint.measureText(text);
            float x = cellLeft + (cellW - textW) / 2;
            float y = cellTop + (cellH - mFontMetricsInt.bottom - mFontMetricsInt.top) / 2; // 竖直居中文字

            if (!StatusUtils.isEnable(status)) {
                mPaint.setColor(TEXT_COLOR_DISABLE);
            } else {
                if (StatusUtils.isSelected(status)) {
                    mPaint.setColor(TEXT_COLOR_SELECTED);
                } else {
                    if (StatusUtils.isHighLight(status)) {
                        mPaint.setColor(TEXT_COLOR_HIGHLIGHT);
                    } else {
                        mPaint.setColor(TEXT_COLOR_ENABLE);
                    }
                }
            }
            canvas.drawText(text, x, y, mPaint);
        }

        @Override
        public void click(int day, int month, int year, int status) {
            if (calendarClickListener != null) {
                calendarClickListener.click(day, month, year, status);
            }
        }


        @Override
        public int getPreDayId(int currentDayId) {
            int index = mDayIdList.indexOf(currentDayId);
            if (index > 0) {
                return mDayIdList.get(index - 1);
            }
            return 0;
        }

        @Override
        public int getNextDayId(int currentDayId) {
            int index = mDayIdList.indexOf(currentDayId);
            if (index < mDayIdList.size() - 1) {
                return mDayIdList.get(index + 1);
            }
            return 0;
        }

        @Override
        public void clickTitle(int year, int month) {
            if (calendarClickListener != null) {
                calendarClickListener.clickTitle(year, month);
            }
        }

    };
}
