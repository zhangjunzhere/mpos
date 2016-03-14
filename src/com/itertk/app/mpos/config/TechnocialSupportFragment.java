package com.itertk.app.mpos.config;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itertk.app.mpos.R;

/**
 * Created by larrylf_lin on 2015/3/7.
 * 技术支持页面
 */
public class TechnocialSupportFragment extends Fragment {


    public TechnocialSupportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_technical_support, container, false);
    }


}
