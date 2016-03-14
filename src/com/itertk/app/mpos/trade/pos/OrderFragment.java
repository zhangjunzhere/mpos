package com.itertk.app.mpos.trade.pos;


import android.app.Fragment;
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
import android.widget.Toast;

import com.centerm.PrinterService;
import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.dbhelper.SaleOrderDao;
import com.itertk.app.mpos.dbhelper.SaleOrderItem;
import com.itertk.app.mpos.dbhelper.SaleOrderItemAttributeValueDao;
import com.itertk.app.mpos.dbhelper.SaleOrderItemDao;
import com.itertk.app.mpos.dbhelper.SaleOrderItemReduceDao;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * 购物车
 */
public class OrderFragment extends Fragment implements View.OnClickListener {
    LinearLayout btnStartTime;
    LinearLayout btnEndTime;

    TextView textStartTime;
    TextView textEndTime;

    Date dateStart;
    Date dateEnd;


    ExpandableListView listData = null;

    ExpandableAdapter expandableAdapter;


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
                textStartTime.setText(simpleDateFormat.format(dateStart));
                pickTimeDialog.dismiss();
                expandableAdapter.notifyDataSetChanged();
            }
        });


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
                expandableAdapter.notifyDataSetChanged();
            }
        });


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

    class ExpandableAdapter extends BaseExpandableListAdapter {
        Date dateStart;
        Date dateEnd;
        SaleOrderDao saleOrderManager;
        SaleOrderItemDao saleOrderItemManager;
        List<SaleOrder> saleOrderList;


        void loadData() {
            Log.d("ExpandableAdapter", "load data between " + dateStart.toString() + " and " + dateEnd.toString());
            saleOrderList = saleOrderManager.queryBuilder()
                    .where(SaleOrderDao.Properties.CreateTime.between(dateStart.getTime(), dateEnd.getTime()), SaleOrderDao.Properties.PayedType.gt(0),
                            SaleOrderDao.Properties.PayedTime.gt(0))
                    .orderDesc(SaleOrderDao.Properties.CreateTime).list();
        }

        ExpandableAdapter(Date dateStart, Date dateEnd) {
            this.dateStart = dateStart;
            this.dateEnd = dateEnd;
            saleOrderManager = ((MPosApplication) OrderFragment.this.getActivity().getApplication()).getDataHelper().getSaleOrderManager();
            saleOrderItemManager = ((MPosApplication) OrderFragment.this.getActivity().getApplication()).getDataHelper().getSaleOrderItemManager();

            loadData();
        }

        @Override
        public void notifyDataSetChanged() {
            loadData();
            super.notifyDataSetChanged();
        }

        @Override
        public void notifyDataSetInvalidated() {
            super.notifyDataSetInvalidated();
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

        public String leftFill(String src, int length) {
            String dest = new String();

            dest = String.format("%1$" + length + "s", src);

            return dest;
        }

        public String rightFill(String src, int length) {
            String dest = new String();

            if (src.length() <= length) {
                dest = String.format("%1$-" + length + "s", src);
            } else
                dest = src.substring(0, length);

            return dest;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            Log.d("ExpandableAdapter", "groupPosition=" + groupPosition);
            final SaleOrder saleOrder = saleOrderList.get(groupPosition);
            View view = convertView;
            GroupViewHold groupViewHold;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.payed_order_list_item, null);

                groupViewHold = new GroupViewHold();
                view.setTag(groupViewHold);

                groupViewHold.textProductName = (TextView) view.findViewById(R.id.textProductName);
                groupViewHold.textPrice = (TextView) view.findViewById(R.id.textPrice);
                groupViewHold.textOrderNo = (TextView) view.findViewById(R.id.textOrderNo);
                groupViewHold.textCreateTime = (TextView) view.findViewById(R.id.textCreateTime);
                groupViewHold.textPayType = (TextView) view.findViewById(R.id.textPayType);


                groupViewHold.btnCancel = (LinearLayout) view.findViewById(R.id.btnCancel);
                groupViewHold.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("cancel order", "postion=" + groupPosition);

                        ((MPosApplication) getActivity().getApplication()).getDataHelper().getDaoSession().runInTx(new Runnable() {
                            @Override
                            public void run() {
                                SaleOrder saleOrder = saleOrderList.get(groupPosition);
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


                groupViewHold.btnPrint = (LinearLayout) view.findViewById(R.id.btnPrint);
                groupViewHold.btnPrint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SaleOrder saleOrder = saleOrderList.get(groupPosition);

                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("             MPOS\n");
                        stringBuffer.append("订单号：" + saleOrder.getSaleOrderNo() + "\n");
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        stringBuffer.append("支付时间：" + simpleDateFormat.format(saleOrder.getPayedTime()) + "\n");
                        stringBuffer.append("名称       单价    数量   金额\n");
                        stringBuffer.append("================================");

                        try {
                            PrinterService.getService().printString(stringBuffer.toString(), 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        stringBuffer.setLength(0);


                        for (SaleOrderItem saleOrderItem : saleOrder.getSaleOrderItemList()) {
                            stringBuffer.append(
                                    rightFill(saleOrderItem.getName(), 5)
                                            + " " + leftFill((new DecimalFormat("0.00")).format(Arith.newBigDecimal(saleOrderItem.getOnePrice())), 7)
                                            + " " + leftFill("" + saleOrderItem.getCountProduct(), 3)
                                            + " " + leftFill((new DecimalFormat("0.00")).format(Arith.newBigDecimal(saleOrderItem.getPrice())), 9)
                                            + "\n");

                            try {
                                PrinterService.getService().printString(stringBuffer.toString(), 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            stringBuffer.setLength(0);
                        }

                        stringBuffer.append("================================");
                        stringBuffer.append("总计：" + (new DecimalFormat("0.00")).format(Arith.newBigDecimal(saleOrder.getPrice())) + "元");

                        stringBuffer.append("\n\n\n\n");

                        Log.d("printer", stringBuffer.toString());

                        try {
                            PrinterService.getService().printString(stringBuffer.toString(), 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            } else {
                groupViewHold = (GroupViewHold) view.getTag();
            }

            String reduceString="";
            if(Arith.newBigDecimal(saleOrder.getPrice()).compareTo(saleOrder.getShihouPrice())!=0){
                reduceString=" (已优惠"+saleOrder.getSaleOrderReduceList().get(0).getReduce().getValue()+"元)";
            }
            groupViewHold.textPrice.setText("￥" + (new DecimalFormat("0.00")).format(saleOrder.getShihouPrice())+reduceString);
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

            switch (saleOrder.getPayedType()) {
                case 1:
                    groupViewHold.textPayType.setText("支付方式：现金支付");
                    break;
                case 2:
                    groupViewHold.textPayType.setText("支付方式：刷卡支付");
                    break;
                case 3:
                    groupViewHold.textPayType.setText("支付方式：会员支付");
                    break;
            }


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
                childViewHold.imageView.setImageResource(R.drawable.item_image);
            } else {
                childViewHold = (ChildViewHold) view.getTag();
                childViewHold.imageView.setImageResource(R.drawable.item_image);
            }

            childViewHold.textCount.setText("" + saleOrderItem.getCountProduct());
            childViewHold.textPrice.setText("￥" + (new DecimalFormat("0.00")).format(Arith.newBigDecimal(saleOrderItem.getPrice())));

            if (saleOrderItem.getProductId() > 0) {
               // childViewHold.imageView.setImageBitmap(BitmapFactory.decodeByteArray(saleOrderItem.getProduct().getDataPhoto(), 0, saleOrderItem.getProduct().getDataPhoto().length));
                childViewHold.textProduct.setText(saleOrderItem.getName());
                if(saleOrderItem.getProduct()==null){
                    childViewHold.imageView.setImageResource(R.drawable.manual);
                }else {
                    if (!saleOrderItem.getProduct().getImage_url().toLowerCase().startsWith("http")) {
                        String imageUri = "file:///" + saleOrderItem.getProduct().getImage_url();
                        ImageLoader.getInstance().displayImage(imageUri, childViewHold.imageView);
                    } else {
                        ImageLoader.getInstance().displayImage(saleOrderItem.getProduct().getImage_url(), childViewHold.imageView);
                    }
                }
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
        TextView textPayType;
        LinearLayout btnCancel;
        LinearLayout btnPrint;
    }

    static class ChildViewHold {
        ImageView imageView;
        TextView textProduct;
        TextView textCount;
        TextView textPrice;
    }

    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dateEnd = new Date();
        dateStart = new Date();
        //dateStart.setDate(1);
        dateStart.setHours(0);
        dateStart.setMinutes(0);

        listData = (ExpandableListView) view.findViewById(R.id.listData);
        expandableAdapter = new ExpandableAdapter(dateStart, dateEnd);
        listData.setAdapter(expandableAdapter);

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
}
