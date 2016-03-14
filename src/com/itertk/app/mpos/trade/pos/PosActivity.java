package com.itertk.app.mpos.trade.pos;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.ConfirmDialog;
import com.itertk.app.mpos.utility.ToastHelper;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.dbhelper.Product;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.dbhelper.SaleOrderItem;
import com.itertk.app.mpos.utility.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 收银
 */
public class PosActivity extends Activity implements View.OnClickListener,CheckoutFragment.OnFragmentInteractionListener,NewSaleOrderAdapter.OnChangeOrderItemnListener{
    public  static final String UPDATE_TOTAL_PRICE ="updatetotalprice";
    static final String TAG = "PosActivity";
    LinearLayout btnProduct;
    LinearLayout btnManual;
    Button btnClear = null;
    Button btnPay = null;
    //Smile_gao add for cash pay ; card pay ; ali pay ; weixin pay
    Button mBtnCashPay = null;
    Button mBtnCardPay = null;
    Button mBtnAliPay = null;
    Button mBtnWeixinPay = null;

    TextView mtextYingshou = null;
    TextView mtextYouhui = null;
    TextView mtextZhekou = null;
    TextView mtextShishou = null;

    RelativeLayout youhuiLyaout= null;

 //   SlideDeleteListView saleOrderListView = null;
 NewSaleOrderAdapter saleOrderAdapter = null;

  //  SaleCatalogListFragment saleCatalogListFragment;
  //  SaleProductListFragment saleProductListFragment;
    SaleManualFragment saleManualFragment;
    //smile_gao
   CheckoutFragment cartFragment;
    //end
    FragmentTransaction transaction;

    EditText textInputGun;
    String scanCode;
    //smile_gao handler message
   public final static int SET_SCANCODE = 1001;
    public final static int SELECTION_LISTVIEW = 1002;
    public final static int CLEAR_FOCUS = 1003;

    public final static int HIDE_IME = 1004;

   public final static String SCANCODE_KEY = "scancode";
   // private long mSaleOrderId = -1;
    private Button mSearchButton;
    private EditText mInputSearchWord;
    private RelativeLayout mInputsnlayout;
    private  NotFindProductDialog mNotfindProductDlg;
   final android.os.Handler mHandler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case SET_SCANCODE:

