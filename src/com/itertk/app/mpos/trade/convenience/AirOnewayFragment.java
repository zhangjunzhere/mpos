package com.itertk.app.mpos.trade.convenience;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.BlankFragment;
import com.itertk.app.mpos.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AirOnewayFragment extends Fragment {
    DepartDateFragment departDateFragment;
    AirCityFragment airCityFragment;
    FragmentTransaction transaction;

    LinearLayout btnFromCity;
    LinearLayout btnToCity;
    LinearLayout btnDepartDate;
    Button btnSearch;

    TextView textDepartDate;

    TextView textFromCity;
    TextView textToCity;

    private void clearItemArea() {
        BlankFragment blankFragment = new BlankFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.itemArea, blankFragment);
        transaction.commit();
    }

    private void showAirFromCityFragment() {
        airCityFragment = new AirCityFragment();
        airCityFragment.setTitle("出发城市");
        airCityFragment.setTextDisplay(textFromCity);
        transaction = getFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_from_right);
        transaction.replace(R.id.itemArea, airCityFragment);
        transaction.commit();
    }

    private void showAirToCityFragment() {
        airCityFragment = new AirCityFragment();
        airCityFragment.setTitle("到达城市");
        airCityFragment.setTextDisplay(textToCity);
        transaction = getFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_from_right);
        transaction.replace(R.id.itemArea, airCityFragment);
        transaction.commit();
    }

    private void showDepartDateFragment() {
        departDateFragment = new DepartDateFragment();
        departDateFragment.setTextView(textDepartDate);
        departDateFragment.setTitle("出发日期");
        transaction = getFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_from_right);
        transaction.replace(R.id.itemArea, departDateFragment);
        transaction.commit();

    }


    public AirOnewayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_air_oneway, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textFromCity = (TextView) view.findViewById(R.id.textFromCity);
        textToCity = (TextView) view.findViewById(R.id.textToCity);

        textDepartDate = (TextView) view.findViewById(R.id.textDepartDate);

        btnFromCity = (LinearLayout) view.findViewById(R.id.btnFromCity);
        btnFromCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAirFromCityFragment();
            }
        });

        btnToCity = (LinearLayout) view.findViewById(R.id.btnToCity);
        btnToCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAirToCityFragment();
            }
        });

        btnDepartDate = (LinearLayout) view.findViewById(R.id.btnDepartDate);
        btnDepartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDepartDateFragment();
            }
        });

        btnSearch = (Button) view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textFromCity.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "请选择出发城市", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (textToCity.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "请选择到达城市", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (textDepartDate.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "请选择出发日期", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Intent it = new Intent(getActivity(), AirTicketOrderActivity.class);
                Intent it = new Intent(getActivity(), AirLineActivity.class);
                it.putExtra("fromcity", textFromCity.getText().toString());
                it.putExtra("tocity", textToCity.getText().toString());
                it.putExtra("from", textFromCity.getTag().toString());
                it.putExtra("to", textToCity.getTag().toString());
                it.putExtra("date", textDepartDate.getText().toString());
                startActivity(it);

            }
        });


        clearItemArea();
    }
}
