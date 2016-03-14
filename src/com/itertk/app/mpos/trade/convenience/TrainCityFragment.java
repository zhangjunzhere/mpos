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
import com.itertk.app.mpos.locationhelper.TrainCity;
import com.itertk.app.mpos.locationhelper.TrainCityDao;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrainCityFragment extends Fragment {
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

    public TrainCityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_train_city, container, false);
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

            }
        });


        cityAdapter = new CityAdapter();
        listCity = (ListView) view.findViewById(R.id.listCity);
        listCity.setAdapter(cityAdapter);

        listCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TrainCity city = cityAdapter.getItem(position);
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
        List<TrainCity> trainCityList;
        TrainCityDao trainCityManager;


        CityAdapter() {
            trainCityManager = ((MPosApplication) getActivity().getApplication()).getLocationHelper().getTrainCityManager();
            trainCityList = trainCityManager.loadAll();
        }

        public void refreshListOnSearch(String strPY) {
            if (strPY.isEmpty())
                trainCityList = trainCityManager.loadAll();
            else
                trainCityList = trainCityManager.queryBuilder().where(TrainCityDao.Properties.Py.like(strPY)).list();
            notifyDataSetChanged();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return trainCityList.size();
        }

        @Override
        public TrainCity getItem(int position) {
            return trainCityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            TrainCity city = getItem(position);

            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.bank_list_item, null);
            }

            TextView textView = (TextView) view.findViewById(R.id.textBank);
            textView.setText(city.getName());

            return view;
        }
    }
}
