package com.itertk.app.mpos;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.itertk.app.mpos.utility.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by waylen_wang on 2015/3/17.
 */
public class PictureDialog extends Dialog {
    final private static String TAG = "PictureDialog";

    Activity activity;
    Fragment fragment;
    ListView mList;
    int requestCode=0;


    public PictureDialog(Activity activity, int theme,Fragment fragment,int requestCode) {
        super(activity, theme);
        this.activity = activity;
        this.fragment=fragment;
        this.requestCode=requestCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.picture_picker_dialog);
        final String[] type=activity.getResources().getStringArray(R.array.picture_type);
        List<HashMap <String,Object>> data = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<type.length;i++){
            HashMap<String,Object> item=new HashMap<String, Object>();
            item.put("type",type[i]);
            data.add(item);
        }
        mList=(ListView)findViewById(R.id.picture_picker_list);
        SimpleAdapter adapter = new SimpleAdapter(activity,data, android.R.layout.simple_list_item_1,
                new String[] { "type" }, new int[] {android.R.id.text1 });
        mList.setAdapter(adapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean camera=false;
                if(id==0){
                    camera=true;
                }else {
                    camera=false;
                }
                if(fragment!=null)
                    Utils.launchCamera(fragment, requestCode, camera);
                else
                    Utils.launchCamera(activity,requestCode,camera);
                dismiss();
            }
        });
        //setCanceledOnTouchOutside(false);
    }


}
