package com.itertk.app.mpos.trade.taobao;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.itertk.app.mpos.R;
import com.itertk.app.mpos.dbhelper.Address;
import com.itertk.app.mpos.dbhelper.SaleOrderItem;

import java.util.List;

/**
 * Created by smile_gao on 2015/3/20.
 * 地址选择适配器
 */
public class AddressAdapter extends BaseAdapter {
    List<Address> addList;
    Context mContext;
    private int selectIndex=-1;
    private String mPrefAddress ;
    public AddressAdapter( Context context, List<Address> list,String prefAddress)
    {
        mContext = context;
        addList = list;
        mPrefAddress = prefAddress;
    }
    @Override
    public int getCount() {
        if(addList == null)
        {
            return  0;
        }
        return addList.size();
    }

    @Override
    public Object getItem(int i) {
        return addList.get(i);
    }
    public Address getSelectedItem()
    {
        if(selectIndex <0 ||selectIndex>= addList.size())
            return  null;
        return addList.get(selectIndex);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final int pos = i;
        Log.d("posactivty", "" + pos);
        final Address address = addList.get(pos);
        View view = convertView;
        final ViewHold viewHold;
        if (view == null) {
            LayoutInflater inflater =LayoutInflater.from(mContext);//GoodsOrderActivity.this.getLayoutInflater();
            view = inflater.inflate(R.layout.address_item, null);
            viewHold = new ViewHold();
            view.setTag(viewHold);
            viewHold.tvaddress = (TextView) view.findViewById(R.id.address);
            viewHold.iv_default = (ImageView) view.findViewById(R.id.iv_default);
        }else
        {
            viewHold = (ViewHold) view.getTag();
        }
        if(i == selectIndex)
        {
            view.setBackgroundColor(Color.BLUE);
        }
        else
        {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        if(address.getAddress1().equals(mPrefAddress)){
            viewHold.iv_default.setVisibility(View.VISIBLE);
        }else{
            viewHold.iv_default.setVisibility(View.GONE);
        }

        viewHold.tvaddress.setText(address.getAddress1());
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
//        saleOrder.resetSaleOrderItemList();
//        saleOrderItemList = saleOrder.getSaleOrderItemList();
        super.notifyDataSetChanged();
    }
    static class ViewHold {
        TextView tvaddress;
        ImageView iv_default;

    }
    public  void setSelectIndex(int pos)
    {
        selectIndex = pos;
    }

}
