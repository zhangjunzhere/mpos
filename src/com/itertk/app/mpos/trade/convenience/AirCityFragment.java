package com.itertk.app.mpos.trade.convenience;


import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.locationhelper.AirCity;
import com.itertk.app.mpos.locationhelper.AirCityDao;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AirCityFragment extends Fragment {
    SideBar sideBar;
    TextView textLetter;
    ListView listCity;
    EditText textPY;


    CityAdapter cityAdapter;

    TextView textDisplay;
    String title;
    TextView textTitle;


    public void setTextDisplay(TextView textDisplay) {
        this.textDisplay = textDisplay;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public AirCityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_air_city, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textTitle = (TextView) view.findViewById(R.id.textTitle);
        textTitle.setText(title);

        textLetter = (TextView) view.findViewById(R.id.textLetter);

        sideBar = (SideBar) view.findViewById(R.id.sidebar);
        sideBar.setTextView(textLetter);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                listCity.setSelection(cityAdapter.getPosition(s));
            }
        });


        cityAdapter = new CityAdapter();
        listCity = (ListView) view.findViewById(R.id.listCity);
        listCity.setAdapter(cityAdapter);

        listCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AirCity city = cityAdapter.getItem(position);
                textDisplay.setText(city.getName());
                textDisplay.setTag(city.getCode());
            }
        });


        textPY = (EditText) view.findViewById(R.id.textPY);
        textPY.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cityAdapter.refreshListOnSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    class CityAdapter extends BaseAdapter {
        List<AirCity> airCityList;
        AirCityDao airCityManager;


        CityAdapter() {
            airCityManager = ((MPosApplication) getActivity().getApplication()).getLocationHelper().getAirCityManager();
            airCityManager.queryBuilder().LOG_SQL = true;
            airCityList = airCityManager.queryBuilder().orderAsc(AirCityDao.Properties.Pyzm).list();
        }

        public void refreshListOnSearch(String strPY) {
            if (strPY.isEmpty())
                airCityList = airCityManager.queryBuilder().orderAsc(AirCityDao.Properties.Pyzm).list();
            else
                airCityList = airCityManager.queryBuilder().where(AirCityDao.Properties.Pyzm.like(strPY + "%")).orderAsc(AirCityDao.Properties.Pyzm).list();
            notifyDataSetChanged();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return airCityList.size();
        }

        @Override
        public AirCity getItem(int position) {
            return airCityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public int getPosition(String s) {
            for (int i = 0; i < airCityList.size(); i++) {
                if (airCityList.get(i).getPy().compareTo(s) == 0) {
                    return i;
                }
            }

            return -1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            AirCity airCity = getItem(position);

            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.bank_list_item, null);
            }

            TextView textView = (TextView) view.findViewById(R.id.textBank);
            textView.setText(airCity.getName());

            TextView textCharacter = (TextView) view.findViewById(R.id.textCharacter);
            if (position == 0) {
                textCharacter.setVisibility(View.VISIBLE);
                textCharacter.setText(airCity.getPy().toUpperCase());
            } else {
                AirCity preAirCity = getItem(position - 1);
                if (preAirCity.getPy().compareTo(airCity.getPy()) == 0) {
                    textCharacter.setVisibility(View.GONE);
                } else {
                    textCharacter.setVisibility(View.VISIBLE);
                    textCharacter.setText(airCity.getPy().toUpperCase());
                }
            }

            return view;
        }
    }
}
