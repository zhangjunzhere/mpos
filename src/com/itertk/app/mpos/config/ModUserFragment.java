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
import com.itertk.app.mpos.dbhelper.User;
import com.itertk.app.mpos.utility.AESEncryptor;
import com.itertk.app.mpos.utility.DataHelper;
import com.itertk.app.mpos.utility.LinkeaRequest;

/*
*修改用户
* */

public class ModUserFragment extends Fragment implements View.OnClickListener{
    UserListFragment userListFragment =null;
    //TextView textUserName = null;
    EditText textOriginalPassword = null;
    EditText textPassword = null;
    EditText textPasswordConfirm = null;
    EditText textFullname = null;
    EditText textUserPhone=null;

    Button btnSave = null;
    Button btnEmployer = null;
    Button btnEmployee = null;

    User user = null;

    long employType = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave: onbtnSave(); break;
            case R.id.btnEmployer: onbtnEmployer(); break;
            case R.id.btnEmployee: onbtnEmployee(); break;
        }
    }

    private void onbtnSave(){
        if(textUserPhone.getText().toString().trim().length() ==0){
            Toast.makeText(getActivity().getApplicationContext(), "手机号为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(textUserPhone.getText().toString().trim().length() != 11){
            Toast.makeText(getActivity().getApplicationContext(), "手机号应为11位",
                    Toast.LENGTH_SHORT).show();
            return;
        }
/*        if(textOriginalPassword.getText().toString().trim().length() ==0){
            Toast.makeText(getActivity().getApplicationContext(), "原始密码为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!AESEncryptor.getInstance(getActivity()).encryption(textOriginalPassword.getText().toString().trim()).equals(user.getLoginPwd())){
            Toast.makeText(getActivity().getApplicationContext(), "原始密码输入错误",
                    Toast.LENGTH_SHORT).show();
            return;
        }*/
        if(textPassword.getText().toString().trim().length() == 0){
            Toast.makeText(getActivity().getApplicationContext(), "密码为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(textPasswordConfirm.getText().toString().trim().length() ==0){
            Toast.makeText(getActivity().getApplicationContext(), "确认密码为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (textFullname.getText().toString().trim().length() == 0){
            Toast.makeText(getActivity().getApplicationContext(), "姓名为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(!textPassword.getText().toString().trim().equals(textPasswordConfirm.getText().toString())){
            Toast.makeText(getActivity().getApplicationContext(), "两次输入密码不一样",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(textPassword.getText().toString().trim().length() < 6){
            Toast.makeText(getActivity().getApplicationContext(), "密码长度最少为6位",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(textPassword.getText().toString().trim().length()> 16){
            Toast.makeText(getActivity().getApplicationContext(), "密码长度最大不能超过16位",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        user.setClerkName(textFullname.getText().toString().trim());
        if(!textPassword.getText().toString().trim().isEmpty()) {
            user.setLoginPwd(textPassword.getText().toString().trim());
        }
        user.setType(employType);
        userListFragment.modUser(user);
    }

    private void onbtnEmployer(){
        btnEmployer.setSelected(true);
        btnEmployee.setSelected(false);
        employType = DataHelper.USER_TPYE_ADMIN;
    }

    private void onbtnEmployee(){
        btnEmployee.setSelected(true);
        btnEmployer.setSelected(false);
        employType = DataHelper.USER_TPYE__CLERK;
    }

    public ModUserFragment(){

    }

    public void setParames(UserListFragment userListFragment, User user) {
        // Required empty public constructor
        this.userListFragment = userListFragment;
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mod_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //textUserName = (TextView)view.findViewById(R.id.textUserName);
        textUserPhone =(EditText)view.findViewById(R.id.textUserPhone);

        TextFocusChangeListener textFocusChangeListener = new TextFocusChangeListener();
        textOriginalPassword = (EditText)view.findViewById(R.id.textOriginalPassword);
        textOriginalPassword.setOnFocusChangeListener(textFocusChangeListener);

        textPassword = (EditText)view.findViewById(R.id.textPassword);
        textPassword.setOnFocusChangeListener(textFocusChangeListener);

        textPasswordConfirm = (EditText)view.findViewById(R.id.textPasswordConfirm);
        textPasswordConfirm.setOnFocusChangeListener(textFocusChangeListener);

        textFullname = (EditText)view.findViewById(R.id.textFullname);
        textFullname.setOnFocusChangeListener(textFocusChangeListener);

        btnSave = (Button)view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        btnEmployer = (Button)view.findViewById(R.id.btnEmployer);
        btnEmployer.setOnClickListener(this);

        btnEmployee = (Button)view.findViewById(R.id.btnEmployee);
        btnEmployee.setOnClickListener(this);

        //textUserName.setText("用户名：" + user.getClerkName());
        textFullname.setText(user.getClerkName());
        textUserPhone.setText(user.getPhone());
        if(user.getType() == DataHelper.USER_TPYE_ADMIN){
            onbtnEmployer();
        }  else{
            onbtnEmployee();
        }
    }
}
