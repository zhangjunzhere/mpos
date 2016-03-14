package com.itertk.app.mpos.trade.pos;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.utility.ToastHelper;
import com.itertk.app.mpos.dbhelper.Product;
import com.itertk.app.mpos.dbhelper.ProductDao;
import com.itertk.app.mpos.dbhelper.Reduce;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.dbhelper.SaleOrderDao;
import com.itertk.app.mpos.dbhelper.SaleOrderItem;
import com.itertk.app.mpos.dbhelper.SaleOrderItemDao;
import com.itertk.app.mpos.dbhelper.SaleOrderReduce;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by smile_gao on 2015/3/11.
 * 销售订单适配器
 */
public class NewSaleOrderAdapter extends BaseAdapter {

        private float x, ux;
     //   ProductDao productManager;
        ProductDao busbaseManager;
        SaleOrderItemDao saleOrderItemManager;
        SaleOrderDao saleOrderManager;
        List<SaleOrderItem> saleOrderItemList;
        SaleOrder saleOrder;
        String TAG = "NewSaleOrderAdapter";
        Activity mInstance;
       private int selectIndex=-1;
    OnChangeOrderItemnListener mOnChangelistener;
    NewSaleOrderAdapter(Activity context,String orderId, OnChangeOrderItemnListener listener) {
        mInstance=context;
        mOnChangelistener = listener;
        busbaseManager = ((MPosApplication) mInstance.getApplication()).getDataHelper().getProductManager();
        saleOrderManager = ((MPosApplication) mInstance.getApplication()).getDataHelper().getSaleOrderManager();
        saleOrderItemManager = ((MPosApplication) mInstance.getApplication()).getDataHelper().getSaleOrderItemManager();

            if (orderId == null) {
                saleOrder = new SaleOrder(null, "", 0, "0", (new Date()).getTime(), 0,null);
                saleOrderManager.insert(saleOrder);
                Log.d(TAG, "create new order=" + saleOrder.getSaleOrderId());
            } else {
                saleOrder = saleOrderManager.load(Long.valueOf(orderId).longValue());
                Log.d(TAG, "load saleOrder from id=" + orderId);
            }

            saleOrder.resetSaleOrderItemList();
            saleOrderItemList = saleOrder.getSaleOrderItemList();
            List<Product> list=busbaseManager.loadAll();
        Log.d("smile", "busbaseManager size" + String.valueOf(list.size()));
//            for(Product b : list)
//            {
//                addSaleOrderItem(b);
//            }
        }
    public  void setSelectIndex(int pos)
    {
        selectIndex = pos;
    }
    public SaleOrderItem getSelectedItem()
    {
        if(selectIndex <0 ||selectIndex>= saleOrderItemList.size())
            return  null;
        return saleOrderItemList.get(selectIndex);
    }
        public  int addOrderItemOrUpdateCount(Product product)
        {
            Boolean isInlist=false;
            int ret = -1;
            for (int i =0 ;i<getCount();i++)
            {
                SaleOrderItem soi =(SaleOrderItem)getItem(i);
                if(soi.getBar_code().equals(product.getBar_code()))
                {
                    isInlist = true;
                    ret= i;
                    soi.setCountProduct(soi.getCountProduct()+1);
                }
            }

            if(!isInlist)
            {
                SaleOrderItem saleOrderItem = new SaleOrderItem(null, product.getLocalPrice(), product.getLocalPrice(), 1, product.getProd_name(),product.getBar_code(), saleOrder.getSaleOrderId(), product.getId());
                //  saleOrderItem.setBusiBase(product);
                saleOrderItemManager.insert(saleOrderItem);
                saleOrderItemList.add(saleOrderItem);
                ret = getCount()-1;
            }
            if(ret !=-1)
            {
                setSelectIndex(ret);
            }
            notifyDataSetChanged();
            return  ret;
        }
        public int  addSaleOrderItem(Product product) {
//            SaleOrderItem saleOrderItem = new SaleOrderItem(null, product.getPrice(), product.getPrice(), 1, product.getProd_name(),product.getBar_code(), saleOrder.getSaleOrderId(), product.getProd_id());
//         //   saleOrderItem.setBusiBase(product);
//            saleOrderItemManager.insert(saleOrderItem);
//            saleOrderItemList.add(saleOrderItem);
          int index =   addOrderItemOrUpdateCount(product);
            return index;
        }
       public  List<Product> getSaleOrderItemListByProductName(String productName)
       {
           QueryBuilder qb = busbaseManager.queryBuilder();
           qb.whereOr(ProductDao.Properties.Prod_name.like("%"+productName+"%"),ProductDao.Properties.Bar_code.like("%"+productName+"%"));
           //    qb.or(ProductDao.Properties.Bar_code.eq("%"));
           List<Product> scanProducts = qb.list();
     //        List<Product> scanProducts = busbaseManager.queryBuilder().where(ProductDao.Properties.Prod_name.like(productName+"%"),ProductDao.Properties.Bar_code.like(productName+"%")).list();
           return scanProducts;
       }
       public  int  addSaleOrderItemByProductName(String productName)
       {
           QueryBuilder qb = busbaseManager.queryBuilder();
           qb.whereOr(ProductDao.Properties.Prod_name.like("%"+productName+"%"),ProductDao.Properties.Bar_code.like("%"+productName+"%"));

           List<Product> scanProducts = qb.list();
           if(scanProducts != null && scanProducts.size() > 0)
           {
               Log.i("smile","addSaleOrderItemByProductName :"+scanProducts.size());
               Product product = scanProducts.get(0);
               addSaleOrderItem(product);
           }
           else
           {
               Log.d(TAG, "addSaleOrderItemByProductName =" + productName);
              ToastHelper.showToast(mInstance,"没有找到商品");
           }
           if(scanProducts ==null)
               return 0;
           else
           {
               return scanProducts.size();
           }
       }
        public int  addSaleOrderItem(String scanCode) {
            if (scanCode.isEmpty()) return -1;
/*
            List<Product> scanProducts = productManager.queryBuilder().where(ProductDao.Properties.Barcode.eq(scanCode)).list();
            if (scanProducts != null && scanProducts.size() > 0) {
                Product product = scanProducts.get(0);
                SaleOrderItem saleOrderItem = new SaleOrderItem(null, product.getPrice(), product.getPrice(), 1, product.getName(), saleOrder.getSaleOrderId(), product.getProductId());
                saleOrderItemManager.insert(saleOrderItem);
                saleOrderItemList.add(saleOrderItem);
                notifyDataSetChanged();
            } else {
                Log.d(TAG, "no such product with barcode=" + scanCode);
            }
            */
            List<Product> scanProducts = busbaseManager.queryBuilder().where(ProductDao.Properties.Bar_code.eq(scanCode)).list();

            if (scanProducts != null && scanProducts.size() > 0) {
                Log.d(TAG, "addSaleOrderItem size:" + scanProducts.size());
                Product product = scanProducts.get(0);
               int ret= addOrderItemOrUpdateCount(product);
                return  ret;
//                SaleOrderItem saleOrderItem = new SaleOrderItem(null, product.getPrice(), product.getPrice(), 1, product.getProd_name(),product.getBar_code(), saleOrder.getSaleOrderId(), product.getProd_id());
//              //  saleOrderItem.setBusiBase(product);
//                saleOrderItemManager.insert(saleOrderItem);
//                saleOrderItemList.add(saleOrderItem);
            } else {
                Log.d(TAG, "addSaleOrderItem with barcode=" + scanCode);
                ToastHelper.showToast(mInstance,"没有找到商品");
            }
            return  -1;
        }

