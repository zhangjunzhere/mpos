package com.itertk.app.mpos.config;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itertk.app.mpos.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ModBankcardFragment extends Fragment {


    public ModBankcardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mod_bankcard, container, false);
    }


}
