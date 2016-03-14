package com.itertk.app.mpos.trade.convenience.applycreditcard;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itertk.app.mpos.R;
import com.itertk.app.mpos.card51dbhelper.CityBank;
import com.itertk.app.mpos.utility.Utils;

public class ChoiceBankAdapter extends BaseAdapter {
	private Context context;
	private List<CityBank> listCityBank;
	private HashMap<String, Integer> bankiconMaps;

	public ChoiceBankAdapter(Context context, List<CityBank> listCityBank) {
		this.context = context;
		this.listCityBank = listCityBank;
		bankiconMaps = Utils.getBankIconMaps();
	}

	@Override
	public int getCount() {
		return listCityBank.size();
	}

	@Override
	public Object getItem(int position) {
		return listCityBank.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder viewHolder;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.item_gridview_bank, null);
			viewHolder = new ViewHolder();
			viewHolder.imageBank = (ImageView) view
					.findViewById(R.id.imageBank);
			viewHolder.textviewAppointmentNumberOfPeople = (TextView) view
					.findViewById(R.id.textviewAppointmentNumberOfPeople);
			viewHolder.textviewBankName = (TextView) view
					.findViewById(R.id.textviewBankName);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		CityBank bank = listCityBank.get(position);
		Integer imageId = bankiconMaps.get(bank.bankname);
		if (imageId != null) {
			viewHolder.imageBank.setImageResource(imageId);
		} else {
			viewHolder.imageBank.setImageResource(R.drawable.bankicon_baoshang);
		}
		viewHolder.textviewBankName.setText(bank.bankname);
		viewHolder.textviewAppointmentNumberOfPeople.setText(bank.applypeople
				+ "人预约");

		return view;
	}

	static class ViewHolder {
		ImageView imageBank;
		TextView textviewBankName;
		TextView textviewAppointmentNumberOfPeople;
	}

}
