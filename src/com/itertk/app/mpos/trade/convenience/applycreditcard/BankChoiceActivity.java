package com.itertk.app.mpos.trade.convenience.applycreditcard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.card51comm.LinkeaMsg51CardBuilder;
import com.itertk.app.mpos.card51comm.Response51CardMsg;
import com.itertk.app.mpos.card51comm.Response51CardMsg.CityBankResponseMsg;
import com.itertk.app.mpos.card51dbhelper.ApplyCreditCardBean;
import com.itertk.app.mpos.card51dbhelper.City;
import com.itertk.app.mpos.card51dbhelper.CityBank;
import com.itertk.app.mpos.card51dbhelper.CityDao;
import com.itertk.app.mpos.card51dbhelper.CityDao.Properties;
import com.itertk.app.mpos.card51dbhelper.LocationCard51Helper;
import com.loopj.android.http.TextHttpResponseHandler;

/**
 * 银行卡选择
 * 
 * @author zhangyifei 2015.03.30
 * 
 */
public class BankChoiceActivity extends Activity {
	// 延时多少秒diss掉dialog
	private static final int DELAY_DISMISS = 1000 * 30;
	private static final int CANCEL_ANIMATION = 0;
	private static final int LOAD_ANIMATION = 1;

	public static final String APPLYCREDITCARDBEAN = "applyCreditCardBean";
	public static final String LISTSELECTEDBANK = "listSelectedBank";
	private static final int REQUEST_CODE = 1;
	private static final String TAG = "BankChoiceActivity";
	private Context mContext;
	private MPosApplication mposApplication;
	private LinkeaMsg51CardBuilder linkeaMsg51CardBuilder;
	private GridView gridViewBankList;
	private ImageView gridViewLoadIng;

	private List<CityBank> listCityBank;
	private ChoiceBankAdapter bankAdapter;

	private Map<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
	private int isSelectedBankCounts = 0;

