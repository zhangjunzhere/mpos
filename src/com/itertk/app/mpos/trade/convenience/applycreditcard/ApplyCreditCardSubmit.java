package com.itertk.app.mpos.trade.convenience.applycreditcard;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.card51comm.LinkeaMsg51CardBuilder;
import com.itertk.app.mpos.card51comm.Response51CardMsg;
import com.itertk.app.mpos.card51comm.Response51CardMsg.ApplyTradeResponseMsg;
import com.itertk.app.mpos.card51dbhelper.ApplyCreditCardBean;
import com.itertk.app.mpos.card51dbhelper.CityBank;
import com.itertk.app.mpos.trade.TradeHomeActivity;
import com.loopj.android.http.TextHttpResponseHandler;

/**
 * 信用卡申请提交
 * @author zhangyifei 2015.04.02
 * 
 */
public class ApplyCreditCardSubmit extends Activity {
	private static final String TAG = "ApplyCreditCardSubmit";
	private Context mContext;
	private TextView textViewUserAgreement;
	private TextView textSelectedBankCount;

	private Button btnChoiceArea;

	private CheckBox checkBoxIsAgree;

	private GridView gridviewSelectedBank;

	private EditText etIdentificationCardNo, etWorkPlaceAddress, etName,
			etMobileNo;

	private ArrayList<CityBank> listSelectedBank;
	private SelectedBankAdapter selectedBankAdapter;

	private ApplyCreditCardBean applyCreditCardBean;
	private String cityName;

	private MPosApplication mposApplication;
	private LinkeaMsg51CardBuilder linkeaMsg51CardBuilder;

