package com.itertk.app.mpos.trade.taobao;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.utility.Utils;
import com.itertk.app.mpos.utility.DataHelper;
import com.itertk.app.mpos.dbhelper.ShopOrder;
import com.itertk.app.mpos.dbhelper.ShopOrderDao;
import com.itertk.app.mpos.dbhelper.ShopOrderItem;
import com.itertk.app.mpos.dbhelper.ShopOrderItemDao;
import com.itertk.app.mpos.utility.ShopOrderOperation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 订货订单dialog
 */

public class ShoppingOrderDialog extends Dialog implements View.OnClickListener{
    private static String TAG = "ShoppingOrderDialog";
    Context context;
    DisplayImageOptions options;

    List<ShopOrder> mShopOrders = new ArrayList<ShopOrder>() ;
    List<List<ShopOrderItem>> mShopOrderItems = new ArrayList<List<ShopOrderItem>>() ;
    ShopOrderDao mShopOrderManager ;
    ShopOrderItemDao  mShopOrderItemManager ;

    View.OnClickListener mOnClickListener;
    ShopCartListViewAdapter mAdapter;


    private int[] mArrStatusComplete  = new int[]{
                    TradeOrderStatusEnum.TRADE_CLOSED_BY_LINKEA.getValue(),
                    TradeOrderStatusEnum.TRADE_CLOSED.getValue(),
                    TradeOrderStatusEnum.TRADE_FINISHED.getValue()
     };

    private int[] mArrStatusPayed  = new int[]{
                    TradeOrderStatusEnum.WAIT_SELLER_SEND_GOODS.getValue(),
                    TradeOrderStatusEnum.WAIT_BUYER_CONFIRM_GOODS.getValue()
     };

    private int[] mArrStatusUnpay  = new int[]{
                    TradeOrderStatusEnum.BUYER_PAY_FAILURE.getValue(),
                    TradeOrderStatusEnum.WAIT_BUYER_PAY.getValue()
     };


    private List<LinearLayout> mCategoryBtnHolders = new ArrayList<LinearLayout>();
    private  int mPayStatusViewId ; //0, completed, 1, unpay, 2, payed

