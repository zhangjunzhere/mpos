package com.itertk.app.mpos.account;



import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.login.BindBankCardActivity;
import com.itertk.app.mpos.trade.taobao.MessageBoxDialog;
import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.utility.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/**
 * A simple {@link Fragment} subclass.
 * 账户提现fragment
 *
 */
public class AccountCashFragment extends Fragment implements InputPasswordDialog.PasswordHandle {
    EditText mInputBalance;
    TextView textBalance;
    Button mBtnGetBalance;
    final  String TAG="AccountCashFragment";
    public static String CASH_KEY="cash_key";
    MPosApplication mPosApplication;

    public AccountCashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_cash, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mInputBalance=(EditText)view.findViewById(R.id.inputBalance);
        mInputBalance.setCompoundDrawablePadding(30);
        Utils.setPricePoint(mInputBalance);

        mPosApplication = (MPosApplication)getActivity().getApplication();

        if(mPosApplication.getMember()!=null) {
            final String balance=mPosApplication.getMember().member.to_cash_amount;
            textBalance = (TextView) view.findViewById(R.id.textBalance);
            textBalance.setText("￥" +balance);


            mBtnGetBalance = (Button) view.findViewById(R.id.btnGetBalance);
            mBtnGetBalance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String newBalance=mPosApplication.getMember().member.to_cash_amount;
                    if(mPosApplication.getMember().member.member_id.equals(Utils.getStringInfo(getActivity(), "BANKBIND_"+mPosApplication.getMember().member.member_id))
                            && !mPosApplication.getMember().authenticate.bank_state.equals(BindBankCardActivity.AUTH_BANK_SUCCESS)) {
                        Toast.makeText(getActivity(),"您的银行卡正在审核中，暂时不能提现",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if("".equals(mInputBalance.getText().toString().trim())){
                        Toast.makeText(getActivity(),"您的提现金额为空",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(Arith.newBigDecimal(mInputBalance.getText().toString().trim()).compareTo(Arith.newBigDecimal(newBalance))>0){
                        Toast.makeText(getActivity(),"您的最大提现金额为￥"+newBalance+",请重新输入正确的金额",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    InputPasswordDialog inputPasswordDialog=new InputPasswordDialog(getActivity(),R.style.MyDialog,AccountCashFragment.this);
                    inputPasswordDialog.show();

                }
            });
        }
    }

    private void getCash(){
        final LoadingDialog loadingDialog = new LoadingDialog(getActivity(), R.style.MyDialog);
        loadingDialog.show();

        mPosApplication.getMsgBuilder().buildCreateOrderMsg2(mInputBalance.getText().toString().trim(), "1002",
                "会员提现-" + mPosApplication.getMember().member.member_id,
                "{\"memberNo\":'"+mPosApplication.getMember().member.member_id+"',\"bankName\":'" +mPosApplication.getMember().member.bank_name+"'"
                        +",\"cardHolder\":'"+mPosApplication.getMember().member.card_holder+"',\"cardNo\":'"+mPosApplication.getMember().member.card_no+"'}").send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                loadingDialog.dismiss();
                Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
                Log.e(TAG, throwable.toString());
                return;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                loadingDialog.dismiss();
                try {
                    LinkeaResponseMsg.CreateOrderResponseMsg createOrderResponseMsg =
                            LinkeaResponseMsgGenerator.generateCreateOrderResponseMsg(responseString);
                    if (createOrderResponseMsg.success) {
                        //Toast.makeText(getActivity(), "提现成功！", Toast.LENGTH_SHORT).show();
                        float newcash= Arith.sub(Float.valueOf(mPosApplication.getMember().member.to_cash_amount),Float.valueOf(mInputBalance.getText().toString().trim()));
                        float newAmountcash= Arith.sub(Float.valueOf(mPosApplication.getMember().member.amount),Float.valueOf(mInputBalance.getText().toString().trim()));;
                        mPosApplication.getMember().member.setTo_cash_amount(Utils.formatPriceNoSympol(getActivity(),newcash));
                        mPosApplication.getMember().member.setcash_amount(Utils.formatPriceNoSympol(getActivity(),newAmountcash));
                        textBalance.setText("￥" +Utils.formatPriceNoSympol(getActivity(),newcash));
                        MessageBoxDialog dialog=new MessageBoxDialog(getActivity(),"提现成功",true);
                        dialog.show();
                    } else {
                        String erroInfo = createOrderResponseMsg.result_code_msg;
                        //Toast.makeText(getActivity(), erroInfo, Toast.LENGTH_SHORT).show();
                        MessageBoxDialog dialog=new MessageBoxDialog(getActivity(),"提现失败",erroInfo,false);
                        dialog.show();
                        Log.e(TAG, erroInfo);
                    }

                } catch (Exception e) {
                    //Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                    MessageBoxDialog dialog=new MessageBoxDialog(getActivity(),"提现失败",false);
                    dialog.show();
                    Log.d(TAG, e.toString());
                }
            }
        });
    }

    @Override
    public void passwordVeriSuccess(){
        getCash();
    }


}
