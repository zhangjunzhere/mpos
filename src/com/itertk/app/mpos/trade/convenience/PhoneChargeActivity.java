package com.itertk.app.mpos.trade.convenience;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.itertk.app.mpos.trade.pos.PrintHelper;
import com.itertk.app.mpos.utility.ToastHelper;
import com.itertk.app.mpos.utility.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneChargeActivity extends Activity {
    private static final String TAG = "PhoneChargeActivity";
    EditText textPhone;
    TextView textMoney;

    CheckBox checkArgree;
    Button btnNext;
    Button btnRule;
    Button btnBack;
    LinearLayout itemArea;

    Button btnMoney50;
    Button btnMoney100;
    Button btnMoney200;

    String no;
    String money;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_charge);
        MyActionbar.setNormalActionBarLayout(this);



        textPhone = (EditText) findViewById(R.id.textPhone);
        textPhone.setFilters(Utils.getLengthFilters(this,11));
        textMoney = (TextView) findViewById(R.id.textMoney);


        checkArgree = (CheckBox) findViewById(R.id.checkArgree);

        itemArea = (LinearLayout) findViewById(R.id.itemArea);

        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                no = textPhone.getText().toString();

                if (textPhone.getText().toString().isEmpty()) {
                    ToastHelper.showToast(PhoneChargeActivity.this,"手机号码不能为空");

                    return;
                }

                if(!Utils.phoneRegex(textPhone.getText().toString()))
                {
                    ToastHelper.showToast(PhoneChargeActivity.this,"手机号码格式不正确");
                    return;
                }

                if (textMoney.getText().toString().isEmpty()) {

                    ToastHelper.showToast(PhoneChargeActivity.this,"充值金额不能为空");
                    return;
                }

                if (!checkArgree.isChecked()) {

                    ToastHelper.showToast(PhoneChargeActivity.this,"请阅读并同意手机充值服务协议");
                    return;
                }
//                SaleOrder saleOrder = new SaleOrder(null, "234214234214", 0, money, (new Date()).getTime(), 0,null);
//
//                long id=((MPosApplication) getApplication()).getDataHelper().getSaleOrderManager().insert(saleOrder);
//                SaleOrderItem saleOrderItem = new SaleOrderItem(null, money, money, 1, no,"", saleOrder.getSaleOrderId(), System.currentTimeMillis());
//
//                ((MPosApplication) getApplication()).getDataHelper().getDaoSession().getSaleOrderItemDao().insert(saleOrderItem);
//                PrintHelper.printExternalOrder(PhoneChargeActivity.this,saleOrder);
                final LoadingDialog loadingDialog = new LoadingDialog(PhoneChargeActivity.this, R.style.MyDialog);
                loadingDialog.show();

                ((MPosApplication) getApplication()).getMsgBuilder().buildPhoneInfoGet(no).send(new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, throwable.toString());

                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, responseString);
                        loadingDialog.dismiss();
                        try {
                            LinkeaResponseMsg.PhoneInfoGetResponseMsg phoneInfoGetResponseMsg =
                                    LinkeaResponseMsgGenerator.generatePhoneInfoGetResponseMsg(responseString);

                            Log.d(TAG, phoneInfoGetResponseMsg.toString());

                            if (phoneInfoGetResponseMsg.success) {
                                Intent it = new Intent(PhoneChargeActivity.this, PhoneChargePayActivity.class);
                                it.putExtra("no", textPhone.getText().toString());
                                it.putExtra("money", money);
                                String[] split = phoneInfoGetResponseMsg.area.split("\\|");
                                it.putExtra("mobile_area", split[1]);
                                it.putExtra("operator", split[2]);
                                startActivity(it);
                                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//                        SpannableString frontSpan = new SpannableString("\n（" + date + " 共有 ");
//                        frontSpan.setSpan(new RelativeSizeSpan(0.6f), 0, frontSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        textTitle.append(frontSpan);
//
//                        SpannableString middleSpan = new SpannableString(""+airTicketQueryResponseMsg.flightInfos.size());
//                        middleSpan.setSpan(new RelativeSizeSpan(0.6f), 0, middleSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        middleSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue_cc)), 0, middleSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        textTitle.append(middleSpan);
//
//                        SpannableString endSpan = new SpannableString(" 个航班）");
//                        endSpan.setSpan(new RelativeSizeSpan(0.6f), 0, endSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        textTitle.append(endSpan);


                            } else {
                                String erroInfo = phoneInfoGetResponseMsg.result_code_msg;
                                Toast.makeText(getApplicationContext(), erroInfo, Toast.LENGTH_SHORT).show();
                               // Log.e(TAG, erroInfo);
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                        }
                    }
                });


            }
        });

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemArea.setVisibility(View.INVISIBLE);
            }
        });

        btnRule = (Button) findViewById(R.id.btnRule);
        btnRule.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        btnRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemArea.setVisibility(View.VISIBLE);
            }
        });


        btnMoney50 = (Button) findViewById(R.id.btnMoney50);
        btnMoney50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMoney50.setSelected(true);
                btnMoney100.setSelected(false);
                btnMoney200.setSelected(false);

                money = "50";
                textMoney.setText(money);
            }
        });

        btnMoney100 = (Button) findViewById(R.id.btnMoney100);
        btnMoney100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMoney50.setSelected(false);
                btnMoney100.setSelected(true);
                btnMoney200.setSelected(false);

                money = "100";
                textMoney.setText(money);
            }
        });

        btnMoney200 = (Button) findViewById(R.id.btnMoney200);
        btnMoney200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMoney50.setSelected(false);
                btnMoney100.setSelected(false);
                btnMoney200.setSelected(true);

                money = "200";
                textMoney.setText(money);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.phone_charge, menu);
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
