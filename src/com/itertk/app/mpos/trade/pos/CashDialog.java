package com.itertk.app.mpos.trade.pos;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.trade.pay.Pay;
import com.itertk.app.mpos.trade.taobao.MessageBoxDialog;
import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.utility.ToastHelper;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.utility.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * 现金支付
 */
public class CashDialog extends Dialog implements View.OnClickListener,Pay.OnPayFinishListener {
    private static String TAG = "CashDialog";
    Context context;
    TextView textPayTitle;
    String title;
    //Button btnOK;
    EditText textCash = null;
    Button btnCheckout;

    Button btnMoney50 = null;
    Button btnMoney100 = null;
    Button btnMoney200 = null;
    BigDecimal bg50;
    BigDecimal bg100;
    BigDecimal bg200;
    Button btnLastMoneyButton = null;
    ImageView backbtn = null;
   SaleOrder saleOrder;
    BigDecimal priceTotal;
 //   float pay = 0.00f;
    BigDecimal pay ;
    TextView textGivebackCharge;
    boolean mDirectPay=false;
    public CashDialog(Context context, int theme,SaleOrder tempsaleorder) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.title = title;
        saleOrder = tempsaleorder;

    }
    public void setDirectPay(boolean direct)
    {
        mDirectPay = direct;
    }
    public void setTitle(String title) {
        textPayTitle.setText(title);
    }
    public  void setThreeButtonPrice(String price)
    {
        setThreeButtonPrice(Arith.newBigDecimal(price));
    }
    public  void setThreeButtonPrice(BigDecimal price)
    {
         bg50 = price;
         bg100 = price;
         bg200 = price;
        if(bg50.compareTo(Arith.newBigDecimal("100000000"))>0)
        {
            btnMoney50.setTextSize(20);
            btnMoney100.setTextSize(20);
            btnMoney200.setTextSize(20);
        }
        btnMoney100.setVisibility(View.VISIBLE);
        btnMoney200.setVisibility(View.VISIBLE);
       //  double temp50 = Math.floor((price))+1;
        btnMoney50.setText(DecimalHelper.getDecimalString(bg50));
        if(bg50.compareTo(Arith.newBigDecimal(1))<0)
        {

            bg100 = new BigDecimal(1);
            bg200 = new BigDecimal(5);
            setPrice100AndPrice200(bg100,bg200);
        }
        else if(bg50.compareTo(Arith.newBigDecimal(1))<0)
        {

            bg100 = new BigDecimal(5);
            bg200 = new BigDecimal(10);
            setPrice100AndPrice200(bg100,bg200);
        }
       else  if(bg50.compareTo(Arith.newBigDecimal(10))<0)
        {

            bg100 = new BigDecimal(10);
            bg200 = new BigDecimal(20);
            setPrice100AndPrice200(bg100,bg200);
        }
        else if(bg50.compareTo(Arith.newBigDecimal(20))<0)
        {

            bg100 = new BigDecimal(20);
            bg200 = new BigDecimal(50);
            setPrice100AndPrice200(bg100,bg200);
        }
        else if(bg50.compareTo(Arith.newBigDecimal(50))<0)
        {

            bg100 = new BigDecimal(50);
            bg200 = new BigDecimal(100);
            setPrice100AndPrice200(bg100,bg200);
        }
        else // if(price50<100)
        {
            BigDecimal b1 = bg50.divide(new BigDecimal(10),BigDecimal.ROUND_CEILING);
            b1 = b1.setScale(0,BigDecimal.ROUND_CEILING);
            bg100 = b1.multiply(new BigDecimal(10));
           // BigDecimal bg100 = bg50.divide(new BigDecimal(10)).round(new MathContext(BigDecimal.ROUND_CEILING)).multiply(new BigDecimal(10));

            btnMoney100.setText(DecimalHelper.getDecimalString(bg100));
            BigDecimal b2 = bg50.divide(new BigDecimal(100),BigDecimal.ROUND_CEILING);
            b2 = b2.setScale(0,BigDecimal.ROUND_UP);
            bg200= b2.multiply(new BigDecimal(100));
            btnMoney200.setText(DecimalHelper.getDecimalString(bg200));

        }
//        else
//        {
//            price100=((int)(price50/10+1))*10;
//            btnMoney100.setText(getDecimalString(price100));
//            price200=((int)(price50/100+1))*100;
//            btnMoney200.setText(getDecimalString(price200));
//        }

        if(bg50.compareTo(bg100) ==0)
        {
            btnMoney100.setVisibility(View.INVISIBLE);

        }
        if(bg100.compareTo(bg200) ==0)
        {
            btnMoney200.setVisibility(View.INVISIBLE);

        }




    }
    public  void setPrice100AndPrice200(BigDecimal p100,BigDecimal p200)
    {
        btnMoney100.setText(DecimalHelper.getDecimalString(p100));

        btnMoney200.setText(DecimalHelper.getDecimalString(p200));
    }



    public CashDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_cash);

        textGivebackCharge = (TextView) findViewById(R.id.givecharge);


        btnMoney50 = (Button) findViewById(R.id.btnMoney50);
        btnMoney50.setOnClickListener(this);
        btnMoney100 = (Button) findViewById(R.id.btnMoney100);
        btnMoney100.setOnClickListener(this);
        btnMoney200 = (Button) findViewById(R.id.btnMoney200);
        btnMoney200.setOnClickListener(this);

        textPayTitle = (TextView) findViewById(R.id.totalPrice);
        setTitle("总计消费"+DecimalHelper.getDecimalString(saleOrder.getShihouPrice())+"元");
        setThreeButtonPrice(saleOrder.getShihouPrice());

        textCash = (EditText) findViewById(R.id.textCash);

