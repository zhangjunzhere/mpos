package com.itertk.app.mpos.trade.convenience;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.TextFocusChangeListener;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.trade.pos.BankcardDialog;
import com.itertk.app.mpos.trade.pos.LoadingDialogHelper;
import com.itertk.app.mpos.trade.taobao.MessageBoxDialog;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.Hashtable;

public class BalanceQueryActivity extends Activity {
    private static final String TAG = "BalanceQueryActivity";
    TextView textBankcard;
    TextView textPassword;
    Button btnQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_query);
        MyActionbar.setNormalActionBarLayout(this);

        TextFocusChangeListener textFocusChangeListener = new TextFocusChangeListener();
        textBankcard = (TextView) findViewById(R.id.textBankcard);
        textBankcard.setOnFocusChangeListener(textFocusChangeListener);

        textPassword = (TextView) findViewById(R.id.textPassword);
        textPassword.setOnFocusChangeListener(textFocusChangeListener);

        btnQuery = (Button) findViewById(R.id.btnQuery);
        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (textBankcard.getText().toString().isEmpty()) {
//                    Toast.makeText(getApplicationContext(), "卡号不能为空", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (textPassword.getText().toString().isEmpty()) {
//                    Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                queryCardInfo();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        queryCardInfo();
    }

    private void queryCardInfo() {
        final BankcardDialog bankcardDialog = new BankcardDialog(this, R.style.MyDialog);
        bankcardDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Hashtable<String, String> cardInfo = bankcardDialog.cardInfo;

                if (cardInfo == null) {
                    Log.d(TAG, "获取磁卡信息失败");
                    return;
                } else {
                    Log.d(TAG, cardInfo.toString());
                }

                if (bankcardDialog.pin1 == null || bankcardDialog.pin2 == null) {
                    Log.d(TAG, "获取密码失败");
                    return;
                } else {
                    Log.d(TAG, "epb=" + bankcardDialog.pin1 + " ksn=" + bankcardDialog.pin2);
                }

                textBankcard.setText(cardInfo.get("no"));
                textPassword.setText(bankcardDialog.pin1);
                String track2 = cardInfo.get("track2");
                String icdata = "";
                boolean isiccard =Boolean.valueOf(cardInfo.get("isiccard"));
                Log.i("smile","isiccard "+isiccard);
                if(isiccard)
                {
                    icdata =  cardInfo.get("icdata");
                    // track2 = "";
                }

                 MPosApplication mPosApplication  = MPosApplication.getInstance();
                LoadingDialogHelper.show(BalanceQueryActivity.this);
                mPosApplication.getMsgBuilder().buildBalanceInfoGet(
                        mPosApplication.getPosId(), cardInfo.get("no"), bankcardDialog.pin1,cardInfo.get("cardseqno"),
                        cardInfo.get("track2"), cardInfo.get("track3"),mPosApplication.getMacKey(),icdata).send(new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, throwable.toString());
                        LoadingDialogHelper.dismiss();
                        return;
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, responseString);
                        try {
                            LinkeaResponseMsg.BalanceInfoGetResponseMsg balanceInfoGetResponseMsg =
                                    LinkeaResponseMsgGenerator.generateBalanceInfoGetResponseMsg(responseString);
                            if (balanceInfoGetResponseMsg.success) {

                                Intent it = new Intent(BalanceQueryActivity.this, BalanceResultActivity.class);
                                it.putExtra("no", textBankcard.getText().toString());
                                it.putExtra("money", balanceInfoGetResponseMsg.balance.amount);

                                startActivity(it);
                                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                                finish();
                            } else {
                                String erroInfo = balanceInfoGetResponseMsg.result_code_msg;
//                                Toast.makeText(getApplicationContext(), erroInfo, Toast.LENGTH_SHORT).show();
                                new MessageBoxDialog(getApplicationContext(),"余额查询失败",erroInfo,false).show();
                                Log.e(TAG, erroInfo);
                            }
                        } catch (Exception e) {
                           // Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                        }
                        LoadingDialogHelper.dismiss();
                    }
                });
            }
        });
        bankcardDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.balance_query, menu);
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
