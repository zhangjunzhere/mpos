package com.itertk.app.mpos.trade.pos;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.dbhelper.Product;
import com.itertk.app.mpos.locationhelper.Bank;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smile_gao on 2015/3/25.
 * 搜索商品列表
 */
public class SearchProductListDialog extends Dialog {
    String TAG = "SearchProductListDialog";
    Button btnBack;
    ListView listproudcts;
    Product mSelectProduct;
    List<Product> mProductList ;
    ProductListAdapter productListAdapter;

    public SearchProductListDialog(Context context, int theme,List<Product> list){
        super(context, theme);
        mProductList = list;

    }
    public  Product getSelectProduct()
    {
        return mSelectProduct;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_product_choose);


        btnBack = (Button)findViewById(R.id.dialogback);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchProductListDialog.this.dismiss();
            }
        });

        listproudcts = (ListView)findViewById(R.id.listproduct);
        productListAdapter =  new ProductListAdapter();
        listproudcts.setAdapter(productListAdapter);


        listproudcts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mSelectProduct =(Product) productListAdapter.getItem(position);
                dismiss();
            }
        });





    }
    class  ProductListAdapter extends BaseAdapter{
        ProductListAdapter(){
            if(mProductList==null)
            {
                mProductList = new ArrayList<Product>();
            }
        }


        @Override
        public int getCount() {
            return mProductList.size();
        }

        @Override
        public Object getItem(int i) {
            return mProductList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            final int pos = i;

            View view = convertView;
            if(view == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.chooseproduct_list_item, null);
            }

            TextView pname = (TextView)view.findViewById(R.id.productname);
            pname.setText(mProductList.get(i).getProd_name());
            TextView barcode = (TextView)view.findViewById(R.id.barcode);
            barcode.setText(mProductList.get(i).getBar_code());
            TextView price = (TextView) view.findViewById(R.id.price);
            price.setText(DecimalHelper.getDecimalString(mProductList.get(i).getLocalPrice())+"元");

            return view;
        }
    }
}
