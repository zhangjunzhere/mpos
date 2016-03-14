package com.itertk.app.mpos.trade.taobao;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.dbhelper.Catalog;
import com.itertk.app.mpos.dbhelper.CatalogDao;
import com.itertk.app.mpos.trade.TradeHomeActivity;
import com.itertk.app.mpos.utility.DataHelper;
import com.itertk.app.mpos.utility.ShopOrderOperation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.util.List;

public class GoodsOrderActivity extends Activity implements View.OnClickListener{

    ProductListFragment goodsListFragment  = null;
    FragmentTransaction transaction;
    DisplayImageOptions options;

    private long mCategoryId = -1L;
    private EditText mEt_condition;
    CategoryAdapter mCategoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_order);
        MyActionbar.setNormalActionBarLayout(this);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.order_recommend)
                .showImageForEmptyUri(R.drawable.order_recommend)
                .showImageOnFail(R.drawable.order_recommend)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        ListView listView = (ListView) findViewById(R.id.lv_catelog);
          mCategoryAdapter  = new CategoryAdapter();
        listView.setAdapter(mCategoryAdapter);

        mEt_condition  =  (EditText) findViewById(R.id.et_condition);
        mEt_condition.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String condition  = mEt_condition.getText().toString();
                goodsListFragment.refreshProduct(mCategoryId, condition);
            }
        });

        listView.requestFocus();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Catalog category  = mCategoryAdapter.getItem(i);
                mCategoryId = category.getCategory_id();
                String condition  = mEt_condition.getText().toString();
                goodsListFragment.refreshProduct(mCategoryId, condition);
            }
        });
        
        findViewById(R.id.btnCart).setOnClickListener(this);
        findViewById(R.id.btnOrder).setOnClickListener(this);
		
		 initProductListFragment();      
    }
    

    
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btnCart:
                ShoppingCartDialog shoppingCartDialog =new ShoppingCartDialog(this, R.style.MyDialog);
                shoppingCartDialog.show();
                break;

            case R.id.btnOrder:
                ShoppingOrderDialog shoppingOrderDialog =new ShoppingOrderDialog(this, R.style.MyDialog, R.id.order_category_unpay);
                shoppingOrderDialog.show();
                break;
            case R.id.btn_catalog:
                onCatalogItemClicked(v);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MPosApplication.getInstance().getMyActivityManager().setBackToActivityClass(GoodsOrderActivity.class);
    }

    private void onCatalogItemClicked(View view){
        CategoryAdapter.ViewHolder holder = (CategoryAdapter.ViewHolder)view.getTag();
        Catalog catalog = holder.catalog;
        mCategoryId = catalog.getCategory_id();
        String condition  = mEt_condition.getText().toString();
        goodsListFragment.refreshProduct(mCategoryId, condition);
        mCategoryAdapter.refresh(mCategoryId);
    }
  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.system_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        ImageLoader.getInstance().stop();
        super.onBackPressed();
    }

    private void initProductListFragment(){

        if(goodsListFragment == null){
            goodsListFragment = new ProductListFragment();
            Bundle bundle = new Bundle();
            bundle.putLong(CatalogDao.Properties.Category_id.toString(),mCategoryId);
            goodsListFragment.setArguments(bundle);
        }

        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.set_container, goodsListFragment);
        transaction.commit();
    }

    private class CategoryAdapter extends BaseAdapter {

        private List<Catalog> categoryList;

        public CategoryAdapter(){
            DataHelper dataHelper =  MPosApplication.getInstance().getDataHelper();
            CatalogDao categoryDao = dataHelper.getDaoSession().getCatalogDao();
            categoryList = categoryDao.loadAll();
            if (categoryList != null && categoryList.size() > 0) {
                //set default select index is 1.
                mCategoryId = categoryList.get(0).getCategory_id();
            }
        }

        public class ViewHolder{
            public LinearLayout btn_catalog;
            public ImageView iv_category;
            public TextView tv_category;
            public Catalog catalog;
        }

        public void refresh(Long catalogId){
            mCategoryId = catalogId;
            notifyDataSetChanged();
        }



        @Override
        public int getCount() {
            return categoryList.size();
        }
        @Override
        public Catalog getItem(int position) {
            return categoryList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getBaseContext());
                convertView = inflater.inflate(R.layout.list_item_product_category, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_category = (ImageView) convertView.findViewById(R.id.iv_category);
                viewHolder.tv_category = (TextView) convertView.findViewById(R.id.tv_category);
                convertView.setTag(viewHolder);
            }else{
                viewHolder =(ViewHolder) convertView.getTag();
            }

            Catalog category = getItem(position);
            viewHolder.tv_category.setText(category.getName());
            ImageLoader.getInstance().displayImage(category.getIconUrl(),viewHolder.iv_category,options);

            viewHolder.catalog = category;
            convertView.setOnClickListener(GoodsOrderActivity.this);


          
            Boolean selected = (category.getCategory_id() == mCategoryId);
            if (selected) {
                convertView.setBackground(getResources().getDrawable(R.color.btn_blue));
                viewHolder.tv_category.setTextColor(getResources().getColor(R.color.solid_white));
            }else {
                convertView.setBackground(getResources().getDrawable(R.color.transparent));
                viewHolder.tv_category.setTextColor( getResources().getColor(R.color.text_normal_color));
            }
            return convertView;
        }
    }

}
