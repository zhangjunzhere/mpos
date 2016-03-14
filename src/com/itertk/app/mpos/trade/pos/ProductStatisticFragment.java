package com.itertk.app.mpos.trade.pos;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.dbhelper.Product;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.dbhelper.SaleOrderDao;
import com.itertk.app.mpos.dbhelper.SaleOrderItem;
import com.itertk.app.mpos.dbhelper.SaleOrderItemDao;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * 商品统计
 */
public class ProductStatisticFragment extends Fragment implements View.OnClickListener {
    static final private String TAG = "ProductStatisticFragment";
    LinearLayout btnStartTime;
    LinearLayout btnEndTime;

    TextView textStartTime;
    TextView textEndTime;
    Date dateStart;
    Date dateEnd;

    ListView listData;
    ProductAdapter productAdapter = null;


    private void onbtnStartTime() {
        final PickTimeDialog pickTimeDialog = new PickTimeDialog(getActivity(), R.style.MyDialog, dateStart);
        pickTimeDialog.show();

        pickTimeDialog.btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date test=new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                if(dateStart.after(test)){
                    Toast.makeText(getActivity(),"开始时间不能在当前时间之后,请重新选择",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(dateEnd.before(dateStart)){
                    Toast.makeText(getActivity(),"开始时间不能在结束时间之后,请重新选择",Toast.LENGTH_SHORT).show();
                    return;
                }
               // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                textStartTime.setText(simpleDateFormat.format(dateStart));
                pickTimeDialog.dismiss();
                productAdapter.notifyDataSetChanged();
            }
        });

