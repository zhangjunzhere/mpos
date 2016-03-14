package com.itertk.app.mpos.trade.pos;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.TextFocusChangeListener;
import com.itertk.app.mpos.utility.ToastHelper;
import com.itertk.app.mpos.dbhelper.Reduce;
import com.itertk.app.mpos.dbhelper.ReduceDao;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.dbhelper.SaleOrderDao;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 *支付
 */


public class PayActivity extends Activity implements View.OnClickListener {
    static final String TAG = "PayActivity";
    SaleOrderDao saleOrderManager;
    Spinner spinnerReduce = null;
    ReduceSpinnerAdapter reduceSpinnerAdapter;
    TextView textPrice = null;
    TextView textCash = null;
    SaleOrder saleOrder;
    BigDecimal pay = Arith.newZeroBigDecimal();
    BigDecimal priceTotal = Arith.newZeroBigDecimal();


    Button btnMoney50 = null;
    Button btnMoney100 = null;
    Button btnMoney200 = null;
    Button btnMoney300 = null;

    Button btnPay = null;
    Button btnMemberPay = null;
    RelativeLayout btnCardPay = null;

    Button btnLastMoneyButton = null;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMoney50:
                onBtnMoney50();
                break;
            case R.id.btnMoney100:
                onBtnMoney100();
                break;
            case R.id.btnMoney200:
                onBtnMoney200();
                break;
            case R.id.btnMoney300:
                onBtnMoney300();
                break;
            case R.id.btnPay:
                onBtnPay();
                break;
            case R.id.btnMemberPay:
                onBtnMemberPay();
                break;
            case R.id.btnCardPay:
                onBtnCardPay();
        }
    }

    private void onBtnMoney(Button btnNowButton, BigDecimal money) {
        if (btnLastMoneyButton == null) {
            btnLastMoneyButton = btnNowButton;
            //btnLastMoneyButton.setBackgroundResource(R.drawable.sel_2);
            pay = money;
        } else {
            if (btnLastMoneyButton == btnNowButton) {
                btnNowButton.setSelected(false);
                btnLastMoneyButton = null;
                //btnNowButton.setBackgroundResource(R.drawable.sel);
                pay = Arith.newZeroBigDecimal();
            } else {
                //btnLastMoneyButton.setBackgroundResource(R.drawable.sel);
                btnLastMoneyButton = btnNowButton;
                //btnLastMoneyButton.setBackgroundResource(R.drawable.sel_2);
                pay = money;
            }
        }

    }

    private void onBtnMoney50() {
        btnMoney50.setSelected(true);
        btnMoney100.setSelected(false);
        btnMoney200.setSelected(false);
        btnMoney300.setSelected(false);

        onBtnMoney(btnMoney50, new BigDecimal(50));
        textCash.setText((new DecimalFormat("0.00")).format(pay));
    }

    private void onBtnMoney100() {
        btnMoney50.setSelected(false);
        btnMoney100.setSelected(true);
        btnMoney200.setSelected(false);
        btnMoney300.setSelected(false);

        onBtnMoney(btnMoney100, new BigDecimal(100));
        textCash.setText((new DecimalFormat("0.00")).format(pay));
    }

    private void onBtnMoney200() {
        btnMoney50.setSelected(false);
        btnMoney100.setSelected(false);
        btnMoney200.setSelected(true);
        btnMoney300.setSelected(false);

        onBtnMoney(btnMoney200, new BigDecimal(200));
        textCash.setText((new DecimalFormat("0.00")).format(pay));
    }

    private void onBtnMoney300() {
        btnMoney50.setSelected(false);
        btnMoney100.setSelected(false);
        btnMoney200.setSelected(false);
        btnMoney300.setSelected(true);

        onBtnMoney(btnMoney300, new BigDecimal(300));
        textCash.setText((new DecimalFormat("0.00")).format(pay));
    }

    private void onBtnPay() {


        Log.d(TAG, "payed with cash " + saleOrder.getShihouPrice());
        saleOrder.setPayedType(1);
        Long time = (new Date()).getTime();
        saleOrder.setPayedTime(time);
        saleOrder.setPrice(priceTotal.toString());
        saleOrder.update();

        try {
            pay = Arith.newBigDecimal(textCash.getText().toString().trim());
        } catch (Exception e) {
            pay = Arith.newZeroBigDecimal();
        }

        if (pay.compareTo(priceTotal)<0) {
            ToastHelper.showToast(this,"现金支付少于商品金额，请重新输入");
            return;
        }

        final Intent it = new Intent(PayActivity.this, PaySuccessActivity.class); //new DecimalFormat("0.00")).format((pay - saleOrder.getPrice())
        it.putExtra("info", "现金收款 " + (new DecimalFormat("0.00")).format(pay) + " 元，找零 " + (DecimalHelper.getDecimalString(pay.subtract(saleOrder.getShihouPrice()))) + "元");
        startActivity(it);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private void onBtnMemberPay() {

    }

    private void onBtnCardPay() {
        saleOrder.setPrice(priceTotal.toString());

        final PosDialog posDialog = new PosDialog(this, R.style.MyDialog , Arith.newBigDecimal(priceTotal.toString()));
        posDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (posDialog.cardInfo == null) {
                    Log.d(TAG, "获取磁卡信息失败");
                    return;
                } else {
                    Log.d(TAG, posDialog.cardInfo.toString());
                }

                if (posDialog.pin1 == null || posDialog.pin2 == null) {
                    Log.d(TAG, "获取密码失败");
                    return;
                } else {
                    Log.d(TAG, "pin1=" + posDialog.pin1 + " pin2=" + posDialog.pin2);
                }

                posDialog.cardInfo.put("pin", posDialog.pin1);
                posDialog.cardInfo.put("ksn", posDialog.pin2);

                final Intent it = new Intent(PayActivity.this, CustomerSignActivity.class);
                it.putExtra("orderId", String.valueOf(saleOrder.getSaleOrderId()));
                Bundle bundle = new Bundle();
                bundle.putSerializable("card", posDialog.cardInfo);
                it.putExtras(bundle);
                startActivity(it);

            }
        });
        posDialog.show();

