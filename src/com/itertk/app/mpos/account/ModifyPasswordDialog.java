package com.itertk.app.mpos.account;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.apache.http.Header;

/**
 * Created by waylen_wang on 2015/3/17.
 * 密码修改框
 */
public class ModifyPasswordDialog extends Dialog {
    final private static String TAG = "ModifyPasswordDialog";
    TextView textPasswordConfirm;
    TextView textPassword;
    TextView textOriginalPassword;
    Activity context;
    Button  btnOK;
    Button btnBack;

    public ModifyPasswordDialog(Activity context, int theme) {
        super(context, theme);
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_modify_password);
        //setCanceledOnTouchOutside(false);

        textOriginalPassword = (TextView) findViewById(R.id.textOriginalPassword);
        textPassword = (TextView) findViewById(R.id.textPassword);
        textPasswordConfirm = (TextView) findViewById(R.id.textPasswordConfirm);

        btnBack=(Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnOK = (Button) findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textOriginalPassword.getText().toString().isEmpty()) {
                    Toast.makeText(context, "原密码为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (textPassword.getText().toString().isEmpty()) {
                    Toast.makeText(context, "新密码为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                if (textPasswordConfirm.getText().toString().isEmpty()) {
                    Toast.makeText(context, "请确认新密码",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!textPassword.getText().toString().equals(textPasswordConfirm.getText().toString())) {

                    Toast.makeText(context, "两次新密码输入不一致",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (textPassword.getText().toString().trim().length() <6) {
                    Toast.makeText(context, "密码至少为6位",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (textPassword.getText().toString().trim().length() >16) {
                    Toast.makeText(context, "密码长度不能超过16位",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                ((MPosApplication)context.getApplication() ).getMsgBuilder().buildUpdatePasswordMsg(textOriginalPassword.getText().toString(), textPassword.getText().toString()).send(new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, throwable.toString());
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, responseString);
                        try {
                            LinkeaResponseMsg.UpdatePasswordResponseMsg updatePasswordResponseMsg = LinkeaResponseMsgGenerator.generateUpdatePasswordResponseMsg(responseString);
                            if (updatePasswordResponseMsg.isSuccess()) {
                                Log.d(TAG, "修改密码成功");
                                Toast.makeText(context, "密码修改成功", Toast.LENGTH_SHORT).show();
                                dismiss();
                            } else {
                                Log.d(TAG, "修改密码失败");
                                String erroInfo = updatePasswordResponseMsg.result_code_msg;
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
