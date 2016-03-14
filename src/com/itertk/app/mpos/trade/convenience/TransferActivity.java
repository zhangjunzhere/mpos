package com.itertk.app.mpos.trade.convenience;

import android.app.Activity;
import android.app.FragmentTransaction;
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
import android.widget.Toast;

import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;

public class TransferActivity extends Activity {
    private static final String TAG = "TransferActivity";
    EditText textCreditcardNo;
    EditText textCreditcardNoConfirm;
    EditText textFullname;
    EditText textMoney;
    EditText textPhone;

    Button btnBank;
    Button btnNext;
    Button btnRule;
    CheckBox checkArgree;

    FragmentTransaction transaction;


    private void showTransferRuleFragment() {

        TransferRuleFragment transferRuleFragment = new TransferRuleFragment();
        transaction = getFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_from_right);
        transaction.replace(R.id.itemArea, transferRuleFragment);
        transaction.commit();
    }

    private void showChooseBankFragment() {
        ChooseBankFragment chooseBankFragment = new ChooseBankFragment();
        transaction = getFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_from_right);
        transaction.replace(R.id.itemArea, chooseBankFragment);
        transaction.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        MyActionbar.setNormalActionBarLayout(this);

        textCreditcardNo = (EditText) findViewById(R.id.textCreditcardNo);
        textCreditcardNoConfirm = (EditText) findViewById(R.id.textCreditcardNoConfirm);
        textFullname = (EditText) findViewById(R.id.textFullname);
        textMoney = (EditText) findViewById(R.id.textMoney);
        textPhone = (EditText) findViewById(R.id.textPhone);

        //btnBank = (Button)findViewById(R.id.btnBank);
//        btnBank.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showChooseBankFragment();
//            }
//        });

        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textCreditcardNo.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "收款方卡号不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (textCreditcardNoConfirm.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "确认收款方卡号不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (textFullname.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "收款方户名不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (textMoney.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "转账金额不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (textPhone.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "收款方手机号码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
//
//                if (textCreditcardNo.getText().toString().compareTo(textCreditcardNoConfirm.getText().toString()) != 0) {
//                    Toast.makeText(getApplicationContext(), "收款方手机号码不能为空",
//                            Toast.LENGTH_SHORT).show();
//                    return;
//                }

                if (!textCreditcardNo.getText().toString().equals(textCreditcardNoConfirm.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "收款方卡号输入不一致",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                Intent it = new Intent(TransferActivity.this, TransferPayActivity.class);

                it.putExtra("name", textFullname.getText().toString());
                it.putExtra("card", textCreditcardNo.getText().toString());
                it.putExtra("money", textMoney.getText().toString());
                it.putExtra("phone", textPhone.getText().toString());

                startActivity(it);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });


        btnRule = (Button) findViewById(R.id.btnRule);
        btnRule.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        btnRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTransferRuleFragment();
            }
        });


        checkArgree = (CheckBox) findViewById(R.id.checkArgree);

        showTransferRuleFragment();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.transfer, menu);
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
