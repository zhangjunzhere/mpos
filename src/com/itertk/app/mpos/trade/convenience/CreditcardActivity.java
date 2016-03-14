package com.itertk.app.mpos.trade.convenience;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.TextFocusChangeListener;

public class CreditcardActivity extends Activity {
    EditText textCreditcardNo;
    EditText textCreditcardNoConfirm;
    EditText textMoney;
    EditText textPhone;

    CheckBox checkArgree;
    Button btnNext;
    Button btnRule;
    Button btnBack;
    LinearLayout itemArea;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditcard);
        MyActionbar.setNormalActionBarLayout(this);

        TextFocusChangeListener textFocusChangeListener = new TextFocusChangeListener();
        textCreditcardNo = (EditText) findViewById(R.id.textCreditcardNo);
        textCreditcardNo.setOnFocusChangeListener(textFocusChangeListener);

        textCreditcardNoConfirm = (EditText) findViewById(R.id.textCreditcardNoConfirm);
        textCreditcardNoConfirm.setOnFocusChangeListener(textFocusChangeListener);

        textMoney = (EditText) findViewById(R.id.textMoney);
        textMoney.setOnFocusChangeListener(textFocusChangeListener);

        textPhone = (EditText) findViewById(R.id.textPhone);
        textPhone.setOnFocusChangeListener(textFocusChangeListener);

        checkArgree = (CheckBox) findViewById(R.id.checkArgree);

        itemArea = (LinearLayout) findViewById(R.id.itemArea);

        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textCreditcardNo.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "信用卡号码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (textCreditcardNoConfirm.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "确认信用卡号码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (textMoney.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "还款金额不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (textPhone.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "信用卡持有人手机号码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!checkArgree.isChecked()) {
                    Toast.makeText(getApplicationContext(), "请阅读并同意信用卡还款规则",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!textCreditcardNo.getText().toString().equals(textCreditcardNoConfirm.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "两次卡号输入不一致",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                Intent it = new Intent(CreditcardActivity.this, CreditcardPayActivity.class);
                it.putExtra("no", textCreditcardNo.getText().toString());
                it.putExtra("money", textMoney.getText().toString());
                it.putExtra("phone", textPhone.getText().toString());
                startActivity(it);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
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


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.creditcard, menu);
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
