package com.itertk.app.mpos.trade.pos;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.TextFocusChangeListener;
import com.itertk.app.mpos.utility.ToastHelper;
import com.itertk.app.mpos.dbhelper.Reduce;
import com.itertk.app.mpos.dbhelper.ReduceDao;
import com.itertk.app.mpos.dbhelper.SaleOrderItem;
import com.itertk.app.mpos.dbhelper.SaleOrderItemAttributeValueDao;
import com.itertk.app.mpos.dbhelper.SaleOrderItemReduce;
import com.itertk.app.mpos.dbhelper.SaleOrderItemReduceDao;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/7/14.
 * 修改手动对话框
 */
public class ModSaleManualDialog extends Dialog {
    static final String TAG = "ModSaleManualDialog";
    SaleOrderItem saleOrderItem;
    TextView textPrice;
    ImageButton btnCut;
    ImageButton btnAdd;
    EditText textCount;
    EditText textProductName;
    Spinner spinnerReduce;
    Button btnOK;
    long count;
    BigDecimal priceTotal = Arith.newZeroBigDecimal();


    ReduceSpinnerAdapter reduceSpinnerAdapter;

    Context context;

    public ModSaleManualDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public ModSaleManualDialog(Context context, int theme, SaleOrderItem saleOrderItem) {
        super(context, theme);
        this.context = context;
        this.saleOrderItem = saleOrderItem;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_mod_sale_manual);

        textCount = (EditText) findViewById(R.id.textCount);
        textPrice = (TextView) findViewById(R.id.textPrice);
        btnCut = (ImageButton) findViewById(R.id.btnCut);
        btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        TextFocusChangeListener textFocusChangeListener = new TextFocusChangeListener();
        textProductName = (EditText) findViewById(R.id.textProductName);
        textProductName.setOnFocusChangeListener(textFocusChangeListener);
        textProductName.setText(saleOrderItem.getName());
        spinnerReduce = (Spinner) findViewById(R.id.spinnerReduce);
        btnOK = (Button) findViewById(R.id.btnOK);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MPosApplication) ModSaleManualDialog.this.getContext().getApplicationContext()).getDataHelper().getDaoSession().runInTx(new Runnable() {
                    @Override
                    public void run() {
                        if (textProductName.length() != 0) {
                            saleOrderItem.setName(textProductName.getText().toString());
                        }
                        saleOrderItem.setPrice(priceTotal.toString());
                        saleOrderItem.setCountProduct(count);
                        saleOrderItem.update();

                        Reduce reduce = reduceSpinnerAdapter.getReduceSelected();
                        SaleOrderItemReduceDao saleOrderItemReduceManager = ((MPosApplication) getContext().getApplicationContext())
                                .getDataHelper().getDaoSession().getSaleOrderItemReduceDao();

                        saleOrderItemReduceManager.queryBuilder()
                                .where(SaleOrderItemAttributeValueDao.Properties.SaleOrderItemId.eq(saleOrderItem.getSaleOrderItemId()))
                                .buildDelete().executeDeleteWithoutDetachingEntities();

                        if (reduce != null && reduce.getReduceId() > 0) {
                            SaleOrderItemReduce saleOrderItemReduce = new SaleOrderItemReduce(null, saleOrderItem.getSaleOrderItemId(), reduce.getReduceId());
                            saleOrderItemReduceManager.insert(saleOrderItemReduce);
                        }
                    }
                });

                ((PosActivity) context).modSaleOrderItem(saleOrderItem);

                ModSaleManualDialog.this.dismiss();
            }
        });


        btnCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        MyOnTouchListener subListener = new MyOnTouchListener(CheckoutFragment.MSG_SUB);
        btnCut.setOnTouchListener(subListener);

        MyOnTouchListener addListener = new MyOnTouchListener(CheckoutFragment.MSG_ADD);
        btnAdd.setOnTouchListener(addListener);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        count = saleOrderItem.getCountProduct();
        priceTotal = Arith.newBigDecimal(saleOrderItem.getPrice());

        textCount.setText(String.valueOf(count));
        textPrice.setText("￥" + (new DecimalFormat("0.00")).format(priceTotal));
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

                    ToastHelper.showToast(getContext(),"数量不能超过999");
                    newcount =999;
                }
                count = newcount;
                textCount.setText(String.valueOf(count));
            }
        });

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
    private void updatePriceDisplay() {
        priceTotal =Arith.mulBig(getOnePrice() , count);
        textPrice.setText("￥" + (new DecimalFormat("0.00")).format(priceTotal));
    }

    private BigDecimal getOnePrice() {
        BigDecimal onePrice =Arith.newBigDecimal(saleOrderItem.getOnePrice());

        Reduce reduce = reduceSpinnerAdapter.getReduceSelected();
        if (reduce != null) {
            if (reduce.getType() == 1) {
                onePrice =Arith.div(Arith.mulBig(onePrice , reduce.getValue()) , 100);
            } else if (reduce.getType() == 0) {
                onePrice = onePrice.subtract(Arith.newBigDecimal(reduce.getValue()));
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
            reduceManager = ((MPosApplication) ModSaleManualDialog.this.getContext().getApplicationContext()).getDataHelper().getReduceManager();
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