        productAdapter.notifyDataSetChanged();
    }

    private void onbtnEndTime() {
        final PickTimeDialog pickTimeDialog = new PickTimeDialog(getActivity(), R.style.MyDialog, dateEnd);
        pickTimeDialog.show();

        pickTimeDialog.btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date test=new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                if(dateEnd.after(test)){
                    Toast.makeText(getActivity(),"结束时间不能在当前时间之前,请重新选择",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(dateEnd.before(dateStart)){
                    Toast.makeText(getActivity(),"结束时间不能在开始时间之前,请重新选择",Toast.LENGTH_SHORT).show();
                    return;
                }
                textEndTime.setText(simpleDateFormat.format(dateEnd));
                pickTimeDialog.dismiss();
                productAdapter.notifyDataSetChanged();
            }
        });

        productAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartTime:
                onbtnStartTime();
                break;
            case R.id.btnEndTime:
                onbtnEndTime();
                break;
        }
    }

    public ProductStatisticFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_statistic, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dateEnd = new Date();
        dateStart = new Date();
        //dateStart.setDate(1);
        dateStart.setHours(0);
        dateStart.setMinutes(0);


        listData = (ListView) view.findViewById(R.id.listData);
        productAdapter = new ProductAdapter(dateStart, dateEnd);
        listData.setAdapter(productAdapter);


        btnStartTime = (LinearLayout) view.findViewById(R.id.btnStartTime);
        btnStartTime.setOnClickListener(this);

        btnEndTime = (LinearLayout) view.findViewById(R.id.btnEndTime);
        btnEndTime.setOnClickListener(this);

        textStartTime = (TextView) view.findViewById(R.id.textStartTime);
        textEndTime = (TextView) view.findViewById(R.id.textEndTime);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        textEndTime.setText(simpleDateFormat.format(dateEnd));
        textStartTime.setText(simpleDateFormat.format(dateStart));
    }

    class ProductSaleCount {
        public Product product;
        public long saleCount;

        public ProductSaleCount(Product product, long saleCount) {
            this.product = product;
            this.saleCount = saleCount;
        }
    }

    private class ProductAdapter extends BaseAdapter {
        List<ProductSaleCount> productSaleCountList;
        Date dateStart;
        Date dateEnd;

        void loadData() {
            productSaleCountList.clear();
            SaleOrderDao saleOrderManager = ((MPosApplication) getActivity().getApplication()).getDataHelper().getSaleOrderManager();
            SaleOrderItemDao saleOrderItemManager = ((MPosApplication) getActivity().getApplication()).getDataHelper().getSaleOrderItemManager();


            List<Product> productList = ((MPosApplication) getActivity().getApplication()).getDataHelper().getProductManager().loadAll();
            List<SaleOrder> saleOrderList = ((MPosApplication) getActivity().getApplication()).getDataHelper().getSaleOrderManager().queryBuilder()
                    .where(SaleOrderDao.Properties.PayedType.gt(0), SaleOrderDao.Properties.CreateTime.between(dateStart.getTime(), dateEnd.getTime())).list();
            for (Product product : productList) {
                ProductSaleCount productSaleCount = new ProductSaleCount(product, 0);

                for (SaleOrder saleOrder : saleOrderList) {
                    for (SaleOrderItem saleOrderItem : saleOrder.getSaleOrderItemList()) {
                        if (saleOrderItem.getProductId() == product.getId()) {
                            productSaleCount.saleCount += saleOrderItem.getCountProduct();
                        }
                    }
                }

                productSaleCountList.add(productSaleCount);
            }


            Collections.sort(productSaleCountList, new ComparatorProduct());

        }

        public class ComparatorProduct implements Comparator {

            public int compare(Object arg0, Object arg1) {
                ProductSaleCount productSaleCount0 = (ProductSaleCount) arg0;
                ProductSaleCount productSaleCount1 = (ProductSaleCount) arg1;
                if(productSaleCount0.saleCount==productSaleCount1.saleCount){
                    return 0;
                }
                return productSaleCount0.saleCount > productSaleCount1.saleCount ? -1 : 1;
            }

        }

        public ProductAdapter(Date dateStart, Date dateEnd) {
            this.dateStart = dateStart;
            this.dateEnd = dateEnd;
            productSaleCountList = new ArrayList<ProductSaleCount>();
            loadData();
        }


        @Override
        public int getCount() {
            return productSaleCountList.size();
        }

        @Override
        public Object getItem(int position) {
            return productSaleCountList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void notifyDataSetChanged() {
            loadData();
            super.notifyDataSetChanged();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            ProductSaleCount productSaleCount = productSaleCountList.get(pos);
            View view = convertView;
            ViewHold viewHold;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.statistic_product_list_item, null);

                viewHold = new ViewHold();
                view.setTag(viewHold);

                viewHold.imageView = (ImageView) view.findViewById(R.id.ItemImage);
                viewHold.textProduct = (TextView) view.findViewById(R.id.ItemText);
                viewHold.textCount = (TextView) view.findViewById(R.id.textSaleCount);
                viewHold.textPrice = (TextView) view.findViewById(R.id.textPrice);
                viewHold.imageView.setImageResource(R.drawable.item_image);
            } else {
                viewHold = (ViewHold) view.getTag();
                viewHold.imageView.setImageResource(R.drawable.item_image);
            }


            viewHold.textProduct.setText(productSaleCount.product.getProd_name());

          //  viewHold.imageView.setImageBitmap(BitmapFactory.decodeByteArray(productSaleCount.product.getDataPhoto(), 0, productSaleCount.product.getDataPhoto().length));
            if(!productSaleCount.product.getImage_url().toLowerCase().startsWith("http"))
            {
/*                Bitmap bitmap = BitmapFactory.decodeFile(productSaleCount.product.getImage_url());
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 60, 60);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                if(thumbnail !=null) {
                    thumbnail.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
                    viewHold.imageView.setImageBitmap(bitmap);
                }*/
                String imageUri="file:///"+productSaleCount.product.getImage_url();
                ImageLoader.getInstance().displayImage(imageUri,  viewHold.imageView);
            }
            else
            {
                ImageLoader.getInstance().displayImage(productSaleCount.product.getImage_url(),  viewHold.imageView);
            }

            viewHold.textPrice.setText("￥" + (new DecimalFormat("0.00")).format(Arith.newBigDecimal(productSaleCount.product.getLocalPrice())));

            viewHold.textCount.setText("已售出 " + productSaleCount.saleCount + " 件");

            return view;
        }

    }


    static class ViewHold {
        ImageView imageView;
        TextView textProduct;
        TextView textCount;
        TextView textPrice;
    }
}
