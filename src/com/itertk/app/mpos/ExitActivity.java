package com.itertk.app.mpos;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by smile_gao on 2015/4/2.
 * 退出activity
 */
public class ExitActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
    }
}
