package com.itertk.app.mpos.config;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.TextFocusChangeListener;
import com.itertk.app.mpos.dbhelper.Attribute;
import com.itertk.app.mpos.dbhelper.AttributeValue;
import com.itertk.app.mpos.dbhelper.AttributeValueDao;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * 添加属性类别
 *
 */
public class AddAttributeFragment extends Fragment implements View.OnClickListener{
    static final String TAG = "AddAttributeFragment";
    AttributeListFragment attributeListFragment;
    EditText textAttributeName;
    LinearLayout btnAddAttributeValue;
    Button btnSave;
    ListView listAttrValue;

    AttributeValueDao attributeValueManager;
    AttributeValueAdapter attributeValueAdapter;

    private void onbtnSave(){
        if(textAttributeName.getText().toString().isEmpty()){
            Toast.makeText(getActivity().getApplicationContext(), "属性名称不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ((MPosApplication)getActivity().getApplication()).getDataHelper().getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                Attribute attribute = new Attribute(null, textAttributeName.getText().toString(), 0L, 0L);
                long attributeId = attributeListFragment.addAttribute(attribute);
                if (attributeId > 0){
                    for (int i = 0; i < attributeValueAdapter.getCount(); i++){
                        AttributeValue value = (AttributeValue)attributeValueAdapter.getItem(i);
                        value.setAttributeId(attributeId);
                        attributeValueManager.insert(value);
                        attribute.getAttributeValueList().add(value);
                        Log.d(TAG, "add AttributeValue=" + value.toString());
                    }
                }
            }
        });
    }

    private void onbtnAddAttributeValue(){
        final AddAttributeValueDialog dialog = new AddAttributeValueDialog(getActivity(), R.style.MyDialog);
        dialog.show();

        final EditText textAttributeValueName = (EditText)dialog.findViewById(R.id.textAttributeValueName);
        final EditText textAttributeValuePrice = (EditText)dialog.findViewById(R.id.textAttributeValuePrice);

        ((Button)dialog.findViewById(R.id.btnOK)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textAttributeValueName.getText().length() == 0){
                    Toast.makeText(getActivity().getApplicationContext(), "名称为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(textAttributeValuePrice.getText().length() == 0){
                    Toast.makeText(getActivity().getApplicationContext(), "值为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String price = "0";
                try {
                    price = textAttributeValuePrice.getText().toString().trim();
                }
                catch (Exception e){

                }

                AttributeValue attributeValue = new AttributeValue(null, textAttributeValueName.getText().toString(), price, -1);
                AddAttributeFragment.this.attributeValueAdapter.addAttributeValue(attributeValue);
                dialog.dismiss();
            }
        });

        ((Button)dialog.findViewById(R.id.btnCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave: onbtnSave(); break;
            case R.id.btnAddAttributeValue: onbtnAddAttributeValue(); break;
        }
    }

    public AddAttributeFragment() {
        // Required empty public constructor

    }

    public void setAttributeListFragment(AttributeListFragment attributeListFragment){
        this.attributeListFragment = attributeListFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_attribute, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnSave = (Button)view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        btnAddAttributeValue = (LinearLayout)view.findViewById(R.id.btnAddAttributeValue);
        btnAddAttributeValue.setOnClickListener(this);

        TextFocusChangeListener textFocusChangeListener = new TextFocusChangeListener();
        textAttributeName = (EditText)view.findViewById(R.id.textAttributeName);
        textAttributeName.setOnFocusChangeListener(textFocusChangeListener);

        attributeValueManager = ((MPosApplication)getActivity().getApplication()).getDataHelper().getAttributeValueManager();

        listAttrValue = (ListView)view.findViewById(R.id.listAttrValue);
        attributeValueAdapter = new AttributeValueAdapter();
        listAttrValue.setAdapter(attributeValueAdapter);
    }

    private class AttributeValueAdapter extends BaseAdapter {
        final ListView listView;
        ArrayList<AttributeValue> valueArray;


        public void addAttributeValue(AttributeValue attributeValue){
            valueArray.add(attributeValue);
            notifyDataSetChanged();
        }

        public void delAttributeValue(int position){
            valueArray.remove(position);
            notifyDataSetChanged();
        }

        AttributeValueAdapter(){
            this.listView = AddAttributeFragment.this.listAttrValue;
            valueArray = new ArrayList<AttributeValue>();
        }


        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return valueArray.size();
        }

        @Override
        public Object getItem(int position) {
            return valueArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            //cursor.moveToPosition(pos);
            View view = convertView;
            if(view == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.attribute_value_list_item, null);
            }

            AttributeValue attributeValue = valueArray.get(pos);
            TextView textView = (TextView)view.findViewById(R.id.ItemText);
            textView.setText(attributeValue.getName());

            TextView textAttributeValuePrice = (TextView)view.findViewById(R.id.textAttributeValuePrice);
            textAttributeValuePrice.setText(String.valueOf(attributeValue.getPriceAddition()));

            ImageButton btnDelete = (ImageButton) view.findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AttributeValueAdapter.this.delAttributeValue(pos);
                    Log.v("btnDelete", "position="+pos);
                }
            });

            return view;
        }

    }
}
