package com.itertk.app.mpos.trade.pos;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.utility.ToastHelper;
import com.itertk.app.mpos.dbhelper.Attribute;
import com.itertk.app.mpos.dbhelper.AttributeValue;
import com.itertk.app.mpos.dbhelper.ProductAttribute;
import com.itertk.app.mpos.dbhelper.Reduce;
import com.itertk.app.mpos.dbhelper.ReduceDao;
import com.itertk.app.mpos.dbhelper.SaleOrderItem;
import com.itertk.app.mpos.dbhelper.SaleOrderItemAttributeValue;
import com.itertk.app.mpos.dbhelper.SaleOrderItemAttributeValueDao;
import com.itertk.app.mpos.dbhelper.SaleOrderItemReduce;
import com.itertk.app.mpos.dbhelper.SaleOrderItemReduceDao;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/7/14.
 * 修改商品对话框
 */
public class ModSaleProductDialog extends Dialog {
    static final String TAG = "ModSaleProductDialog";
    Context context;
    SaleOrderItem saleOrderItem;
    TextView textNameTitle;
    TextView textPrice;
    ImageButton btnCut;
    ImageButton btnAdd;
    EditText textCount;
    Spinner spinnerReduce;
    Button btnOK;
    LinearLayout containerGridList;
    long count;
    BigDecimal priceTotal = Arith.newZeroBigDecimal();
    ReduceSpinnerAdapter reduceSpinnerAdapter;
    List<GridViewAdapter> gridViewAdapterList;

    public ModSaleProductDialog(Context context, int theme, SaleOrderItem saleOrderItem) {
        super(context, theme);
        this.context = context;
        this.saleOrderItem = saleOrderItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_mod_sale_product);

        gridViewAdapterList = new ArrayList<GridViewAdapter>();
        textNameTitle = (TextView) findViewById(R.id.textNameTitle);
        textNameTitle.setText(saleOrderItem.getName());
        textCount = (EditText) findViewById(R.id.textCount);
        textPrice = (TextView) findViewById(R.id.textPrice);
        btnCut = (ImageButton) findViewById(R.id.btnCut);
        btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        spinnerReduce = (Spinner) findViewById(R.id.spinnerReduce);
        btnOK = (Button) findViewById(R.id.btnOK);
        containerGridList = (LinearLayout) findViewById(R.id.containerGridList);


        count = saleOrderItem.getCountProduct();
        priceTotal = Arith.newBigDecimal(saleOrderItem.getPrice());

