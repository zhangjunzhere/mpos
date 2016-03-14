package com.itertk.app.mpos.trade.convenience.applycreditcard;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.itertk.app.mpos.R;
/**
 * 用户协议
 * @author zyf
 *
 */
public class UserAgreementDialog extends Dialog {

	private ImageButton btnBack;

	public UserAgreementDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_user_agreement);
		btnBack = (ImageButton) findViewById(R.id.selector_btnback_apply_credit_info);
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}

		});
	}
}
