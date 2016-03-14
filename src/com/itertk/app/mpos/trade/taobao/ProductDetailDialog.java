package com.itertk.app.mpos.trade.taobao;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.utility.ShopOrderOperation;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * 订单详情 dialog
 */
public class ProductDetailDialog extends Dialog implements  View.OnClickListener {
    private static String TAG = "ProductDetailDialog";
    Context context;
    DisplayImageOptions options;

    Product mProduct;
    int mCount = 1;


    public ProductDetailDialog(Context context, int theme, Product busiBase) {
        super(context, theme);
        this.context = context;
        mProduct = busiBase;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_product_detail);


        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.item_image)
                .showImageForEmptyUri(R.drawable.item_image)
                .showImageOnFail(R.drawable.item_image)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        final EditText et_count = (EditText) findViewById(R.id.et_count);

        et_count.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String count = et_count.getText().toString();
                Log.e("afterTextChanged", count);
                mCount = 0;
                if(!TextUtils.isEmpty(count) ){
                    mCount = Integer.parseInt(count);
                }
                if(mCount < 1){
                    mCount = 1;
                    et_count.setText(String.valueOf(mCount));
                }
            }
        });

        ImageView imageView = (ImageView) findViewById(R.id.iv_product);
        ImageLoader.getInstance().displayImage(mProduct.getImage_url(), imageView);

        TextView tv_product_name = (TextView) findViewById(R.id.tv_product_name);
        tv_product_name.setText(mProduct.getProd_name());

        updateProductPriceUI();

        int[] buttonIds = new int[]{
                R.id.btnBuyNow,
                R.id.btnAddCart,
                R.id.btnAdd,
                R.id.btnCut,
                R.id.btn_back,
                R.id.btn_cart_go
        };

        for (int viewId : buttonIds) {
            findViewById(viewId).setOnClickListener(this);
        }


    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.btn_back:
                onBtnBack();
                break;
            case R.id.btn_cart_go:
                onBtnCartGo();
                break;
            case R.id.btnBuyNow:
                onBtnBuyNow();
                break;
            case R.id.btnAddCart:
                addToCart();
                break;
            case R.id.btnAdd:
            case R.id.btnCut:
                onBtnChangeQuantity(viewId);
                break;

            default:
                break;
        }
    }

    private void addToCart() {
        DataHelper dbHelper =  MPosApplication.getInstance().getDataHelper();
        CartDao cartDAO = dbHelper.getCartManager();
        long productQuantity = mCount;

        Cart cart = new Cart(mProduct.getSupplier());
        cartDAO.insertOrReplace(cart);

        CartItemDao cartItemDao = dbHelper.getDaoSession().getCartItemDao();

        List<CartItem> cartItems = cartItemDao.queryBuilder().where(CartItemDao.Properties.Supplier.eq(mProduct.getSupplier()), CartItemDao.Properties.ProductId.eq(mProduct.getId())).list();
        for (CartItem entity : cartItems) {
            if (entity.getProduct().getId() == mProduct.getId()) {
                productQuantity += entity.getProductQuantity();
            }
        }
        CartItem cartItem;
        if (cartItems.size() > 0) {
            cartItem = cartItems.get(0);
            mCount += cartItem.getProductQuantity();
        } else {
            cartItem = new CartItem(null, mProduct.getSupplier(), mProduct.getId(), productQuantity, "0.00");
        }
        cartItem.setProductQuantity(productQuantity);
        cartItem.setPrice(DecimalHelper.getDecimalString(Arith.mul(mProduct.getPrice(), mCount)));
        cartItemDao.insertOrReplace(cartItem);

        Toast.makeText(this.getContext(), mProduct.getProd_name() + "已加入购物车", Toast.LENGTH_SHORT).show();
        this.dismiss();
    }

    private void onBtnBuyNow() {
        MPosApplication mPosApplication = MPosApplication.getInstance();
        DataHelper dbHelper = mPosApplication.getDataHelper();
        ShopOrderDao shopOrderManager = dbHelper.getDaoSession().getShopOrderDao();
        ShopOrderItemDao shopOrderItemManager = dbHelper.getDaoSession().getShopOrderItemDao();
        String supplierId = mProduct.getSupplier();
        LinkeaResponseMsg.LoginResponseMsg msg= mPosApplication.getMember();

        Date orderDate = new Date();
        String consigneeName =msg.member.member_name;
        String consigneePhone = msg.member.phone;
        String consigneeAddress = Utils.getConsignAddress();
        String paymentName = msg.member.bank_name;
        String transferName = "huodaofukuan";
        BigDecimal totalPrice = Arith.mul(mProduct.getPrice(), mCount);
        int totalQuantity = mCount;
        int payed = TradeOrderStatusEnum.WAIT_BUYER_PAY.getValue();
        String remark = "";

        ShopOrder shopOrder = new ShopOrder(null, null, supplierId, orderDate, consigneeName, consigneePhone, consigneeAddress, paymentName, transferName, totalPrice.toString(), totalQuantity, payed, remark);
        shopOrderManager.insert(shopOrder);

        ShopOrderItem shopOrderItem = new ShopOrderItem(null, shopOrder.getId(), mProduct.getId(), totalPrice.toString(), mCount,mProduct.getPrice());
        shopOrderItemManager.insert(shopOrderItem);

        long[] shopOrderIds = new long[]{shopOrder.getId()};

        ConfirmOrderDialog confirmOrderDialog = new ConfirmOrderDialog(getContext(), R.style.MyDialog,shopOrderIds);
        confirmOrderDialog.show();
        this.dismiss();
    }

    private void onBtnChangeQuantity(int viewId) {

        if (viewId == R.id.btnAdd) {
            mCount++;
        } else {
            mCount--;
        }
        if (mCount < 1) {
            mCount = 1;
        }
        updateProductQuantity();
    }

    private void onBtnCartGo() {
        ShoppingCartDialog shoppingCartDialog = new ShoppingCartDialog(this.getContext(), R.style.MyDialog);
        shoppingCartDialog.show();
        this.dismiss();
    }

    private void onBtnBack() {
        this.dismiss();
    }

    private void updateProductPriceUI() {
        TextView tv_product_price = (TextView) findViewById(R.id.tv_product_price);
//        String price = DecimalHelper.getDecimalString(Arith.mul(mProduct.getPrice(), mCount));
        String price = DecimalHelper.getDecimalString(mProduct.getPrice());
        price = String.format(getContext().getResources().getString(R.string.product_price), price);
        tv_product_price.setText(price);
    }

    private void updateProductQuantity(){
        EditText et_count = (EditText) findViewById(R.id.et_count);
        et_count.setText(String.valueOf(mCount));
    }

}
