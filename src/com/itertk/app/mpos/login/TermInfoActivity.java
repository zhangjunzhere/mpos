package com.itertk.app.mpos.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.utility.Utils;
import com.itertk.app.mpos.map.BaiduMapChoiceAddressActivity;

/**
*终端信息
* */
public class TermInfoActivity extends Activity {
    TextView posId,termId,termMAC;
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_info);
        MyActionbar.setLoginActionBarLayout(this, R.drawable.activate, "激活设备");
        Bundle bundle=getIntent().getBundleExtra("TERMINFO");
        String posIdInfo=bundle.getString("posid");
        String termIdInfo=bundle.getString("termid");
        String termMACInfo=bundle.getString("termmac");
        posId=(TextView)findViewById(R.id.textposID);
        termId=(TextView)findViewById(R.id.texttermID);
        termMAC=(TextView)findViewById(R.id.texttermMAC);
        posId.setText(posIdInfo);
        termId.setText(termIdInfo);
        termMAC.setText(termMACInfo);
        btnNext=(Button)findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TermInfoActivity.this, BaiduMapChoiceAddressActivity.class);
                startActivity(intent);
                if(!Utils.getInfo(TermInfoActivity.this,"termInfoShow")) {
                    Utils.saveInfo(TermInfoActivity.this, "termInfoShow", true);
                }
                finish();
            }
        });
    }

}
