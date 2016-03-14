package com.itertk.app.mpos.trade.convenience.applycreditcard;

import java.util.ArrayList;

import org.apache.http.Header;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.itertk.app.mpos.R;
import com.itertk.app.mpos.card51comm.LinkeaMsg51CardBuilder;
import com.itertk.app.mpos.card51comm.Response51CardMsg;
import com.itertk.app.mpos.card51comm.Response51CardMsg.CityAreaResponseMsg;
import com.itertk.app.mpos.card51dbhelper.ApplyCreditCardBean;
import com.itertk.app.mpos.card51dbhelper.CityArea;
import com.loopj.android.http.TextHttpResponseHandler;

/**
 * 选择城市区域
 * 
 * @author zyf
 * 
 */
public class ChoiceCityAreaDialog extends Dialog {

	private static final String TAG = "ChoiceCityAreaDialog";
	// 延时多少秒diss掉dialog
	private static final int DELAY_DISMISS = 1000 * 30;
	private static final int CANCEL_ANIMATION = 0;
	private static final int LOAD_ANIMATION = 1;

	private Context context;
	private ListView listViewSelectArea;
	private SelectCityAreaAdapter adapter;
	private ImageView listViewSelectAreaLoadIng;
	private ImageButton btnBack;

	private ApplyCreditCardBean applyCreditCardBean;

	private ArrayList<CityArea> listCityArea;

	private LinkeaMsg51CardBuilder linkeaMsg51CardBuilder;

	private String cityAreaName = "";

	public String getCityAreaName() {
		return cityAreaName;
	}

	public ChoiceCityAreaDialog(Context context, int theme,
			ApplyCreditCardBean applyCreditCardBean,
			LinkeaMsg51CardBuilder linkeaMsg51CardBuilder) {
		super(context, theme);
		this.applyCreditCardBean = applyCreditCardBean;
		this.context = context;
		this.linkeaMsg51CardBuilder = linkeaMsg51CardBuilder;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_choice_cityarea);
		btnBack = (ImageButton) findViewById(R.id.btnBack);
		listCityArea = new ArrayList<CityArea>();
		listViewSelectArea = (ListView) findViewById(R.id.listViewSelectArea);
		listViewSelectAreaLoadIng = (ImageView) findViewById(R.id.listViewSelectAreaLoadIng);

		adapter = new SelectCityAreaAdapter(context, listCityArea);
		listViewSelectArea.setAdapter(adapter);
		getCityAreaByCityCode();

		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}

		});

		listViewSelectArea
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						CityArea cityArea = listCityArea.get(position);
						applyCreditCardBean.areacode = cityArea.getCode();
						cityAreaName = cityArea.getName();
						dismiss();
					}

				});
	}

	/**
	 * 获取城市区域码
	 */
	private void getCityAreaByCityCode() {
		loadLoadingAnimation();
		linkeaMsg51CardBuilder.buildQueryCityAreaMsg(
				applyCreditCardBean.citycode).send(
				new TextHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							String responseString) {
						Log.v(TAG, responseString + "");
						cancelLoadingAnimation();
						CityAreaResponseMsg cityAreaResponseMsg = Response51CardMsg
								.generateCityAreaResponseMsg(responseString);
						listCityArea.clear();
						listCityArea.addAll(cityAreaResponseMsg.data);

						adapter.notifyDataSetChanged();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						Log.v(TAG, responseString + "");
						cancelLoadingAnimation();
						Toast.makeText(context, "网络连接失败", Toast.LENGTH_LONG)
								.show();
					}
				});
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
	 * 加载进度条
	 */
	public void loadLoadingAnimation() {
		hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context,
				R.anim.dialog_loading_animation);
		listViewSelectArea.setVisibility(View.GONE);
		listViewSelectAreaLoadIng.setVisibility(View.VISIBLE);
		// 使用ImageView显示动画
		listViewSelectAreaLoadIng.startAnimation(hyperspaceJumpAnimation);
		if (listViewSelectAreaLoadIng != null
				&& listViewSelectAreaLoadIng.getVisibility() == View.VISIBLE) {
			loadingHandler.sendEmptyMessageDelayed(CANCEL_ANIMATION,
					DELAY_DISMISS);
		}
	}

	/**
	 * 取消进度条
	 */
	public void cancelLoadingAnimation() {
		if (listViewSelectAreaLoadIng != null) {
			listViewSelectAreaLoadIng.clearAnimation();
			listViewSelectAreaLoadIng.setVisibility(View.GONE);
			listViewSelectArea.setVisibility(View.VISIBLE);
		}
	}
}
