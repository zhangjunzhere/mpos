package com.itertk.app.mpos.trade.convenience.applycreditcard;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itertk.app.mpos.R;
import com.itertk.app.mpos.card51dbhelper.CityArea;

public class SelectCityAreaAdapter extends BaseAdapter {
	private Context context;
	private List<CityArea> listCityArea;

	public SelectCityAreaAdapter(Context context,
			ArrayList<CityArea> listCityArea) {
		this.context = context;
		this.listCityArea = listCityArea;
	}

	@Override
	public int getCount() {
		return listCityArea.size();
	}

	@Override
	public Object getItem(int position) {
		return listCityArea.get(position);
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
					R.layout.list_item_cityarea, null);
			viewHolder = new ViewHolder();
			viewHolder.textViewCityAreaName = (TextView) view
					.findViewById(R.id.textViewCityAreaName);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		CityArea cityArea = listCityArea.get(position);
		viewHolder.textViewCityAreaName.setText(cityArea.getName());
		return view;
	}

	static class ViewHolder {
		TextView textViewCityAreaName;
	}

}