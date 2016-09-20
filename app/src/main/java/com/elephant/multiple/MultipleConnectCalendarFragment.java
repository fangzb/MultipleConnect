package com.elephant.multiple;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elephant.multiple.model.CalendarDates;
import com.elephant.multiple.view.CalendarTime;
import com.elephant.multiple.view.CalendarView;
import com.elephant.multiple.view.MaxLineListView;
import com.elephant.multiple.view.StatusUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Elephant
 * @class MultipleConnectCalendarFragment
 * @description
 * @time 16/9/18 下午4:21
 */

public class MultipleConnectCalendarFragment extends Fragment implements View.OnClickListener, CityHunterCalendarDateView, CalendarView.OnCalendarClickListener {
    /**
     * 产品ID
     */
    public static final String KEY_PRODUCT_ID = "key_product_id";
    private long mProductId;
    /**
     * 日期列表
     */
    private ArrayList<CalendarDates> datesArrayList = new ArrayList<>();

//    private MultipleConnectCalendarDatePresenter mPresenter;

    private CalendarView calendarView;
    private LinearLayout llSet;
    private TextView tvSelectedDate, tvSetPrice, tvSetedPrice;
    private MaxLineListView lvPriceType;
    private ImageView ivClose;
    private int startYear, startMonth;

    public MultipleConnectCalendarFragment() {
        // Required empty public constructor
    }

    public static MultipleConnectCalendarFragment newInstance(long productId) {
        MultipleConnectCalendarFragment fragment = new MultipleConnectCalendarFragment();
        Bundle args = new Bundle();
        args.putLong(KEY_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProductId = getArguments().getLong(KEY_PRODUCT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_multiple_connect_calendar, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calendarView = (CalendarView) view.findViewById(R.id.calendarView);
        llSet = (LinearLayout) view.findViewById(R.id.llSet);
        tvSelectedDate = (TextView) view.findViewById(R.id.tvSelectedDate);
        tvSetPrice = (TextView) view.findViewById(R.id.tvSetPrice);
        tvSetedPrice = (TextView) view.findViewById(R.id.tvSetedPrice);

        lvPriceType = (MaxLineListView) view.findViewById(R.id.lvPriceType);
        lvPriceType.setMaxLines(3, 48);
        ivClose = (ImageView) view.findViewById(R.id.ivClose);
        tvSetPrice.setOnClickListener(this);
        ivClose.setOnClickListener(this);

        long time = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        startYear = calendar.get(Calendar.YEAR);
        startMonth = calendar.get(Calendar.MONTH);
        calendarView.init(CalendarTime.create(startYear, startMonth, 1)
                , CalendarTime.create(calendar.get(Calendar.YEAR) + 1, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
        calendarView.setCalendarClickListener(this);

//        mPresenter = new MultipleConnectCalendarDatePresenter(this);
//        mPresenter.getPriceDates(mProductId);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivClose:
                calendarView.clearAllSelect();
                datesArrayList.clear();
                llSet.setVisibility(View.GONE);
                break;
        }
    }


    @Override
    public void deleteSuccess() {
        llSet.setVisibility(View.GONE);
//        mPresenter.getPriceDates(mProductId);
    }

    @Override
    public void toastNoNetWork() {
        if (getActivity() != null && !getActivity().isFinishing()) {
//            Utility.showToast(getActivity(), R.string.toast_error_network);
        }
    }

    @Override
    public void toastMsg(String msg) {
        if (getActivity() != null && !getActivity().isFinishing()) {
//            Utility.showToast(getActivity(), msg);
        }
    }

    @Override
    public void clickTitle(final int year, final int month) {

        if (calendarView.isMonthAllSelected(year, month, true)) {
            calendarView.clearSelectMonth(year, month);
            showSetedPrice(calendarView.getAllSelectDates());
        } else {
            calendarView.selectMonth(year, month, true);
            showSetedPrice(calendarView.getAllSelectDates());
            calendarView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int listIndex = monthIndex(year, month) - monthIndex(startYear, startMonth);
                    calendarView.smoothScrollToPositionFromTop(listIndex, 0);
                }
            }, 75);

        }
    }

    @Override
    public void click(int day, int month, int year, int status) {
        if (StatusUtils.isEnable(status)) {
            if (StatusUtils.isSelected(status)) {
                calendarView.unSelect(CalendarTime.create(year, month, day));
            } else {
                calendarView.select(CalendarTime.create(year, month, day));
            }
        }
        showSetedPrice(calendarView.getAllSelectDates());
    }

