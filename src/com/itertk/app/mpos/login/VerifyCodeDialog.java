package com.itertk.app.mpos.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by waylen_wang on 2015/3/17.
 * 短信验证码对话框
 */
public class VerifyCodeDialog extends Dialog {
    final private static String TAG = "VerifyCodeDialog";
    TextView textPhone;
    TextView textVerify;
    Activity context;
    Button  btnNext;
    Button btnBack;
    String phone;
    MPosApplication mPosApplication;

    public VerifyCodeDialog(Activity context, int theme,String phone) {
        super(context, theme);
        this.context = context;
        this.phone=phone;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_verify_code);
        mPosApplication=(MPosApplication)context.getApplication();

        textPhone = (TextView) findViewById(R.id.textPhone);
        textPhone.setText(phone);
        textVerify = (TextView) findViewById(R.id.text_verfiyTxt);

        btnBack=(Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnNext = (Button) findViewById(R.id.verify_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textVerify.getText().toString().isEmpty()) {
                    Toast.makeText(context, "密码为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                mPosApplication.getMsgBuilder().buildLoginMsg(textPhone.getText().toString(), textVerify.getText().toString()).send(new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, throwable.toString());
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, responseString);
                        Boolean success = false;
                        try {
                            LinkeaResponseMsg.LoginResponseMsg loginResponseMsg =
                                    LinkeaResponseMsgGenerator.generateLoginResponseMsg(responseString);

                            success = loginResponseMsg.isSuccess();
                            if (success) {
                                //login success
                                Intent intent = new Intent(context, BindBankCardActivity.class);
                                context.startActivity(intent);
                                dismiss();

                            } else {
                                String erroInfo = loginResponseMsg.result_code_msg;
                                if(loginResponseMsg.result_code.equals("910"))
                                    erroInfo="密码错误！";
                                Toast.makeText(context, erroInfo, Toast.LENGTH_SHORT).show();
                                Log.e(TAG, erroInfo);
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                        }
                    }
                });

            }
        });
    }


}