                    String scancode =  msg.getData().getString(SCANCODE_KEY);
                    Log.i("smile","SET_SCANCODE "+scancode);
                    cartFragment.setSn(scancode);
                    break;
                case SELECTION_LISTVIEW:
                    Log.i("smile","SELECTION_LISTVIEW   "+msg.arg1);
                    cartFragment.setListSelection(msg.arg1);
                    break;
                case CLEAR_FOCUS:
                    mInputSearchWord.clearFocus();
                    break;
            }
        }
    };

    public void delSaleItem(int position) {
        saleOrderAdapter.delSaleOrderItem(position);
        showTotal(saleOrderAdapter.getTotalPrice());
    }
    public void showTotal(BigDecimal totalprice)
    {
        saleOrderAdapter.getSaleOrder().setPrice(totalprice.toString());
        mtextYingshou.setText("￥" + DecimalHelper.getDecimalString(saleOrderAdapter.getSaleOrder().getPrice()));
        mtextShishou.setText("￥" + DecimalHelper.getDecimalString(saleOrderAdapter.getSaleOrder().getShihouPrice()));
    }

    public void showYouhui(BigDecimal totalprice)
    {
       mtextYouhui.setText("￥" + DecimalHelper.getDecimalString(totalprice));
        showTotal(saleOrderAdapter.getTotalPrice());
    }

    public void modSaleOrderItem(SaleOrderItem saleOrderItem) {
        saleOrderAdapter.modSaleOrderItem(saleOrderItem);
       // btnPay.setText("开始结算 ￥" + (new DecimalFormat("0.00")).format(saleOrderAdapter.getTotalPrice()));
        showTotal(saleOrderAdapter.getTotalPrice());
    }
    public void addManualItem(Product product) {
        saleOrderAdapter.addSaleOrderItemByProduct(product);
        Log.i("smile", "addmanulitem: " + DecimalHelper.getDecimalString(saleOrderAdapter.getTotalPrice()));
        showTotal(saleOrderAdapter.getTotalPrice());
        // btnPay.setText("开始结算 ￥" + (new DecimalFormat("0.00")).format(saleOrderAdapter.getTotalPrice()));
    }
    public void addManualItem(String money) {
        if(Arith.newBigDecimal(money).compareTo(Arith.newZeroBigDecimal())<=0)
        {
            return;
        }
        saleOrderAdapter.addSaleOrderItemByPrice(money);
        Log.i("smile", "addmanulitem: " + DecimalHelper.getDecimalString(saleOrderAdapter.getTotalPrice()));
        showTotal(saleOrderAdapter.getTotalPrice());
       // btnPay.setText("开始结算 ￥" + (new DecimalFormat("0.00")).format(saleOrderAdapter.getTotalPrice()));
    }

    public void onSaleProductItemClick(Product product) {
        saleOrderAdapter.addSaleOrderItem(product);
        showTotal(saleOrderAdapter.getTotalPrice());
       // btnPay.setText("开始结算 ￥" + (new DecimalFormat("0.00")).format(saleOrderAdapter.getTotalPrice()));
    }




    @Override
    public void onFragmentInteraction(Uri uri) {

    }



    static class ViewHold {
        ImageView imageView;
        TextView textProduct;
        TextView textCount;
        TextView textPrice;
    }

    private void onbtnProduct() {
        mInputsnlayout.setVisibility(View.VISIBLE);
        btnProduct.setSelected(true);
        btnManual.setSelected(false);

//        if (null == saleCatalogListFragment) {
//            saleCatalogListFragment = new SaleCatalogListFragment();
//        }

        if(null ==  cartFragment)
        {
            cartFragment = new CheckoutFragment();
        }

        transaction = getFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_from_right);
        //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.pos_container, cartFragment);
        transaction.commit();
    }
    public  NewSaleOrderAdapter getSaleAdapter()
    {
            return saleOrderAdapter;
    }
    private void onbtnManual() {
        mInputsnlayout.setVisibility(View.GONE);
        btnManual.setSelected(true);
        btnProduct.setSelected(false);

        if (null == saleManualFragment) {
            saleManualFragment = new SaleManualFragment();
        }
        transaction = getFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_from_right);
        //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.pos_container, saleManualFragment);
        transaction.commit();
    }


    private void onbtnClear() {
        ConfirmDialog confirmDialog = new ConfirmDialog(this, R.style.MyDialog, "清空购物车？");
        confirmDialog.setOnClickButtonOKListener(new ConfirmDialog.OnClickButtonOKListener() {
            @Override
            public void onClickButtonOK() {
                saleOrderAdapter.clearSaleOrderItemList();
                btnPay.setText("开始结算 ￥" + (new DecimalFormat("0.00")).format(saleOrderAdapter.getTotalPrice()));
            }
        });
        confirmDialog.show();
    }
    public  void updateTotalPrice()
    {
        showTotal(saleOrderAdapter.getTotalPrice());
    }
    private void onbtnPay() {
        long saleOrderId = saleOrderAdapter.saveOrder();
     //   Intent it = new Intent(PosActivity.this, PayActivity.class);
     //   it.putExtra("orderId", String.valueOf(saleOrderId));
     //   startActivity(it);
     //   overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnProduct:
                onbtnProduct();
                break;
            case R.id.btnManual:
                onbtnManual();
                break;
            case R.id.btnClear:
                onbtnClear();
                break;
            case R.id.btnPay:
                onbtnPay();
                break;
            case R.id.cashpay:
                onCashPay();
                break;
            case R.id.cardpay:
                onCardPay();
                break;
            case R.id.alipay:
                onAliPay();
                break;
            case R.id.weixinpay:
                onWeixinPay();
                break;
            case R.id.btnproductsearch:
                doSearchProduct();//saleOrderAdapter.addSaleOrderItemByProductName(mInputSearchWord.getText().toString().trim());
                break;
        }
    }
    private  void doSearchProduct()
    {
        String words = mInputSearchWord.getText().toString().trim();
        if(words.equals("")||words.length()==0)
        {
            ToastHelper.showToast(this,"查询不能为空");
            return;
        }
       List<Product> list =  saleOrderAdapter.getSaleOrderItemListByProductName(words);
        if(list == null || list.size() ==0)
        {
            ToastHelper.showToast(this,"没有找到商品");
            return;
        }
       final   SearchProductListDialog dlg = new SearchProductListDialog(this, R.style.MyDialog,list);
        dlg.show();
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mInputSearchWord.setText("");
                Message msg = mHandler.obtainMessage(CLEAR_FOCUS);
                mHandler.sendMessage(msg);
                if(dlg.getSelectProduct() == null)
                {
                    return;
                }
               int index = saleOrderAdapter.addSaleOrderItem(dlg.getSelectProduct());
                if(index ==-1)
                {
                    return;
                }
                updateTotalPrice();
               msg = mHandler.obtainMessage(SELECTION_LISTVIEW);
                msg.arg1 = index;
                mHandler.sendMessage(msg);
                mInputSearchWord.setText("");
            }
        });



    }
    private  boolean priceMoreThanZero()
    {
        if(getSaleOrder().getShihouPrice().compareTo(Arith.newZeroBigDecimal())<=0)
        {
            ToastHelper.showToast(this, "金额必须大于零");
            return false;
        }
        return  true;
    }
    private  void onCashPay()
    {
        if(!priceMoreThanZero())
        {
            return;
        }

//        PrintHelper.printExternalOrder(this,saleOrder);
//        Log.i("smile","cash ok");

        long saleOrderId = saleOrderAdapter.saveOrder();
        SaleOrder saleOrder = saleOrderAdapter.getSaleOrder();
//        Intent intent = new Intent(PosActivity.this,CashActivity.class);
//        startActivity(intent);
       CashDialog dlg = new CashDialog(this, R.style.MyDialog,saleOrder);
       // dlg.setCanceledOnTouchOutside(false);
        dlg.show();
    }
    private SaleOrder getSaleOrder()
    {
          return saleOrderAdapter.getSaleOrder();
    }

    private void onCardPay()
    {
        if(!Utils.isMemberLogin())
        {
            ToastHelper.showToast(this,"请登陆后再使用刷卡支付");
            return;
        }
        if(!priceMoreThanZero())
        {
            return;
        }
        getSaleAdapter().saveOrder();
        //getSaleOrder();
        final PosDialog posDialog = new PosDialog(this, R.style.MyDialog, getSaleOrder().getShihouPrice());
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

                LoadingDialogHelper.show(PosActivity.this);
                MPosApplication.getInstance().getMsgBuilder().buildCreateOrderMsg2("" + getSaleAdapter().getSaleOrder().getShihouPrice(), "1009", "支付-" + MPosApplication.getInstance().getMember().member.member_id,UpLoadOrderHelper.genChoutoutOrderToJson(getSaleAdapter().getSaleOrder())
                ).send(new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d(TAG, throwable.toString());
                        Toast.makeText(getApplicationContext(), "创建订单失败", Toast.LENGTH_SHORT).show();
                        LoadingDialogHelper.dismiss();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, responseString);
                        try {
                            LinkeaResponseMsg.CreateOrderResponseMsg createOrderResponseMsg =
                                    LinkeaResponseMsgGenerator.generateCreateOrderResponseMsg(responseString);
                            if (createOrderResponseMsg.success) {
                                saleOrderAdapter.getSaleOrder().setSaleOrderNo(createOrderResponseMsg.order.trade_no);
                                saleOrderAdapter.saveOrder();

                                final Intent it = new Intent(PosActivity.this, CustomerSignActivity.class);
                                it.putExtra("orderId", String.valueOf(getSaleAdapter().getSaleOrder().getSaleOrderId()));
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("card", posDialog.cardInfo);
                                it.putExtras(bundle);
                                startActivity(it);

                            }else {
                                String erroInfo = createOrderResponseMsg.result_code_msg;
                                Toast.makeText(getApplicationContext(), erroInfo, Toast.LENGTH_SHORT).show();
                                Log.e(TAG, erroInfo);
                            }

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                        }
                        LoadingDialogHelper.dismiss();
                    }
                });



            }
        });
        posDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MPosApplication.getInstance().getMyActivityManager().setBackToActivityClass(PosActivity.class);
    }

    private  void onAliPay()
    {
        if(!priceMoreThanZero())
        {
            return;
        }
    }
    private void onWeixinPay()
    {
        if(!Utils.isMemberLogin())
        {
            ToastHelper.showToast(this,"请登陆后再使用微信支付");
            return;
        }
        if(!priceMoreThanZero())
        {
            return;
        }
        long saleOrderId = saleOrderAdapter.saveOrder();
        SaleOrder saleOrder = saleOrderAdapter.getSaleOrder();
        WeixinPayDialog weixindlg  = new WeixinPayDialog(this,R.style.MyWeixinDialog ,saleOrder);
        weixindlg.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);
        MyActionbar.setNormalActionBarLayout(this);
        ((MPosApplication)getApplication()).getMyActivityManager().push(this);
        Intent intent = this.getIntent();
        String orderId = intent.getStringExtra("orderId");
        mInputsnlayout = (RelativeLayout) findViewById(R.id.inputsnlayout);
        scanCode = new String();
        mInputSearchWord = (EditText)findViewById(R.id.inputsn);
        textInputGun = (EditText) findViewById(R.id.textInputGun);
