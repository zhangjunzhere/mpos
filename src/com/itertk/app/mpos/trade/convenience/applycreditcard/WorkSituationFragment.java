package com.itertk.app.mpos.trade.convenience.applycreditcard;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.itertk.app.mpos.R;

/**
 * 工作情况
 * 
 * @author zyf
 * 
 */
public class WorkSituationFragment extends Fragment {
	private Button btnWorkNext, btnGuoYou, btnJiGuanShiYe, btnWaiMaoDuZi,
			btnMinYin, btnHeZiHeZuo, btnGuFenZhi, btnGeTiSiYing,
			btnGongZuoDanWeiXingZhiQiTa, btnWu, btnLianXuJiaoNa3GeYue,
			btnLianXuJiaoNaBanNian, btnLianXuJiaoNaYiNian,
			btnLianXuJiaoNaYiNianYiShang, btnGongPai, btnYingYeZhiZhao,
			btnGongZuoZhengMing, btnMingPian, btnWuGongZuoZhengMing;
	ApplyCreditCardInfoDialog dialog;
	private String wrongMsg = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_work_situation, container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setupView(view);
		setListener();
	}

	public boolean isSelectedAllItem() {
		if (dialog.applyCreditCardBean.jobtype == null) {
			wrongMsg = "请选择工作单位性质";
			return false;
		} else if (dialog.applyCreditCardBean.socialensure == null) {
			wrongMsg = "请选择社保情况";
			return false;
		} else if (dialog.applyCreditCardBean.jobprove == null) {
			wrongMsg = "请可选择工作证明";
			return false;
		}
		return true;
	}

	private void setupView(View view) {
		btnWorkNext = (Button) view.findViewById(R.id.btnWorkNext);

		btnGuoYou = (Button) view.findViewById(R.id.btnGuoYou);
		btnJiGuanShiYe = (Button) view.findViewById(R.id.btnJiGuanShiYe);
		btnWaiMaoDuZi = (Button) view.findViewById(R.id.btnWaiMaoDuZi);
		btnMinYin = (Button) view.findViewById(R.id.btnMinYin);
		btnHeZiHeZuo = (Button) view.findViewById(R.id.btnHeZiHeZuo);
		btnGuFenZhi = (Button) view.findViewById(R.id.btnGuFenZhi);
		btnGeTiSiYing = (Button) view.findViewById(R.id.btnGeTiSiYing);
		btnGongZuoDanWeiXingZhiQiTa = (Button) view
				.findViewById(R.id.btnGongZuoDanWeiXingZhiQiTa);

		btnWu = (Button) view.findViewById(R.id.btnWu);
		btnLianXuJiaoNa3GeYue = (Button) view
				.findViewById(R.id.btnLianXuJiaoNa3GeYue);
		btnLianXuJiaoNaBanNian = (Button) view
				.findViewById(R.id.btnLianXuJiaoNaBanNian);
		btnLianXuJiaoNaYiNian = (Button) view
				.findViewById(R.id.btnLianXuJiaoNaYiNian);
		btnLianXuJiaoNaYiNianYiShang = (Button) view
				.findViewById(R.id.btnLianXuJiaoNaYiNianYiShang);

		btnGongPai = (Button) view.findViewById(R.id.btnGongPai);
		btnYingYeZhiZhao = (Button) view.findViewById(R.id.btnYingYeZhiZhao);
		btnGongZuoZhengMing = (Button) view
				.findViewById(R.id.btnGongZuoZhengMing);
		btnMingPian = (Button) view.findViewById(R.id.btnMingPian);
		btnWuGongZuoZhengMing = (Button) view
				.findViewById(R.id.btnWuGongZuoZhengMing);
		dialog = (ApplyCreditCardInfoDialog) getActivity();
	}

	private void setListener() {
		// 工作单位性质
		btnGuoYou.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnGuoYou();
			}
		});
		btnJiGuanShiYe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnJiGuanShiYe();
			}
		});
		btnWaiMaoDuZi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnWaiMaoDuZi();
			}
		});
		btnMinYin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnMinYin();
			}
		});
		btnHeZiHeZuo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnHeZiHeZuo();
			}
		});
		btnGuFenZhi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnGuFenZhi();
			}
		});
		btnGeTiSiYing.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnGeTiSiYing();
			}
		});
		btnGongZuoDanWeiXingZhiQiTa
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onBtnGongZuoDanWeiXingZhiQiTa();
					}
				});
		// 社保缴纳情况

		btnWu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnWu();
			}
		});
		btnLianXuJiaoNa3GeYue.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnLianXuJiaoNa3GeYue();
			}
		});
		btnLianXuJiaoNaBanNian.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnLianXuJiaoNaBanNian();
			}
		});
		btnLianXuJiaoNaYiNian.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnLianXuJiaoNaYiNian();
			}
		});
		btnLianXuJiaoNaYiNianYiShang
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onBtnLianXuJiaoNaYiNianYiShang();
					}
				});
		// 工作证明
		btnGongPai.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnGongPai();
			}
		});
		btnYingYeZhiZhao.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnYingYeZhiZhao();
			}
		});
		btnGongZuoZhengMing.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnGongZuoZhengMing();
			}
		});
		btnMingPian.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnMingPian();
			}
		});
		btnWuGongZuoZhengMing.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnWuGongZuoZhengMing();
			}
		});
		btnWorkNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isSelectedAllItem()) {
					Intent intent = new Intent();
					intent.putExtra(BankChoiceActivity.APPLYCREDITCARDBEAN,
							dialog.applyCreditCardBean);
					dialog.setResult(Activity.RESULT_OK, intent);
					dialog.finish();
				} else {
					Toast.makeText(getActivity(), wrongMsg, Toast.LENGTH_LONG)
							.show();
				}
			}
		});
	}

	public void onBtnGuoYou() {
		btnGuoYou.setSelected(true);
		btnJiGuanShiYe.setSelected(false);
		btnWaiMaoDuZi.setSelected(false);
		btnMinYin.setSelected(false);
		btnHeZiHeZuo.setSelected(false);
		btnGuFenZhi.setSelected(false);
		btnGeTiSiYing.setSelected(false);
		btnGongZuoDanWeiXingZhiQiTa.setSelected(false);
		dialog.applyCreditCardBean.jobtype = getButtonTextByButton(btnGuoYou);
	}

	public void onBtnJiGuanShiYe() {
		btnGuoYou.setSelected(false);
		btnJiGuanShiYe.setSelected(true);
		btnWaiMaoDuZi.setSelected(false);
		btnMinYin.setSelected(false);
		btnHeZiHeZuo.setSelected(false);
		btnGuFenZhi.setSelected(false);
		btnGeTiSiYing.setSelected(false);
		btnGongZuoDanWeiXingZhiQiTa.setSelected(false);
		dialog.applyCreditCardBean.jobtype = getButtonTextByButton(btnJiGuanShiYe);
	}

	public void onBtnWaiMaoDuZi() {
		btnGuoYou.setSelected(false);
		btnJiGuanShiYe.setSelected(false);
		btnWaiMaoDuZi.setSelected(true);
		btnMinYin.setSelected(false);
		btnHeZiHeZuo.setSelected(false);
		btnGuFenZhi.setSelected(false);
		btnGeTiSiYing.setSelected(false);
		btnGongZuoDanWeiXingZhiQiTa.setSelected(false);
		dialog.applyCreditCardBean.jobtype = getButtonTextByButton(btnWaiMaoDuZi);
	}

	public void onBtnMinYin() {
		btnGuoYou.setSelected(false);
		btnJiGuanShiYe.setSelected(false);
		btnWaiMaoDuZi.setSelected(false);
		btnMinYin.setSelected(true);
		btnHeZiHeZuo.setSelected(false);
		btnGuFenZhi.setSelected(false);
		btnGeTiSiYing.setSelected(false);
		btnGongZuoDanWeiXingZhiQiTa.setSelected(false);
		dialog.applyCreditCardBean.jobtype = getButtonTextByButton(btnMinYin);
	}

	public void onBtnHeZiHeZuo() {
		btnGuoYou.setSelected(false);
		btnJiGuanShiYe.setSelected(false);
		btnWaiMaoDuZi.setSelected(false);
		btnMinYin.setSelected(false);
		btnHeZiHeZuo.setSelected(true);
		btnGuFenZhi.setSelected(false);
		btnGeTiSiYing.setSelected(false);
		btnGongZuoDanWeiXingZhiQiTa.setSelected(false);
		dialog.applyCreditCardBean.jobtype = getButtonTextByButton(btnHeZiHeZuo);
	}

	public void onBtnGuFenZhi() {
		btnGuoYou.setSelected(false);
		btnJiGuanShiYe.setSelected(false);
		btnWaiMaoDuZi.setSelected(false);
		btnMinYin.setSelected(false);
		btnHeZiHeZuo.setSelected(false);
		btnGuFenZhi.setSelected(true);
		btnGeTiSiYing.setSelected(false);
		btnGongZuoDanWeiXingZhiQiTa.setSelected(false);
		dialog.applyCreditCardBean.jobtype = getButtonTextByButton(btnGuFenZhi);
	}

	public void onBtnGeTiSiYing() {
		btnGuoYou.setSelected(false);
		btnJiGuanShiYe.setSelected(false);
		btnWaiMaoDuZi.setSelected(false);
		btnMinYin.setSelected(false);
		btnHeZiHeZuo.setSelected(false);
		btnGuFenZhi.setSelected(false);
		btnGeTiSiYing.setSelected(true);
		btnGongZuoDanWeiXingZhiQiTa.setSelected(false);
		dialog.applyCreditCardBean.jobtype = getButtonTextByButton(btnGeTiSiYing);
	}

	public void onBtnGongZuoDanWeiXingZhiQiTa() {
		btnGuoYou.setSelected(false);
		btnJiGuanShiYe.setSelected(false);
		btnWaiMaoDuZi.setSelected(false);
		btnMinYin.setSelected(false);
		btnHeZiHeZuo.setSelected(false);
		btnGuFenZhi.setSelected(false);
		btnGeTiSiYing.setSelected(false);
		btnGongZuoDanWeiXingZhiQiTa.setSelected(true);
		dialog.applyCreditCardBean.jobtype = getButtonTextByButton(btnGongZuoDanWeiXingZhiQiTa);
	}

	public void onBtnWu() {
		btnWu.setSelected(true);
		btnLianXuJiaoNa3GeYue.setSelected(false);
		btnLianXuJiaoNaBanNian.setSelected(false);
		btnLianXuJiaoNaYiNian.setSelected(false);
		btnLianXuJiaoNaYiNianYiShang.setSelected(false);
		dialog.applyCreditCardBean.socialensure = getButtonTextByButton(btnWu);
	}

	public void onBtnLianXuJiaoNa3GeYue() {
		btnWu.setSelected(false);
		btnLianXuJiaoNa3GeYue.setSelected(true);
		btnLianXuJiaoNaBanNian.setSelected(false);
		btnLianXuJiaoNaYiNian.setSelected(false);
		btnLianXuJiaoNaYiNianYiShang.setSelected(false);
		dialog.applyCreditCardBean.socialensure = getButtonTextByButton(btnLianXuJiaoNa3GeYue);
	}

	public void onBtnLianXuJiaoNaBanNian() {
		btnWu.setSelected(false);
		btnLianXuJiaoNa3GeYue.setSelected(false);
		btnLianXuJiaoNaBanNian.setSelected(true);
		btnLianXuJiaoNaYiNian.setSelected(false);
		btnLianXuJiaoNaYiNianYiShang.setSelected(false);
		dialog.applyCreditCardBean.socialensure = getButtonTextByButton(btnLianXuJiaoNaBanNian);
	}

	public void onBtnLianXuJiaoNaYiNian() {
		btnWu.setSelected(false);
		btnLianXuJiaoNa3GeYue.setSelected(false);
		btnLianXuJiaoNaBanNian.setSelected(false);
		btnLianXuJiaoNaYiNian.setSelected(true);
		btnLianXuJiaoNaYiNianYiShang.setSelected(false);
		dialog.applyCreditCardBean.socialensure = getButtonTextByButton(btnLianXuJiaoNaYiNian);
	}

	public void onBtnLianXuJiaoNaYiNianYiShang() {
		btnWu.setSelected(false);
		btnLianXuJiaoNa3GeYue.setSelected(false);
		btnLianXuJiaoNaBanNian.setSelected(false);
		btnLianXuJiaoNaYiNian.setSelected(false);
		btnLianXuJiaoNaYiNianYiShang.setSelected(true);
		dialog.applyCreditCardBean.socialensure = getButtonTextByButton(btnLianXuJiaoNaYiNianYiShang);
	}

	public void onBtnGongPai() {
		btnGongPai.setSelected(true);
		btnYingYeZhiZhao.setSelected(false);
		btnGongZuoZhengMing.setSelected(false);
		btnMingPian.setSelected(false);
		btnWuGongZuoZhengMing.setSelected(false);
		dialog.applyCreditCardBean.jobprove = getButtonTextByButton(btnLianXuJiaoNaYiNianYiShang);
	}

	public void onBtnYingYeZhiZhao() {
		btnGongPai.setSelected(false);
		btnYingYeZhiZhao.setSelected(true);
		btnGongZuoZhengMing.setSelected(false);
		btnMingPian.setSelected(false);
		btnWuGongZuoZhengMing.setSelected(false);
		dialog.applyCreditCardBean.jobprove = getButtonTextByButton(btnYingYeZhiZhao);
	}

	public void onBtnGongZuoZhengMing() {
		btnGongPai.setSelected(false);
		btnYingYeZhiZhao.setSelected(false);
		btnGongZuoZhengMing.setSelected(true);
		btnMingPian.setSelected(false);
		btnWuGongZuoZhengMing.setSelected(false);
		dialog.applyCreditCardBean.jobprove = getButtonTextByButton(btnGongZuoZhengMing);
	}

	public void onBtnMingPian() {
		btnGongPai.setSelected(false);
		btnYingYeZhiZhao.setSelected(false);
		btnGongZuoZhengMing.setSelected(false);
		btnMingPian.setSelected(true);
		btnWuGongZuoZhengMing.setSelected(false);
		dialog.applyCreditCardBean.jobprove = getButtonTextByButton(btnMingPian);
	}

	public void onBtnWuGongZuoZhengMing() {
		btnGongPai.setSelected(false);
		btnYingYeZhiZhao.setSelected(false);
		btnGongZuoZhengMing.setSelected(false);
		btnMingPian.setSelected(false);
		btnWuGongZuoZhengMing.setSelected(true);
		dialog.applyCreditCardBean.jobprove = getButtonTextByButton(btnWuGongZuoZhengMing);
	}

	private String getButtonTextByButton(Button button) {
		return button.getText().toString().trim();
	}
}
