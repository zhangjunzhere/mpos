package com.itertk.app.mpos.trade.pos;

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

/**
 * 台帐
 */
public class BillActivity extends Activity implements View.OnClickListener {
    LinearLayout btnUnpayOrder;
    LinearLayout btnOrder;
    LinearLayout btnSaleStatistic;
    LinearLayout btnProductStatistic;

    UnpayOrderFragment unpayOrderFragment;
    OrderFragment orderFragment;
    SaleStatisticFragment saleStatisticFragment;
    ProductStatisticFragment productStatisticFragment;
    FragmentTransaction transaction;

    private void onbtnUnpayOrder() {
        btnUnpayOrder.setSelected(true);
        btnOrder.setSelected(false);
        btnSaleStatistic.setSelected(false);
        btnProductStatistic.setSelected(false);


        unpayOrderFragment = new UnpayOrderFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.set_container, unpayOrderFragment);
        transaction.commit();
    }

    private void onbtnOrder() {
        btnUnpayOrder.setSelected(false);
        btnOrder.setSelected(true);
        btnSaleStatistic.setSelected(false);
        btnProductStatistic.setSelected(false);


        orderFragment = new OrderFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.set_container, orderFragment);
        transaction.commit();
    }

    private void onbtnSaleStatistic() {
        btnUnpayOrder.setSelected(false);
        btnOrder.setSelected(false);
        btnSaleStatistic.setSelected(true);
        btnProductStatistic.setSelected(false);


        saleStatisticFragment = new SaleStatisticFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.set_container, saleStatisticFragment);
        transaction.commit();
    }

    private void onbtnProductStatistic() {
        btnUnpayOrder.setSelected(false);
        btnOrder.setSelected(false);
        btnSaleStatistic.setSelected(false);
        btnProductStatistic.setSelected(true);

        productStatisticFragment = new ProductStatisticFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.set_container, productStatisticFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUnpayOrder:
                onbtnUnpayOrder();
                break;
            case R.id.btnOrder:
                onbtnOrder();
                break;
            case R.id.btnSaleStatistic:
                onbtnSaleStatistic();
                break;
            case R.id.btnProductStatistic:
                onbtnProductStatistic();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        MyActionbar.setNormalActionBarLayout(this);

        btnUnpayOrder = (LinearLayout) findViewById(R.id.btnUnpayOrder);
        btnUnpayOrder.setOnClickListener(this);

        btnOrder = (LinearLayout) findViewById(R.id.btnOrder);
        btnOrder.setOnClickListener(this);

        btnSaleStatistic = (LinearLayout) findViewById(R.id.btnSaleStatistic);
        btnSaleStatistic.setOnClickListener(this);

        btnProductStatistic = (LinearLayout) findViewById(R.id.btnProductStatistic);
        btnProductStatistic.setOnClickListener(this);


        onbtnUnpayOrder();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.bill, menu);
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
