package com.itertk.app.mpos.config;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.dbhelper.Attribute;
import com.itertk.app.mpos.dbhelper.AttributeDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/7/13.
 * 修改属性类别对话框
 */
public class ModAttributeDialog extends Dialog{
    Context context;
    GridView gridAttribute;
    ModProductFragment modProductFragment;
    Button btnOK;
    AttributeDao attributeManager;
    GridViewAdapter gridViewAdapter;
    ArrayList<Attribute> selectAttribute;

    public ModAttributeDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }
    public ModAttributeDialog(Context context, int theme, ModProductFragment modProductFragment){
        super(context, theme);
        this.context = context;
        this.modProductFragment = modProductFragment;
        selectAttribute = modProductFragment.getSelectAttribute();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_add_attribute);



        attributeManager =((MPosApplication)(ModAttributeDialog.this.getContext().getApplicationContext())).getDataHelper().getAttributeManager();;

        gridAttribute = (GridView)findViewById(R.id.gridAttribute);
        gridAttribute.setSelector(new ColorDrawable(Color.TRANSPARENT));

        gridViewAdapter = new GridViewAdapter();
        gridAttribute.setAdapter(gridViewAdapter);

        btnOK = (Button) findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modProductFragment.modAttribute();

                ModAttributeDialog.this.dismiss();
            }
        });


    }

    private class GridViewAdapter extends BaseAdapter{
        AttributeDao attributeManager;
        List<Attribute> attributeList;
        GridViewAdapter(){
            attributeManager = ModAttributeDialog.this.attributeManager;
            attributeList = attributeManager.loadAll();
        }
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
            if(view == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.attribute_grid_item, null);
            }

            final Button btnAttribute = (Button)view.findViewById(R.id.btnAttribute);
            btnAttribute.setText(attribute.getName());

            for (Attribute attributeSelected : selectAttribute) {
                if(attributeSelected.getAttributeId() == attribute.getAttributeId()){
                    btnAttribute.setBackgroundResource(R.drawable.attribute_2);
                }
            }

            btnAttribute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   Attribute attribute = attributeList.get(pos);

                    boolean find = false;

                    for (Attribute attributeSelected : selectAttribute) {
                        if(attributeSelected.getAttributeId() == attribute.getAttributeId()){
                            find = true;
                            selectAttribute.remove(attributeSelected);
                            break;
                        }
                    }

                    if (find){
                        btnAttribute.setBackgroundResource(R.drawable.attribute);
                    }
                    else{
                        selectAttribute.add(attribute);
                        btnAttribute.setBackgroundResource(R.drawable.attribute_2);
                    }

                    Log.d("GridViewAdapter", "position="+pos+" data="+ attribute.getName());
                    Log.d("GridViewAdapter", selectAttribute.toString());
                }
            });



            return view;
        }
    }
}
