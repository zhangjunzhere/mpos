package com.itertk.app.mpos.config;



import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.itertk.app.mpos.BlankFragment;
import com.itertk.app.mpos.R;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class BankcardListFragment extends Fragment implements View.OnClickListener{
    ListView listData = null;
    Button btnAdd = null;
    FragmentTransaction transaction;

    AddBankcardFragment addBankcardFragment = null;
    ModBankcardFragment modBankcardFragment = null;

    public void clearItemArea(){
        BlankFragment blankFragment = new BlankFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.itemArea, blankFragment);
        transaction.commit();
    }

    private void onbtnAddBankcard(){
        if (null == addBankcardFragment)
        {
            addBankcardFragment = new AddBankcardFragment();
            addBankcardFragment.setBankcardListFragment(this);
        }
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.itemArea, addBankcardFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAdd: onbtnAddBankcard();
        }
    }

    public BankcardListFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bankcard_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listData = (ListView)view.findViewById(R.id.listData);

        btnAdd = (Button)view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);


    }

}
