package com.itertk.app.mpos.account;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.login.BindBankCardActivity;
import com.itertk.app.mpos.login.VerifyCodeDialog;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by waylen_wang on 2015/3/17.
 * 银行卡信息dialog
 */
public class BankCardInfoDialog extends Dialog {
    final private static String TAG = "BankCardInfoDialog";
    TextView text_cardNo;
    TextView text_cardOwner;
    TextView text_cardOrg;
    Activity context;
    Button  btnOK;
    Button btnBack;

    public BankCardInfoDialog(Activity context, int theme) {
        super(context, theme);
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_bankcard_info);

        final MPosApplication mPosApplication = (MPosApplication)context.getApplication();

        text_cardNo = (TextView) findViewById(R.id.text_cardNo);
        text_cardOwner = (TextView) findViewById(R.id.text_cardOwner);
        text_cardOrg = (TextView) findViewById(R.id.text_cardOrg);
        if(mPosApplication.getMember()!=null) {
            text_cardNo.setText(mPosApplication.getMember().member.card_no);
            text_cardOwner.setText(mPosApplication.getMember().member.card_holder);
            text_cardOrg.setText(mPosApplication.getMember().member.bank_name);
        }

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
/*                AccountHomeActivity accountHomeActivity = (AccountHomeActivity)context;
                Intent intent = new Intent(accountHomeActivity, BindBankCardActivity.class);
                context.startActivity(intent);*/
                if(mPosApplication.getMember()==null) {
                    Toast.makeText(context,"离线用户，不能绑定银行卡",Toast.LENGTH_SHORT).show();
                    return;
                }
                VerifyCodeDialog verifyCodeDialog=new VerifyCodeDialog(context,R.style.MyDialog,mPosApplication.getMember().member.member_id);
                verifyCodeDialog.show();
                dismiss();
            }
        });
    }


}
