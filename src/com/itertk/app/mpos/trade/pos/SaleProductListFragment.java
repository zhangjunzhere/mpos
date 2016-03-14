package com.itertk.app.mpos.trade.pos;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.dbhelper.Catalog;
import com.itertk.app.mpos.dbhelper.Product;

import java.text.DecimalFormat;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * 销售商品统计
 */
public class SaleProductListFragment extends Fragment implements View.OnClickListener {
    ListView listData = null;
    Button btnBack = null;
    Catalog catalog = null;
    TextView textCatalogName;

    SaleProductAdapter saleProductAdapter = null;

    private void onbtnBack() {
      //  ((PosActivity) getActivity()).onSaleProductListFragmentBack();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onbtnBack();
                break;
        }
    }

    public SaleProductListFragment() {
        // Required empty public constructor
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sale_product_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textCatalogName = (TextView) view.findViewById(R.id.textCatalogName);
        textCatalogName.setText(catalog.getName());

        listData = (ListView) view.findViewById(R.id.listData);
        btnBack = (Button) view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        saleProductAdapter = new SaleProductAdapter();
        listData.setAdapter(saleProductAdapter);


        listData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = (Product) parent.getAdapter().getItem(position);
                ((PosActivity) getActivity()).onSaleProductItemClick(product);
            }
        });
    }

    private class SaleProductAdapter extends BaseAdapter {
        List<Product> productList;
        Catalog catalog;

        public SaleProductAdapter() {
            this.catalog = SaleProductListFragment.this.catalog;
            catalog.resetProductList();
            productList = catalog.getProductList();
        }

        @Override
        public int getCount() {
            return productList.size();
        }

        @Override
        public Object getItem(int position) {
            return productList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            catalog.resetProductList();
            productList = catalog.getProductList();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            Product product = productList.get(pos);
            View view = convertView;
            ViewHold viewHold;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.sale_product_list_item, null);

                viewHold = new ViewHold();
                view.setTag(viewHold);

                viewHold.imageView = (ImageView) view.findViewById(R.id.ItemImage);
                viewHold.textProduct = (TextView) view.findViewById(R.id.ItemText);
                viewHold.textPrice = (TextView) view.findViewById(R.id.textPrice);
            } else {
                viewHold = (ViewHold) view.getTag();
            }


            viewHold.textPrice.setText(product.getProd_name());

//            Bitmap bitmap = BitmapFactory.decodeByteArray(product.getDataPhoto(), 0, product.getDataPhoto().length);
//            viewHold.imageView.setImageBitmap(bitmap);

            viewHold.textPrice.setText("￥" + (new DecimalFormat("0.00")).format(Arith.newBigDecimal(product.getLocalPrice())));

            return view;
        }

    }

    static class ViewHold {
        ImageView imageView;
        TextView textProduct;
        TextView textPrice;
    }
}
