package com.itertk.app.mpos.trade.taobao;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.utility.Utils;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.dbhelper.Cart;
import com.itertk.app.mpos.dbhelper.CartDao;
import com.itertk.app.mpos.dbhelper.CartItem;
import com.itertk.app.mpos.dbhelper.CartItemDao;
import com.itertk.app.mpos.utility.DataHelper;
import com.itertk.app.mpos.dbhelper.Product;
import com.itertk.app.mpos.dbhelper.ShopOrder;
import com.itertk.app.mpos.dbhelper.ShopOrderDao;
import com.itertk.app.mpos.dbhelper.ShopOrderItem;
import com.itertk.app.mpos.dbhelper.ShopOrderItemDao;
import com.itertk.app.mpos.trade.pos.DecimalHelper;
import com.itertk.app.mpos.utility.ShopOrderOperation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 购物车dialog
 */
public class ShoppingCartDialog extends Dialog implements View.OnClickListener, DialogInterface.OnDismissListener {


    private static String TAG = "ShoppingCartDialog";
    Context context;
    DisplayImageOptions options;

    List<Cart> mCarts ;
    List<List<CartItem>> mCartItems = new ArrayList<List<CartItem>>() ;
    List<ItemStatus> mCartStatus = new ArrayList<ItemStatus>();
    List<List<ItemStatus>> mCartItemStatus =  new ArrayList<List<ItemStatus>>();

    CartDao mCartManager ;
    CartItemDao mCartItemManager ;

    View.OnClickListener  mOnClickListener;

    ShopCartListViewAdapter mAdapter;
    ExpandableListView mListView;

    private ImageView btn_select_all ;

