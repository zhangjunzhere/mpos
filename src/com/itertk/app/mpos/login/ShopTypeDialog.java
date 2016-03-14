package com.itertk.app.mpos.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by waylen_wang on 2015/3/17.
 * 店铺类型
 */
public class ShopTypeDialog extends Dialog {
    final private static String TAG = "ShopTypeDialog";

    Activity context;
    ListView mList;
    Button mBackBtn;


    public ShopTypeDialog(Activity context, int theme) {
        super(context, theme);
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.shop_type_dialog);
        final String[] type=context.getResources().getStringArray(R.array.shop_type);
        List<HashMap <String,Object>> data = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<type.length;i++){
            HashMap<String,Object> item=new HashMap<String, Object>();
            item.put("type",type[i]);
            data.add(item);
        }
        mList=(ListView)findViewById(R.id.shop_type_list);
        SimpleAdapter adapter = new SimpleAdapter(context,data, android.R.layout.simple_list_item_1,
                new String[] { "type" }, new int[] {android.R.id.text1 });
        mList.setAdapter(adapter);
        mList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent("com.iteritk.app.mpos.send_type");
                int ii=position+1;
                i.putExtra("TYPE_ID",ii);
                i.putExtra("TYPE_NAME",type[position]);
                context.sendBroadcast(i);
                dismiss();
            }
        });
        mBackBtn=(Button)findViewById(R.id.btnBack);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //setCanceledOnTouchOutside(false);
    }


}