//        ((MPosApplication)getApplication()).getBbPos().checkCard(new BBPos.CheckCardListener() {
//            @Override
//            public void onCheckCardListener(boolean cancel, WisePadController.CheckCardResult checkCardResult, final Hashtable<String, String> stringStringHashtable) {
//                if(cancel){
//                    Log.d(TAG, "cancel checkCard by wisepad");
//                    bankcardDialog.dismiss();
//                }
//
//                if (checkCardResult == WisePadController.CheckCardResult.MCR){
//                    Log.d(TAG, stringStringHashtable.toString());
//
//
//                    bankcardDialog.setTitle("请输入密码");
//                    ((MPosApplication)getApplication()).getBbPos().startPinEntry(new BBPos.StartPinEntryListener() {
//                        @Override
//                        public void onStartPinEntryListener(WisePadController.PinEntryResult pinEntryResult, String s, String s2) {
//                            //Log.d(TAG, );
//                            if(pinEntryResult == WisePadController.PinEntryResult.ENTERED){
//                                    bankcardDialog.dismiss();
//                                final Intent it = new Intent(PayActivity.this, CustomerSignActivity.class);
//                                it.putExtra("orderId", String.valueOf(saleOrder.getSaleOrderId()));
//                                Bundle bundle = new Bundle();
//                                bundle.putSerializable("card", stringStringHashtable);
//                                it.putExtras(bundle);
//                                startActivity(it);
//                            }
//                        }
//                    });
//                }else{
//
//                    bankcardDialog.setTitle("请重刷");
//                    bankcardDialog.setButtonText("确定");
//                }
//            }
//        });


