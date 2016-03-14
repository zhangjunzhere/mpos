package com.itertk.app.mpos.trade.pos;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.utility.ToastHelper;
import com.itertk.app.mpos.dbhelper.Product;
import com.itertk.app.mpos.dbhelper.ProductDao;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by smile_gao on 2015/3/25.
 * 为扫描到货物添加窗口
 */
public class NotFindProductDialog extends  Dialog{

    String TAG = "SearchProductListDialog";
    Button btnBack;
    Button btnOk;
    TextView tvBarcode;
    EditText etName;
    EditText etPrice;
    String scanCode;
    BigDecimal price = Arith.newZeroBigDecimal();
    Product newProduct;
    Activity mPosActivity;
    public NotFindProductDialog(Activity context, int theme,String scancode){
        super(context, theme);
        scanCode = scancode;
        mPosActivity = context;

    }
    public  Product getNewProduct()
    {
        return  newProduct;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_product_notfound);

        etName = (EditText) findViewById(R.id.name);
        etPrice = (EditText) findViewById(R.id.price);
        etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i2, int i3) {
//                int dotindex = s.toString().indexOf(".");
//                Log.i("smile","dotindex: "+dotindex +" "+s.length());
//                if(dotindex ==-1)
//                {
//                    if(s.length()>8)
//                    {
//                        etPrice.setText(s.subSequence(0,8));
//                    }
//                }
//                else
//                {
//                    if(dotindex>=7)
//                    {
//                        etPrice.setText(s);
//                    }
//                }
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i2, int i3) {

                int dotindex = s.toString().indexOf(".");

                if(dotindex ==-1)
                {
                    if(s.length()>8)
                    {
                        s = s.subSequence(0,8);
                        etPrice.setText(s);
                        etPrice.setSelection(s.length());
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
                        etPrice.setText(s);
                        etPrice.setSelection(i);
                        return;
                    }
                }

                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        etPrice.setText(s);
                        etPrice.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etPrice.setText(s);
                    etPrice.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        etPrice.setText(s.subSequence(0, 1));
                        etPrice.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        tvBarcode = (TextView) findViewById(R.id.barcode);
        tvBarcode.setText(scanCode);
        btnOk = (Button) findViewById(R.id.ok);

        btnBack = (Button)findViewById(R.id.dialogback);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSave();
            }
        });



    }
    void onSave()
    {

        String str = etPrice.getText().toString().trim();
        try{
            price = Arith.newBigDecimal(str);
        }catch (Exception e)
        {
            ToastHelper.showToast(getContext(),"价格输入错误");
            return;
        }
//        if(Arith.compareMax(price)>0)
//        {
//            ToastHelper.showToast(getContext(),"价格不能超过10,000,000");
//        }
 //       long barcodenum =1;

        String name = etName.getText().toString();
        if(name.length()==0)
        {
            name = "未知";
        }
        else if(name.length()>20)
        {
            ToastHelper.showToast(getContext(),"名称不能超过20");
            return;
        }

        Product p = new Product(System.currentTimeMillis(),scanCode,"","","",name,"",price.toString(),price.toString(),1,1L);
        ProductDao  productManager = ((MPosApplication)mPosActivity.getApplication()).getDataHelper().getProductManager();
        List<Product> list = productManager.queryBuilder().where(ProductDao.Properties.Bar_code.eq(scanCode)).list();
        if(list ==null || list.size() ==0) {
            long productId = productManager.insert(p);
            if (productId > 0) {
                newProduct = p;
                ToastHelper.showToast(getContext(),"添加商品成功");
            } else {
                newProduct = null;
                ToastHelper.showToast(getContext(),"添加商品失败");
            }
        }
        else
        {
            Log.i("NtfindProduct","exist");
        }
        dismiss();
    }

}
