package com.itertk.app.mpos.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.TextFocusChangeListener;

public class BindMerchantActivity extends Activity {
    EditText textMerchantNo;
    EditText textMerchantName;
    Button btnBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_merchant);

        MyActionbar.setLoginActionBarLayout(this, R.drawable.merchant, "绑定商户");

        TextFocusChangeListener textFocusChangeListener = new TextFocusChangeListener();
        textMerchantNo = (EditText) findViewById(R.id.textMerchantNo);
        textMerchantNo.setOnFocusChangeListener(textFocusChangeListener);

        textMerchantName = (EditText) findViewById(R.id.textMerchantName);
        textMerchantName.setOnFocusChangeListener(textFocusChangeListener);

        btnBind = (Button) findViewById(R.id.btnBind);
        btnBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.bind_merchant, menu);
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