	private String cityCode = "";
	private String cityName = "";
	private Button btnBankChoice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bankchoice);
		this.mContext = this;
		MyActionbar.setNormalActionBarLayout(this);
		btnBankChoice = (Button) findViewById(R.id.btnBankChoice);
		mposApplication = (MPosApplication) getApplication();
		linkeaMsg51CardBuilder = mposApplication.get51CardMsgBuilder();

		gridViewBankList = (GridView) findViewById(R.id.gridViewBankList);
		gridViewLoadIng = (ImageView) findViewById(R.id.gridViewLoadIng);

		loadLoadingAnimation();
		setButtonText(LOAD_ANIMATION);
		getBankListByCityCode(getCityCode());

		listCityBank = new ArrayList<CityBank>();
		bankAdapter = new ChoiceBankAdapter(mContext, listCityBank);
		gridViewBankList.setAdapter(bankAdapter);

		gridViewBankList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						CityBank bank = listCityBank.get(position);
						Boolean b = mSelectMap.get(bank.bankid);
						if (canSelectBank(bank)) {
							// 选中银行
							selectBank(bank, view);
						} else if (canCancelSelectBank(bank)) {
							// 取消选择
							cancelSelectBank(bank, view);
						} else {
							Toast.makeText(mContext, "已经选了3个银行",
									Toast.LENGTH_LONG).show();
						}

					}

				});
	}

	/**
	 * 设置选择银行按钮状态
	 * 
	 * @param state
	 */
	private void setButtonText(int state) {
		if (state == LOAD_ANIMATION) {
			btnBankChoice.setEnabled(false);
			btnBankChoice.setText("读取数据中...");
		} else {
			btnBankChoice.setEnabled(true);
			btnBankChoice.setText("开始预约");
		}
	}

	/**
	 * 通过城市名称获得城市行政区域编码
	 * 
	 * @return citycode
	 */
	private String getCityCode() {
		getCityName();
		if (!cityName.isEmpty()) {
			LocationCard51Helper locationCard51Helper = mposApplication
					.getLocationCard51Helper();
			CityDao cityDao = locationCard51Helper.getCityManager();
			List<City> listCity = cityDao.queryBuilder()
					.where(Properties.Name.like(cityName + "%")).list();
			if (isCityExist(listCity)) {
				City city = listCity.get(0);
				if (city != null) {
					cityCode = city.getCode();
				}
			}
		}
		return cityCode;
	}

	/**
	 * 获得城市名
	 */
	private void getCityName() {
		MPosApplication mposApplication = MPosApplication.getInstance();
		cityName = mposApplication.getCityName();
		if (cityName != null && cityName.length() > 1) {
			// 百度地图查的城市名称后面有一个市字，本地库没有。
			String cityNameLastText = cityName.substring(cityName.length() - 1);
			if (cityNameLastText.equals("市")) {
				cityName = cityName.substring(0, cityName.length() - 1);
			}
		}
	}

	/**
	 * 城市是否存在
	 * 
	 * @param listCity
	 * @return true 存在，false 不存在
	 */
	private boolean isCityExist(List<City> listCity) {
		boolean isExist = false;
		if (listCity != null && listCity.size() > 0) {
			isExist = true;
		}
		return isExist;
	}

	/**
	 * 能否选择银行
	 * 
	 * @param bank
	 * @return true 可以，false 不可以
	 */
	private boolean canSelectBank(CityBank bank) {
		boolean flag = false;
		Boolean b = mSelectMap.get(bank.bankid);
		if (b == null && isSelectedBankCounts < 3) {
			flag = true;
		} else if (b != null && b == false && isSelectedBankCounts < 3) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 能否取消选择银行
	 * 
	 * @param bank
	 * @return true 可以，false 不可以
	 */
	private boolean canCancelSelectBank(CityBank bank) {
		boolean flag = false;
		Boolean b = mSelectMap.get(bank.bankid);
		if (b != null && b == true) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 点击选择银行
	 * 
	 * @param bank
	 * @param view
	 */
	public void selectBank(CityBank bank, View view) {
		isSelectedBankCounts++;
		mSelectMap.put(bank.bankid, true);
		view.setBackgroundResource(R.drawable.bank_select_bankground_press);
	}

	/**
	 * 点击取消选择银行
	 * 
	 * @param bank
	 * @param view
	 */
	private void cancelSelectBank(CityBank bank, View view) {
		isSelectedBankCounts--;
		mSelectMap.put(bank.bankid, false);
		view.setBackgroundResource(R.drawable.bank_select_bankground);
	}

	/**
	 * 获取银行列表
	 * 
	 * @param cityCode
	 */
	private void getBankListByCityCode(String cityCode) {
		if (!cityCode.isEmpty()) {
			linkeaMsg51CardBuilder.buildQueryCityBankMsg(cityCode).send(
					new TextHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								String responseString) {
							Log.v(TAG, responseString + "");
							cancelLoadingAnimation();
							if (!responseString.isEmpty()) {
								CityBankResponseMsg cityBankResponseMsg = Response51CardMsg
										.generateCityBankResponseMsg(responseString);
								boolean success = cityBankResponseMsg
										.isSuccess();
								if (success) {
									refreashGridViewData(cityBankResponseMsg.data);
								} else {
									Toast.makeText(mContext,
											cityBankResponseMsg.msg,
											Toast.LENGTH_LONG).show();
								}
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								String responseString, Throwable throwable) {
							Log.v(TAG, responseString + "");
							cancelLoadingAnimation();
							btnBankChoice.setEnabled(false);
							btnBankChoice.setVisibility(View.INVISIBLE);
							Toast.makeText(mContext, "网络连接失败",
									Toast.LENGTH_LONG).show();
						}
					});
		} else {
			cancelLoadingAnimation();
		}
	}

	/**
	 * 更新页面银行列表显示
	 * 
	 * @param listCityBank
	 */
	private void refreashGridViewData(List<CityBank> listCityBank) {
		this.listCityBank.clear();
		this.listCityBank.addAll(listCityBank);
		bankAdapter.notifyDataSetChanged();
		gridViewBankList.setVisibility(View.VISIBLE);
	}

	private static Animation hyperspaceJumpAnimation = null;
	Handler loadingHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CANCEL_ANIMATION: {
				cancelLoadingAnimation();
				break;
			}
			case LOAD_ANIMATION: {
				// 加载动画
				loadLoadingAnimation();
				break;
			}
			default:
				break;
			}
		}
	};

	/**
	 * 界面加载中，界面显示
	 */
	private void loadLoadingAnimation() {
		setButtonText(LOAD_ANIMATION);
		hyperspaceJumpAnimation = AnimationUtils.loadAnimation(mContext,
				R.anim.dialog_loading_animation);
		gridViewBankList.setVisibility(View.GONE);
		gridViewLoadIng.setVisibility(View.VISIBLE);
		// 使用ImageView显示动画
		gridViewLoadIng.startAnimation(hyperspaceJumpAnimation);
		if (gridViewLoadIng != null
				&& gridViewLoadIng.getVisibility() == View.VISIBLE) {
			loadingHandler.sendEmptyMessageDelayed(CANCEL_ANIMATION,
					DELAY_DISMISS);
		}
	}

	/**
	 * 界面停止加载中，界面显示
	 */
	private void cancelLoadingAnimation() {
		setButtonText(CANCEL_ANIMATION);
		if (gridViewLoadIng != null) {
			gridViewLoadIng.clearAnimation();
			gridViewLoadIng.setVisibility(View.GONE);
		}
	}

	/**
	 * 预约按钮
	 * 
	 * @param v
	 */
	public void startAppoint(View v) {
		if (isSelectedBank()) {
			btnBankChoice.setEnabled(false);
			Intent intent = new Intent(mContext,
					ApplyCreditCardInfoDialog.class);
			startActivityForResult(intent, REQUEST_CODE);
		} else {
			Toast.makeText(mContext, "请选择银行", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 是否已经选择银行
	 * 
	 * @return true已经选择，false 未选择
	 */
	private boolean isSelectedBank() {
		if (isSelectedBankCounts == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 获取已经选择银行
	 * 
	 * @param mSelectedMap
	 * @return
	 */
	private ArrayList<CityBank> getSelectedCityBank(
			Map<Integer, Boolean> mSelectedMap) {
		ArrayList<CityBank> list = new ArrayList<CityBank>();

		for (Entry<Integer, Boolean> entry : mSelectedMap.entrySet()) {
			Integer key = entry.getKey();
			Boolean isSelected = entry.getValue();

			for (CityBank cityBank : listCityBank) {
				if (isSameBank(cityBank.bankid, key) && isSelected) {
					list.add(cityBank);
				}
			}
		}

		return list;
	}

	private boolean isSameBank(int bankid, int key) {
		if (bankid == key) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置已经选择银行的代码，到申请信用卡的bean中
	 * 
	 * @param listSelectedCityBank
	 * @param applyCreditCardBean
	 */
	private void setSelectedBankToApplyCreaditCardBean(
			List<CityBank> listSelectedCityBank,
			ApplyCreditCardBean applyCreditCardBean) {
		StringBuilder sb = new StringBuilder();
		for (CityBank cityBank : listSelectedCityBank) {
			sb.append(cityBank.bankid + ",");
		}
		String selectedBankId = sb.toString();
		applyCreditCardBean.bankids = selectedBankId.substring(0,
				selectedBankId.length() - 1).trim();
	}

	/**
	 * 设置城市新政区域编码，到申请信用卡的bean中
	 * 
	 * @param applyCreditCardBean
	 */
	private void setCityCodeToApplyCreaditCardBean(
			ApplyCreditCardBean applyCreditCardBean) {
		applyCreditCardBean.citycode = cityCode;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		btnBankChoice.setEnabled(true);
		switch (resultCode) {
		case Activity.RESULT_OK:
			Bundle bundle = data.getExtras(); // data为B中回传的Intent

			ApplyCreditCardBean applyCreditCardBean = bundle
					.getParcelable(APPLYCREDITCARDBEAN);
			ArrayList<CityBank> list = getSelectedCityBank(mSelectMap);
			setSelectedBankToApplyCreaditCardBean(list, applyCreditCardBean);
			setCityCodeToApplyCreaditCardBean(applyCreditCardBean);

			Intent intent = new Intent(mContext, ApplyCreditCardSubmit.class);
			intent.putParcelableArrayListExtra(LISTSELECTEDBANK, list);
			intent.putExtra(APPLYCREDITCARDBEAN, applyCreditCardBean);
			intent.putExtra("cityName", cityName);
			startActivity(intent);
			break;
		default:
			break;

		}
	}
}
