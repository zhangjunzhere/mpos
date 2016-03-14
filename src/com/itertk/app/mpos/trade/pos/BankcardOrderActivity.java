package com.itertk.app.mpos.trade.pos;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.utility.LinkeaRequest;
import com.itertk.app.mpos.utility.ToastHelper;
import com.itertk.app.mpos.utility.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
/*
* 银行卡支付
* */
public class BankcardOrderActivity extends Activity {
    TextView textOrderNo;
    TextView textBankcardNo;
    TextView textDate;
    TextView textMoney;
    ImageView imageSign;
    Button btnOK;
//    SaleOrder saleOrder;
    RelativeLayout layoutSign;
    TextView textShopNo;
    TextView textTermNo;

    private BigDecimal mTotalPrice;
    private long mOrderId;
    private String mTradeNo;

    private boolean mSaved = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankcard_order);
        MyActionbar.setNormalActionBarLayout(this);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        mOrderId = bundle.getLong(LinkeaRequest.KEY_ORDER_ID);
        mTradeNo = bundle.getString(LinkeaRequest.KEY_TRADE_NO);
        mTotalPrice = Arith.newBigDecimal(bundle.getString(LinkeaRequest.KEY_TRADE_PRICE));
        HashMap<String, String> card = (HashMap<String, String>) bundle.getSerializable(LinkeaRequest.KEY_CARD_INFO);

        layoutSign = (RelativeLayout) findViewById(R.id.layoutSign);
        textOrderNo = (TextView) findViewById(R.id.textOrderNo);
        textBankcardNo = (TextView) findViewById(R.id.textBankcardNo);
        textDate = (TextView) findViewById(R.id.textDate);
        textMoney = (TextView) findViewById(R.id.textMoney);

        textOrderNo.setText(mTradeNo);
        textBankcardNo.setText(card.get("no"));
        textDate.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        textMoney.setText((new DecimalFormat("0.00")).format(mTotalPrice) + "元");

        textShopNo = (TextView) findViewById(R.id.textShopno);
        textShopNo.setText(MPosApplication.getInstance().getPosId());

        textTermNo = (TextView) findViewById(R.id.textTermNo);
        textTermNo.setText(MPosApplication.getInstance().getMsgBuilder().getTerm_id());


        imageSign = (ImageView) findViewById(R.id.imageSign);
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 1;
        //Bitmap bm = BitmapFactory.decodeFile(sign, option);
        Bitmap bm = intent.getParcelableExtra("sign");
        imageSign.setImageBitmap(bm);

        btnOK = (Button) findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(BankcardOrderActivity.this, PaySuccessActivity.class);
                it.putExtra("orderid",mOrderId);
                it.putExtra("info", "刷卡支付 " + (new DecimalFormat("0.00")).format(mTotalPrice) + " 元");
                startActivity(it);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                finish();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        uploadSign();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.bankcard_order, menu);
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
    String getSign()
    {
        //get bitmap
        findViewById(R.id.content).setDrawingCacheEnabled(true);
        findViewById(R.id.content).buildDrawingCache();
        Bitmap bm=findViewById(R.id.content).getDrawingCache();

        String imagebase64= Utils.convertBimapToBase64(bm);
        return  imagebase64;
    }

   private  void uploadSign()
    {
        if(!mSaved) {
            mSaved = true;
            Bitmap bmpSign =Utils.convertViewToBitmap(layoutSign);
            String fileName = mTradeNo + ".png";
            String imagebase64 = Utils.convertBimapToBase64(bmpSign);
            //will upload in PreloadService fileobserver
            Utils.writeFile(getBaseContext(), fileName, imagebase64);
        }
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