    public ShoppingCartDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        mOnClickListener = this;
    }

    public class ItemStatus{
        public boolean edit;
        public boolean select;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_shop_cart);
        setOnDismissListener(this);

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

        findViewById(R.id.btnBuyNow).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
        btn_select_all = (ImageView) findViewById(R.id.btn_select_all);
        btn_select_all.setOnClickListener(this);

        updateSelectButton(btn_select_all, allSelected());
        updateTotalPrice();


        mListView = (ExpandableListView) findViewById(R.id.listData);
        mAdapter = new ShopCartListViewAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setGroupIndicator(null);
        for(int i = 0; i < mCarts.size(); i++){
            mListView.expandGroup(i);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        List<CartItem> cartItems = new ArrayList<CartItem>();
        for(List<CartItem> items : mCartItems){
            cartItems.addAll(items);
        }
        mCartItemManager.updateInTx(cartItems);

    }

    private void loadData(){
        DataHelper dbHelper =  MPosApplication.getInstance().getDataHelper();
        mCartManager = dbHelper.getCartManager();
        mCartItemManager = dbHelper.getDaoSession().getCartItemDao();
        mCarts = mCartManager.loadAll();

        int totalQuantity = 0;

        for(int i = 0; i < mCarts.size(); i++){
            ItemStatus status = new ItemStatus();
            status.select = true;
            mCartStatus.add(status);
        }

        for(Cart cart : mCarts) {
            List<CartItem> cartItems = cart.getCartItemList();
            mCartItems.add(cartItems);

            List<ItemStatus> itemStatus =  new ArrayList<ItemStatus>();
            for(int i = 0; i < cartItems.size(); i++){
                ItemStatus status = new ItemStatus();
                status.select = true;
                itemStatus.add(status);
                totalQuantity += cartItems.get(i).getProductQuantity();
            }
            mCartItemStatus.add(itemStatus);
        }
    }



    //	@Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId){
            case R.id.btnBack:
                this.dismiss();
                break;
            case R.id.btnBuyNow:
                onBtnBuyNow();
                break;
            case R.id.btn_cut:
            case R.id.btn_add:
                onBtnChangeQuantityClick(viewId,(ChildHolder)v.getTag());
                break;
            case R.id.btn_delete:
                onBtnDeleteChildClick((ChildHolder)v.getTag()) ;
                break;
            case R.id.btn_edit:
                onBtnEdit((GroupHolder)v.getTag());
                break;
            case R.id.tv_done:
                onBtnDone((GroupHolder)v.getTag());
                break;

            case R.id.btn_select_all:
                onBtnSelectAll();
                break;

            case R.id.btn_select_group:
                onBtnSelectGroup((GroupHolder)v.getTag());
                break;

            case R.id.btn_select_child:
                onBtnSelectChild((ChildHolder) v.getTag());
                break;

            default:
                break;
        }
    }

    private void onBtnSelectChild(ChildHolder childHolder){
        ItemStatus status  = mCartItemStatus.get(childHolder.groupPosition).get(childHolder.childPosition) ;
        status.select = ! status.select;

        boolean groupSelected = groupSelected(childHolder.groupPosition);
        mCartStatus.get(childHolder.groupPosition).select = groupSelected;

        updateSelectButton(btn_select_all, allSelected());
        updateTotalPrice();

        mAdapter.notifyDataSetChanged();
    }



    private void onBtnSelectGroup(GroupHolder groupHolder){
        ItemStatus status  = mCartStatus.get(groupHolder.groupPosition) ;

        status.select = ! status.select;

        List<ItemStatus> itemStatus = mCartItemStatus.get(groupHolder.groupPosition);
        for(int i =0 ; i < itemStatus.size(); i++){
            itemStatus.get(i).select = status.select;
        }

        updateSelectButton(btn_select_all, allSelected());
        updateTotalPrice();

        mListView.expandGroup(groupHolder.groupPosition);
        mAdapter.notifyDataSetChanged();
    }

    private void onBtnSelectAll(){
        if(mCartItems.size() == 0){
            return;
        }

        Boolean selectAll =!allSelected();

        for(int i = 0; i < mCartStatus.size(); i++){
            mCartStatus.get(i).select = selectAll;
            for(int j = 0; j < mCartItemStatus.get(i).size(); j++){
                mCartItemStatus.get(i).get(j).select = selectAll;
            }
        }
        updateSelectButton(btn_select_all, selectAll);
        updateTotalPrice();

        mAdapter.notifyDataSetChanged();
    }

    private void updateSelectButton(ImageView view, Boolean select) {
        int resId = select ? R.drawable.btn_select : R.drawable.btn_select_n;
        view.setImageResource(resId);
    }

    private void onBtnDone(GroupHolder groupHolder){
        mCartStatus.get(groupHolder.groupPosition).edit = false;
        for (ItemStatus status: mCartItemStatus.get(groupHolder.groupPosition)){
            status.edit = false;
        }

        mListView.expandGroup(groupHolder.groupPosition);
        mAdapter.notifyDataSetChanged();
    }
    private void onBtnEdit(GroupHolder groupHolder){
        mCartStatus.get(groupHolder.groupPosition).edit = true;
        for (ItemStatus status: mCartItemStatus.get(groupHolder.groupPosition)){
            status.edit = true;
        }
        mListView.expandGroup(groupHolder.groupPosition);
        mAdapter.notifyDataSetChanged();
    }

    private boolean allSelected(){
        Boolean selectAll = true;
        if(mCartItems.size() == 0){
            return false;
        }

        for(int i = 0; i < mCartStatus.size(); i ++){
            for(int j = 0; j < mCartItemStatus.get(i).size(); j++){
                if(!mCartItemStatus.get(i).get(j).select){
                    selectAll = false;
                    break;
                }
            }
        }
        return selectAll;
    }

    private boolean singleSelected(){
        Boolean selected = false;

        for(int i = 0; i < mCartStatus.size(); i ++){
            for(int j = 0; j < mCartItemStatus.get(i).size(); j++){
                if(mCartItemStatus.get(i).get(j).select){
                    selected = true;
                    break;
                }
            }
        }
        return selected;
    }

    private boolean groupSelected(int groupPostion){
        boolean selectAll = true;
        List<ItemStatus> status  = mCartItemStatus.get(groupPostion);
        for(int i = 0 ; i < status.size(); i++){
            if(!status.get(i).select){
                selectAll = false;
                break;
            }
        }
        return  selectAll;
    }

    private void onBtnBuyNow(){
        if(!singleSelected()){
            Toast.makeText(getContext(),"没有选中商品",Toast.LENGTH_SHORT).show();
            return;
        }

        MPosApplication mPosApplication = MPosApplication.getInstance();
        DataHelper dbHelper = mPosApplication.getDataHelper();
        ShopOrderDao shopOrderManager = dbHelper.getDaoSession().getShopOrderDao();
        ShopOrderItemDao shopOrderItemManager = dbHelper.getDaoSession().getShopOrderItemDao();

        LinkeaResponseMsg.LoginResponseMsg msg= mPosApplication.getMember();
        Date orderDate = new Date();
        String consigneeName =msg.member.member_name;
        String consigneePhone = msg.member.phone;
        String consigneeAddress = Utils.getConsignAddress();
        String paymentName = msg.member.bank_name;
        String transferName = "";
        String remark = "";

        List<Long> shopOrderIds = new ArrayList<Long>();
        List<ShopOrderItem> shopOrderItems = new ArrayList<ShopOrderItem>();

        for(int groupPosion = 0; groupPosion < mCartStatus.size(); groupPosion++){
            Cart cart  =  mCarts.get(groupPosion);
            String supplier = cart.getSupplier();
            BigDecimal totalPrice = Arith.newZeroBigDecimal();
            int totalQuantity = 0;
            int payed = TradeOrderStatusEnum.WAIT_BUYER_PAY.getValue();
            ShopOrder shopOrder = new ShopOrder(null, null,supplier, orderDate,  consigneeName,  consigneePhone,  consigneeAddress,  paymentName,  transferName,  totalPrice.toString(),  totalQuantity,payed,remark);
            shopOrderManager.insert(shopOrder);

            CartItemDao cartItemDao = dbHelper.getDaoSession().getCartItemDao();

            List<CartItem> deletedCartItems  = new ArrayList<CartItem>();

            Boolean groupSelected = true;

            for(int childPosition = 0; childPosition < mCartItemStatus.get(groupPosion).size();  childPosition++ ){
                CartItem cartItem =  mCartItems.get(groupPosion).get(childPosition);
                if(mCartItemStatus.get(groupPosion).get(childPosition).select) {
                    String price = cartItem.getPrice();
                    long quantity = cartItem.getProductQuantity();
                    ShopOrderItem shopOrderItem = new ShopOrderItem(null, shopOrder.getId(), cartItem.getProductId(), price, quantity,cartItem.getProduct().getPrice());
                    shopOrderItems.add(shopOrderItem);
                    totalPrice = Arith.add(totalPrice,price);
                    totalQuantity +=  quantity;
                    //remove cartItem from database and memory cache
                    cartItem.delete();
                    deletedCartItems.add(cartItem);
                }else{
                    groupSelected = false;
                }
            }

            if(groupSelected){
                cart.delete();
            }
            //remove form memory cache
            for(CartItem item : deletedCartItems){
                cart.getCartItemList().remove(item);
            }

            shopOrder.setTotalPrice(totalPrice.toString());
            shopOrder.setTotalQuantity(totalQuantity);

            if(Arith.newBigDecimal(shopOrder.getTotalPrice()).compareTo(Arith.newBigDecimal("0.01")) <0) {
                shopOrder.delete();
            }else{
                shopOrder.update();
                shopOrderItemManager.insertInTx(shopOrderItems);
                shopOrderIds.add(shopOrder.getId());
            }
        }

//        ConfirmOrderDialog confirmOrderDialog = new ConfirmOrderDialog(getContext(),R.style.MyDialog);
//        confirmOrderDialog.setShopOrderIds(shopOrderIds);
//        confirmOrderDialog.show();

        ShoppingOrderDialog shoppingOrderDialog =new ShoppingOrderDialog(getContext(), R.style.MyDialog, R.id.order_category_unpay);
        shoppingOrderDialog.show();
        this.dismiss();
    }

    private void onBtnChangeQuantityClick(int viewId, ChildHolder childHolder) {
        CartItem cartItem = mCartItems.get(childHolder.groupPosition).get(childHolder.childPosition);
        long quantity = cartItem.getProductQuantity();
        switch (viewId) {
            case R.id.btn_cut:
                quantity--;
                break;
            case R.id.btn_add:
                quantity++;
                break;
        }
        if(quantity <= 0){
            quantity  = 1;
        }

//        cartItem.setProductQuantity(quantity);
//        cartItem.update();
//        updateChildCartQuantity(childHolder, quantity);
//        updateTotalPrice();
          childHolder.et_quantity.setText(String.format("%s", quantity));
    }
    

    private void onBtnDeleteChildClick(ChildHolder childHolder) {

        int groupPosition = childHolder.groupPosition;
        int childPosition = childHolder.childPosition ;
        CartItem cartItem = mCartItems.get(groupPosition).get(childPosition);
        cartItem.delete();

        mCartItems.get(groupPosition).remove(childPosition);

        mCartItemStatus.get(childHolder.groupPosition).remove(childPosition);

        if(mCartItems.get(groupPosition).size() == 0) {
            Cart cart = mCarts.get(groupPosition);
            cart.delete();
            mCarts.remove(groupPosition);
            mCartItems.remove(groupPosition);
            mCartStatus.remove(groupPosition);
            mCartItemStatus.remove(groupPosition);
        }

        mAdapter.notifyDataSetChanged();
        updateTotalPrice();
        updateSelectButton(btn_select_all, allSelected());
    };

    private  void updateTotalPrice(){
        BigDecimal totalPrice = Arith.newZeroBigDecimal();
        int totalQuantity = 0;

        for(int i = 0; i < mCarts.size(); i++){
            Cart cart  = mCarts.get(i);
            List<CartItem> cartItems = cart.getCartItemList();
            for(int j = 0; j < cartItems.size(); j++){
                CartItem cartItem = cartItems.get(j);
                totalQuantity += cartItem.getProductQuantity();
                if(mCartItemStatus.get(i).get(j).select){
                    totalPrice =Arith.add(new BigDecimal(cartItem.getPrice()), totalPrice);
                }
            }
        }

        TextView tv_totalPrice = (TextView) findViewById(R.id.tv_totalPrice);
        tv_totalPrice.setText(Utils.formatPrice(getContext(), totalPrice.toString()));

        TextView tv_title = (TextView)findViewById(R.id.title);
        tv_title.setText(String.format("购物车(%s)",totalQuantity));
    }



    private class GroupHolder {
        private View header;
        private ImageView btn_select_group;
        private TextView  tv_company_name;
        private LinearLayout btn_edit;
        private TextView tv_done;
        private int groupPosition;
    }

    private class ChildHolder {
        private ImageView btn_select_child;
        private ImageView  iv_product;
        private TextView tv_product;

        private LinearLayout panel_money;
        private TextView tv_total_price;
        private TextView tv_product_price  ;

        private TextView tv_quantity;
        private LinearLayout panel_edit;
        private ImageButton btn_cut;
        private ImageButton btn_add;
        private EditText et_quantity;
        private ImageView btn_delete;
        public int groupPosition;
        public int childPosition;
    }

    public class ShopCartListViewAdapter extends BaseExpandableListAdapter  {
        @Override
        public int getGroupCount() {
            return mCarts.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mCartItems.get(groupPosition).size();
        }

        @Override
        public Cart getGroup(int groupPosition) {
            return mCarts.get(groupPosition);
        }

        @Override
        public CartItem getChild(int groupPosition, int childPosition) {
            return mCartItems.get(groupPosition).get(childPosition);
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
            GroupHolder groupHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_group_shopcart, null);
                groupHolder = new GroupHolder();
                groupHolder.header = convertView.findViewById(R.id.header);
                groupHolder.btn_select_group = (ImageView) convertView.findViewById(R.id.btn_select_group);
                groupHolder.tv_company_name = (TextView)convertView.findViewById(R.id.tv_company_name);
                groupHolder.btn_edit = (LinearLayout) convertView.findViewById(R.id.btn_edit);
                groupHolder.tv_done = (TextView) convertView.findViewById(R.id.tv_done);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }

            if(groupPosition == 0){
                groupHolder.header.setVisibility(View.GONE);
            }

            groupHolder.groupPosition = groupPosition;
            Cart cart =  getGroup(groupPosition);
            String groupName = getGroup(groupPosition).getSupplier();
            groupHolder.tv_company_name.setText(groupName);

            groupHolder.btn_edit.setTag(groupHolder);
            groupHolder.btn_edit.setOnClickListener(ShoppingCartDialog.this);

            groupHolder.tv_done.setTag(groupHolder);
            groupHolder.tv_done.setOnClickListener(ShoppingCartDialog.this);

            groupHolder.btn_select_group.setTag(groupHolder);
            groupHolder.btn_select_group.setOnClickListener(mOnClickListener);



            ItemStatus status = mCartStatus.get(groupPosition);
            if(status.edit){
                groupHolder.btn_edit.setVisibility(View.GONE);
                groupHolder.tv_done.setVisibility(View.VISIBLE);
                groupHolder.btn_select_group.setVisibility(View.GONE);
            }else{
                groupHolder.btn_edit.setVisibility(View.VISIBLE );
                groupHolder.tv_done.setVisibility(View.GONE);
                groupHolder.btn_select_group.setVisibility(View.VISIBLE);
            }
            updateSelectButton(groupHolder.btn_select_group, status.select);

            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            final ChildHolder childHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_child_shopcart, null);
                childHolder = new ChildHolder();

                childHolder.btn_select_child = (ImageView) convertView.findViewById(R.id.btn_select_child);
                childHolder.iv_product = (ImageView)convertView.findViewById(R.id.iv_product);
                childHolder.tv_product = (TextView)convertView.findViewById(R.id.tv_product);
                childHolder.tv_total_price = (TextView)convertView.findViewById(R.id.tv_total_price);
                childHolder.tv_product_price = (TextView)convertView.findViewById(R.id.tv_product_price);
                childHolder.tv_quantity = (TextView)convertView.findViewById(R.id.tv_quantity);
                childHolder.btn_cut = (ImageButton)convertView.findViewById(R.id.btn_cut);
                childHolder.btn_add = (ImageButton)convertView.findViewById(R.id.btn_add);
                childHolder.et_quantity = (EditText)convertView.findViewById(R.id.et_quantity);
                childHolder.btn_delete = (ImageView)convertView.findViewById(R.id.btn_delete);

                childHolder.panel_edit = (LinearLayout) convertView.findViewById(R.id.panel_edit);
                childHolder.panel_money = (LinearLayout) convertView.findViewById(R.id.panel_money);

                convertView.setTag(childHolder);
            } else {
                childHolder = (ChildHolder) convertView.getTag();
            }


            childHolder.groupPosition = groupPosition;
            childHolder.childPosition = childPosition;

            childHolder.btn_select_child.setTag(childHolder);
            childHolder.btn_select_child.setOnClickListener(mOnClickListener);

            childHolder.btn_add.setTag(childHolder);
            childHolder.btn_add.setOnClickListener(mOnClickListener);

            childHolder.btn_cut.setTag(childHolder);
            childHolder.btn_cut.setOnClickListener(mOnClickListener);

            childHolder.btn_delete.setTag(childHolder);
            childHolder.btn_delete.setOnClickListener(mOnClickListener);

            final CartItem cartItem = getChild(groupPosition, childPosition);
            Product product = cartItem.getProduct();
            childHolder.tv_product.setText(product.getProd_name());
            ImageLoader.getInstance().displayImage(product.getImage_url(),childHolder.iv_product);

            long  quantity =  cartItem.getProductQuantity();

            String price = Utils.formatPrice(convertView.getContext(), cartItem.getProduct().getPrice());
            String totalPrice = Utils.formatPrice(convertView.getContext(), cartItem.getPrice());
            childHolder.tv_product_price.setText(price);
            childHolder.tv_total_price.setText(totalPrice );

            ItemStatus status= mCartItemStatus.get(groupPosition).get(childPosition);
            if(status.edit){
                childHolder.panel_edit.setVisibility(View.VISIBLE);
                childHolder.panel_money.setVisibility(View.GONE);
            }else{
                childHolder.panel_edit.setVisibility(View.GONE );
                childHolder.panel_money.setVisibility(View.VISIBLE);
            }

            updateSelectButton(childHolder.btn_select_child, status.select);
            childHolder.et_quantity.setText(String.valueOf(cartItem.getProductQuantity()));
            childHolder.tv_quantity.setText(String.format("x%s", cartItem.getProductQuantity()));


            childHolder.et_quantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    CartItem item = getChild(childHolder.groupPosition, childHolder.childPosition);
                    String count = childHolder.et_quantity.getText().toString();
                    Log.d("afterTextChanged", count);
                    long quantity = 0L;
                    if(!TextUtils.isEmpty(count) ){
                        quantity = Long.parseLong(count);
                        if(quantity == item.getProductQuantity()){
                            return;
                        }
                    }
                    if(quantity == 0){
                        quantity = 1;
                        childHolder.et_quantity.setText(String.valueOf(quantity));
                    }

                    item.setProductQuantity(quantity );
                    String cartPrice = DecimalHelper.getDecimalString(Arith.mul(item.getProduct().getPrice(),quantity));
                    item.setPrice(cartPrice );

                    childHolder.tv_total_price.setText(cartPrice );
                    updateTotalPrice();
                }
            });

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return false;
        }
    }

}
