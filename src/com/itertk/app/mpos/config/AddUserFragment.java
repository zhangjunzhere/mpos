package com.itertk.app.mpos.config;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itertk.app.mpos.R;
import com.itertk.app.mpos.TextFocusChangeListener;
import com.itertk.app.mpos.dbhelper.User;
import com.itertk.app.mpos.utility.AESEncryptor;
import com.itertk.app.mpos.utility.DataHelper;
import com.itertk.app.mpos.utility.Utils;

/**
 * A simple {@link Fragment} subclass.
 * 添加用户 *
 */
public class AddUserFragment extends Fragment implements View.OnClickListener{
    UserListFragment userListFragment =null;
    EditText textPhone = null;
    EditText textPassword = null;
    EditText textPasswordConfirm = null;
    EditText textFullname = null;

    Button btnSave = null;
    Button btnEmployer = null;
    Button btnEmployee = null;

    long employType = DataHelper.USER_TPYE__CLERK;

    private void onbtnSave(){
        if(textPhone.getText().toString().trim().length() == 0){
            Toast.makeText(getActivity().getApplicationContext(), "手机号为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(textPhone.getText().toString().trim().length() != 11){
            Toast.makeText(getActivity().getApplicationContext(), "手机号应为11位",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Utils.phoneRegex(textPhone.getText().toString().trim())){
            Toast.makeText(getActivity().getApplicationContext(), "手机号格式不正确，请检查后重新输入",
                    Toast.LENGTH_SHORT).show();
            return;
        }
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

        if(!textPassword.getText().toString().trim().equals(textPasswordConfirm.getText().toString().trim())){
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

        User user = new User(null, textFullname.getText().toString().trim(), textPassword.getText().toString().trim(),
                textPhone.getText().toString().trim(), employType,null,false,null,null);
        userListFragment.addUser(user);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave: onbtnSave(); break;
            case R.id.btnEmployer: onbtnEmployer(); break;
            case R.id.btnEmployee: onbtnEmployee(); break;
        }
    }

    public AddUserFragment() {

        // Required empty public constructor
    }

    public void setUserListFragment(UserListFragment userListFragment){
        this.userListFragment = userListFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextFocusChangeListener textFocusChangeListener = new TextFocusChangeListener();
        textPhone = (EditText)view.findViewById(R.id.textPhone);
        textPhone.setOnFocusChangeListener(textFocusChangeListener);
        textPhone.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

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

        onbtnEmployee();
    }
}
