package com.itertk.app.mpos.config;



import android.app.FragmentTransaction;
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
import android.widget.ListView;
import android.widget.TextView;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.dbhelper.Attribute;
import com.itertk.app.mpos.dbhelper.AttributeDao;
import com.itertk.app.mpos.dbhelper.AttributeValue;
import com.itertk.app.mpos.dbhelper.AttributeValueDao;
import com.itertk.app.mpos.dbhelper.ProductAttributeDao;
import com.itertk.app.mpos.dbhelper.SaleOrderItemAttributeValue;
import com.itertk.app.mpos.dbhelper.SaleOrderItemAttributeValueDao;


import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 *属性类别列表
 */
public class AttributeListFragment extends Fragment implements View.OnClickListener{
    static final String TAG = "AttributeListFragment";
    ListView listData = null;
    Button btnAdd = null;

    AddAttributeFragment addAttributeFragment;
    ModAttributeFragment modAttributeFragment;
    FragmentTransaction transaction;

    AttributeAdapter attributeAdapter;

    public long addAttribute(Attribute attribute){
        return attributeAdapter.addAttribute(attribute);
    }

    public void modAttribute(Attribute attribute){
        attributeAdapter.modAttribute(attribute);
    }

    private void onbtnAddAttribute(){
        addAttributeFragment = new AddAttributeFragment();
        addAttributeFragment.setAttributeListFragment(this);
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.itemArea, addAttributeFragment);
        transaction.commit();
    }

    private void onbtnModAttribute(Attribute attribute){
        modAttributeFragment = new ModAttributeFragment();
        modAttributeFragment.setParames(this, attribute);
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.itemArea, modAttributeFragment);
        transaction.commit();
    }

    private void onbtnDelAttribute(Attribute attribute){
        attributeAdapter.delAttribute(attribute);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAdd: onbtnAddAttribute(); break;
        }
    }


    public AttributeListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attribute_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listData = (ListView)view.findViewById(R.id.listData);

        btnAdd = (Button)view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        attributeAdapter = new AttributeAdapter();
        listData.setAdapter(attributeAdapter);
    }

    private class AttributeAdapter extends BaseAdapter {
        final ListView listView;
        AttributeDao attributeManager;
        AttributeValueDao attributeValueManager;
        ProductAttributeDao productAttributeManager;
        SaleOrderItemAttributeValueDao saleOrderItemAttributeValueManager;
        List<Attribute> attributeList;
        int preSelect = -1;


        public long addAttribute(Attribute attribute){
            long result = 0;
            try {
                result  = attributeManager.insert(attribute);
                notifyDataSetChanged();
            }catch (Exception e){
                Log.e(TAG, "insert " + attribute.toString() + " error:" + e.toString());
                result = -1;
            }

            return result;
        }

        public long modAttribute(Attribute attribute){
            long result = 0;
            try {
                attributeManager.update(attribute);
                notifyDataSetChanged();
            }catch (Exception e){
                Log.e(TAG, "modify " + attribute.toString() + " error:" + e.toString());
                result = -1;
            }

            return result;
        }

        public long delAttribute(Attribute attribute){
            final Attribute atb = attribute;
            long result = 0;
            try {

                ((MPosApplication) getActivity().getApplication()).getDataHelper().getDaoSession().runInTx(new Runnable() {
                    @Override
                    public void run() {
                        for (AttributeValue attributeValue : atb.getAttributeValueList()){
                            saleOrderItemAttributeValueManager.queryBuilder()
                                    .where(SaleOrderItemAttributeValueDao.Properties.AttributeValueId.eq(attributeValue.getValueId())).buildDelete().executeDeleteWithoutDetachingEntities();
                        }

                        attributeValueManager.deleteInTx(atb.getAttributeValueList());
                        productAttributeManager.queryBuilder().where(ProductAttributeDao.Properties.AttributeId.eq(atb.getAttributeId())).buildDelete().executeDeleteWithoutDetachingEntities();
                        attributeManager.delete(atb);
                    }
                });


                notifyDataSetChanged();
            }catch (Exception e){
                Log.e(TAG, "delete " + attribute.toString() + " error:" + e.toString());
                result = -1;
            }

            return result;
        }

        AttributeAdapter(){
            this.listView = AttributeListFragment.this.listData;
            this.attributeManager = ((MPosApplication)getActivity().getApplication()).getDataHelper().getAttributeManager();
            this.attributeValueManager = ((MPosApplication)getActivity().getApplication()).getDataHelper().getAttributeValueManager();
            this.productAttributeManager = ((MPosApplication)getActivity().getApplication()).getDataHelper().getProductAttributeManager();
            this.saleOrderItemAttributeValueManager = ((MPosApplication) getActivity().getApplication()).getDataHelper().getSaleOrderItemAttributeValueManager();
            attributeList = attributeManager.loadAll();
        }


        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            attributeList = attributeManager.loadAll();
        }

        @Override
        public int getCount() {
           return attributeList.size();
        }

        @Override
        public Object getItem(int position) {
            return attributeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            Attribute attribute = attributeList.get(pos);
            View view = convertView;
            ViewHold viewHold;
            if(view == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.attribute_list_item, null);

                viewHold = new ViewHold();
                view.setTag(viewHold);


                viewHold.textAttribute = (TextView)view.findViewById(R.id.ItemText);

                viewHold.btnModify = (ImageButton) view.findViewById(R.id.btnModify);
                viewHold.btnModify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Attribute attribute = attributeList.get(pos);

                        AttributeListFragment.this.onbtnModAttribute(attribute);

                        Log.d("modify click", "position=" + pos);

                        ((ImageButton)v).setImageResource(R.drawable.btn_modify_2);

                        if(preSelect < 0 || preSelect == pos){}
                        else{
                            View preView = listView.getChildAt(preSelect);
                            if(preView != null)
                                ((ImageButton)preView.findViewById(R.id.btnModify)).setImageResource(R.drawable.btn_modify);
                        }
                        preSelect = pos;
                    }
                });

                viewHold.btnDelete = (ImageButton) view.findViewById(R.id.btnDelete);
                viewHold.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Attribute attribute = attributeList.get(pos);

                        AttributeListFragment.this.onbtnDelAttribute(attribute);
                        Log.v("btnDelete", "position="+pos);
                    }
                });
                viewHold.btnDelete.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN)
                            ((ImageButton)v).setImageResource(R.drawable.btn_delete_2);
                        else
                            ((ImageButton)v).setImageResource(R.drawable.btn_delete);
                        return false;
                    }
                });

            }else{
                viewHold = (ViewHold)view.getTag();
            }

            viewHold.textAttribute.setText(attribute.getName());

            return view;
        }

    }

    static class ViewHold{
        TextView textAttribute;
        ImageButton btnModify;
        ImageButton btnDelete;
    }
}
