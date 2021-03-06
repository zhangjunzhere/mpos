package com.itertk.app.mpos.config;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itertk.app.mpos.R;
import com.itertk.app.mpos.TextFocusChangeListener;
import com.itertk.app.mpos.dbhelper.Reduce;

/**
 * A simple {@link Fragment} subclass.
 *修改优惠
 */
public class ModReduceFragment extends Fragment implements View.OnClickListener{
    ReduceListFragment reduceListFragment;
    Reduce reduce;

    Button btnSave;
    Button btnYuan;
    Button btnPercent;

    EditText textReduceName;
    EditText textReduceValue;
    int reduceType = 0;

    private void onbtnSave(){
        if (textReduceName.getText().length() == 0){
            Toast.makeText(getActivity().getApplicationContext(), "优惠名称为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (textReduceValue.getText().length() == 0){
            Toast.makeText(getActivity().getApplicationContext(), "优惠幅度为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        reduce.setName(textReduceName.getText().toString());
        reduce.setType(reduceType);
        reduce.setValue(textReduceValue.getText().toString().trim());
        reduceListFragment.modReduce(reduce);
    }

    private void onbtnYuan(){
        btnYuan.setSelected(true);
        btnPercent.setSelected(false);
        reduceType = 0;
    }

    private void onbtnPercent(){
        btnYuan.setSelected(false);
        btnPercent.setSelected(true);
        reduceType = 1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave: onbtnSave(); break;
            case R.id.btnYuan: onbtnYuan(); break;
            case R.id.btnPercent: onbtnPercent(); break;
        }
    }

    public ModReduceFragment(){

    }

    public void setParames(ReduceListFragment reduceListFragment, Reduce reduce) {
        // Required empty public constructor
        this.reduceListFragment = reduceListFragment;
        this.reduce = reduce;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mod_reduce, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnSave = (Button)view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        btnYuan = (Button)view.findViewById(R.id.btnYuan);
        btnYuan.setOnClickListener(this);

        btnPercent = (Button)view.findViewById(R.id.btnPercent);
        btnPercent.setOnClickListener(this);

        TextFocusChangeListener textFocusChangeListener = new TextFocusChangeListener();
        textReduceName = (EditText)view.findViewById(R.id.textReduceName);
        textReduceName.setOnFocusChangeListener(textFocusChangeListener);

        textReduceValue = (EditText)view.findViewById(R.id.textReduceValue);
        textReduceValue.setOnFocusChangeListener(textFocusChangeListener);

        textReduceName.setText(reduce.getName());
        textReduceValue.setText(String.valueOf(reduce.getValue()));

        if(reduce.getType() == 0) onbtnYuan();
        else onbtnPercent();
    }
}