    public ShoppingOrderDialog(Context context, int theme,int payStatusViewId) {
        super(context, theme);
        this.context = context;
        mOnClickListener = this;

        mPayStatusViewId = payStatusViewId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_shop_order);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.item_image)
                .showImageForEmptyUri(R.drawable.item_image)
                .showImageOnFail(R.drawable.item_image)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        loadData(mPayStatusViewId);
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.listData);
        findViewById(R.id.btnBack).setOnClickListener(this);

        mAdapter = new ShopCartListViewAdapter();
        listView.setAdapter(mAdapter);
        listView.setGroupIndicator(null);

        for(int i = 0; i < mShopOrders.size(); i++){
            listView.expandGroup(i);
        }

        initCategoryBtnHolder();
        int viewId = mPayStatusViewId;
        updateCategoryBtnHolder(mPayStatusViewId);
    }

    private void initCategoryBtnHolder(){
        int[] btnIds = new int[]{R.id.order_category_complete,R.id.order_category_unpay,R.id.order_category_payed};
        String[] descs = new String[]{"已完成","待付款","待收货"};
        for(int i = 0; i < btnIds.length; i++) {
            int viewId = btnIds[i];
            LinearLayout category = (LinearLayout) findViewById(viewId);
            category.setOnClickListener(this);
            mCategoryBtnHolders.add(category);

            TextView tv_desc = (TextView)category.findViewById(R.id.tv_desc);
            tv_desc.setText(descs[i]);

            if(viewId == R.id.order_category_complete){
                category.findViewById(R.id.tv_indicator).setVisibility(View.GONE);
            }
        }
    }

    private void updateCategoryBtnHolder(int viewId){
        List<Integer> unpayCatelogList = new ArrayList<Integer>() ;
        List<Integer> payedCatelogList = new ArrayList<Integer>() ;
        for(int item : mArrStatusUnpay){
            unpayCatelogList.add(item);
        }
        for(int item : mArrStatusPayed){
            payedCatelogList.add(item);
        }

        long unpayedCount =  mShopOrderManager.queryBuilder().where(ShopOrderDao.Properties.Status.in(unpayCatelogList)).buildCount().count();
        long payedCount =  mShopOrderManager.queryBuilder().where(ShopOrderDao.Properties.Status.in(payedCatelogList)).buildCount().count();
        long[] counts = new long[]{0L, unpayedCount,payedCount };

        for(int i = 0; i < mCategoryBtnHolders.size(); i++) {
            LinearLayout category = mCategoryBtnHolders.get(i);
            int colorId = R.color.text_normal_color;
            int visibility = View.INVISIBLE;
            if( category.getId() == viewId) {
                colorId = R.color.solid_red ;
                visibility = View.VISIBLE;
            }

            TextView tv_desc = (TextView)category.findViewById(R.id.tv_desc);
            tv_desc.setTextColor(getContext().getResources().getColor(colorId));
            TextView tv_indicator = (TextView)category.findViewById(R.id.tv_indicator);
            if(counts[i] == 0){
                tv_indicator.setVisibility(View.INVISIBLE);
            }else {
                String indicator = String.valueOf(counts[i]);
                if (counts[i] > 99) {
                    indicator = "99+";
                }
                tv_indicator.setText(indicator);
                tv_indicator.setVisibility(View.VISIBLE);
            }

            View line = category.findViewById(R.id.line);
            line.setVisibility(visibility);
        }
    }

    private void loadData(int payStatusViewId){
        DataHelper dbHelper =  MPosApplication.getInstance().getDataHelper();
        mShopOrderManager = dbHelper.getDaoSession().getShopOrderDao();
        mShopOrderItemManager = dbHelper.getDaoSession().getShopOrderItemDao();

        mShopOrders.clear();
        mShopOrderItems.clear();

        int[] paystatus ;//= new int[]{TradeOrderStatusEnum.BUYER_PAY_FAILURE.getValue(),TradeOrderStatusEnum.WAIT_BUYER_PAY.getValue()};


        switch (payStatusViewId){
            case R.id.order_category_complete:
                paystatus = new int[]{
                        TradeOrderStatusEnum.TRADE_CLOSED_BY_LINKEA.getValue(),
                        TradeOrderStatusEnum.TRADE_CLOSED.getValue(),
                        TradeOrderStatusEnum.TRADE_FINISHED.getValue()
                };
                break;
            case R.id.order_category_payed:
                paystatus = new int[]{
                        TradeOrderStatusEnum.WAIT_SELLER_SEND_GOODS.getValue(),
                        TradeOrderStatusEnum.WAIT_BUYER_CONFIRM_GOODS.getValue()
                };

                break;
            case R.id.order_category_unpay:
            default:
                paystatus = new int[]{
                        TradeOrderStatusEnum.BUYER_PAY_FAILURE.getValue(),
                        TradeOrderStatusEnum.WAIT_BUYER_PAY.getValue()
                };
                break;
        }
        List<Integer> statusList = new ArrayList<Integer>();
        for(int status: paystatus){
            statusList.add(status );
        }
        mShopOrders = mShopOrderManager.queryBuilder().where(ShopOrderDao.Properties.Status.in(statusList)).orderDesc(ShopOrderDao.Properties.OrderDate).list();
        mShopOrderItems.clear();
        for(ShopOrder shopOrder : mShopOrders) {
            List<ShopOrderItem> shopOrderItems = shopOrder.getShopOrderItemList();
            mShopOrderItems.add(shopOrderItems);
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId){
            case R.id.btnBack:
                dismiss();
                break;
            case R.id.btn_delete:
                mPayStatusViewId = R.id.order_category_complete;
                onBtnDelete(view);
                break;
            case R.id.btn_cancelorder:
                mPayStatusViewId = R.id.order_category_unpay;
                onBtnDelete(view);
                break;
            case R.id.btn_payorder:
                onBtnPayOrder(view);
                break;
            case R.id.order_category_payed:
            case R.id.order_category_unpay:
            case R.id.order_category_complete:
                updateCategoryBtnHolder(viewId);
                onBtnCategoryClicked(viewId);
                break;
            default:
                    break;
        }
    }

    private void onBtnCategoryClicked(int category_id){
        loadData(category_id);
        mAdapter.notifyDataSetChanged();
    }

    private void onBtnDelete(View view){
        int position = (Integer) view.getTag();
        ShopOrder shopOrder = mShopOrders.get(position);
        ShopOrderOperation operation = ShopOrderOperation.getInstance();
        operation.cancelShopOrder(getContext(),shopOrder, mAdapter);
    }

    private void onBtnPayOrder(View view){
         int position = (Integer)view.getTag();
        ShopOrder shopOrder = mShopOrders.get(position);
//        List<Long> shopOrderIds = new ArrayList<Long>();
//        shopOrderIds.add(shopOrder.getId());
//        confirmOrderDialog.setShopOrderIds(shopOrderIds);

        long[] shopOrderIds = new long[]{shopOrder.getId()};

        ConfirmOrderDialog confirmOrderDialog  = new ConfirmOrderDialog(getContext(),R.style.MyDialog,shopOrderIds);
        confirmOrderDialog.show();
        confirmOrderDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                loadData(mPayStatusViewId);
                updateCategoryBtnHolder(mPayStatusViewId);
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    public class ShopCartListViewAdapter extends BaseExpandableListAdapter {
        private Context mContext;
        private class GroupHolder {
            private TextView  tv_company_name;
            private LinearLayout btn_delete;
            private TextView tv_quantity;
            private TextView tv_totalPrice;
            private Button btn_payOrder;
            private Button btn_cancelOrder;
            private  TextView tv_orderDate;
        }

        private class ChildHolder {
            private ImageView  iv_product;
            private TextView tv_product;
            private TextView tv_money;
            private TextView tv_quantity;
        }

        public void deleteShopOrder(ShopOrder shopOrder){
            shopOrder.delete();
            mShopOrderItemManager.deleteInTx(shopOrder.getShopOrderItemList());

            loadData(mPayStatusViewId);
            notifyDataSetChanged();
            updateCategoryBtnHolder(mPayStatusViewId);
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
            // TODO Auto-generated method stub
            return mShopOrders.get(groupPosition);
        }

        @Override
        public ShopOrderItem getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return mShopOrderItems.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            // TODO Auto-generated method stub
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            mContext = parent.getContext();
            GroupHolder mGroupHolder ;
            ShopOrder shopOrder = getGroup(groupPosition);

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_group_shoporder, null);
                mGroupHolder = new GroupHolder();
                mGroupHolder.tv_company_name = (TextView)convertView.findViewById(R.id.tv_company_name);
                mGroupHolder.btn_delete = (LinearLayout) convertView.findViewById(R.id.btn_delete);
                mGroupHolder.tv_quantity = (TextView)convertView.findViewById(R.id.tv_quantity);
                mGroupHolder.tv_totalPrice = (TextView)convertView.findViewById(R.id.tv_totalPrice);
                mGroupHolder.btn_payOrder = (Button)convertView.findViewById(R.id.btn_payorder);
                mGroupHolder.btn_cancelOrder = (Button)convertView.findViewById(R.id.btn_cancelorder);
                mGroupHolder.tv_orderDate = (TextView) convertView.findViewById(R.id.tv_orderDate);
                convertView.setTag(mGroupHolder);
            } else {
                mGroupHolder = (GroupHolder) convertView.getTag();
            }

            mGroupHolder.btn_delete.setOnClickListener(mOnClickListener);
            mGroupHolder.btn_delete.setTag(groupPosition);

            mGroupHolder.btn_payOrder.setOnClickListener(mOnClickListener);
            mGroupHolder.btn_payOrder.setTag(groupPosition);

            mGroupHolder.btn_cancelOrder.setOnClickListener(mOnClickListener);
            mGroupHolder.btn_cancelOrder.setTag(groupPosition);

            updateOperationButtons(mGroupHolder,shopOrder);

            String groupName = shopOrder.getSupplierId();
            mGroupHolder.tv_company_name.setText(groupName);

            mGroupHolder.tv_totalPrice.setText(Utils.formatPrice(convertView.getContext(),shopOrder.getTotalPrice()));
            mGroupHolder.tv_quantity.setText(String.format(convertView.getContext().getResources().getString(R.string.product_quantity_all), shopOrder.getTotalQuantity()));
            mGroupHolder.tv_orderDate.setText(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(shopOrder.getOrderDate()));

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder mChildHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_child_shoporder, null);
                mChildHolder = new ChildHolder();
                mChildHolder.iv_product = (ImageView)convertView.findViewById(R.id.iv_product);
                mChildHolder.tv_product = (TextView)convertView.findViewById(R.id.tv_product);
                mChildHolder.tv_money = (TextView)convertView.findViewById(R.id.tv_money);
                mChildHolder.tv_quantity = (TextView)convertView.findViewById(R.id.tv_quantity);
                convertView.setTag(mChildHolder);
            } else {
                mChildHolder = (ChildHolder) convertView.getTag();
            }

            ShopOrderItem shopOrderItem = getChild(groupPosition, childPosition);
            mChildHolder.tv_product.setText(shopOrderItem.getProduct().getProd_name());
            ImageLoader.getInstance().displayImage(shopOrderItem.getProduct().getImage_url(),mChildHolder.iv_product);

            mChildHolder.tv_money.setText(Utils.formatPrice(convertView.getContext(),shopOrderItem.getPrice()));
            mChildHolder.tv_quantity.setText(String.format("x%s",shopOrderItem.getQuantity()));

            return convertView;
        }
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return false;
        }

        private void updateOperationButtons(GroupHolder groupHolder, ShopOrder shopOrder){
            int status  = shopOrder.getStatus();
            if(status >= TradeOrderStatusEnum.TRADE_CLOSED_BY_LINKEA.getValue() ) {
                groupHolder.btn_delete.setVisibility(View.VISIBLE);
                groupHolder.btn_payOrder.setText("查看订单");
                groupHolder.btn_cancelOrder.setVisibility(View.GONE);
            }else if(status <= TradeOrderStatusEnum.WAIT_BUYER_PAY.getValue() ) {
                groupHolder.btn_delete.setVisibility(View.GONE);
                groupHolder.btn_payOrder.setText("支付订单");
                groupHolder.btn_cancelOrder.setVisibility(View.VISIBLE);
            }else {
                groupHolder.btn_delete.setVisibility(View.GONE);
                groupHolder.btn_payOrder.setText("查看订单");
                groupHolder.btn_cancelOrder.setVisibility(View.GONE);
            }
        }
    }

}