//        backbtn = (ImageView) findViewById(R.id.dialogback);
//        backbtn.setOnClickListener(this);

        btnCheckout = (Button) findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 if(onBtnCheckout()) {

                    if(!mDirectPay)
                    {
                        placeOrder();
                    }
                     else
                    {
                        openPaySuccess();
                    }

                 }
//                if (onClickButtonCancelListener != null)
//                    onClickButtonCancelListener.onClickButtonCancel();
            }
        });

        backbtn = (ImageView) findViewById(R.id.dialogback);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

//        btnMoney50.setSelected(false);
//        btnMoney100.setSelected(false);
//        btnMoney200.setSelected(false);
        Log.d(TAG, "onCreate");
        textCash.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d(TAG, "setOnTouchListener");
                    btnMoney50.setSelected(false);
                    btnMoney100.setSelected(false);
                    btnMoney200.setSelected(false);
                }
                return false;
            }
        });
        textCash.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.i("smile", "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i2, int i3) {

                int dotindex = s.toString().indexOf(".");

                if(dotindex ==-1)
                {
                    if(s.length()>8)
                    {
                        s = s.subSequence(0,8);
                        textCash.setText(s);
                        textCash.setSelection(s.length());
                        calcZhaoLing(s);
                        return;
                    }
                }
                else
                {
                    if(dotindex>8) {
                        CharSequence zhenshu = s.subSequence(0, 8);
                        CharSequence xiaoshu = s.subSequence(dotindex,s.length());
                        s = zhenshu.toString() + xiaoshu.toString();
                        Log.i("smile", "dotindex: " + dotindex + " " + s);
                        textCash.setText(s);
                        textCash.setSelection(i);
                        calcZhaoLing(s);
                        return;
                    }
                }

                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        textCash.setText(s);
                        textCash.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    textCash.setText(s);
                    textCash.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        s = s.subSequence(0, 1);
                        textCash.setText(s);
                        textCash.setSelection(1);
                        calcZhaoLing(s);
                        return;
                    }
                }

                calcZhaoLing(s);

            }

            @Override
            public void afterTextChanged(Editable editable) {

                Log.i("smile", "afterTextChanged");
            }
        });

        updatePriceDisplay();
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Log.d(TAG, "onShow");

            }
        });
    }

    void calcZhaoLing(CharSequence charSequence)
    {
        BigDecimal inputprice = new BigDecimal(0);
        Log.d("smile", "onTextChanged " + charSequence.toString());
        try {
            inputprice = new BigDecimal(charSequence.toString());
        } catch (Exception e) {
            Log.d("smile", "exception input not correct ");
            return;
        }
        if (inputprice.compareTo(new BigDecimal(0))>0) {
            BigDecimal zhaoling = inputprice.subtract(saleOrder.getShihouPrice());

            if (zhaoling.compareTo(new BigDecimal(0.0001)) <= 0) {
                textGivebackCharge.setText("找零 0.00");
            } else {
                textGivebackCharge.setText("找零 " + DecimalHelper.getDecimalString(zhaoling));
            }
        }
    }

    private void updatePriceDisplay() {
        priceTotal =saleOrder.getShihouPrice();
//        Reduce reduce = reduceSpinnerAdapter.getReduceSelected();
//        if (reduce != null) {
//            if (reduce.getType() == 1) {
//                priceTotal = priceTotal * reduce.getValue() / 100;
//            } else if (reduce.getType() == 0) {
//                priceTotal -= reduce.getValue();
//            }
//        }
//        textPrice.setText("总计消费 " + (new DecimalFormat("0.00")).format(priceTotal) + " 元");
    }
    private boolean onBtnCheckout() {


        Log.d(TAG, "payed with cash " + saleOrder.getShihouPrice());
        saleOrder.setPayedType(1);
     //   Long time = (new Date()).getTime();
     //   saleOrder.setPayedTime(time);
     //   saleOrder.setPrice(priceTotal.toString());
     //   saleOrder.update();

        try {
            pay =new BigDecimal(textCash.getText().toString());
        } catch (Exception e) {
            pay =new BigDecimal(0);
        }

        if (pay.compareTo(priceTotal)<0) {
            ToastHelper.showToast(getContext(), "现金支付少于商品金额，请重新输入");
            return false;
        }
      //  PrintHelper.print(saleOrder);

        return  true;
    }
    public  void placeOrder()
    {
       // PrintHelper.printExternalOrder(getContext(),saleOrder);

        LoadingDialogHelper.show(getContext());
        MPosApplication.getInstance().getMsgBuilder().buildCreateOrderMsg2( saleOrder.getShihouPrice().toString(), "1009", "CASH-"+ Utils.getMemberId(),UpLoadOrderHelper.genChoutoutOrderToJson(saleOrder)
        ).send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, throwable.toString());
                Toast.makeText(getContext(), "网络无法连接", Toast.LENGTH_SHORT).show();
               // saleOrder.setUpload(false);

                saleOrder.setSaleOrderNo(Utils.genOrderTradeNo());
                saleOrder.update();
                LoadingDialogHelper.dismiss();
                openPaySuccess();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);
                try {
                    LinkeaResponseMsg.CreateOrderResponseMsg createOrderResponseMsg =
                            LinkeaResponseMsgGenerator.generateCreateOrderResponseMsg(responseString);
                    if (createOrderResponseMsg.success) {
                        saleOrder.setSaleOrderNo(createOrderResponseMsg.order.trade_no);
                        saleOrder.update();
                    }
                    else
                    {
                        saleOrder.setSaleOrderNo(Utils.genOrderTradeNo());
                        saleOrder.update();
                        Toast.makeText(getContext(), createOrderResponseMsg.result_code_msg, Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e)
                {
                    saleOrder.setSaleOrderNo(Utils.genOrderTradeNo());
                    saleOrder.update();
                   // Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
                openPaySuccess();
                LoadingDialogHelper.dismiss();
            }
        });

    }
    void openPaySuccess()
    {
        saleOrder.setPayedTime(Utils.getTime());
        saleOrder.update();
        final Intent it = new Intent(this.getContext(), PaySuccessActivity.class);
        it.putExtra("orderid",saleOrder.getSaleOrderId());
        it.putExtra("cash",true);
        it.putExtra("pay",pay.toString());
        it.putExtra("priceTotal",priceTotal.toString());
        it.putExtra("info", "现金收款 " + (new DecimalFormat("0.00")).format(pay) + " 元，找零 " +DecimalHelper.getDecimalString(pay.subtract(priceTotal)) + "元");
        CashDialog.this.getContext().startActivity(it);
        CashDialog.this.dismiss();
    }



    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnMoney50:
                onBtnMoney50();
                break;
            case R.id.btnMoney100:
                onBtnMoney100();
                break;
            case R.id.btnMoney200:
                onBtnMoney200();
                break;
