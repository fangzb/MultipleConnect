package com.elephant.multiple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * @class MultipleConnectCalendarActivity
 * @description 
 * @author Elephant
 * @time 16/9/18 下午3:52
 */
public class MultipleConnectCalendarActivity extends AppCompatActivity {
    /**
     * 产品ID
     */
    private long mProductId;

    private MultipleConnectCalendarFragment calendarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_connect);
        mProductId = getIntent().getLongExtra(MultipleConnectCalendarFragment.KEY_PRODUCT_ID, 0);
        calendarFragment = MultipleConnectCalendarFragment.newInstance(mProductId);
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, calendarFragment).commit();
    }

}
