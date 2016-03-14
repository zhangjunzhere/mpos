package com.itertk.app.mpos.trade.convenience;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.dbhelper.SaleOrderItem;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.Date;

public class PhoneChargePayActivity extends Activity {
    private static final String TAG = "PhoneChargePayActivity";
    String no;
    String money;
    String area;
    String operator;

    TextView textNo;
    TextView textMoney;
    TextView textArea;

    Button btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_charge_pay);
        MyActionbar.setNormalActionBarLayout(this);

        Intent it = getIntent();
        no = it.getStringExtra("no");
        money = it.getStringExtra("money");
        area = it.getStringExtra("mobile_area");
        operator = it.getStringExtra("operator");


        textNo = (TextView) findViewById(R.id.textNo);
        textNo.setText(no);

        textArea = (TextView) findViewById(R.id.textArea);
        textArea.setText(area + "  " + operator);

        textMoney = (TextView) findViewById(R.id.textMoney);
        textMoney.setText(money);


        btnPay = (Button) findViewById(R.id.btnPay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LoadingDialog loadingDialog = new LoadingDialog(PhoneChargePayActivity.this, R.style.MyDialog);
                loadingDialog.show();


                ((MPosApplication) getApplication()).getMsgBuilder().buildPhoneChargeOrderCreate(money, area, operator, no).send(new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, throwable.toString());
                        loadingDialog.dismiss();
                        return;
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, responseString);
                        loadingDialog.dismiss();

                        try {
                            LinkeaResponseMsg.PhoneChargeOrderCreateResponseMsg phoneChargeOrderCreateResponseMsg =
                                    LinkeaResponseMsgGenerator.generatePhoneChargeOrderCreateResponseMsg(responseString);
                            if (phoneChargeOrderCreateResponseMsg.success) {
                                SaleOrder saleOrder = new SaleOrder(null, phoneChargeOrderCreateResponseMsg.trade_no, 0, money, (new Date()).getTime(), 0,null);
                                ((MPosApplication) getApplication()).getDataHelper().getSaleOrderManager().insert(saleOrder);
                                SaleOrderItem saleOrderItem = new SaleOrderItem(null, money, money, 1, no,"", saleOrder.getSaleOrderId(), System.currentTimeMillis());

                                ((MPosApplication) getApplication()).getDataHelper().getDaoSession().getSaleOrderItemDao().insert(saleOrderItem);

                                Intent it = new Intent(PhoneChargePayActivity.this, ConveniencePayActivity.class);
                                it.putExtra("orderId", String.valueOf(saleOrder.getSaleOrderId()));

                                startActivity(it);
                                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                            } else {
                                String erroInfo = phoneChargeOrderCreateResponseMsg.result_code_msg;
                                Toast.makeText(getApplicationContext(), erroInfo, Toast.LENGTH_SHORT).show();
                                Log.e(TAG, erroInfo);
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                        }
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.phone_chage_pay, menu);
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
