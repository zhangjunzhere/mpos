package com.itertk.app.mpos.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.TextFocusChangeListener;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.map.BaiduMapChoiceAddressActivity;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
/*
* 终端激活
* */
public class TerminalActivateActivity extends Activity {
    static final String TAG = "ActivateActivity";
    TextView textTerminal;
    EditText textPOS;
    Button btnActivate;
    MPosApplication mPosApplication;

    private static final String terminalConfig = "terminalconfig";
    private static final String keyPosId = "posid";
    private static final String keyTerminalId = "terminalId";
    private static final String keyTerminalMac = "terminalMac";

    private static final String test_terminal_no = "T0128662";
    private static final String test_pos_no = "00070140100000";
    private static final String test_terminal_mac = "D7C89CFAE8BF41E4AB069D4D42EEA8B7";


    private void saveActivateInfo(String posId, String terminalId, String terminalMac) {
        Log.d(TAG, "saveActivateInfo");
        SharedPreferences sharedPre = getSharedPreferences(terminalConfig, MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor = sharedPre.edit();
        //设置参数
        editor.putString(keyPosId, posId);
        editor.putString(keyTerminalId, terminalId);
        editor.putString(keyTerminalMac, terminalMac);
        //提交
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal_activate);
        MyActionbar.setLoginActionBarLayout(this, R.drawable.activate, "激活设备");

        mPosApplication = (MPosApplication) getApplication();

        TextFocusChangeListener textFocusChangeListener = new TextFocusChangeListener();
        textTerminal = (TextView) findViewById(R.id.textTerminal);
        textTerminal.setOnFocusChangeListener(textFocusChangeListener);
        textTerminal.setText(mPosApplication.getSerialNum());

        textPOS = (EditText)findViewById(R.id.textPOS);
//        textPOS.setText("00070140100000");
        //for test
        //textTerminal.setText(test_terminal_no);
        //textPOS.setText(test_pos_no);

        btnActivate = (Button) findViewById(R.id.btnActivate);
        btnActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceId = textTerminal.getText().toString();


                if (deviceId.length() == 0) {
                    Toast.makeText(getApplicationContext(), "终端编号为空",
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "终端编号为空");
                    return;
                }

                final LoadingDialog loadingDialog = new LoadingDialog(TerminalActivateActivity.this, R.style.MyDialog);
                loadingDialog.show();

                mPosApplication.getMsgBuilder().buildTerminalActivationMsg(deviceId, textPOS.getText().toString().trim()).send(new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, throwable.toString());
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        loadingDialog.dismiss();
                        try {
                            Log.d("response", responseString);

                            LinkeaResponseMsg.TerminalActivationResponseMsg terminalActivationResponseMsg =
                                    LinkeaResponseMsgGenerator.generateTerminalActivationResponseMsg(responseString);

                            if (terminalActivationResponseMsg.success) {
                                mPosApplication.setOnlineState(true);

                                String terminalId = terminalActivationResponseMsg.terminal.term_id;
                                String terminalMac = terminalActivationResponseMsg.terminal.term_mac;

                                mPosApplication.setPosId(textPOS.getText().toString());
                                saveActivateInfo(textPOS.getText().toString().trim(),terminalId, terminalMac);

                                mPosApplication.getMsgBuilder().setTerm_id(terminalId);
                                mPosApplication.getMsgBuilder().setTerm_mac(terminalMac);

                                Intent intent=new Intent(TerminalActivateActivity.this, TermInfoActivity.class);
                                Bundle bundle=new Bundle();
                                bundle.putString("posid",textPOS.getText().toString().trim());
                                bundle.putString("termid",terminalActivationResponseMsg.terminal.term_id);
                                bundle.putString("termmac",terminalActivationResponseMsg.terminal.term_mac);
                                intent.putExtra("TERMINFO",bundle);
                                startActivity(intent);
                                finish();
                            } else {
                                String erroInfo = terminalActivationResponseMsg.result_code_msg;
                                if(terminalActivationResponseMsg.result_code.equals("901")){
                                    erroInfo="无效的序列号。";
                                }
                                Toast.makeText(getApplicationContext(), erroInfo, Toast.LENGTH_SHORT).show();
                                Log.e(TAG, erroInfo);
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                        }
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activate, menu);
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