    public void addSaleOrderItemByProduct(Product product) {
        SaleOrderItem saleOrderItem = new SaleOrderItem(null, product.getLocalPrice(), product.getLocalPrice(), 1, product.getProd_name(),product.getBar_code(), saleOrder.getSaleOrderId(), -1L);
        saleOrderItemManager.insert(saleOrderItem);
        saleOrderItemList.add(saleOrderItem);
        notifyDataSetChanged();
    }
        public void addSaleOrderItemByPrice(String money) {
            SaleOrderItem saleOrderItem = new SaleOrderItem(null, money, money, 1, "手动","", saleOrder.getSaleOrderId(), -1L);
            saleOrderItemManager.insert(saleOrderItem);
            saleOrderItemList.add(saleOrderItem);
            notifyDataSetChanged();
        }


        public void modSaleOrderItem(SaleOrderItem saleOrderItem) {
            //saleOrderItemManager.update(saleOrderItem);
            notifyDataSetChanged();
        }

        public void delSaleOrderItem(int postion) {
            saleOrderItemManager.delete((SaleOrderItem) getItem(postion));
            saleOrderItemList.remove(postion);
            notifyDataSetChanged();
        }

        private void removeAllSaleOrderItem() {
            Log.d(TAG, "removeAllSaleOrderItem" + saleOrder.toString());

            ((MPosApplication) mInstance.getApplication()).getDataHelper().getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (SaleOrderItem saleOrderItem : saleOrder.getSaleOrderItemList()) {
                        ((MPosApplication) mInstance.getApplication()).getDataHelper().getDaoSession().getSaleOrderItemReduceDao()
                                .deleteInTx(saleOrderItem.getSaleOrderItemReduceList());
                        ((MPosApplication) mInstance.getApplication()).getDataHelper().getDaoSession().getSaleOrderItemAttributeValueDao()
                                .deleteInTx(saleOrderItem.getSaleOrderItemAttributeValueList());
                    }
                    saleOrderItemManager.deleteInTx(saleOrder.getSaleOrderItemList());
                }
            });
        }

        public void clearSaleOrderItemList() {
            removeAllSaleOrderItem();
            notifyDataSetChanged();
        }

        public void cancelImcompleteOrder() {
            if (saleOrder.getCreateTime() == 0) {
                removeAllSaleOrderItem();
                saleOrder.delete();
            } else {
                this.saveOrder();
            }
        }
        public  SaleOrder getSaleOrder()
        {
            return  saleOrder;
        }
       public  void addReduce(BigDecimal reducePrice)
       {
           List<SaleOrderReduce> list = getSaleOrder().getSaleOrderReduceList();
           if(list ==null ||list.size()==0)
           {
               Reduce reduce = new Reduce(null,"优惠",reducePrice.toString(),1);
               MPosApplication.getInstance().getDataHelper().getDaoSession().getReduceDao().insert(reduce);
               SaleOrderReduce  sor = new SaleOrderReduce(null,getSaleOrder().getSaleOrderId(),reduce.getReduceId());
               MPosApplication.getInstance().getDataHelper().getDaoSession().getSaleOrderReduceDao().insert(sor);
               getSaleOrder().getSaleOrderReduceList().add(sor);

           }
           else
           {
               list.get(0).getReduce().setValue(reducePrice.toString());

           }
       }
    public  BigDecimal getReduce()
    {
        List<SaleOrderReduce> list = getSaleOrder().getSaleOrderReduceList();
        if(list == null || list.size()==0)
        {
            return  null;
        }
        return Arith.newBigDecimal(list.get(0).getReduce().getValue());
    }
        public long saveOrder() {
            long result = 0;

            try {
                if (saleOrder.getCreateTime() == 0) {
                    Long time = (new Date()).getTime();
                    saleOrder.setCreateTime(time);
                    saleOrder.setSaleOrderNo(time.toString());
                }

                saleOrder.setPrice(getTotalPrice().toString());
                saleOrder.update();

                result = saleOrder.getSaleOrderId();
            } catch (Exception e) {
                result = -1;
            }

            return result;
        }

        public BigDecimal getTotalPrice() {
            BigDecimal totalPrice = new BigDecimal(0);

            for (SaleOrderItem item : saleOrderItemList) {

                totalPrice =totalPrice.add(Arith.mulBig(item.getPrice(),item.getCountProduct())); //Arith.add(totalPrice));//(item.getPrice()*item.getCountProduct());
            }

            return totalPrice;
        }

        @Override
        public int getCount() {
            return saleOrderItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return saleOrderItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void notifyDataSetChanged() {
            saleOrder.resetSaleOrderItemList();
            saleOrderItemList = saleOrder.getSaleOrderItemList();
            super.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, final View convertView, ViewGroup parent) {
            final int pos = position;
            Log.d("posactivty", "" + pos);
            final SaleOrderItem saleOrderItem = saleOrderItemList.get(pos);
            View view = convertView;
            final ViewHold viewHold;
            if (view == null) {
                LayoutInflater inflater = mInstance.getLayoutInflater();
                view = inflater.inflate(R.layout.order_list_item, null);

                viewHold = new ViewHold();
                view.setTag(viewHold);

                viewHold.textBarcode = (TextView) view.findViewById(R.id.barcode);
                viewHold.textProductName = (TextView) view.findViewById(R.id.pname);
                viewHold.btnSubstract = (Button) view.findViewById(R.id.count_substract);
                viewHold.textCount = (TextView) view.findViewById(R.id.itemCount);
                viewHold.btnAdd = (Button) view.findViewById(R.id.count_add);
                viewHold.textPrice = (TextView) view.findViewById(R.id.itemPrice);
                viewHold.textTotalPrice = (TextView) view.findViewById(R.id.totalPrice);
                viewHold.btnDelete = (Button) view.findViewById(R.id.item_delete);
            } else {
                viewHold = (ViewHold) view.getTag();
            }
            MyOnTouchListener sublistener = new MyOnTouchListener(viewHold, saleOrderItem,CheckoutFragment.MSG_SUB);
            viewHold.btnSubstract.setOnTouchListener(sublistener);
//            viewHold.btnSubstract.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    subCount(saleOrderItem, viewHold);
//                }
//            });
            MyOnTouchListener addlistener = new MyOnTouchListener(viewHold, saleOrderItem,CheckoutFragment.MSG_ADD);
            viewHold.btnAdd.setOnTouchListener(addlistener);
//            viewHold.btnAdd.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    addCount(saleOrderItem, viewHold);
//                }
//            });
            viewHold.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("smile", "del");
                    mOnChangelistener.onChangeItem("del",pos);
                }
            });
            Log.i("smile","New sal orderAdapter product id: "+String.valueOf(saleOrderItem.getProductId()));
            if (saleOrderItem.getProductId() > 0) {
             //   BusiBase product = busbaseManager.load(saleOrderItem.getProductId());
                viewHold.textBarcode.setText(saleOrderItem.getBar_code());
                viewHold.textProductName.setText(saleOrderItem.getName());
         //       viewHold.textPrice.setText(String.valueOf(saleOrderItem.getPrice()));
                viewHold.textCount.setText(String.valueOf(saleOrderItem.getCountProduct()));
       //         viewHold.textTotalPrice.setText(String.valueOf(getOneOrderItemTotalPrice(saleOrderItem)));

               // Bitmap bitmap = BitmapFactory.decodeByteArray(product.getDataPhoto(), 0, product.getDataPhoto().length);
               // viewHold.imageView.setImageBitmap(bitmap);
            } else {
                viewHold.textBarcode.setText(saleOrderItem.getBar_code());
                viewHold.textProductName.setText(saleOrderItem.getName());

                viewHold.textCount.setText(String.valueOf(saleOrderItem.getCountProduct()));


            }
            if(pos == selectIndex)
            {
                view.setBackgroundColor(Color.parseColor("#ff1c96f4"));
            }
            else
            {
                view.setBackgroundColor(Color.TRANSPARENT);
            }
            viewHold.textCount.setText("" + saleOrderItem.getCountProduct());
            viewHold.textPrice.setText(DecimalHelper.getDecimalString(saleOrderItem.getPrice()));
            viewHold.textTotalPrice.setText(DecimalHelper.getDecimalString(getOneOrderItemTotalPrice(saleOrderItem)));

            return view;
        }

    private void addCount(SaleOrderItem saleOrderItem, ViewHold viewHold,MyOnTouchListener listener ) {
        long count = saleOrderItem.getCountProduct();
        count++;
        if(count>999)
        {
            ToastHelper.showToast(mInstance, "不能超过最大数量999");
            count =999;
            listener.SetPress(false);
        }
        saleOrderItem.setCountProduct(count);
        Log.d("smile", "sub count=" + count);
        viewHold.textCount.setText(String.valueOf(count));
        viewHold.textTotalPrice.setText("￥" + DecimalHelper.getDecimalString(getOneOrderItemTotalPrice(saleOrderItem)));
        mOnChangelistener.onChangeItem("add",0);
    }

    private void subCount(SaleOrderItem saleOrderItem, ViewHold viewHold, MyOnTouchListener listener) {
        long count = saleOrderItem.getCountProduct();
        count--;

        if(count<1)
        {
            count =1;
            listener.SetPress(false);
        }
        if(count>999)
        {
            count =999;
        }
        saleOrderItem.setCountProduct(count);
        viewHold.textCount.setText(String.valueOf(count));
        Log.d("smile", "sub count=" + count);
        viewHold.textTotalPrice.setText("￥" + DecimalHelper.getDecimalString(getOneOrderItemTotalPrice(saleOrderItem)));
        mOnChangelistener.onChangeItem("sub",0);
    }

    private BigDecimal getOneOrderItemTotalPrice(SaleOrderItem item)
    {
        return  Arith.mulBig(item.getPrice(),item.getCountProduct());
    }

    static class ViewHold {
        TextView textBarcode;
        TextView textProductName;
        Button  btnSubstract;
        TextView textCount;
        Button  btnAdd;
        TextView textPrice;
        TextView textTotalPrice;
        Button  btnDelete;
    }
    public interface OnChangeOrderItemnListener  {
        // TODO: Update argument type and name
        public void onChangeItem(String pams,int position);
    }
    class   MyOnTouchListener  implements View.OnTouchListener {
        boolean pressed=false;
        int index=0;
        Thread t;
        int currMsg;
        ViewHold mViewHold;
        SaleOrderItem mSaleOrderItem;
        Handler h = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case CheckoutFragment.MSG_ADD:
                        addCount(mSaleOrderItem,mViewHold, NewSaleOrderAdapter.MyOnTouchListener.this);
                        break;
                    case CheckoutFragment.MSG_SUB:
                        subCount(mSaleOrderItem,mViewHold,NewSaleOrderAdapter.MyOnTouchListener.this);
                        break;
                }
            }
        };
        public  void SetPress(boolean p)
        {
            pressed = p;
        }

        public MyOnTouchListener(ViewHold viewHold , SaleOrderItem saleOrderItem, int msg)
        {
            mSaleOrderItem = saleOrderItem;
            mViewHold = viewHold;
            currMsg = msg;

        }
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction()==MotionEvent.ACTION_DOWN)
            {
                Log.i("smile","index "+(index++));
                if(pressed)
                {
                    return  false;
                }

                if(t!=null&&t.isAlive())
                {
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                pressed=true;
                t = new Thread (){
                    @Override
                    public void run() {
                        super.run();
                        while (pressed)
                        {
                            Message  message= h.obtainMessage();
                            message.what = currMsg;
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            h.sendMessage(message);
                        }
                    }
                };
                t.start();

            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE ||motionEvent.getAction() == MotionEvent.ACTION_CANCEL||motionEvent.getAction() == MotionEvent.ACTION_HOVER_EXIT
                    || motionEvent.getAction() == MotionEvent.ACTION_POINTER_UP
                    || motionEvent.getAction() ==MotionEvent.ACTION_UP)
            {
                Log.i("smile","index "+(index++)+" "+motionEvent.getAction());
                pressed =false;
            }
            return false;
        }
    }
}
