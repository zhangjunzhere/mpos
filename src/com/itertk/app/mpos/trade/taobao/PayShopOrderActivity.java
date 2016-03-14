package com.itertk.app.mpos.trade.taobao;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.trade.pos.BankcardOrderActivity;
import com.itertk.app.mpos.trade.pos.LoadingDialogHelper;
import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.dbhelper.ShopOrder;
import com.itertk.app.mpos.dbhelper.ShopOrderDao;
import com.itertk.app.mpos.utility.LinkeaRequest;
import com.itertk.app.mpos.utility.ShopOrderOperation;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * 订货刷卡支付界面
 */
public class PayShopOrderActivity extends Activity {
    private static String TAG = "CustomerSignActivity";
    Button btnOK;
    Button btnCancel;
    TextView textMoney;
    FrameLayout signArea;
    ViewGroup.LayoutParams p;
    PaintView mView;

    private Boolean mPaySuccess = false;

    class PaintView extends View {
        private Paint paint;
        private Canvas cacheCanvas;
        private Bitmap cachebBitmap;
        private Path path;

        public Bitmap getCachebBitmap() {
            return cachebBitmap;
        }

        public PaintView(Context context) {
            super(context);
            init();
        }

        private void init() {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(6);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            path = new Path();
            cachebBitmap = Bitmap.createBitmap(1200, 400, Bitmap.Config.ARGB_8888);
            //cachebBitmap = Bitmap.createBitmap(p.width, (int)(p.height*0.8), Bitmap.Config.ARGB_8888);
            cacheCanvas = new Canvas(cachebBitmap);
            cacheCanvas.drawColor(Color.WHITE);
        }

        public void clear() {
            if (cacheCanvas != null) {
                cacheCanvas.drawPaint(paint);
                paint.setColor(Color.BLACK);
                cacheCanvas.drawColor(Color.WHITE);
                invalidate();
            }
        }


        @Override
        protected void onDraw(Canvas canvas) {
            // canvas.drawColor(BRUSH_COLOR);
            canvas.drawBitmap(cachebBitmap, 0, 0, null);
            canvas.drawPath(path, paint);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            int curW = cachebBitmap != null ? cachebBitmap.getWidth() : 0;
            int curH = cachebBitmap != null ? cachebBitmap.getHeight() : 0;
            if (curW >= w && curH >= h) {
                return;
            }

            if (curW < w)
                curW = w;
            if (curH < h)
                curH = h;

            Bitmap newBitmap = Bitmap.createBitmap(curW, curH, Bitmap.Config.ARGB_8888);
            Canvas newCanvas = new Canvas();
            newCanvas.setBitmap(newBitmap);
            if (cachebBitmap != null) {
                newCanvas.drawBitmap(cachebBitmap, 0, 0, null);
            }
            cachebBitmap = newBitmap;
            cacheCanvas = newCanvas;
        }

        private float cur_x, cur_y;

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    cur_x = x;
                    cur_y = y;
                    path.moveTo(cur_x, cur_y);
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    path.quadTo(cur_x, cur_y, x, y);
                    cur_x = x;
                    cur_y = y;
                    break;
                }

                case MotionEvent.ACTION_UP: {
                    cacheCanvas.drawPath(path, paint);
                    path.reset();
                    break;
                }
            }

            invalidate();

            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_sign);
        MyActionbar.setNormalActionBarLayout(this);

        final Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        final HashMap<String, String> card = (HashMap<String, String>) bundle.getSerializable("card");
        final String mTotalPrice = bundle.getString(LinkeaRequest.KEY_TRADE_PRICE);
        final String mTradeNo = bundle.getString(LinkeaRequest.KEY_TRADE_NO);

        textMoney = (TextView) findViewById(R.id.textMoney);
        textMoney.setText((new DecimalFormat("0.00")).format(Arith.newBigDecimal(mTotalPrice)) + "元");

        signArea = (FrameLayout) findViewById(R.id.signArea);
        p = signArea.getLayoutParams();
        mView = new PaintView(this);
        signArea.addView(mView);
        mView.requestFocus();


        btnOK = (Button) findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String track2 = card.get("track2");
                String icdata = "";
                boolean isiccard =Boolean.valueOf(card.get("isiccard"));
                Log.i("smile","isiccard "+isiccard);
                if(isiccard)
                {
                    icdata =  card.get("icdata");
                   // track2 = "";
                }

                LoadingDialogHelper.show(PayShopOrderActivity.this);

                MPosApplication mPosApplication = MPosApplication.getInstance();

                mPosApplication.getMsgBuilder().buildPayOrderMsg(
                        "PAY_MPOS", mTradeNo, ((MPosApplication) getApplication())
                                .getPosId(), card.get("no"),card.get("pin"),
                        card.get("track1"), icdata, card.get("cardseqno"), track2, card.get("track3"), mPosApplication.getTrackKey(),
                        mPosApplication.getMacKey(),mPosApplication.getPinKey() ).send(new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d(TAG, throwable.toString());
                        Log.d("smile", "onFailture " + responseString);
                        Toast.makeText(getApplicationContext(), throwable.toString(), Toast.LENGTH_LONG).show();

                        LoadingDialogHelper.dismiss();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        LoadingDialogHelper.dismiss();
                        try {
                            Log.d("smile", "onSuccess " + responseString);
                            LinkeaResponseMsg.ResponseMsg payOrderResponseMsg =
                                    LinkeaResponseMsgGenerator.generatePayOrderResponseMsg(responseString);
                            if (payOrderResponseMsg.success) {
                                Toast.makeText(getApplicationContext(), "订单支付成功", Toast.LENGTH_LONG).show();
                                mPaySuccess = true;
                                Intent it = new Intent(PayShopOrderActivity.this, BankcardOrderActivity.class);
                                it.putExtras( intent.getExtras());
                                //it.putExtra("card", intent.getStringExtra("card"));
                                it.putExtra("sign", saveSign());
                                startActivity(it);
                                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

                                finish();
                            } else {
                                String erroInfo = payOrderResponseMsg.result_code_msg;
                                Toast.makeText(getApplicationContext(), erroInfo, Toast.LENGTH_LONG).show();
                                Log.e(TAG, erroInfo);
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                            Log.d(TAG, e.toString());
                        }
                    }
                });
            }
        });

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mView.clear();
            }
        });
    }

    @Override
    public void onDestroy(){
        //return pay result to comfirmorderdialog, to decide which page to navigate
        Intent intent  = new Intent(ConfirmOrderDialog.PAY_RESULT_ACTION);
        intent.putExtra(ConfirmOrderDialog.KEY_PAY_STATUS, mPaySuccess);
        sendBroadcast(intent);
        super.onDestroy();
    }

    private Bitmap saveSign() {
        Bitmap bm = mView.getCachebBitmap();
        int width = bm.getWidth();
        int height = bm.getHeight();
//        // 设置想要的大小
//        int newWidth = 350;
//        int newHeight = 100;
        // 计算缩放比例
        float scaleWidth = 0.25f;
        float scaleHeight = 0.25f;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleHeight, scaleWidth);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);

//        String fileName = "/sdcard/" + saleOrder.getSaleOrderNo() + ".png";
//        File file = new File(fileName);
//        FileOutputStream out;
//        try {
//            out = new FileOutputStream(file);
//            if (newbm.compress(Bitmap.CompressFormat.PNG, 100, out)) {
//                out.flush();
//                out.close();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return newbm;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.customer_sign, menu);
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
