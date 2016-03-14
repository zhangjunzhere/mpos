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
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.Date;

public class CreditcardPayActivity extends Activity {
    private static final String TAG = "CreditcardPayActivity";
    TextView textCreditcardNo;
    TextView textMoney;

    String no;
    String money;
    String phone;


    Button btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditcard_pay);
        MyActionbar.setNormalActionBarLayout(this);

        Intent it = getIntent();
        no = it.getStringExtra("no");
        money = it.getStringExtra("money");
        phone = it.getStringExtra("phone");


        textCreditcardNo = (TextView) findViewById(R.id.textCreditcardNo);
        textCreditcardNo.setText(no);
        textMoney = (TextView) findViewById(R.id.textMoney);
        textMoney.setText("￥" + money);


        btnPay = (Button) findViewById(R.id.btnPay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LoadingDialog loadingDialog = new LoadingDialog(CreditcardPayActivity.this, R.style.MyDialog);
                loadingDialog.show();

                ((MPosApplication) getApplication()).getMsgBuilder().buildCreateOrderMsg(money, "0003", no, phone, "信用卡还款-" + no).send(new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        loadingDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, throwable.toString());
                        return;
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        loadingDialog.dismiss();
                        Log.d(TAG, responseString);
                        try {
                            LinkeaResponseMsg.CreateOrderResponseMsg createOrderResponseMsg =
                                    LinkeaResponseMsgGenerator.generateCreateOrderResponseMsg(responseString);
                            if (createOrderResponseMsg.success) {
                                SaleOrder saleOrder = new SaleOrder(null, createOrderResponseMsg.order.trade_no, 0, money, (new Date()).getTime(), 0,null);
                                ((MPosApplication) getApplication()).getDataHelper().getSaleOrderManager().insert(saleOrder);

                                Intent it = new Intent(CreditcardPayActivity.this, ConveniencePayActivity.class);
                                it.putExtra("orderId", String.valueOf(saleOrder.getSaleOrderId()));

                                startActivity(it);
                                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                            } else {
                                String erroInfo = createOrderResponseMsg.result_code_msg;
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
        //getMenuInflater().inflate(R.menu.creditcard_pay, menu);
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
