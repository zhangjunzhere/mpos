package com.itertk.app.mpos.trade.convenience;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;

public class CreditcardApplyActivity extends Activity {
    LinearLayout btnGS;
    LinearLayout btnNY;
    LinearLayout btnJS;
    LinearLayout btnZG;
    LinearLayout btnJT;
    LinearLayout btnZS;
    LinearLayout btnGF;
    LinearLayout btnGD;
    LinearLayout btnZX;
    LinearLayout btnXY;
    LinearLayout btnMS;

    private void showWebView(String url) {
        Intent intent = new Intent(this, ApplyContentActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditcard_apply);
        MyActionbar.setNormalActionBarLayout(this);

        btnGS = (LinearLayout) findViewById(R.id.btnGS);
        btnGS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWebView("http://www.icbc.com.cn/icbc/%E7%89%A1%E4%B8%B9%E5%8D%A1/default.htm");
            }
        });

        btnNY = (LinearLayout) findViewById(R.id.btnNY);
        btnNY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWebView("http://www.abchina.com/cn/CreditCard/ProductsRec/CardFilter/default.htm?cardType=-1&applyArea=-1&cardGrade=-1&askFor=1&cardName=");
            }
        });

        btnJS = (LinearLayout) findViewById(R.id.btnJS);
        btnJS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWebView("http://creditcard.ccb.com/creditCard/creditCard.html");
            }
        });

        btnZG = (LinearLayout) findViewById(R.id.btnZG);
        btnZG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWebView("http://www.boc.cn/ebanking/online/201310/t20131024_2567778.html");
            }
        });

        btnJT = (LinearLayout) findViewById(R.id.btnJT);
        btnJT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWebView("http://creditcard.bankcomm.com/content/pccc/index.html");
            }
        });

        btnZS = (LinearLayout) findViewById(R.id.btnZS);
        btnZS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWebView("http://ccclub.cmbchina.com/ccproduct/newcustomer.aspx");
            }
        });

        btnGF = (LinearLayout) findViewById(R.id.btnGF);
        btnGF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWebView("http://card.cgbchina.com.cn/");
            }
        });

        btnGD = (LinearLayout) findViewById(R.id.btnGD);
        btnGD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWebView("http://xyk.cebbank.com/home/ps/index.htm");
            }
        });

        btnZX = (LinearLayout) findViewById(R.id.btnZX);
        btnZX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWebView("http://cards.ecitic.com/");
            }
        });

        btnXY = (LinearLayout) findViewById(R.id.btnXY);
        btnXY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWebView("http://creditcard.cib.com.cn/index.html");
            }
        });

        btnMS = (LinearLayout) findViewById(R.id.btnMS);
        btnMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWebView("http://creditcard.cmbc.com.cn/");
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.creditcard_apply, menu);
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
