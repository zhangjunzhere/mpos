package com.itertk.app.mpos.trade.pos;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.dbhelper.SaleOrderDao;
import com.itertk.app.mpos.dbhelper.SaleOrderItem;
import com.itertk.app.mpos.dbhelper.SaleOrderItemAttributeValueDao;
import com.itertk.app.mpos.dbhelper.SaleOrderItemDao;
import com.itertk.app.mpos.dbhelper.SaleOrderItemReduceDao;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * 挂单
 */
public class UnpayOrderFragment extends Fragment {
    ExpandableListView listData = null;

    ExpandableAdapter expandableAdapter;

    class ExpandableAdapter extends BaseExpandableListAdapter {
        SaleOrderDao saleOrderManager;
        SaleOrderItemDao saleOrderItemManager;
        List<SaleOrder> saleOrderList;

        void loadData() {
            Log.d("ExpandableAdapter", "load data");
            saleOrderList = saleOrderManager.queryBuilder()
                    .where(SaleOrderDao.Properties.PayedType.eq(0))
                    .orderDesc(SaleOrderDao.Properties.CreateTime).list();
        }

        ExpandableAdapter() {
            saleOrderManager = ((MPosApplication) UnpayOrderFragment.this.getActivity().getApplication()).getDataHelper().getSaleOrderManager();
            saleOrderItemManager = ((MPosApplication) UnpayOrderFragment.this.getActivity().getApplication()).getDataHelper().getSaleOrderItemManager();

            loadData();
        }

        @Override
        public void notifyDataSetChanged() {
            loadData();
            super.notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            return saleOrderList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return saleOrderList.get(groupPosition).getSaleOrderItemList().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            Log.d("ExpandableAdapter", "groupPosition=" + groupPosition);
            final int position = groupPosition;
            final SaleOrder saleOrder = saleOrderList.get(groupPosition);
            View view = convertView;
            GroupViewHold groupViewHold;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.unpay_order_list_item, null);

                groupViewHold = new GroupViewHold();
                view.setTag(groupViewHold);

                groupViewHold.textPrice = (TextView) view.findViewById(R.id.textPrice);
                groupViewHold.textOrderNo = (TextView) view.findViewById(R.id.textOrderNo);
                groupViewHold.textCreateTime = (TextView) view.findViewById(R.id.textCreateTime);
                groupViewHold.textProductName = (TextView) view.findViewById(R.id.textProductName);

                groupViewHold.btnCancel = (LinearLayout) view.findViewById(R.id.btnCancel);
                groupViewHold.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("cancel unpay order", "postion=" + position);

                        ((MPosApplication) getActivity().getApplication()).getDataHelper().getDaoSession().runInTx(new Runnable() {
                            @Override
                            public void run() {
                                SaleOrder saleOrder = saleOrderList.get(position);
                                SaleOrderItemDao saleOrderItemDao =
                                        ((MPosApplication) getActivity().getApplication()).getDataHelper().getDaoSession().getSaleOrderItemDao();
                                SaleOrderItemReduceDao saleOrderItemReduceDao =
                                        ((MPosApplication) getActivity().getApplication()).getDataHelper().getDaoSession().getSaleOrderItemReduceDao();
                                SaleOrderItemAttributeValueDao saleOrderItemAttributeValueDao =
                                        ((MPosApplication) getActivity().getApplication()).getDataHelper().getDaoSession().getSaleOrderItemAttributeValueDao();
                                for (SaleOrderItem saleOrderItem : saleOrder.getSaleOrderItemList()) {
                                    saleOrderItemReduceDao.deleteInTx(saleOrderItem.getSaleOrderItemReduceList());
                                    saleOrderItemAttributeValueDao.deleteInTx(saleOrderItem.getSaleOrderItemAttributeValueList());
                                }
                                saleOrderItemDao.deleteInTx(saleOrder.getSaleOrderItemList());
                                saleOrder.delete();
                            }
                        });

                        notifyDataSetChanged();
                    }
                });

                groupViewHold.btnRepay = (LinearLayout) view.findViewById(R.id.btnPrint);
                groupViewHold.btnRepay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(getActivity(), PosActivity.class);
                        it.putExtra("orderId", saleOrder.getSaleOrderId().toString());
                        startActivity(it);
                        getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    }
                });

            } else {
                groupViewHold = (GroupViewHold) view.getTag();
            }

            groupViewHold.textPrice.setText("￥" + (new DecimalFormat("0.00")).format(saleOrder.getShihouPrice()));
            groupViewHold.textOrderNo.setText("订单号：" + saleOrder.getSaleOrderNo());
            String productName = new String();
            for (SaleOrderItem saleOrderItem : saleOrder.getSaleOrderItemList()) {
                productName += (saleOrderItem.getName() + " ");
            }
            groupViewHold.textProductName.setText(productName);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            date.setTime(saleOrder.getCreateTime());
            groupViewHold.textCreateTime.setText(sdf.format(date));


            return view;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            Log.d("ExpandableAdapter", "groupPosition=" + groupPosition + " childPosition=" + childPosition);
            final SaleOrderItem saleOrderItem = saleOrderList.get(groupPosition).getSaleOrderItemList().get(childPosition);
            View view = convertView;
            ChildViewHold childViewHold;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.unpay_orderitem_list_item, null);

                childViewHold = new ChildViewHold();
                view.setTag(childViewHold);

                childViewHold.imageView = (ImageView) view.findViewById(R.id.ItemImage);
                childViewHold.textProduct = (TextView) view.findViewById(R.id.ItemText);
                childViewHold.textCount = (TextView) view.findViewById(R.id.itemCount);
                childViewHold.textPrice = (TextView) view.findViewById(R.id.itemPrice);

            } else {
                childViewHold = (ChildViewHold) view.getTag();
            }

            childViewHold.textCount.setText("" + saleOrderItem.getCountProduct());
            childViewHold.textPrice.setText("￥" + (new DecimalFormat("0.00")).format(Arith.newBigDecimal(saleOrderItem.getPrice())));

            if (saleOrderItem.getProductId() > 0) {
                //childViewHold.imageView.setImageBitmap(BitmapFactory.decodeByteArray(saleOrderItem.getProduct().getDataPhoto(), 0, saleOrderItem.getProduct().getDataPhoto().length));
                childViewHold.textProduct.setText(saleOrderItem.getName());
            } else {
                childViewHold.imageView.setImageResource(R.drawable.manual);
                childViewHold.textProduct.setText(saleOrderItem.getName());
            }

            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    static class GroupViewHold {
        TextView textPrice;
        TextView textProductName;
        TextView textOrderNo;
        TextView textCreateTime;
        LinearLayout btnCancel;
        LinearLayout btnRepay;
    }

    static class ChildViewHold {
        ImageView imageView;
        TextView textProduct;
        TextView textCount;
        TextView textPrice;
    }


    public UnpayOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_unpay_order, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listData = (ExpandableListView) view.findViewById(R.id.listData);
        expandableAdapter = new ExpandableAdapter();
        listData.setAdapter(expandableAdapter);
    }

    @Override
    public void onResume() {
        expandableAdapter.notifyDataSetChanged();
        super.onResume();
    }
}
