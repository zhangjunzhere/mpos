package com.itertk.app.mpos.trade.convenience.applycreditcard;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.itertk.app.mpos.R;

/**
 * 用户基本信息
 * @author zhangyifei 2015.04.01
 * 
 */
public class BasicCustomerInfoFragment extends Fragment {
	private Button btnBasicCustomerNext, btnZhuanKe, btnBenKe, btnXueLiQiTa,
			btnZuFang, btnBenDiGouFang, btnJiTiSuShe, btnFuMu, btnQinQiJia,
			btnZhuFangXingZhiQiTa, btnWuXinYongKa, btnYouQiTaXinYongKa,
			btnYouBenHangXinYongKa, btnBaiLingShangBanZu,
			btnGongWuYuanShiYeDanWei, btnFangDiChanJingJi, btnMeiRongMeiFa,
			btnXiaoXinCanYin, btnBaoAnBaoJie, btnGongZuoXinXiQiTa;
	private String wrongMsg = "";
	ApplyCreditCardInfoDialog dialog;
	WorkSituationFragment workSituationFragment = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_basic_customer_info,
				container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		setupView(view);
		setListener();
	}

	private void setupView(View view) {
		btnBasicCustomerNext = (Button) view
				.findViewById(R.id.btnBasicCustomerNext);
		btnZhuanKe = (Button) view.findViewById(R.id.btnZhuanKe);
		btnBenKe = (Button) view.findViewById(R.id.btnBenKe);
		btnXueLiQiTa = (Button) view.findViewById(R.id.btnXueLiQiTa);

		btnZuFang = (Button) view.findViewById(R.id.btnZuFang);
		btnBenDiGouFang = (Button) view.findViewById(R.id.btnBenDiGouFang);
		btnJiTiSuShe = (Button) view.findViewById(R.id.btnJiTiSuShe);
		btnFuMu = (Button) view.findViewById(R.id.btnFuMu);
		btnQinQiJia = (Button) view.findViewById(R.id.btnQinQiJia);
		btnZhuFangXingZhiQiTa = (Button) view
				.findViewById(R.id.btnZhuFangXingZhiQiTa);

		btnWuXinYongKa = (Button) view.findViewById(R.id.btnWuXinYongKa);
		btnYouQiTaXinYongKa = (Button) view
				.findViewById(R.id.btnYouQiTaXinYongKa);
		btnYouBenHangXinYongKa = (Button) view
				.findViewById(R.id.btnYouBenHangXinYongKa);

		btnBaiLingShangBanZu = (Button) view
				.findViewById(R.id.btnBaiLingShangBanZu);
		btnGongWuYuanShiYeDanWei = (Button) view
				.findViewById(R.id.btnGongWuYuanShiYeDanWei);
		btnFangDiChanJingJi = (Button) view
				.findViewById(R.id.btnFangDiChanJingJi);
		btnMeiRongMeiFa = (Button) view.findViewById(R.id.btnMeiRongMeiFa);
		btnXiaoXinCanYin = (Button) view.findViewById(R.id.btnXiaoXinCanYin);
		btnBaoAnBaoJie = (Button) view.findViewById(R.id.btnBaoAnBaoJie);
		btnGongZuoXinXiQiTa = (Button) view
				.findViewById(R.id.btnGongZuoXinXiQiTa);

		dialog = (ApplyCreditCardInfoDialog) getActivity();
	}

	public boolean isSelectedAllItem() {
		if (dialog.applyCreditCardBean.graduation == null) {
			wrongMsg = "请选择学历";
			return false;
		} else if (dialog.applyCreditCardBean.fund == null) {
			wrongMsg = "请选择住房性质";
			return false;
		} else if (dialog.applyCreditCardBean.cardgrade == null) {
			wrongMsg = "请选择信用卡信息";
			return false;
		} else if (dialog.applyCreditCardBean.occupation == null) {
			wrongMsg = "请选择工作信息";
			return false;
		}
		return true;
	}

	private void setListener() {
		btnBasicCustomerNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isSelectedAllItem()) {
					showWorkSituationFragment();
				} else {
					Toast.makeText(getActivity(), wrongMsg, Toast.LENGTH_LONG)
							.show();
				}
			}

		});
		// 学历
		btnZhuanKe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnZhuanKe();
			}
		});
		btnBenKe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnBenKe();
			}
		});
		btnXueLiQiTa.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnXueLiQiTa();
			}
		});
		// 住房性质

		btnZuFang.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnZuFang();
			}
		});
		btnBenDiGouFang.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnBenDiGouFang();
			}
		});
		btnJiTiSuShe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnJiTiSuShe();
			}
		});
		btnFuMu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnFuMu();
			}
		});
		btnQinQiJia.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnQinQiJia();
			}
		});
		btnZhuFangXingZhiQiTa.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnZhuFangXingZhiQiTa();
			}
		});
		// 是否拥有信用卡
		btnWuXinYongKa.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnWuXinYongKa();
			}
		});
		btnYouQiTaXinYongKa.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnYouQiTaXinYongKa();
			}
		});
		btnYouBenHangXinYongKa.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnYouBenHangXinYongKa();
			}
		});

		btnBaiLingShangBanZu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnBaiLingShangBanZu();
			}
		});
		btnGongWuYuanShiYeDanWei.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnGongWuYuanShiYeDanWei();
			}
		});
		btnFangDiChanJingJi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnFangDiChanJingJi();
			}
		});
		btnMeiRongMeiFa.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnMeiRongMeiFa();
			}
		});
		btnXiaoXinCanYin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnXiaoXinCanYin();
			}
		});
		btnBaoAnBaoJie.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnBaoAnBaoJie();
			}
		});
		btnGongZuoXinXiQiTa.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnGongZuoXinXiQiTa();
			}
		});
	}

	public void onBtnZhuanKe() {
		btnZhuanKe.setSelected(true);
		btnBenKe.setSelected(false);
		btnXueLiQiTa.setSelected(false);
		dialog.applyCreditCardBean.graduation = getButtonTextByButton(btnZhuanKe);
	}

	public void onBtnBenKe() {
		btnZhuanKe.setSelected(false);
		btnBenKe.setSelected(true);
		btnXueLiQiTa.setSelected(false);
		dialog.applyCreditCardBean.graduation = getButtonTextByButton(btnBenKe);
	}

	public void onBtnXueLiQiTa() {
		btnZhuanKe.setSelected(false);
		btnBenKe.setSelected(false);
		btnXueLiQiTa.setSelected(true);
		dialog.applyCreditCardBean.graduation = getButtonTextByButton(btnXueLiQiTa);
	}

	public void onBtnZuFang() {
		btnZuFang.setSelected(true);
		btnBenDiGouFang.setSelected(false);
		btnJiTiSuShe.setSelected(false);
		btnFuMu.setSelected(false);
		btnQinQiJia.setSelected(false);
		btnZhuFangXingZhiQiTa.setSelected(false);
		dialog.applyCreditCardBean.fund = getButtonTextByButton(btnZuFang);
	}

	public void onBtnBenDiGouFang() {
		btnZuFang.setSelected(false);
		btnBenDiGouFang.setSelected(true);
		btnJiTiSuShe.setSelected(false);
		btnFuMu.setSelected(false);
		btnQinQiJia.setSelected(false);
		btnZhuFangXingZhiQiTa.setSelected(false);
		dialog.applyCreditCardBean.fund = getButtonTextByButton(btnBenDiGouFang);
	}

	public void onBtnJiTiSuShe() {
		btnZuFang.setSelected(false);
		btnBenDiGouFang.setSelected(false);
		btnJiTiSuShe.setSelected(true);
		btnFuMu.setSelected(false);
		btnQinQiJia.setSelected(false);
		btnZhuFangXingZhiQiTa.setSelected(false);
		dialog.applyCreditCardBean.fund = getButtonTextByButton(btnJiTiSuShe);
	}

	public void onBtnFuMu() {
		btnZuFang.setSelected(false);
		btnBenDiGouFang.setSelected(false);
		btnJiTiSuShe.setSelected(false);
		btnFuMu.setSelected(true);
		btnQinQiJia.setSelected(false);
		btnZhuFangXingZhiQiTa.setSelected(false);
		dialog.applyCreditCardBean.fund = getButtonTextByButton(btnFuMu);
	}

	public void onBtnQinQiJia() {
		btnZuFang.setSelected(false);
		btnBenDiGouFang.setSelected(false);
		btnJiTiSuShe.setSelected(false);
		btnFuMu.setSelected(false);
		btnQinQiJia.setSelected(true);
		btnZhuFangXingZhiQiTa.setSelected(false);
		dialog.applyCreditCardBean.fund = getButtonTextByButton(btnQinQiJia);
	}

	public void onBtnZhuFangXingZhiQiTa() {
		btnZuFang.setSelected(false);
		btnBenDiGouFang.setSelected(false);
		btnJiTiSuShe.setSelected(false);
		btnFuMu.setSelected(false);
		btnQinQiJia.setSelected(false);
		btnZhuFangXingZhiQiTa.setSelected(true);
		dialog.applyCreditCardBean.fund = getButtonTextByButton(btnZhuFangXingZhiQiTa);
	}

	public void onBtnWuXinYongKa() {
		btnWuXinYongKa.setSelected(true);
		btnYouQiTaXinYongKa.setSelected(false);
		btnYouBenHangXinYongKa.setSelected(false);
		dialog.applyCreditCardBean.cardgrade = getButtonTextByButton(btnWuXinYongKa);
	}

	public void onBtnYouQiTaXinYongKa() {
		btnWuXinYongKa.setSelected(false);
		btnYouQiTaXinYongKa.setSelected(true);
		btnYouBenHangXinYongKa.setSelected(false);
		dialog.applyCreditCardBean.cardgrade = getButtonTextByButton(btnYouQiTaXinYongKa);
	}

	public void onBtnYouBenHangXinYongKa() {
		btnWuXinYongKa.setSelected(false);
		btnYouQiTaXinYongKa.setSelected(false);
		btnYouBenHangXinYongKa.setSelected(true);
		dialog.applyCreditCardBean.cardgrade = getButtonTextByButton(btnYouBenHangXinYongKa);
	}

	public void onBtnBaiLingShangBanZu() {
		btnBaiLingShangBanZu.setSelected(true);
		btnGongWuYuanShiYeDanWei.setSelected(false);
		btnFangDiChanJingJi.setSelected(false);
		btnMeiRongMeiFa.setSelected(false);
		btnXiaoXinCanYin.setSelected(false);
		btnBaoAnBaoJie.setSelected(false);
		btnGongZuoXinXiQiTa.setSelected(false);
		dialog.applyCreditCardBean.occupation = getButtonTextByButton(btnBaiLingShangBanZu);
	}

	public void onBtnGongWuYuanShiYeDanWei() {
		btnBaiLingShangBanZu.setSelected(false);
		btnGongWuYuanShiYeDanWei.setSelected(true);
		btnFangDiChanJingJi.setSelected(false);
		btnMeiRongMeiFa.setSelected(false);
		btnXiaoXinCanYin.setSelected(false);
		btnBaoAnBaoJie.setSelected(false);
		btnGongZuoXinXiQiTa.setSelected(false);
		dialog.applyCreditCardBean.occupation = getButtonTextByButton(btnGongWuYuanShiYeDanWei);
	}

	public void onBtnFangDiChanJingJi() {
		btnBaiLingShangBanZu.setSelected(false);
		btnGongWuYuanShiYeDanWei.setSelected(false);
		btnFangDiChanJingJi.setSelected(true);
		btnMeiRongMeiFa.setSelected(false);
		btnXiaoXinCanYin.setSelected(false);
		btnBaoAnBaoJie.setSelected(false);
		btnGongZuoXinXiQiTa.setSelected(false);
		dialog.applyCreditCardBean.occupation = getButtonTextByButton(btnFangDiChanJingJi);
	}

	public void onBtnMeiRongMeiFa() {
		btnBaiLingShangBanZu.setSelected(false);
		btnGongWuYuanShiYeDanWei.setSelected(false);
		btnFangDiChanJingJi.setSelected(false);
		btnMeiRongMeiFa.setSelected(true);
		btnXiaoXinCanYin.setSelected(false);
		btnBaoAnBaoJie.setSelected(false);
		btnGongZuoXinXiQiTa.setSelected(false);
		dialog.applyCreditCardBean.occupation = getButtonTextByButton(btnMeiRongMeiFa);
	}

	public void onBtnXiaoXinCanYin() {
		btnBaiLingShangBanZu.setSelected(false);
		btnGongWuYuanShiYeDanWei.setSelected(false);
		btnFangDiChanJingJi.setSelected(false);
		btnMeiRongMeiFa.setSelected(false);
		btnXiaoXinCanYin.setSelected(true);
		btnBaoAnBaoJie.setSelected(false);
		btnGongZuoXinXiQiTa.setSelected(false);
		dialog.applyCreditCardBean.occupation = getButtonTextByButton(btnXiaoXinCanYin);
	}

	public void onBtnBaoAnBaoJie() {
		btnBaiLingShangBanZu.setSelected(false);
		btnGongWuYuanShiYeDanWei.setSelected(false);
		btnFangDiChanJingJi.setSelected(false);
		btnMeiRongMeiFa.setSelected(false);
		btnXiaoXinCanYin.setSelected(false);
		btnBaoAnBaoJie.setSelected(true);
		btnGongZuoXinXiQiTa.setSelected(false);
		dialog.applyCreditCardBean.occupation = getButtonTextByButton(btnXiaoXinCanYin);
	}

	public void onBtnGongZuoXinXiQiTa() {
		btnBaiLingShangBanZu.setSelected(false);
		btnGongWuYuanShiYeDanWei.setSelected(false);
		btnFangDiChanJingJi.setSelected(false);
		btnMeiRongMeiFa.setSelected(false);
		btnXiaoXinCanYin.setSelected(false);
		btnBaoAnBaoJie.setSelected(false);
		btnGongZuoXinXiQiTa.setSelected(true);
		dialog.applyCreditCardBean.occupation = getButtonTextByButton(btnGongZuoXinXiQiTa);
	}

	private String getButtonTextByButton(Button button) {
		return button.getText().toString().trim();
	}

	private void showWorkSituationFragment() {
		if (workSituationFragment == null) {
			workSituationFragment = new WorkSituationFragment();
		}
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		// transaction.setCustomAnimations(R.anim.slide_in_from_left,
		// R.anim.slide_out_from_right);
		transaction.replace(R.id.mainContainer, workSituationFragment);
		transaction.commit();
	}

}
