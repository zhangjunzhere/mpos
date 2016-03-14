package com.itertk.app.mpos.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.ExternalPos;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.trade.TradeHomeActivity;
import com.itertk.app.mpos.utility.BlueToothHelper;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/**
 * 会员绑定
 */

public class BindMemberActivity extends Activity {
    TextView textTerminal;
    Button btnBind;
    static String TAG="BindMemberActivity";

    ExternalPos pos;

    private String getTeminalId() {
        String terminalConfig = "terminalconfig";
        String keyTerminalId = "terminalId";
        String keyTerminalMac = "terminalMac";


        SharedPreferences sharedPre = getSharedPreferences(terminalConfig, MODE_PRIVATE);
        return sharedPre.getString(keyTerminalId, "");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_member);
        MyActionbar.setLoginActionBarLayout(this, R.drawable.activate, "绑定设备");
        SharedPreferences sharedPre = getSharedPreferences(LoginActivity.terminalConfig, Context.MODE_PRIVATE);
        final String posID = sharedPre.getString(LoginActivity.keyPosId, "");


        textTerminal = (TextView) findViewById(R.id.textTerminal);
        textTerminal.setText("终端设备编号：" + getTeminalId());

        btnBind = (Button) findViewById(R.id.btnBind);
        btnBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PosConDialog posConDialog=new PosConDialog(BindMemberActivity.this,R.style.MyDialog);
                posConDialog.show();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.bind_member, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
