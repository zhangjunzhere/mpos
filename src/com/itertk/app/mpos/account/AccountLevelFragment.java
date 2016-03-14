package com.itertk.app.mpos.account;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itertk.app.mpos.R;

/**
 * A simple {@link Fragment} subclass.
 * 账户等级fragment
 *
 */
public class AccountLevelFragment extends Fragment {


    public AccountLevelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_level, container, false);
    }


}
