package com.itertk.app.mpos.account;



import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.login.BindBankCardActivity;
import com.itertk.app.mpos.login.CertificateActivity;
import com.itertk.app.mpos.login.LoginActivity;
import com.itertk.app.mpos.utility.Utils;

/**
 * A simple {@link Fragment} subclass.
 * 账户中心fragment
 *
 */
public class AccountCenterFragment extends Fragment {
    static final String TAG = "AccountCenterFragment";
    RelativeLayout btnSecurity;
    RelativeLayout btnBankcard;
    RelativeLayout btnAuth;
    TextView btnLogout;
    RelativeLayout btnDeposit;

    TextView textAccount;
    TextView textFullName;
    TextView textBankcard;
    TextView textBalance;

    public AccountCenterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_center, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final  MPosApplication mPosApplication = (MPosApplication)getActivity().getApplication();

        if(mPosApplication.getMember()!=null) {
            textAccount = (TextView) view.findViewById(R.id.textAccount);
            textAccount.setText(mPosApplication.getMember().member.member_id);

            textFullName = (TextView) view.findViewById(R.id.textFullName);
            textFullName.setText(mPosApplication.getMember().member.member_name);

            textBankcard = (TextView) view.findViewById(R.id.textBankcard);
            if(mPosApplication.getMember().member.member_id.equals(Utils.getStringInfo(getActivity(), "BANKBIND_"+mPosApplication.getMember().member.member_id))
                    && !mPosApplication.getMember().authenticate.bank_state.equals(BindBankCardActivity.AUTH_BANK_SUCCESS)) {
                textBankcard.setText(mPosApplication.getMember().member.card_no +"\n(审核中)");
            }else{
                textBankcard.setText(mPosApplication.getMember().member.card_no);
            }

            textBalance =(TextView) view.findViewById(R.id.textBalance);
            textBalance.setText("￥" + mPosApplication.getMember().member.amount);
        }


        btnSecurity = (RelativeLayout)view.findViewById(R.id.btnSecurity);
        btnSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*AccountHomeActivity accountHomeActivity = (AccountHomeActivity)getActivity();
                Intent intent = new Intent(accountHomeActivity, SecurityActivity.class);
                startActivity(intent);*/
                ModifyPasswordDialog modifyPasswordDialog=new ModifyPasswordDialog(getActivity(), R.style.MyDialog);
                modifyPasswordDialog.show();
            }
        });

        btnBankcard = (RelativeLayout)view.findViewById(R.id.btnBankcard);
        btnBankcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                AccountHomeActivity accountHomeActivity = (AccountHomeActivity)getActivity();
                Intent intent = new Intent(accountHomeActivity, BindBankCardActivity.class);
                startActivity(intent);*/
                if(mPosApplication.getMember().member.member_id.equals(Utils.getStringInfo(getActivity(), "BANKBIND_"+mPosApplication.getMember().member.member_id))
                        && !mPosApplication.getMember().authenticate.bank_state.equals(BindBankCardActivity.AUTH_BANK_SUCCESS)) {
                    Toast.makeText(getActivity(), "您的银行卡正在审核中，暂时不能做此操作", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    BankCardInfoDialog bankCardInfoDialog = new BankCardInfoDialog(getActivity(), R.style.MyDialog);
                    bankCardInfoDialog.show();
                }
            }
        });

        btnAuth = (RelativeLayout)view.findViewById(R.id.btnAuth);
        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountHomeActivity accountHomeActivity = (AccountHomeActivity)getActivity();
                Intent intent = new Intent(accountHomeActivity, CertificateActivity.class);
                startActivity(intent);
            }
        });

        btnLogout = (TextView)view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountHomeActivity accountHomeActivity = (AccountHomeActivity)getActivity();
                Intent intent = new Intent(accountHomeActivity, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                accountHomeActivity.finish();
            }
        });

        btnDeposit = (RelativeLayout)view.findViewById(R.id.btnDeposit);
        btnDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountHomeActivity accountHomeActivity = (AccountHomeActivity)getActivity();
                accountHomeActivity.onBtnCash();
            }
        });
    }
}
