package com.itertk.app.mpos.config;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.itertk.app.mpos.R;
import com.itertk.app.mpos.TextFocusChangeListener;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ShopFragment extends Fragment implements View.OnClickListener{
    EditText textShopName = null;
    EditText textShopAddr = null;
    TextView textMID = null;
    TextView textTID = null;
    TextView textCPU = null;

    Button btnSave = null;

//    ShopManager shopManager = null;
//    SystemManager systemManager = null;
//    BackupManager backupManager = null;

    private void onbtnSave(){
//        Shop shop = new Shop(textShopName.getText().toString(), textShopAddr.getText().toString());
//        shopManager.updateShop(shop);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave: onbtnSave();
        }
    }

    public ShopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        shopManager = new ShopManager(view.getContext());
//        systemManager = new SystemManager(view.getContext());
//        backupManager = new BackupManager(view.getContext());

        TextFocusChangeListener textFocusChangeListener = new TextFocusChangeListener();
        textShopName = (EditText)view.findViewById(R.id.textShopName);
        textShopName.setOnFocusChangeListener(textFocusChangeListener);

        textShopAddr = (EditText)view.findViewById(R.id.textShopAddr);
        textShopAddr.setOnFocusChangeListener(textFocusChangeListener);

        textMID = (TextView)view.findViewById(R.id.textMID);
        textTID = (TextView)view.findViewById(R.id.textTID);
        textCPU = (TextView)view.findViewById(R.id.textCPU);

        btnSave = (Button)view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);


//        Shop shop = shopManager.getShop();
//        if(shop != null){
//            textShopName.setText(shop.getName());
//            textShopAddr.setText(shop.getAddr());
//        }

    }
}
