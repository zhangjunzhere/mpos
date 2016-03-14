package com.itertk.app.mpos.trade.convenience;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.BlankFragment;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;

public class TrainTicketActivity extends Activity {
    LinearLayout btnFromCity;
    LinearLayout btnToCity;
    LinearLayout btnDepartDate;
    Button btnSearch;

    TextView textDepartDate;

    TextView textFromCity;
    TextView textToCity;

    FragmentTransaction transaction;


    private void clearItemArea() {
        BlankFragment blankFragment = new BlankFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.itemArea, blankFragment);
        transaction.commit();
    }


    private void showDepartDateFragment() {
        DepartDateFragment departDateFragment = new DepartDateFragment();
        departDateFragment.setTextView(textDepartDate);
        departDateFragment.setTitle("出发日期");
        transaction = getFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_from_right);
        transaction.replace(R.id.itemArea, departDateFragment);
        transaction.commit();

    }

    private void showTrainFromCityFragment() {
        TrainCityFragment trainCityFragment = new TrainCityFragment();
        trainCityFragment.setTitle("出发城市");
        trainCityFragment.setTextDisplay(textFromCity);
        transaction = getFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_from_right);
        transaction.replace(R.id.itemArea, trainCityFragment);
        transaction.commit();
    }

    private void showTrainToCityFragment() {
        TrainCityFragment trainCityFragment = new TrainCityFragment();
        trainCityFragment.setTitle("到达城市");
        trainCityFragment.setTextDisplay(textToCity);
        transaction = getFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_from_right);
        transaction.replace(R.id.itemArea, trainCityFragment);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_ticket);
        MyActionbar.setNormalActionBarLayout(this);

        textFromCity = (TextView) findViewById(R.id.textFromCity);
        textToCity = (TextView) findViewById(R.id.textToCity);

        textDepartDate = (TextView) findViewById(R.id.textDepartDate);

        btnFromCity = (LinearLayout) findViewById(R.id.btnFromCity);
        btnFromCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTrainFromCityFragment();
            }
        });

        btnToCity = (LinearLayout) findViewById(R.id.btnToCity);
        btnToCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTrainToCityFragment();
            }
        });

        btnDepartDate = (LinearLayout) findViewById(R.id.btnDepartDate);
        btnDepartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDepartDateFragment();
            }
        });

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textFromCity.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "请选择出发城市", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (textToCity.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "请选择到达城市", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (textDepartDate.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "请选择出发日期", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent it = new Intent(getApplicationContext(), TrainLineActivity.class);
                it.putExtra("fromcity", textFromCity.getText().toString());
                it.putExtra("tocity", textToCity.getText().toString());
                it.putExtra("from", textFromCity.getTag().toString());
                it.putExtra("to", textToCity.getTag().toString());
                it.putExtra("date", textDepartDate.getText().toString());
                startActivity(it);

            }
        });


        clearItemArea();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.railway_ticket, menu);
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
