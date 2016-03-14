package com.itertk.app.mpos.trade.taobao;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;

import android.media.Image;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.account.InputPasswordDialog;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.dbhelper.AddressDao;
import com.itertk.app.mpos.dbhelper.Shop;
import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.utility.LinkeaRequest;
import com.itertk.app.mpos.utility.Utils;
import com.itertk.app.mpos.comm.LinkeaMsgBuilder;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.dbhelper.Address;
import com.itertk.app.mpos.utility.DataHelper;
import com.itertk.app.mpos.dbhelper.Product;
import com.itertk.app.mpos.dbhelper.ShopOrder;
import com.itertk.app.mpos.dbhelper.ShopOrderDao;
import com.itertk.app.mpos.dbhelper.ShopOrderItem;
import com.itertk.app.mpos.dbhelper.ShopOrderItemDao;
import com.itertk.app.mpos.utility.ShopOrderOperation;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 确认订单dialog
 */
public class ConfirmOrderDialog extends Dialog  implements View.OnClickListener{
    private static String TAG = "ConfirmOrderDialog";

    Context mContext ;
    Resources mResources ;
    DisplayImageOptions options;

    List<ShopOrder> mShopOrders ;
    List<List<ShopOrderItem>> mShopOrderItems = new ArrayList<List<ShopOrderItem>>() ;
    ShopOrderDao mShopOrderManager ;
    ShopOrderItemDao mShopOrderItemManager ;


    long[] mShopOrderIds ;      //= new ArrayList<Long>();
    //private BigDecimal mTotolPrice ;//= Arith.newZeroBigDecimal();

    public static String PAY_RESULT_ACTION = "com.itertk.app.mpos.trade.taobao.cardpay";
    public static String KEY_PAY_STATUS = "key_pay_status";

    PayResultReceiver mPayResultReceiver;
    private boolean mOrderEditable = true;