//        Timer timer = new Timer();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                bankcardDialog.dismiss();
//                startActivity(it);
//                PayActivity.this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//            }
//        };
//        timer.schedule(task, 1000 * 2);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        MyActionbar.setNormalActionBarLayout(this);

        saleOrderManager = ((MPosApplication) getApplication()).getDataHelper().getSaleOrderManager();

        Intent intent = this.getIntent();
        String orderId = intent.getStringExtra("orderId");
        saleOrder = saleOrderManager.load(Long.valueOf(orderId).longValue());


        spinnerReduce = (Spinner) findViewById(R.id.spinnerReduce);

        btnMoney50 = (Button) findViewById(R.id.btnMoney50);
        btnMoney50.setOnClickListener(this);

        btnMoney100 = (Button) findViewById(R.id.btnMoney100);
        btnMoney100.setOnClickListener(this);

        btnMoney200 = (Button) findViewById(R.id.btnMoney200);
        btnMoney200.setOnClickListener(this);

        btnMoney300 = (Button) findViewById(R.id.btnMoney300);
        btnMoney300.setOnClickListener(this);

        btnPay = (Button) findViewById(R.id.btnPay);
        btnPay.setOnClickListener(this);

        btnMemberPay = (Button) findViewById(R.id.btnMemberPay);
        btnMemberPay.setOnClickListener(this);

        btnCardPay = (RelativeLayout) findViewById(R.id.btnCardPay);
        btnCardPay.setOnClickListener(this);

        textPrice = (TextView) findViewById(R.id.textPrice);
        textPrice.setText("总计消费 " + (new DecimalFormat("0.00")).format(saleOrder.getShihouPrice()) + " 元");

        TextFocusChangeListener textFocusChangeListener = new TextFocusChangeListener();
        textCash = (TextView) findViewById(R.id.textCash);
        textCash.setOnFocusChangeListener(textFocusChangeListener);
        textCash.setInputType(EditorInfo.TYPE_CLASS_NUMBER);


        reduceSpinnerAdapter = new ReduceSpinnerAdapter(this, R.layout.spinner_reduce, R.id.txtvwSpinner);
        reduceSpinnerAdapter.setDropDownViewResource(R.layout.spinner_reduce_item);
        spinnerReduce.setAdapter(reduceSpinnerAdapter);
        reduceSpinnerAdapter.setReduceSelected(null);

        spinnerReduce.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reduceSpinnerAdapter.setReduceSelected(position);
                Log.d(TAG, "spinnerReduce select position=" + position);
                updatePriceDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updatePriceDisplay() {
        priceTotal = Arith.newBigDecimal(saleOrder.getPrice());
        Reduce reduce = reduceSpinnerAdapter.getReduceSelected();
        if (reduce != null) {
            if (reduce.getType() == 1) {
                priceTotal = Arith.div(Arith.mulBig(priceTotal , reduce.getValue()) , 100);
            } else if (reduce.getType() == 0) {
                priceTotal =priceTotal.subtract(Arith.newBigDecimal(reduce.getValue()));
            }
        }
        textPrice.setText("总计消费 " + (new DecimalFormat("0.00")).format(priceTotal) + " 元");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.pay, menu);
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

    class ReduceSpinnerAdapter extends ArrayAdapter<String> {
        static final String TAG = "ReduceSpinnerAdapter";
        Reduce reduceSelected;
        ReduceDao reduceManager;
        List<Reduce> reduceList;
        ArrayList<Reduce> fakeReduceList;

        public ReduceSpinnerAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
            reduceManager = ((MPosApplication) getContext().getApplicationContext()).getDataHelper().getReduceManager();
            reduceList = reduceManager.loadAll();
            fakeReduceList = new ArrayList<Reduce>();
            Reduce root = new Reduce(-1L, "添加优惠", "0", 0);
            fakeReduceList.add(root);
            for (int i = 0; i < reduceList.size(); i++) {
                fakeReduceList.add(reduceList.get(i));
            }

        }

        @Override
        public int getCount() {
//            int count = fakeReduceList.size();
//            return count > 0 ? count - 1 : count;
            return fakeReduceList.size();
        }

        @Override
        public String getItem(int position) {
            return fakeReduceList.get(position).getName();
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        public void setReduceSelected(Reduce reduce) {
            if (reduce == null) {
                //spinnerReduce.setSelection(getCount());
            } else {
                for (int i = 0; i < fakeReduceList.size(); i++) {
                    if (fakeReduceList.get(i).getReduceId() == reduce.getReduceId()) {
                        spinnerReduce.setSelection(i);
                        break;
                    }
                }
            }
        }

        public Reduce getReduceSelected() {
            return reduceSelected;
        }

        public void setReduceSelected(int position) {
            if (position < 0 || position >= getCount()) {
                reduceSelected = null;
                Log.d(TAG, "no reduce selected by postion=" + position);
            } else {
                reduceSelected = fakeReduceList.get(position);
                Log.d(TAG, "select reduce " + reduceSelected.getName());
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.spinner_reduce_item, parent, false);
            }

            TextView txtvwDropdown = (TextView) convertView.findViewById(R.id.txtvwSpinner);
            if (position == 0) {
                txtvwDropdown.setText("无优惠");
            } else {
                txtvwDropdown.setText(getItem(position));
            }

            return convertView;
        }
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
