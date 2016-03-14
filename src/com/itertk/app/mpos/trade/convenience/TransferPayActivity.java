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

public class TransferPayActivity extends Activity {
    private final static String TAG = "TransferPayActivity";
    TextView textFullname;
    TextView textCreditcardNo;
    TextView textMoney;

    Button btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_pay);
        MyActionbar.setNormalActionBarLayout(this);

        textFullname = (TextView) findViewById(R.id.textFullname);
        textCreditcardNo = (TextView) findViewById(R.id.textCreditcardNo);
        textMoney = (TextView) findViewById(R.id.textMoney);


        Intent it = getIntent();

        textFullname.setText(it.getStringExtra("name"));
        textCreditcardNo.setText(it.getStringExtra("card"));
        textMoney.setText(it.getStringExtra("money"));
        final String phone = it.getStringExtra("phone");


        btnPay = (Button) findViewById(R.id.btnPay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LoadingDialog loadingDialog = new LoadingDialog(TransferPayActivity.this, R.style.MyDialog);
                loadingDialog.show();
                ((MPosApplication) getApplication()).getMsgBuilder().buildCreateOrderMsg(textMoney.getText().toString(),
                        "0004", textCreditcardNo.getText().toString(), phone, "转账汇款-" + textCreditcardNo.getText().toString()).send(new TextHttpResponseHandler() {
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
                                SaleOrder saleOrder = new SaleOrder(null, createOrderResponseMsg.order.trade_no, 0, textMoney.getText().toString().trim(), (new Date()).getTime(), 0,null);
                                ((MPosApplication) getApplication()).getDataHelper().getSaleOrderManager().insert(saleOrder);

                                Intent it = new Intent(TransferPayActivity.this, ConveniencePayActivity.class);
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
        //getMenuInflater().inflate(R.menu.transfer_pay, menu);
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