    private void showSetedPrice(ArrayList<CalendarTime> selectedDates) {
        llSet.setVisibility(View.VISIBLE);
        tvSetPrice.setVisibility(View.GONE);
        lvPriceType.setVisibility(View.GONE);
        /** ------冗余代码 ＝。＝  主要目的是给日期排序。。。。------- */
        String[] array = buffSort(convertDate(selectedDates).toArray(new String[convertDate(selectedDates).size()]));

        String[] convertarray = new String[array.length];
        for (int i = 0; i < convertarray.length; i++) {
            convertarray[i] = array[i].replace("-", "");
        }

        int[] arr = new int[convertarray.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Integer.parseInt(convertarray[i]);
        }

        Arrays.sort(arr);
        /** ------------------------------------------------    */

        /**若当前没有被选中的日期  显示日期的文本置空*/
        if (arr.length == 0) {
            tvSelectedDate.setText("");
            /**若只有一天被选中 显示该日期 并记录该日期对应的价格*/
        } else if (arr.length == 1) {

            tvSelectedDate.setText((array[0].split("-"))[1] + "-" + (array[0].split("-"))[2]);
            datesArrayList.clear();
            CalendarDates calendarDates = new CalendarDates();
            calendarDates.start = array[0];
            calendarDates.end = array[0];
            datesArrayList.add(calendarDates);
            /** 连续日期 */
        } else if (arr.length > 1 && arr[arr.length - 1] - arr[0] == arr.length - 1) {
            datesArrayList.clear();
            CalendarDates calendarDates = new CalendarDates();
            calendarDates.start = array[0];
            calendarDates.end = array[array.length - 1];
            datesArrayList.add(calendarDates);
            tvSelectedDate.setText((array[0].split("-"))[1] + "-" + (array[0].split("-"))[2] + " 至 " + (array[array.length - 1].split("-"))[1] + "-" + (array[array.length - 1].split("-"))[2]);
            /** 多选日期 */
        } else {
            datesArrayList.clear();
            datesArrayList.addAll(getMultipleDate(array));
            tvSelectedDate.setText("多个日期");
        }
//        /** 若当前有日期被选中 ［修改按钮］变换颜色 */
//        if (selectedDates != null && !selectedDates.isEmpty()) {
//            if (priceLables != null && priceLables.size() > 0) {
//                if (priceLables.size() > 1 || priceLables.get(0).id > 0) {
//                    lvPriceType.setVisibility(View.VISIBLE);
//                    rlEditeType.setVisibility(View.VISIBLE);
//                    rlDeletechedule.setVisibility(View.VISIBLE);
//                    typepriceLables.clear();
//                    typepriceLables.addAll(priceLables);
//                    typeAdapter.notifyDataSetChanged();
//                } else {
//                    rlPriceSeted.setVisibility(View.VISIBLE);
//                    rlDeletechedule.setVisibility(View.VISIBLE);
//                    tvSetedPrice.setText(getShowPrice(priceLables.get(0).price) + "元");
//                }
//            } else {
//                tvSetPrice.setVisibility(View.VISIBLE);
//            }
//        } else {
//            llSet.setVisibility(View.GONE);
//        }
    }

    public String getShowPrice(double priceValue) {
        String price = "0";
        if (priceValue > 0) {
            int fee = (int) priceValue;
            if (fee == priceValue) {
                price = fee + "";
            } else {
                price = priceValue + "";
            }
        }
        return price;
    }

    /**
     * 组装多选日期列表
     *
     * @param date
     * @return
     */
    public List<CalendarDates> getMultipleDate(String[] date) {

        List<CalendarDates> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            for (int i = 0; i < date.length; i++) {
                Date d1 = sdf.parse(date[i]);
                Date d2 = null;
                if (date.length - i == 1) {
                    d2 = sdf.parse(date[i]);
                } else {
                    d2 = sdf.parse(date[i + 1]);
                }

                Calendar c = Calendar.getInstance();
                c.setTime(d1);
                c.add(Calendar.DATE, 1);//日期加1
                if (c.getTime().equals(d2)) {
                    CalendarDates netDate = new CalendarDates();
                    netDate.start = sdf.format(d1);
                    netDate.end = sdf.format(d2);
                    dates.add(netDate);
                } else {
                    CalendarDates netDate = new CalendarDates();
                    netDate.start = sdf.format(d1);
                    netDate.end = sdf.format(d1);
                    dates.add(netDate);

                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dates;
    }

    /**
     * 把CalendarDay转换成字符串类型
     *
     * @param selectedDates
     * @return
     */
    private List<String> convertDate(List<CalendarTime> selectedDates) {

        List<String> dates = new ArrayList<>();
        for (CalendarTime day : selectedDates) {
            dates.add(day.parseToString());
        }
        return dates;
    }

    /**
     * 对日期排序
     *
     * @param date
     * @return
     */
    public static String[] buffSort(String date[]) {
        String temp = "";
        for (int i = 0; i < date.length - 1; i++) {
            for (int j = 0; j < date.length - 1; j++) {
                if (date[j].compareTo(date[j + 1]) > 0) {
                    temp = date[j];
                    date[j] = date[j + 1];
                    date[j + 1] = temp;
                }
            }
        }
        return date;
    }


    static int monthIndex(int year, int month) {
        return year * 12 + month;
    }
}