        textCount.setText(String.valueOf(count));
        textCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newcountstr=textCount.getText().toString();
                int newcount = 1;
                try{
                    newcount = Integer.parseInt(newcountstr);
                    if(newcount==count)
                    {
                        return;
                    }
                }catch (Exception e)
                {
                    count = 1;
                }
                if(newcount>999)
                {
                    ToastHelper.showToast(getContext(),"不能超过最大数量999");
                    newcount =999;
                }
                count = newcount;
                textCount.setText(String.valueOf(count));
            }
        });
        textPrice.setText("￥" + (new DecimalFormat("0.00")).format(priceTotal));

        createAttributeValueGridViewes();


        reduceSpinnerAdapter = new ReduceSpinnerAdapter(getContext(), R.layout.spinner_reduce, R.id.txtvwSpinner);
        reduceSpinnerAdapter.setDropDownViewResource(R.layout.spinner_reduce_item);
        spinnerReduce.setAdapter(reduceSpinnerAdapter);
        saleOrderItem.resetSaleOrderItemReduceList();
        if (saleOrderItem.getSaleOrderItemReduceList().size() > 0) {
            reduceSpinnerAdapter.setReduceSelected(saleOrderItem.getSaleOrderItemReduceList().get(0).getReduce());
        } else {
            reduceSpinnerAdapter.setReduceSelected(null);
        }


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

        MyOnTouchListener sublistener = new MyOnTouchListener(CheckoutFragment.MSG_SUB);
        btnCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // subCount();
            }
        });
        btnCut.setOnTouchListener(sublistener);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  addCount();
            }
        });
        MyOnTouchListener addlistener = new MyOnTouchListener(CheckoutFragment.MSG_ADD);
        btnAdd.setOnTouchListener(addlistener);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MPosApplication) getContext().getApplicationContext()).getDataHelper().getDaoSession().runInTx(new Runnable() {
                    @Override
                    public void run() {
                        saleOrderItem.setCountProduct(count);
                        saleOrderItem.setPrice(priceTotal.toString());
                        saleOrderItem.update();

                        Reduce reduce = reduceSpinnerAdapter.getReduceSelected();

                        SaleOrderItemReduceDao saleOrderItemReduceManager = ((MPosApplication) getContext().getApplicationContext())
                                .getDataHelper().getDaoSession().getSaleOrderItemReduceDao();

                        saleOrderItemReduceManager.queryBuilder()
                                .where(SaleOrderItemAttributeValueDao.Properties.SaleOrderItemId.eq(saleOrderItem.getSaleOrderItemId()))
                                .buildDelete().executeDeleteWithoutDetachingEntities();

                        if (reduce != null && reduce.getReduceId() > 0) {
                            Log.d(TAG, "" + reduce.getName());
                            SaleOrderItemReduce saleOrderItemReduce = new SaleOrderItemReduce(null, saleOrderItem.getSaleOrderItemId(), reduce.getReduceId());
                            saleOrderItemReduceManager.insert(saleOrderItemReduce);
                        }


                        SaleOrderItemAttributeValueDao saleOrderItemAttributeValueManager = ((MPosApplication) getContext().getApplicationContext())
                                .getDataHelper().getDaoSession().getSaleOrderItemAttributeValueDao();

                        saleOrderItemAttributeValueManager.queryBuilder()
                                .where(SaleOrderItemAttributeValueDao.Properties.SaleOrderItemId.eq(saleOrderItem.getSaleOrderItemId()))
                                .buildDelete().executeDeleteWithoutDetachingEntities();

                        for (GridViewAdapter gridViewAdapter : gridViewAdapterList) {
                            List<AttributeValueSelect> attributeValueSelectList = gridViewAdapter.getAttributeValueSelectList();
                            for (AttributeValueSelect attributeValueSelect : attributeValueSelectList) {
                                if (attributeValueSelect.selected) {
                                    SaleOrderItemAttributeValue saleOrderItemAttributeValue =
                                            new SaleOrderItemAttributeValue(null, saleOrderItem.getSaleOrderItemId(), attributeValueSelect.attributeValue.getValueId());
                                    saleOrderItemAttributeValueManager.insert(saleOrderItemAttributeValue);
                                }
                            }
                        }
                    }
                });


                ((PosActivity) context).modSaleOrderItem(saleOrderItem);

                ModSaleProductDialog.this.dismiss();
            }
        });
    }
    private  void subCount()
    {
        if (count > 1) {
            count--;
            textCount.setText(String.valueOf(count));
            updatePriceDisplay();
        }
    }
    private void addCount()
    {
        if(count>=CheckoutFragment.MAX_CART)
        {
            return;
        }
        count++;
        textCount.setText(String.valueOf(count));
        updatePriceDisplay();
    }
    private void createAttributeValueGridViewes() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 10);
        int i = 0;
        for (ProductAttribute productAttribute : saleOrderItem.getProduct().getProductAttributeList()) {
            final GridView gridView = new GridView(getContext());
            gridView.setNumColumns(4);
            gridView.setVerticalSpacing(5);
            gridView.setId(0x100000 + (i++));
            gridView.setGravity(Gravity.LEFT);
            gridView.setPadding(0, 0, 0, 0);
            gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
            GridViewAdapter gridViewAdapter = new GridViewAdapter(saleOrderItem, productAttribute.getAttribute());
            gridView.setAdapter(gridViewAdapter);
            gridViewAdapterList.add(gridViewAdapter);
            containerGridList.addView(gridView, layoutParams);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GridViewAdapter gridViewAdapter = (GridViewAdapter) parent.getAdapter();
                    gridViewAdapter.setViewSelected(position);
                    updatePriceDisplay();
                }
            });
        }
    }

    private void updatePriceDisplay() {
        priceTotal = Arith.mulBig(getOnePrice() , count);
        textPrice.setText("￥" + (new DecimalFormat("0.00")).format(priceTotal));
    }

    private BigDecimal getOnePrice() {
        BigDecimal onePrice =Arith.newBigDecimal(saleOrderItem.getOnePrice());

        Reduce reduce = reduceSpinnerAdapter.getReduceSelected();
        if (reduce != null) {
            if (reduce.getType() == 1) {
                onePrice = Arith.div(onePrice.multiply(new BigDecimal(reduce.getValue())),new BigDecimal(100));// / 100;
            } else if (reduce.getType() == 0) {
                onePrice = onePrice.subtract(Arith.newBigDecimal(reduce.getValue()));
            }
        }

        for (GridViewAdapter gridViewAdapter : gridViewAdapterList) {
            List<AttributeValueSelect> attributeValueSelectList = gridViewAdapter.getAttributeValueSelectList();
            for (AttributeValueSelect attributeValueSelect : attributeValueSelectList) {
                if (attributeValueSelect.selected) {
                    onePrice =Arith.add(onePrice,attributeValueSelect.attributeValue.getPriceAddition());
                }
            }
        }


        return onePrice;
    }

    class ReduceSpinnerAdapter extends ArrayAdapter<String> {
        static final String TAG = "ReduceSpinnerAdapter";
        Reduce reduceSelected;
        ReduceDao reduceManager;
        List<Reduce> reduceList;
        ArrayList<Reduce> fakeReduceList;

        public ReduceSpinnerAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
            reduceManager = ((MPosApplication) ModSaleProductDialog.this.getContext().getApplicationContext()).getDataHelper().getReduceManager();
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
                LayoutInflater inflater = LayoutInflater.from(context);
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

    class AttributeValueSelect {
        public AttributeValue attributeValue;
        public boolean selected;

        AttributeValueSelect(AttributeValue attributeValue, boolean selected) {
            this.attributeValue = attributeValue;
            this.selected = selected;
        }
    }

    class GridViewAdapter extends BaseAdapter {
        List<AttributeValueSelect> attributeValueSelectList;
        Attribute attribute;
        SaleOrderItem saleOrderItem;
        List<SaleOrderItemAttributeValue> saleOrderItemAttributeValueList;
        SaleOrderItemAttributeValueDao saleOrderItemAttributeValueManager;


        public void setViewSelected(int position) {
            for (int i = 0; i < attributeValueSelectList.size(); i++) {
                if (i == position) {
                    attributeValueSelectList.get(i).selected = !attributeValueSelectList.get(i).selected;
                } else {
                    attributeValueSelectList.get(i).selected = false;
                }
            }
            notifyDataSetChanged();
        }


        void loadData() {
            saleOrderItem.resetSaleOrderItemAttributeValueList();
            saleOrderItemAttributeValueList = saleOrderItem.getSaleOrderItemAttributeValueList();

            attribute.resetAttributeValueList();
            for (AttributeValue attributeValue : attribute.getAttributeValueList()) {
                boolean selected = false;
                for (SaleOrderItemAttributeValue saleOrderItemAttributeValue : saleOrderItemAttributeValueList) {
                    if (saleOrderItemAttributeValue.getAttributeValueId() == attributeValue.getValueId()) {
                        selected = true;
                        break;
                    }
                }
                attributeValueSelectList.add(new AttributeValueSelect(attributeValue, selected));
            }
        }

        GridViewAdapter(SaleOrderItem saleOrderItem, Attribute attribute) {
            saleOrderItemAttributeValueManager = ((MPosApplication) getContext().getApplicationContext()).getDataHelper().getSaleOrderItemAttributeValueManager();
            this.saleOrderItem = saleOrderItem;
            this.attribute = attribute;
            attributeValueSelectList = new ArrayList<AttributeValueSelect>();
            loadData();
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();

        }

        public List<AttributeValueSelect> getAttributeValueSelectList() {
            return attributeValueSelectList;
        }

        @Override
        public int getCount() {
            return attributeValueSelectList.size();
        }

        @Override
        public Object getItem(int position) {
            return attributeValueSelectList.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final AttributeValueSelect attributeValueSelect = attributeValueSelectList.get(pos);
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.attribute_value_grid_item, null);
            }
            view.setTag(String.valueOf(position));

            final Button btnAttribute = (Button) view.findViewById(R.id.btnAttribute);
            btnAttribute.setText(attributeValueSelect.attributeValue.getName());

            if (attributeValueSelect.selected) {
                btnAttribute.setBackgroundResource(R.drawable.attribute_2);
            } else {
                btnAttribute.setBackgroundResource(R.drawable.attribute);
            }

            return view;
        }
    }
   class   MyOnTouchListener  implements View.OnTouchListener {
        boolean pressed=false;
        int index=0;
        Thread t;
       int currMsg;

       Handler h = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case CheckoutFragment.MSG_ADD:
                        addCount();
                        break;
                    case CheckoutFragment.MSG_SUB:
                        subCount();
                        break;
                }
            }
        };
       public MyOnTouchListener(int msg)
       {
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
            else if(motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE ||motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE || motionEvent.getAction() ==MotionEvent.ACTION_UP)
            {
                Log.i("smile","index "+(index++)+" "+motionEvent.getAction());
                pressed =false;
            }
            return false;
        }
    }
}