//            case R.id.btnCheckout:
//                onBtnCheckout();
//                break;
            case R.id.dialogback:
                this.dismiss();
                break;
        }
    }
    private void onBtnMoney50() {
        btnMoney50.setSelected(true);
        btnMoney100.setSelected(false);
        btnMoney200.setSelected(false);


        onBtnMoney(btnMoney50, bg50);
        Log.i("smile", "pay: " + pay);
        textCash.setText((new DecimalFormat("0.00")).format(pay));
    }

    private void onBtnMoney100() {
        btnMoney50.setSelected(false);
        btnMoney100.setSelected(true);
        btnMoney200.setSelected(false);

        onBtnMoney(btnMoney100, bg100);
        Log.i("smile","pay: "+pay);
        textCash.setText((new DecimalFormat("0.00")).format(pay));
    }

    private void onBtnMoney200() {
        btnMoney50.setSelected(false);
        btnMoney100.setSelected(false);
        btnMoney200.setSelected(true);

        onBtnMoney(btnMoney200, bg200);
        Log.i("smile","pay: "+pay);
        textCash.setText((new DecimalFormat("0.00")).format(pay));
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
                pay = new BigDecimal(0);
            } else {
                //btnLastMoneyButton.setBackgroundResource(R.drawable.sel);
                btnLastMoneyButton = btnNowButton;
                //btnLastMoneyButton.setBackgroundResource(R.drawable.sel_2);
                pay = money;
            }
        }

    }

    @Override
    public void onPaySuccess() {
        openPaySuccess();
    }

    @Override
    public void onPayFail(Pay.Pay_Fail_Type failtype, String reason) {
        //ToastHelper.showToast(getContext(),reason);
        new MessageBoxDialog(getContext(),"支付失败",reason,false).show();
        LoadingDialogHelper.dismiss();
    }



}