//        mInputSearchWord.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                return getScancode(event);
//            }
//        });


        btnProduct = (LinearLayout) findViewById(R.id.btnProduct);
        btnProduct.setOnClickListener(this);

        btnManual = (LinearLayout) findViewById(R.id.btnManual);
        btnManual.setOnClickListener(this);

      //  saleOrderListView = (SlideDeleteListView) findViewById(R.id.payList);
        saleOrderAdapter = new NewSaleOrderAdapter(this,null,this);
      //  saleOrderListView.setAdapter(saleOrderAdapter);


        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        btnPay = (Button) findViewById(R.id.btnPay);
        btnPay.setOnClickListener(this);
        //smile_gao add for 4 pay button
        mBtnCashPay = (Button) findViewById(R.id.cashpay);
        mBtnCashPay.setOnClickListener(this);
        mBtnCardPay = (Button) findViewById(R.id.cardpay);
        mBtnCardPay.setOnClickListener(this);
        mBtnAliPay = (Button) findViewById(R.id.alipay);
        mBtnAliPay.setOnClickListener(this);
        mBtnWeixinPay = (Button) findViewById(R.id.weixinpay);
        mBtnWeixinPay.setOnClickListener(this);

        mtextYingshou = (TextView) findViewById(R.id.yingshoutotal);
        mtextYouhui = (TextView) findViewById(R.id.youhuitotal);
        mtextZhekou = (TextView) findViewById(R.id.zhekoutotal);
        mtextShishou = (TextView) findViewById(R.id.shishoutotal);

      //  onbtnManual();
        onbtnProduct();
        btnPay.setText("开始结算 ￥" + DecimalHelper.getDecimalString(saleOrderAdapter.getTotalPrice()));


        mSearchButton = (Button)findViewById(R.id.btnproductsearch);
        mSearchButton.setOnClickListener(this);

        youhuiLyaout= (RelativeLayout) findViewById(R.id.youhuilayout);
        youhuiLyaout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final YouhuiDialog dlg = new YouhuiDialog(PosActivity.this,R.style.MyDialog,getSaleAdapter().getReduce(),saleOrderAdapter.getTotalPrice());
                dlg.show();
                dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        BigDecimal youhuiprice= dlg.getYouhuiprice();
                        if(youhuiprice!=null&&youhuiprice.compareTo(Arith.newZeroBigDecimal())>0) {
                            saleOrderAdapter.addReduce(youhuiprice);
                            showYouhui(youhuiprice);
                        }
                        hideIme();