    public ConfirmOrderDialog(Context context, int theme, long[] shopOrderIds) {
        super(context, theme);
        mShopOrderIds = shopOrderIds;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_confirm_order);
        mContext = getContext();
        mResources = getContext().getResources();

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.item_image)
                .showImageForEmptyUri(R.drawable.item_image)
                .showImageOnFail(R.drawable.item_image)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        loadData();
        initUI();
        initBroadCastReceiver();
    }

    private void  initBroadCastReceiver(){
        mPayResultReceiver = new PayResultReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PAY_RESULT_ACTION);
        getContext().registerReceiver(mPayResultReceiver, intentFilter);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getContext().unregisterReceiver(mPayResultReceiver);
            }
        });
    }

    private void initUI(){
        String title = "订单详情";
        int visibility = View.GONE;
        if(mOrderEditable){
            title = "确认订单";
            visibility = View.VISIBLE;
        }
        ((TextView) findViewById(R.id.title)).setText(title);
        findViewById(R.id.panel_pay).setVisibility(visibility);

        ExpandableListView listView = (ExpandableListView) findViewById(R.id.listData);
        ShopCartListViewAdapter adapter = new ShopCartListViewAdapter();
        listView.setAdapter(adapter);
        listView.setGroupIndicator(null);

        for (int i = 0; i < mShopOrders.size(); i++) {
            listView.expandGroup(i);
        }
        findViewById(R.id.btn_pay).setOnClickListener(this);
        findViewById(R.id.btn_wechat).setOnClickListener(this);
        findViewById(R.id.btn_delivery).setOnClickListener(this);
        findViewById(R.id.btn_member).setOnClickListener(this);


        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    private void loadData(){
        DataHelper dbHelper =  MPosApplication.getInstance().getDataHelper();
        mShopOrderManager = dbHelper.getDaoSession().getShopOrderDao();
        mShopOrderItemManager = dbHelper.getDaoSession().getShopOrderItemDao();

        List<Long> shopOrderIdList = new ArrayList<Long>();
        for(int i = 0; i < mShopOrderIds.length; i++){
            shopOrderIdList.add(mShopOrderIds[i]);
        }
        mShopOrders = mShopOrderManager.queryBuilder().where(ShopOrderDao.Properties.Id.in(shopOrderIdList)).list();

        for(ShopOrder shopOrder : mShopOrders) {
            List<ShopOrderItem> shopOrderItems = shopOrder.getShopOrderItemList();
            mShopOrderItems.add(shopOrderItems);

            if(shopOrder.getStatus() > TradeOrderStatusEnum.WAIT_BUYER_PAY.getValue()){
                mOrderEditable = false;
            }
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId){
            case R.id.btn_pay:
            case R.id.btn_wechat:
            case R.id.btn_delivery:
            case R.id.btn_member:
                submitOrders( viewId);
                break;
            default:
                break;
        }
    }

    public class ShopCartListViewAdapter extends BaseExpandableListAdapter {
        private class GroupHolder {
            private TextView  tv_company_name;
            private TextView tv_quantity;
            private TextView tv_price;
            private EditText et_comment;

            private TextView tv_user_name;
            private TextView tv_user_phone;
            private TextView tv_user_address;
            private RelativeLayout rl_addressdetail;
            private TextView tv_orderNo;
            private TextView tv_orderDate;
            private ImageView iv_goto;
            private  int groupPosition;
        }
        private class ChildHolder {
            private ImageView  iv_product;
            private TextView tv_product;
            private TextView tv_price;
            private TextView tv_quantity;
        }

        @Override
        public int getGroupCount() {
            return mShopOrders.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mShopOrderItems.get(groupPosition).size();
        }

        @Override
        public ShopOrder getGroup(int groupPosition) {
            return mShopOrders.get(groupPosition);
        }

        @Override
        public ShopOrderItem getChild(int groupPosition, int childPosition) {
            return mShopOrderItems.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final GroupHolder groupHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_group_confirmorder , null);
                groupHolder = new GroupHolder();
                groupHolder.tv_company_name = (TextView)convertView.findViewById(R.id.tv_company_name);
                groupHolder.tv_quantity = (TextView) convertView.findViewById(R.id.tv_quantity);
                groupHolder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
                groupHolder.et_comment  = (EditText) convertView.findViewById(R.id.et_comment);

                groupHolder.tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
                groupHolder.tv_user_phone = (TextView) convertView.findViewById(R.id.tv_user_phone);
                groupHolder.tv_user_address = (TextView) convertView.findViewById(R.id.tv_user_address);
                groupHolder.rl_addressdetail = (RelativeLayout) convertView.findViewById(R.id.rl_addressdetail);
                groupHolder.tv_orderNo = (TextView) convertView.findViewById(R.id.tv_orderNo);
                groupHolder.tv_orderDate = (TextView) convertView.findViewById(R.id.tv_orderDate);
                groupHolder.iv_goto = (ImageView)convertView.findViewById(R.id.iv_goto);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }

            final ShopOrder shopOrder = getGroup(groupPosition);
            String trade_no = shopOrder.getTrade_no();
            if(TextUtils.isEmpty(trade_no)){
                trade_no = "订单待提交";
            }
            groupHolder.tv_orderNo.setText(trade_no);
            groupHolder.tv_orderDate.setText(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(shopOrder.getOrderDate()));

            groupHolder.tv_company_name.setText(shopOrder.getSupplierId());
            String quantity = String.format(mContext.getResources().getString(R.string.product_quantity_all), shopOrder.getTotalQuantity() );
            groupHolder.tv_quantity.setText(quantity);
            groupHolder.tv_price.setText(Utils.formatPrice(convertView.getContext(), shopOrder.getTotalPrice()));
            groupHolder.et_comment.setText(shopOrder.getRemark());
            groupHolder.et_comment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }
                @Override
                public void afterTextChanged(Editable editable) {
                    String comment = groupHolder.et_comment.getText().toString();
                    if (comment.equals(shopOrder.getRemark())) {
                        return;
                    } else {
                        shopOrder.setRemark(comment);
                    }
                }
            });

            if(!mOrderEditable){
                if(TextUtils.isEmpty(shopOrder.getRemark())){
                    groupHolder.et_comment.setVisibility(View.GONE);
                }else{
                    groupHolder.et_comment.setEnabled(false);
                    groupHolder.et_comment.setBackground(null);
                }

                groupHolder.iv_goto.setVisibility(View.GONE);
            }

            groupHolder.groupPosition = groupPosition;

            updateUserAddressInfo(groupHolder);

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder childHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_child_confirmorder, null);
                childHolder = new ChildHolder();
                childHolder.iv_product = (ImageView)convertView.findViewById(R.id.iv_product);
                childHolder.tv_product = (TextView)convertView.findViewById(R.id.tv_product);
                childHolder.tv_price = (TextView)convertView.findViewById(R.id.tv_price);
                childHolder.tv_quantity = (TextView)convertView.findViewById(R.id.tv_quantity);
                convertView.setTag(childHolder);
            } else {
                childHolder = (ChildHolder) convertView.getTag();
            }

            ShopOrderItem shopOrderItem  = getChild(groupPosition, childPosition);
            Product product = shopOrderItem.getProduct();
            childHolder.tv_product.setText(shopOrderItem.getProduct().getProd_name());
            ImageLoader.getInstance().displayImage(product.getImage_url(),childHolder.iv_product);

            childHolder.tv_price.setText(Utils.formatPrice(convertView.getContext(), shopOrderItem.getPrice()));
            childHolder.tv_quantity.setText(String.format(mResources.getString(R.string.product_quantity_single), shopOrderItem.getQuantity()));

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return false;
        }

        private void updateUserAddressInfo(final GroupHolder groupHolder){
             ShopOrder shopOrder = getGroup(groupHolder.groupPosition);
            final MPosApplication mPosApplication = MPosApplication.getInstance();

            LinkeaResponseMsg.LoginResponseMsg msg = mPosApplication.getMember();
            String userName = msg.member.member_name;
            String phone = msg.member.phone;
            final String address ;//= Utils.getConsignAddress();
            if(mShopOrders.size() > 0){
                address = mShopOrders.get(0).getConsigneeAddress();
            }else{
                address = Utils.getConsignAddress();
            }
            groupHolder.tv_user_name.setText(String.format(mContext.getResources().getString(R.string.user_info_name), userName ));
            groupHolder.tv_user_phone.setText(String.format(mContext.getResources().getString(R.string.user_info_phone), phone ));
            groupHolder.tv_user_address.setText(String.format(mContext.getResources().getString(R.string.user_info_adrees), address));

            if(mOrderEditable) {
                groupHolder.rl_addressdetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String address ;//= Utils.getConsignAddress();
                        if(mShopOrders.size() > 0){
                            address = mShopOrders.get(0).getConsigneeAddress();
                        }else{
                            address = Utils.getConsignAddress();
                        }

                        final AddressDialog adialog = new AddressDialog(getContext(), R.style.MyDialog, address);
                        adialog.show();
                        adialog.setOnDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                String addr = adialog.getmUseAddress();
                                if (addr == null) {
                                    return;
                                }
                                ShopOrder shopOrder = mShopOrders.get(0);
                                shopOrder.setConsigneeAddress(addr);
                                shopOrder.update();
                                groupHolder.tv_user_address.setText(String.format(mContext.getResources().getString(R.string.user_info_adrees), addr));
                            }
                        });
                    }
                });
            }
        }
    }

    private void submitOrders( final int viewId){
            final   LoadingDialog  loadingDialog = new LoadingDialog(getContext(),R.style.MyDialog, "会员支付中");
            loadingDialog.show();

           final MPosApplication mPosApplication = MPosApplication.getInstance();
            String biz_code = "1008";
            String summery="订货订单-" +  mPosApplication.getMember().member.member_id;
           if(viewId == R.id.btn_delivery){
               summery="COD-" +  mPosApplication.getMember().member.member_id;
           }
           LinkeaMsgBuilder.ShopOrderDetail  order_detail = getOrderDetailJson();
           String amount  = order_detail.amount;

            mPosApplication.getMsgBuilder().buildCreateOrderMsg2(amount,biz_code,summery,order_detail.toString()).send(new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    loadingDialog.dismiss();
                    Toast.makeText(getContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, throwable.toString());
                    return;
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d(TAG,responseString);
                    loadingDialog.dismiss();
                    try {
                        LinkeaResponseMsg.CreateOrderResponseMsg msg =
                                LinkeaResponseMsgGenerator.generateCreateOrderResponseMsg(responseString);
                        if (msg.success) {
                            //only one order could pay
                            final ShopOrder shopOrder = mShopOrders.get(0);
                            shopOrder.setTrade_no(msg.order.trade_no);
                            shopOrder.setStatus(TradeOrderStatusEnum.WAIT_BUYER_PAY.getValue());
                            shopOrder.setOrderDate(new Date());
                            shopOrder.update();
                            payShopOrder( viewId,   shopOrder);
                        } else {
                            String erroInfo = msg.result_code_msg;
                            Toast.makeText(getContext(), erroInfo, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, erroInfo);
                        }

                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                }
            });
        }

    private void payShopOrder(int viewId, final ShopOrder shopOrder){
        final LinkeaRequest.OnRequestResultListener  listener = new LinkeaRequest.OnRequestResultListener() {
            @Override
            public void onSuccess() {
                postPaySuccess(shopOrder,"支付成功");
            }
            @Override
            public void onFailure() {
                MessageBoxDialog messageBox = new MessageBoxDialog(getContext(),"支付失败",false);
                messageBox.show();
            }
        };

        //支付订单
        switch (viewId) {
            case R.id.btn_delivery:
                postPaySuccess(shopOrder,"下单成功");
                break;
            case  R.id.btn_pay:
                LinkeaRequest.onCardPay(getContext(),shopOrder.getTrade_no(),shopOrder.getTotalPrice(), shopOrder.getId());
                break;
            case R.id.btn_wechat:
                LinkeaRequest.onWechatPay(getContext(),shopOrder.getTrade_no(),shopOrder.getTotalPrice(),"微信支付", listener);
                break;
            case R.id.btn_member:
                LinkeaRequest.onMemberPay(getContext(), shopOrder.getTrade_no(), listener);
                break;
            default:
                break;
        }
    }


    private  LinkeaMsgBuilder.ShopOrderDetail  getOrderDetailJson() {
        LinkeaMsgBuilder.ShopOrderDetail shopOrderDetail = new LinkeaMsgBuilder.ShopOrderDetail();
        shopOrderDetail.supplier = new ArrayList<LinkeaMsgBuilder.ShopOrderDetail.Supplier>();

        BigDecimal totolPrice = Arith.newZeroBigDecimal();

        for(ShopOrder shopOrder : mShopOrders){
            LinkeaMsgBuilder.ShopOrderDetail.Supplier supplier  = new LinkeaMsgBuilder.ShopOrderDetail.Supplier();
            supplier.supplierId = shopOrder.getSupplierId();
            supplier.orderDate = Utils.formatDate();
            supplier.consigneeName = shopOrder.getConsigneeName();
            supplier.consigneeMobile = shopOrder.getConsigneePhone() ;
            supplier.consigneePhone =   shopOrder.getConsigneePhone() ;
            supplier.consigneeAddress =  shopOrder.getConsigneeAddress();
            supplier.paymentName = shopOrder.getPaymentName();
            supplier.transferName =  shopOrder.getTransferName();
            supplier.totalPrice =  shopOrder.getTotalPrice();
            supplier.remark = shopOrder.getRemark();

            totolPrice = totolPrice.add(Arith.newBigDecimal(supplier.totalPrice));
            supplier.items = new ArrayList<LinkeaMsgBuilder.ShopOrderDetail.Item>();
            List<ShopOrderItem> shopOrderItems = shopOrder.getShopOrderItemList();
            for(ShopOrderItem shopOrderItem : shopOrderItems){
                LinkeaMsgBuilder.ShopOrderDetail.Item item = new LinkeaMsgBuilder.ShopOrderDetail.Item();
                item.productID =  shopOrderItem.getProductId();
                item.productName = shopOrderItem.getProduct().getProd_name();
                item.price =  shopOrderItem.getPrice();
                item.quantity =  shopOrderItem.getQuantity();
                item.totalPrice = shopOrderItem.getTotalPrice();
                supplier.items.add(item);
            }
            shopOrderDetail.supplier.add(supplier);
        }

        shopOrderDetail.amount = String.valueOf(totolPrice);

        Log.v(TAG,shopOrderDetail.toString());
        return shopOrderDetail;
    }



    private void postPaySuccess(ShopOrder shopOrder, String message ){
        shopOrder.setStatus(TradeOrderStatusEnum.WAIT_SELLER_SEND_GOODS.getValue());
        shopOrder.update();
//        ShoppingOrderDialog shoppingOrderDialog = new ShoppingOrderDialog(getContext(),R.style.MyDialog, ShopOrderOperation.PAY_STATUS_PAYED);
//        shoppingOrderDialog.show();
        MessageBoxDialog messageBox = new MessageBoxDialog(getContext(), message,true);
        messageBox.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ConfirmOrderDialog.this.dismiss();
            }
        });
        messageBox.show();
    }

    private class PayResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            Intent intent = arg1;
            if (intent.getAction().equals(PAY_RESULT_ACTION)) {
                Boolean success = intent.getBooleanExtra(KEY_PAY_STATUS, false);
                if (success) {
                    postPaySuccess(mShopOrders.get(0),"支付成功");
                }else{
                    MessageBoxDialog messageBox = new MessageBoxDialog(getContext(), "支付失败",false);
                    messageBox.show();
                }
            }
        }
    }

}
