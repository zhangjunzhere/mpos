package com.itertk.app.mpos.trade.pos;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.login.LoginActivity;
import com.itertk.app.mpos.utility.Arith;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 支付成功
 */
public class PaySuccessActivity extends Activity {

    String payInfo;
    TextView textPayInfo = null;
    Button btnOK = null;

    SaleOrder mSaleOrder = null;
    BigDecimal pay = null;
    BigDecimal priceTotal = null;
    boolean iscashpay= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_success);
       // MyActionbar.setNormalActionBarLayout(this);

        Intent intent = this.getIntent();
        payInfo = intent.getStringExtra("info");
        long orderId = intent.getLongExtra("orderid",-1);
         if(orderId!=-1)
         {
             mSaleOrder = ((MPosApplication) getApplication()).getDataHelper().getSaleOrderManager().load(Long.valueOf(orderId).longValue());
         }
        iscashpay = intent.getBooleanExtra("cash",false);
       final  String paystr  = intent.getStringExtra("pay");// it.putExtra("pay",pay.toString());
       final String pricetotalstr = intent.getStringExtra("priceTotal");
        if(iscashpay)
        {
            pay = Arith.newBigDecimal(paystr);
            priceTotal = Arith.newBigDecimal(pricetotalstr);
        }
        textPayInfo = (TextView) findViewById(R.id.textPayInfo);
        textPayInfo.setText(payInfo);

        btnOK = (Button) findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MPosApplication)getApplication()).getMyActivityManager().pop(PosActivity.class);
                if(mSaleOrder != null)
                {
                    if(iscashpay)
                    {
                        PrintHelper.printExternalOrder(PaySuccessActivity.this,mSaleOrder,paystr,pay.subtract(priceTotal).toString());
                    }
                    else
                    {
                        PrintHelper.printExternalOrder(PaySuccessActivity.this,mSaleOrder,null,null);
                    }
                }

                PaySuccessActivity.this.finish();
                Intent it = new Intent(PaySuccessActivity.this, MPosApplication.getInstance().getMyActivityManager().getBackToActivityClass());
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });


        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                ((MPosApplication)getApplication()).getMyTTS().speak("支付成功！"+ payInfo);
            }
        };
        timer.schedule(task, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.pay_success, menu);
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

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.d("dispatchTouchEvent", ev.toString());
//        if (ev.getDeviceId() == 3) return false;
//        return super.dispatchTouchEvent(ev);
//    }
//
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        Log.d("dispatchKeyEvent", event.toString());
//        if(event.getDeviceId() == 3) return false;
//        return super.dispatchKeyEvent(event);
//    }
//
//    @Override
//    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
//        Log.d("dispatchKeyShortcutEvent", event.toString());
//        if(event.getDeviceId() == 3) return false;
//        return super.dispatchKeyShortcutEvent(event);
//    }
}
