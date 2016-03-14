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
import android.widget.ImageButton;

import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;

public class ConvenienceActivity extends Activity {
    ImageButton btnCreditcard;
    ImageButton btnTransfer;
    ImageButton btnAir;
    ImageButton btnTrain;
    Button btnPhone;
    Button btnBalance;
    ImageButton btnLoan;
    ImageButton btnApply;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convenience);
        MyActionbar.setNormalActionBarLayout(this);

//        btnCreditcard = (ImageButton) findViewById(R.id.btnCreditcard);
//        btnCreditcard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent it = new Intent(ConvenienceActivity.this, CreditcardActivity.class);
//                startActivity(it);
//                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//            }
//        });
//
//        btnTransfer = (ImageButton) findViewById(R.id.btnTransfer);
//        btnTransfer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent it = new Intent(ConvenienceActivity.this, TransferActivity.class);
//                startActivity(it);
//                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//            }
//        });

//        btnAir = (ImageButton) findViewById(R.id.btnAir);
//        btnAir.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent it = new Intent(ConvenienceActivity.this, AirTicketActivity.class);
//                startActivity(it);
//                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//            }
//        });
//
//        btnTrain = (ImageButton) findViewById(R.id.btnTrain);
//        btnTrain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent it = new Intent(ConvenienceActivity.this, TrainTicketActivity.class);
//                startActivity(it);
//                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//            }
//        });

        btnPhone = (Button) findViewById(R.id.btnPhone);
        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ConvenienceActivity.this, PhoneChargeActivity.class);
                startActivity(it);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });

        btnBalance = (Button) findViewById(R.id.btnBalance);
        btnBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ConvenienceActivity.this, BalanceQueryActivity.class);
                startActivity(it);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });

//        btnLoan = (ImageButton) findViewById(R.id.btnLoan);
//        btnLoan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent it = new Intent(ConvenienceActivity.this, LoanActivity.class);
//                startActivity(it);
//                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//            }
//        });
//
//        btnApply = (ImageButton) findViewById(R.id.btnApply);
//        btnApply.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent it = new Intent(ConvenienceActivity.this, CreditcardApplyActivity.class);
//                startActivity(it);
//                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//            }
//        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.convenience, menu);
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
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ConvenienceActivity", "on destroy");
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
