package com.itertk.app.mpos.trade.convenience;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;

public class AirTicketActivity extends Activity {
    LinearLayout btnOneway;
    LinearLayout btnRound;

    AirOnewayFragment airOnewayFragment;
    AirRoundFragment airRoundFragment;
    FragmentTransaction transaction;


    private void onBtnOneway() {
        btnOneway.setSelected(true);
        btnRound.setSelected(false);

        airOnewayFragment = new AirOnewayFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_from_right);
        transaction.replace(R.id.tabContainer, airOnewayFragment);
        transaction.commit();
    }

    private void onBtnRound() {
        btnOneway.setSelected(false);
        btnRound.setSelected(true);

        airRoundFragment = new AirRoundFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_from_right);
        transaction.replace(R.id.tabContainer, airRoundFragment);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_ticket);
        MyActionbar.setNormalActionBarLayout(this);


        btnOneway = (LinearLayout) findViewById(R.id.btnOneway);
        btnOneway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBtnOneway();
            }
        });

        btnRound = (LinearLayout) findViewById(R.id.btnRound);
        btnRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnRound();
            }
        });


        onBtnOneway();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.air_ticket, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
