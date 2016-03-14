package com.itertk.app.mpos.trade.convenience;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.dbhelper.User;
import com.itertk.app.mpos.trade.TradeHomeActivity;
import com.itertk.app.mpos.trade.pos.CashDialog;
import com.itertk.app.mpos.trade.pos.WeixinPayDialog;
import com.itertk.app.mpos.trade.taobao.MessageBoxDialog;
import com.itertk.app.mpos.utility.AESEncryptor;
import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.dbhelper.Reduce;
import com.itertk.app.mpos.dbhelper.ReduceDao;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.dbhelper.SaleOrderDao;
import com.itertk.app.mpos.trade.pos.BankcardDialog;
import com.itertk.app.mpos.trade.pos.CustomerSignActivity;
import com.itertk.app.mpos.utility.LinkeaRequest;
import com.itertk.app.mpos.utility.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ConveniencePayActivity extends Activity implements View.OnClickListener {

    static final String TAG = "PayActivity";
    SaleOrderDao saleOrderManager;
    //  Spinner spinnerReduce = null;
    // ReduceSpinnerAdapter reduceSpinnerAdapter;
    TextView textPrice = null;
    SaleOrder saleOrder;
    Float pay = 0.00f;
    BigDecimal priceTotal = Arith.newZeroBigDecimal();
    Button btnMemberPay = null;
    Button btnCardPay = null;
    Button btnCashPay = null;
    Button btnWechatPay = null;

    Button btnLastMoneyButton = null;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMemberPay:
                onBtnMemberPay();
                break;
            case R.id.btnCardPay:
                onBtnCardPay();
                break;
            case R.id.btnCashPay:
                onBtnCashPay();
                break;
            case R.id.btnWechatPay:
                onBtnWechatPay();
                break;
        }
    }

    private void onBtnMoney(Button btnNowButton, Float money) {
        if (btnLastMoneyButton == null) {
            btnLastMoneyButton = btnNowButton;
            //btnLastMoneyButton.setBackgroundResource(R.drawable.sel_2);
            pay = money;
        } else {
            if (btnLastMoneyButton == btnNowButton) {
                btnNowButton.setSelected(false);
                btnLastMoneyButton = null;
                //btnNowButton.setBackgroundResource(R.drawable.sel);
                pay = 0.00f;
            } else {
                //btnLastMoneyButton.setBackgroundResource(R.drawable.sel);
                btnLastMoneyButton = btnNowButton;
                //btnLastMoneyButton.setBackgroundResource(R.drawable.sel_2);
                pay = money;
            }
        }

    }

    private void onBtnMemberPay() {

        final String ip = Utils.getWifiIp(this);
        Log.i("smile", "ip: " + ip);

        User member = Utils.getMemberFromDB(this);
        if(member == null){
            Toast.makeText(this, "没有会员登录过", Toast.LENGTH_SHORT);
            return;
        }

        String memberNo = String.valueOf(member.getClerkNo());
        String password = Utils.convertToMD5(AESEncryptor.getInstance(this).decryption(member.getLoginPwd()));

        final LinkeaRequest.OnRequestResultListener  listener = new LinkeaRequest.OnRequestResultListener() {
            @Override
            public void onSuccess() {
                saleOrder.setPayedTime(Utils.getTime());
                saleOrder.update();
                MessageBoxDialog messageBox = new MessageBoxDialog(ConveniencePayActivity.this,"支付成功",true);
                messageBox.show();

                messageBox.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Intent it = new Intent(ConveniencePayActivity.this, TradeHomeActivity.class);
                        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(it);
                    }
                });
            }
            @Override
            public void onFailure() {
                MessageBoxDialog messageBox = new MessageBoxDialog(ConveniencePayActivity.this,"支付失败",false);
                messageBox.show();
            }
        };

        LinkeaRequest.onMemberPay(ConveniencePayActivity.this, saleOrder.getSaleOrderNo(), listener);


    }

    private void onBtnWechatPay() {
        WeixinPayDialog weixindlg = new WeixinPayDialog(this, R.style.MyWeixinDialog, saleOrder);
        weixindlg.setDirectPay(true);
        weixindlg.show();
    }

    private void onBtnCashPay() {
        CashDialog dlg = new CashDialog(this, R.style.MyDialog, saleOrder);
        // dlg.setCanceledOnTouchOutside(false);
        dlg.setDirectPay(true);
        dlg.show();
    }

    private void onBtnCardPay() {
        //saleOrder.setPrice(priceTotal);

        final BankcardDialog bankcardDialog = new BankcardDialog(this, R.style.MyDialog);
        bankcardDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (bankcardDialog.cardInfo == null) {
                    Log.d(TAG, "获取磁卡信息失败");
                    return;
                } else {
                    Log.d(TAG, bankcardDialog.cardInfo.toString());
                }

                if (bankcardDialog.pin1 == null || bankcardDialog.pin2 == null) {
                    Log.d(TAG, "获取密码失败");
                    return;
                } else {
                    Log.d(TAG, "pin1=" + bankcardDialog.pin1 + " pin2=" + bankcardDialog.pin2);
                }
                bankcardDialog.cardInfo.put("pin", bankcardDialog.pin1);
                bankcardDialog.cardInfo.put("ksn", bankcardDialog.pin2);
                final Intent it = new Intent(ConveniencePayActivity.this, CustomerSignActivity.class);
                it.putExtra("orderId", String.valueOf(saleOrder.getSaleOrderId()));
                Bundle bundle = new Bundle();
                bundle.putSerializable("card", bankcardDialog.cardInfo);
                it.putExtras(bundle);
                startActivity(it);

            }
        });
        bankcardDialog.show();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convenience_pay);
        MyActionbar.setNormalActionBarLayout(this);

        saleOrderManager = ((MPosApplication) getApplication()).getDataHelper().getSaleOrderManager();

        Intent intent = this.getIntent();
        String orderId = intent.getStringExtra("orderId");
        saleOrder = saleOrderManager.load(Long.valueOf(orderId).longValue());


        //spinnerReduce = (Spinner) findViewById(R.id.spinnerReduce);


        btnMemberPay = (Button) findViewById(R.id.btnMemberPay);
        btnMemberPay.setOnClickListener(this);

        btnCardPay = (Button) findViewById(R.id.btnCardPay);
        btnCardPay.setOnClickListener(this);

        btnCashPay = (Button) findViewById(R.id.btnCashPay);
        btnCashPay.setOnClickListener(this);

        btnWechatPay = (Button) findViewById(R.id.btnWechatPay);
        btnWechatPay.setOnClickListener(this);

        textPrice = (TextView) findViewById(R.id.textPrice);
        textPrice.setText("总计消费 " + (new DecimalFormat("0.00")).format(Arith.newBigDecimal(saleOrder.getPrice())) + " 元");


    }

    private void updatePriceDisplay() {
        priceTotal = Arith.newBigDecimal(saleOrder.getPrice());
//        Reduce reduce = reduceSpinnerAdapter.getReduceSelected();
//        if (reduce != null) {
//            if (reduce.getType() == 1) {
//                priceTotal =  Arith.div(priceTotal.multiply(Arith.newBigDecimal(reduce.getValue())),new BigDecimal(100));
//            } else if (reduce.getType() == 0) {
//                priceTotal =priceTotal.subtract(Arith.newBigDecimal(reduce.getValue()));
//            }
//        }
//        textPrice.setText("总计消费 " + (new DecimalFormat("0.00")).format(priceTotal) + " 元");
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
                        // spinnerReduce.setSelection(i);
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
}
