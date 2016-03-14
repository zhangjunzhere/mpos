package com.itertk.app.mpos.config;




import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.itertk.app.mpos.R;

/**
 * A simple {@link Fragment} subclass.
 *用户中心
 */
public class UserCenterFragment extends Fragment implements View.OnClickListener{
    static final String TAG = "UserCenterFragment";
    RelativeLayout btnSecurity;
    RelativeLayout btnBankcard;
    Button btnLogout;

    private void onbtnLogout(){
        Log.d(TAG, "user logout");
    }

    private void onbtnSecurity(){

    }

    private void onbtnBankcard(){

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogout: onbtnLogout(); break;
            case R.id.btnSecurity: onbtnSecurity(); break;
            case R.id.btnBankcard: onbtnBankcard(); break;
        }
    }

    public UserCenterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_center, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnLogout = (Button)view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        btnSecurity = (RelativeLayout)view.findViewById(R.id.btnSecurity);
        btnSecurity.setOnClickListener(this);

        btnBankcard = (RelativeLayout)view.findViewById(R.id.btnBankcard);
        btnBankcard.setOnClickListener(this);
    }
}
