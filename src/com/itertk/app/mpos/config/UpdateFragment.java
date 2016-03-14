package com.itertk.app.mpos.config;



import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.R;
import com.itertk.app.mpos.utility.LinkeaRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 *更新
 */
public class UpdateFragment extends Fragment {
    private static final String TAG = "UpdateFragment";
    RelativeLayout btnCheckUpdate = null;
    TextView textVersion;
    TextView textUpdateTime;
    String version;

    public UpdateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textVersion = (TextView)view.findViewById(R.id.textVersion);
        textUpdateTime = (TextView)view.findViewById(R.id.textUpdateTime);

        btnCheckUpdate = (RelativeLayout)view.findViewById(R.id.btnCheckUpdate);
        btnCheckUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinkeaRequest.checkApplicationUpdate(getActivity(),null);
//                AsyncHttpClient client = new AsyncHttpClient();
//
//                client.get("http://www.itertk.com/app/update.json", new JsonHttpResponseHandler(){
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        super.onSuccess(statusCode, headers, response);
//
//                        Log.d(TAG, response.toString());
//
//                        try {
//                            String updateVersion = response.getString("version");
//                            if(updateVersion.compareTo(version) > 0){
//                                Log.d(TAG, "need to update "  + updateVersion + " > " + version);
//
//                                (new UpdateManager(getActivity(), response.getString("url"), response.getString("name") + ".apk")).showNoticeDialog();
//                            }else{
//                                Log.d(TAG, "no need update " + updateVersion + " < " + version);
//                                Toast.makeText(getActivity(), "已经是最新版本",
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        super.onFailure(statusCode, headers, responseString, throwable);
//                        Log.d(TAG, throwable.toString());
//                    }
//                });

            }
        });


        try {
            PackageManager pm = getActivity().getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo("com.itertk.app.mpos", 0);
            version = packageInfo.versionName;
            textVersion.setText("当前软件版本：" + packageInfo.versionName);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            textUpdateTime.setText( "上次更新时间：" + simpleDateFormat.format(new Date(packageInfo.lastUpdateTime)));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