	private LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apply_credit_card_submit);
		MyActionbar.setNormalActionBarLayout(this);
		mContext = this;
		loadingDialog = new LoadingDialog(this, R.style.MyDialog);
		loadingDialog.setCanceledOnTouchOutside(false);

		setupView();
		getData();
		selectedBankAdapter = new SelectedBankAdapter(mContext,
				listSelectedBank);
		gridviewSelectedBank.setAdapter(selectedBankAdapter);

		mposApplication = (MPosApplication) getApplication();
		linkeaMsg51CardBuilder = mposApplication.get51CardMsgBuilder();

		setSelectedBankCount();
		setCityNameToButton();
	}

	private void setupView() {
		textViewUserAgreement = (TextView) findViewById(R.id.textViewUserAgreement);
		textSelectedBankCount = (TextView) findViewById(R.id.textSelectedBankCount);

		checkBoxIsAgree = (CheckBox) findViewById(R.id.checkBoxIsAgree);

		etIdentificationCardNo = (EditText) findViewById(R.id.etIdentificationCardNo);
		etWorkPlaceAddress = (EditText) findViewById(R.id.etWorkPlaceAddress);
		etName = (EditText) findViewById(R.id.etName);
		etMobileNo = (EditText) findViewById(R.id.etMobileNo);

		gridviewSelectedBank = (GridView) findViewById(R.id.gridviewSelectedBank);

		btnChoiceArea = (Button) findViewById(R.id.btnChoiceArea);

		textViewUserAgreement.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
	}

	private void getData() {
		Intent intent = getIntent();
		listSelectedBank = intent
				.getParcelableArrayListExtra(BankChoiceActivity.LISTSELECTEDBANK);
		cityName = intent.getStringExtra("cityName");
		applyCreditCardBean = intent
				.getParcelableExtra(BankChoiceActivity.APPLYCREDITCARDBEAN);
	}

	private void setSelectedBankCount() {
		textSelectedBankCount.setText("您已预约了" + listSelectedBank.size()
				+ "家银行  :");
	}

	private void setCityNameToButton() {
		btnChoiceArea.setText(cityName + "   请选择区县");
	}

	private void setCityAreaNameToButton(String cityAreaName) {
		if (isSelectedCityArea(cityAreaName)) {
			btnChoiceArea.setText(cityName + "   " + cityAreaName);
		}
	}

	/**
	 * 是否选择了城市区域
	 * 
	 * @param cityAreaName
	 * @return true 已选择，false 未选择
	 */
	private boolean isSelectedCityArea(String cityAreaName) {
		if ("".equals(cityAreaName)) {
			return false;
		}
		return true;
	}

	/**
	 * 用户协议
	 * 
	 * @param v
	 */
	public void onClickUserAgreement(View v) {
		UserAgreementDialog userAgreementDialog = new UserAgreementDialog(
				mContext, R.style.MyDialog);
		userAgreementDialog.show();
	}

	/*
	 * 选择城市区域
	 */
	public void onBtnChoiceArea(View v) {
		ChoiceCityAreaDialog choiceCityAreaDialog = new ChoiceCityAreaDialog(
				mContext, R.style.MyDialog, applyCreditCardBean,
				linkeaMsg51CardBuilder);

		choiceCityAreaDialog
				.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						final String cityAreaName = ((ChoiceCityAreaDialog) dialog)
								.getCityAreaName();
						setCityAreaNameToButton(cityAreaName);
					}

				});
		choiceCityAreaDialog.show();
	}

	/**
	 * 提交
	 * 
	 * @param v
	 */
	public void onSubmit(View v) {
		if (checkIDCard()) {
			loadingDialog.show();
			sendApplyCreditCardInfo();
		}
	}

	private void cancelLoadingDialog() {
		if (loadingDialog.isShowing()) {
			loadingDialog.cancel();
		}
	}

	/**
	 * 发送申请信用卡
	 */
	private void sendApplyCreditCardInfo() {
		linkeaMsg51CardBuilder.buildApplyTradeMsg(applyCreditCardBean).send(
				new TextHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							String responseString) {
						Log.v(TAG, responseString+"");
						cancelLoadingDialog();
						ApplyTradeResponseMsg response = Response51CardMsg
								.generateApplyTradeResponseMsg(responseString);
						if (response.isSuccess()) {
							Toast.makeText(mContext, "提交成功", Toast.LENGTH_LONG)
									.show();
							Intent intent = new Intent(mContext,
									TradeHomeActivity.class);
							startActivity(intent);
						} else {
							Toast.makeText(mContext, response.msg,
									Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						Log.v(TAG, responseString+"");
						cancelLoadingDialog();
						Toast.makeText(mContext, "网络连接失败", Toast.LENGTH_LONG)
								.show();
					}
				});
	}

	/**
	 * 校验身份证号码合法性
	 * 
	 * @return
	 */
	private boolean checkIDCard() {
		String IdentificationCardNo = etIdentificationCardNo.getText()
				.toString().trim();
		IDCard idcard = new IDCard();
		String result = idcard.IDCardValidate(IdentificationCardNo);
		if ("".equals(result)) {
			applyCreditCardBean.idcard = IdentificationCardNo;
			return checkWorkPlaceAddress();
		} else {
			Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
			return false;
		}
	}

	/**
	 * 校验工作场所地址
	 * 
	 * @return
	 */
	private boolean checkWorkPlaceAddress() {
		String workPlaceAddress = etWorkPlaceAddress.getText().toString()
				.trim();
		if (!"".equals(workPlaceAddress)) {
			applyCreditCardBean.address = workPlaceAddress;
			return checkName();
		} else {
			Toast.makeText(mContext, "请输入单位详细地址", Toast.LENGTH_LONG).show();
			return false;
		}
	}

	/**
	 * 校验姓名合法性
	 * 
	 * @return
	 */
	private boolean checkName() {
		String name = etName.getText().toString().trim();
		if (!"".equals(name)) {
			if (name.length() >= 2 && name.length() < 50) {
				applyCreditCardBean.truename = name;
				return checkMobileNo();
			} else {
				Toast.makeText(mContext, "请输入正确姓名", Toast.LENGTH_LONG).show();
				return false;
			}
		} else {
			Toast.makeText(mContext, "请输入姓名", Toast.LENGTH_LONG).show();
			return false;
		}
	}

	/**
	 * 校验手机号码合法性
	 * 
	 * @return
	 */
	private boolean checkMobileNo() {
		String mobileNo = etMobileNo.getText().toString().trim();
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobileNo);
		if (m.matches()) {
			applyCreditCardBean.mobile = mobileNo;
			return checkAreaCode();
		} else {
			Toast.makeText(mContext, "手机号码格式不正确", Toast.LENGTH_LONG).show();
			return false;
		}
	}

	/**
	 * 校验城市区域
	 * 
	 * @return
	 */
	private boolean checkAreaCode() {
		if (applyCreditCardBean.areacode == null) {
			Toast.makeText(mContext, "请选择地区", Toast.LENGTH_LONG).show();
			return false;
		}
		return checkBoxIsAgree();
	}

	/**
	 * 校验同意用户协议
	 * 
	 * @return
	 */
	private boolean checkBoxIsAgree() {
		if (checkBoxIsAgree.isChecked()) {
			return true;
		} else {
			Toast.makeText(mContext, "请勾选同意用户协议", Toast.LENGTH_LONG).show();
			return false;
		}
	}

}
