package com.itertk.app.mpos.trade.convenience.applycreditcard;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import com.itertk.app.mpos.R;
import com.itertk.app.mpos.card51dbhelper.ApplyCreditCardBean;

/**
 * 信用卡申请信息对话框
 * @author zhangyifei 2015.04.01
 * 
 */
public class ApplyCreditCardInfoDialog extends Activity {

	public ApplyCreditCardBean applyCreditCardBean;
	FragmentTransaction transaction;
	BasicCustomerInfoFragment basicCustomerInfoFragment = null;

	private void showbasicCustomerInfoFragment() {
		if (basicCustomerInfoFragment == null) {
			basicCustomerInfoFragment = new BasicCustomerInfoFragment();
		}
		transaction.replace(R.id.mainContainer, basicCustomerInfoFragment);
		transaction.commit();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_apply_creditcard_info);
		applyCreditCardBean = new ApplyCreditCardBean();
		transaction = getFragmentManager().beginTransaction();
		showbasicCustomerInfoFragment();
	}

	public void onBtnBack(View v) {
		finish();
	}
}
