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

import java.util.List;

/*
* 修改属性类别
* */
public class ModAttributeFragment extends Fragment implements View.OnClickListener{
    static final String TAG = "AddAttributeFragment";
    AttributeListFragment attributeListFragment;
    Attribute attribute;

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

        attribute.setName(textAttributeName.getText().toString());
        attributeListFragment.modAttribute(attribute);
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
                    price = textAttributeValuePrice.getText().toString();
                }
                catch (Exception e){

                }

                AttributeValue attributeValue = new AttributeValue(null, textAttributeValueName.getText().toString(), price, -1);
                ModAttributeFragment.this.attributeValueAdapter.addAttributeValue(attributeValue);
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

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave: onbtnSave(); break;
            case R.id.btnAddAttributeValue: onbtnAddAttributeValue(); break;
        }
    }

    public ModAttributeFragment(){

    }


    public void setParames(AttributeListFragment attributeListFragment, Attribute attribute) {
        // Required empty public constructor
        this.attributeListFragment = attributeListFragment;
        this.attribute = attribute;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mod_attribute, container, false);
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
        textAttributeName.setText(attribute.getName().toString());

        attributeValueManager = ((MPosApplication)getActivity().getApplication()).getDataHelper().getAttributeValueManager();

        listAttrValue = (ListView)view.findViewById(R.id.listAttrValue);
        attributeValueAdapter = new AttributeValueAdapter();
        listAttrValue.setAdapter(attributeValueAdapter);
    }


    private class AttributeValueAdapter extends BaseAdapter {
        final ListView listView;
        AttributeValueDao attributeValueManager;
       List<AttributeValue> attributeValueList;


        public long addAttributeValue(AttributeValue attributeValue){
            long result = 0;
            try {
                attributeValue.setAttribute(attribute);
                result  = attributeValueManager.insert(attributeValue);
                notifyDataSetChanged();
            }catch (Exception e){
                Log.e(TAG, "insert " + attributeValue.toString() + " error:" + e.toString());
                result = -1;
            }

            return result;
        }

        public long delAttributeValue(AttributeValue value){
            long result = 0;
            try {
               attributeValueManager.delete(value);
                notifyDataSetChanged();
            }catch (Exception e){
                Log.e(TAG, "insert " + value.toString() + " error:" + e.toString());
                result = -1;
            }

            return result;
        }

        AttributeValueAdapter(){
            this.listView = ModAttributeFragment.this.listAttrValue;
            this.attributeValueManager = ModAttributeFragment.this.attributeValueManager;
            attribute.resetAttributeValueList();
            attributeValueList = attribute.getAttributeValueList();
            attribute = ModAttributeFragment.this.attribute;
        }


        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            attribute.resetAttributeValueList();
            attributeValueList = attribute.getAttributeValueList();
        }

        @Override
        public int getCount() {
            return attributeValueList.size();
        }

        @Override
        public Object getItem(int position) {
            return attributeValueList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            AttributeValue attributeValue = attributeValueList.get(pos);
            View view = convertView;
            if(view == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.attribute_value_list_item, null);
            }

            TextView textView = (TextView)view.findViewById(R.id.ItemText);
            textView.setText(attributeValue.getName());

            TextView textAttributeValuePrice = (TextView)view.findViewById(R.id.textAttributeValuePrice);
            textAttributeValuePrice.setText(String.valueOf(attributeValue.getPriceAddition()));

            ImageButton btnDelete = (ImageButton) view.findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AttributeValue attributeValue = attributeValueList.get(pos);

                    AttributeValueAdapter.this.delAttributeValue(attributeValue);
                    Log.v("btnDelete", "position=" + pos);
                }
            });

            return view;
        }

    }
}
