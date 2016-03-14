/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.itertk.app.mpos.trade.taobao;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itertk.app.mpos.dbhelper.Catalog;
import com.itertk.app.mpos.dbhelper.CatalogDao;
import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;

import com.itertk.app.mpos.utility.DataHelper;
import com.itertk.app.mpos.dbhelper.Product;
import com.itertk.app.mpos.dbhelper.ProductDao;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.DecimalFormat;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * 商品列表fragment
 */
public class ProductListFragment extends Fragment {

    final long CATALOG_NOT_EXIST = -2L;

    //    String[] imageUrls = Constants.IMAGES;
    DisplayImageOptions options;

    ImageAdapter mProductListAdapter;
    Long mCategoryId = CATALOG_NOT_EXIST;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.item_image)
                .showImageForEmptyUri(R.drawable.item_image)
                .showImageOnFail(R.drawable.item_image)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        mCategoryId = getArguments().getLong(CatalogDao.Properties.Category_id.toString(),CATALOG_NOT_EXIST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gridview_product_order, container, false);
        GridView listView = (GridView) rootView.findViewById(R.id.grid);


        mProductListAdapter = new ImageAdapter();
        listView.setAdapter(mProductListAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product busiBase = (Product)mProductListAdapter.getItem(position);
                ProductDetailDialog productDetailDialog =new ProductDetailDialog(getActivity(), R.style.MyDialog,busiBase);
                productDetailDialog.show();
            }
        });
        return rootView;
    }

    public void refreshProduct(long categoryId,String condition){
         mProductListAdapter.refreshProduct(categoryId, condition);
    }

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        ProductDao productManager;
        List<Product> products;

        class ViewHolder {
            ImageView iv_product_image;
            TextView tv_product_name;
            TextView tv_product_price;
        }

        ImageAdapter() {
            inflater = LayoutInflater.from(getActivity());
            productManager =  ((MPosApplication) getActivity().getApplication()).getDataHelper().getProductManager();
            products = productManager.queryBuilder().where(
                    ProductDao.Properties.Supplier.notEq(DataHelper.LOCAL_SUPPLIER),ProductDao.Properties.Category_id.eq(mCategoryId)).list();
        }

       public void refreshProduct(long categoryId,String condition){
            QueryBuilder queryBuilder = productManager.queryBuilder();

            if(categoryId != -1L){
                queryBuilder = queryBuilder.where(ProductDao.Properties.Category_id.eq(categoryId));
            }
            if(!TextUtils.isEmpty(condition)) {
                queryBuilder = queryBuilder.where(ProductDao.Properties.Prod_name.like("%"+condition + "%"));
            }

           products = queryBuilder.where(ProductDao.Properties.Supplier.notEq(DataHelper.LOCAL_SUPPLIER)).list();
           
            notifyDataSetChanged();
       }

        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public Product getItem(int position) {
            return products.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.grid_item_product_order, parent, false);
                holder = new ViewHolder();
                holder.iv_product_image = (ImageView) view.findViewById(R.id.iv_product_image);
                holder.tv_product_name =  (TextView) view.findViewById(R.id.tv_product_name);
                holder.tv_product_price =  (TextView) view.findViewById(R.id.tv_product_price);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Product product = getItem(position);
            holder.tv_product_name.setText(product.getProd_name());

            String price = (new DecimalFormat("0.00")).format(Arith.newBigDecimal(product.getPrice()));
            price = String.format(view.getContext().getResources().getString(R.string.product_price),price);
            holder.tv_product_price.setText(price);

            String image_url = getItem(position).getImage_url();
            ImageLoader.getInstance().displayImage(image_url, holder.iv_product_image, options, null);

            return view;
        }
    }
}