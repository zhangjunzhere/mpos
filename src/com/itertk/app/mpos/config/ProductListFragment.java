package com.itertk.app.mpos.config;



import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.dbhelper.Catalog;
import com.itertk.app.mpos.dbhelper.CatalogDao;
import com.itertk.app.mpos.dbhelper.Product;
import com.itertk.app.mpos.dbhelper.ProductDao;
import com.itertk.app.mpos.utility.DataHelper;
import com.itertk.app.mpos.utility.ToastHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *修改商品主界面
 */
public class ProductListFragment extends Fragment implements View.OnClickListener {
    static final String TAG = "ProductListFragment";
    ListView listData = null;
    Button btnAdd = null;
    Button btnBack = null;
    Catalog catalog = null;
    TextView textCatalogName;

    AddProductFragment addProductFragment = null;
    ModProductFragment modProductFragment = null;
    FragmentTransaction transaction = null;

    ProductAdapter productAdapter = null;

    public long addProduct(Product product){
        return productAdapter.addProduct(product);
    }

    public long modProduct(Product product){
        return productAdapter.modProduct(product);
    }
    public boolean isBarcodeExist(String newBarcode,Product product){
        return productAdapter.isBarcodeExist(newBarcode,product);
    }

    private void onbtnAddProduct(){
        addProductFragment = new AddProductFragment();
        addProductFragment.setParames(this, null);
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.itemArea, addProductFragment);
        transaction.commit();
        productAdapter.preSelect=-1;
        productAdapter.notifyDataSetChanged();
    }
    //smile_gao add for scan code
    public  void setScanCode(String scancode)
    {
        if(addProductFragment != null)
        {
            addProductFragment.setScancode(scancode);
        }
    }

    private void onbtnModProduct(Product product){
        modProductFragment = new ModProductFragment();
        modProductFragment.setParames(this, product);
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.itemArea, modProductFragment);
        transaction.commit();
    }

    private void onbtnDelProduct(Product product){
        productAdapter.delProduct(product);
    }

    private void onbtnBack(){
        ((ProductConfigActivity)getActivity()).onProductListFragmentBack();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAdd: onbtnAddProduct(); break;
            case R.id.btnBack: onbtnBack(); break;
        }
    }

    public ProductListFragment(){
    }

    public void setParames(Catalog catalog) {
        // Required empty public constructor
        this.catalog = catalog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        if(catalog!=null){
            savedInstanceState.putLong("CATALOG", catalog.getCategory_id());
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState!=null){
            long id=savedInstanceState.getLong("CATALOG");
            CatalogDao catalogManager;
            catalogManager = ((MPosApplication)getActivity().getApplication()).getDataHelper().getDaoSession().getCatalogDao();
            catalog=catalogManager.load(id);
        }

        //textCatalogName = (TextView)view.findViewById(R.id.textCatalogName);
        //textCatalogName.setText(catalog.getName());

        listData = (ListView)view.findViewById(R.id.listData);

        btnAdd = (Button)view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

       // btnBack = (Button)view.findViewById(R.id.btnBack);
       // btnBack.setOnClickListener(this);
        productAdapter = new ProductAdapter();
        listData.setAdapter(productAdapter);
    }



    private class ProductAdapter extends BaseAdapter {
        Catalog catalog;
        final ListView listView;
        ProductDao productManager;
        List<Product> productList;
        int preSelect = -1;

        public boolean isBarcodeExist(String barcode,Product product){
            List<Product> list=productManager.queryBuilder().where(ProductDao.Properties.Bar_code.eq(barcode)).list();
            if(list.size()>0){
                for(int i=0;i<list.size();i++) {
                    if (list.get(i).getId()!=product.getId()){
                        return true;
                    }
                }
            }
            return false;
        }

        public long addProduct(Product product){
            long result = 0;
            List<Product> list=productManager.queryBuilder().where(ProductDao.Properties.Bar_code.eq(product.getBar_code())).list();
            if(list.size()>0){
                return -1;
            }
            try {
                result  = productManager.insert(product);
                notifyDataSetChanged();
                if(addProductFragment!=null) {
                    transaction = getFragmentManager().beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.remove(addProductFragment);
                    transaction.commit();
                }
                listView.setSelection(productList.size()-1);
            }catch (Exception e){
                Log.e(TAG, "insert " + product.toString() + " error:" + e.toString());
                result = -1;
            }

            return result;
        }

        public long modProduct(Product product){
            long result = 0;
/*            List<Product> list=productManager.queryBuilder().where(ProductDao.Properties.Bar_code.eq(product.getBar_code())).list();
            if(list.size()>0){
                for(int i=0;i<list.size();i++) {
                    if (list.get(i).getId()!=product.getId()){
                        return -1;
                    }
                }
                //return -1;
            }*/
            try {
                productManager.update(product);
                notifyDataSetChanged();
                if(modProductFragment!=null) {
                    transaction = getFragmentManager().beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.remove(modProductFragment);
                    transaction.commit();
                }
                Toast.makeText(getActivity(),"商品修改成功",Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Log.e(TAG, "modify " + product.toString() + " error:" + e.toString());
                Toast.makeText(getActivity(),"商品修改失败",Toast.LENGTH_SHORT).show();
                result = -1;
            }

            return result;
        }

        public long delProduct(Product product){
            long result = 0;
            try {
                productManager.delete(product);
                notifyDataSetChanged();
            }catch (Exception e){
                Log.e(TAG, "delete " + product.toString() + " error:" + e.toString());
                result = -1;
            }

            return result;
        }

        ProductAdapter(){
            listView = ProductListFragment.this.listData;
            productManager = ((MPosApplication)getActivity().getApplication()).getDataHelper().getProductManager();
            this.catalog = ProductListFragment.this.catalog;
            catalog.resetProductList();
            //productList = catalog.getProductList();
            productList=productManager.loadAll();
        }


        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            catalog.resetProductList();
            //productList = catalog.getProductList();
            productList=productManager.loadAll();
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            Product product = productList.get(pos);
            View view = convertView;
            ViewHold viewHold;
            if(view == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.product_list_item, null);

                viewHold = new ViewHold();
                view.setTag(viewHold);

                viewHold.textProductName = (TextView)view.findViewById(R.id.ItemText);
                viewHold.textPrice = (TextView)view.findViewById(R.id.textPrice);
                viewHold.imageProduct = (ImageView)view.findViewById(R.id.ItemImage);

                viewHold.btnModify = (ImageButton) view.findViewById(R.id.btnModify);
                viewHold.btnDelete = (ImageButton) view.findViewById(R.id.btnDelete);
                viewHold.imageProduct.setImageResource(R.drawable.item_image);

            }else {
                viewHold = (ViewHold)view.getTag();
                if(preSelect == pos) {
                    viewHold.btnModify.setImageResource(R.drawable.btn_modify_2);
                }
                else {
                    viewHold.btnModify.setImageResource(R.drawable.btn_modify);
                }
                viewHold.imageProduct.setImageResource(R.drawable.item_image);

            }

            viewHold.btnModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Product product = productList.get(pos);

                    ProductListFragment.this.onbtnModProduct(product);
                    Log.d("modify click", "position=" + pos);

                    ((ImageButton)v).setImageResource(R.drawable.btn_modify_2);

                    if(preSelect < 0 || preSelect == pos){}
                    else{
                        View preView = listView.getChildAt(preSelect);
                        if(preView != null)
                            ((ImageButton)preView.findViewById(R.id.btnModify)).setImageResource(R.drawable.btn_modify);
                    }
                    preSelect = pos;
                    notifyDataSetChanged();
                }
            });

            viewHold.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Product product = productList.get(pos);
                    ProductListFragment.this.onbtnDelProduct(product);
                    preSelect=-1;
                    if(modProductFragment!=null) {
                        transaction = getFragmentManager().beginTransaction();
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        transaction.remove(modProductFragment);
                        transaction.commit();
                    }

                    Log.v("btnDelete", "position="+pos);
                }
            });

            //viewHold.imageProduct.setImageBitmap(BitmapFactory.decodeByteArray(product.getDataPhoto(), 0, product.getDataPhoto().length));
            //Log.i("smile","imgurl:"+product.getImage_url());
            if(!product.getImage_url().toLowerCase().startsWith("http"))
            {
                String imageUri="file:///"+product.getImage_url();
                ImageLoader.getInstance().displayImage(imageUri, viewHold.imageProduct);


                viewHold.btnDelete.setAlpha(1.0f);
                viewHold.btnDelete.setEnabled(true);
            }
            else
            {
                ImageLoader.getInstance().displayImage(product.getImage_url(),  viewHold.imageProduct);
                viewHold.btnDelete.setAlpha(0.5f);
                viewHold.btnDelete.setEnabled(false);
            }

            viewHold.textProductName.setText(product.getProd_name());
            viewHold.textPrice.setText("￥"+ product.getLocalPrice());
            return view;
        }

    }

    static class ViewHold{
        TextView textProductName;
        TextView textPrice;
        ImageView imageProduct;
        ImageButton btnModify;
        ImageButton btnDelete;
    }

}
