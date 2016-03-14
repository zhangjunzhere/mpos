package com.itertk.app.mpos.account;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.utility.DataHelper;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by waylen_wang on 2015/3/17.
 * 密码确认框
 */
public class InputPasswordDialog extends Dialog {
    final private static String TAG = "InputPasswordDialog";
    EditText textPassword;

    Context context;
    Button  btnOK;
    Button btnBack;
    PasswordHandle passwordHandle;
    private Boolean mSuccess = false;

    public InputPasswordDialog(Context context, int theme,PasswordHandle handle) {
        super(context, theme);
        this.context = context;
        passwordHandle=handle;
    }

    public Boolean verifySuccess(){
        return mSuccess;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_input_password);


        textPassword = (EditText) findViewById(R.id.inputpwd);

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
                String pwd=textPassword.getText().toString().trim();
                if (pwd.length()==0) {
                    Toast.makeText(context, "密码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pwd.length() <6) {
                    Toast.makeText(context, "密码至少为6位",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pwd.length() >16) {
                    Toast.makeText(context, "密码长度不能超过16位",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                MPosApplication mPosApplication = MPosApplication.getInstance();
                DataHelper dataHelper= mPosApplication.getDataHelper();
                String UserName=mPosApplication.getMember().member.member_id;
                if(dataHelper.userLogin(context,UserName,pwd)!=null) {
                    if(passwordHandle != null) {
                        passwordHandle.passwordVeriSuccess();
                    }
                    mSuccess = true;
                    dismiss();
                }else{
                    Toast.makeText(context,"密码错误，请重新输入",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public interface PasswordHandle{
        public void passwordVeriSuccess();
    }


}