//                        Message msg = Message.obtain(null, PosActivity.HIDE_IME, 0, 0);
//                        mHandler.sendMessage(msg);
                    }
                });
            }
        });


    }
    void hideIme()
    {
        try {

            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        }catch(Exception e)
        {

        }
    }
    private boolean getScancode(KeyEvent event) {
        Log.d("textInput", "smile " + scanCode);

        if (event.getDevice().getName().contains("HID")) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                Log.d("textInput", "getDevice Name:  "+event.getDevice().getName()+" num: "+event.getNumber()+" scancode: "+event.getScanCode()+" chars: "+event.getCharacters()+" kecode: "+event.getKeyCode());
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    Log.d("textInput", scanCode);
                 //   ToastHelper.showToast(this,scanCode);
                    if(btnManual.isSelected())
                    {
                        onbtnProduct();
                    }
                  final int ret = saleOrderAdapter.addSaleOrderItem(scanCode);
                    if(mNotfindProductDlg!=null && mNotfindProductDlg.isShowing())
                    {
                        mNotfindProductDlg.dismiss();
                    }
                    if(ret ==-1)
                    {
                        mNotfindProductDlg = new NotFindProductDialog(this, R.style.MyDialog,scanCode);
                        mNotfindProductDlg.show();
                        mNotfindProductDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                Product p = mNotfindProductDlg.getNewProduct();
                                if(p == null)
                                {
                                    return;
                                }
                                addManualItem(p);

                            }
                        });
                        scanCode = "";
                        return true;
                    }
                    updateTotalPrice();
                 //   btnPay.setText("开始结算 ￥" + (new DecimalFormat("0.00")).format(saleOrderAdapter.getTotalPrice()));
                    Message msg = Message.obtain(null, PosActivity.SELECTION_LISTVIEW, 0, 0);
                    msg.arg1 = ret;
//                            Bundle bd = new Bundle();
//                            bd.putString(PosActivity.SELECTION_LISTVIEW,scanCode);
//                            msg.setData(bd);
                    mHandler.sendMessage(msg);
                    scanCode = "";
                } else {
                    if(event.getScanCode()<10)
                        scanCode += event.getNumber();
                    else
                    {
                        scanCode += (char) event.getUnicodeChar();
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onChangeItem(String pams,int position) {
        if(pams.equals("del"))
        {
            delSaleItem(position);
        }
        else if(pams.equals("sub")||pams.equals("add"))
        {
            updateTotalPrice();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.pos, menu);
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
        Log.d(TAG, "on destroy");
        saleOrderAdapter.cancelImcompleteOrder();

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "" + keyCode + " " + event.toString());
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
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d("dispatchKeyEvent", event.toString());
        getScancode(event);
        return super.dispatchKeyEvent(event);
    }
//
//    @Override
//    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
//        Log.d("dispatchKeyShortcutEvent", event.toString());
//        if(event.getDeviceId() == 3) return false;
//        return super.dispatchKeyShortcutEvent(event);
//    }
}
