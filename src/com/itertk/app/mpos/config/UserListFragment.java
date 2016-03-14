package com.itertk.app.mpos.config;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.itertk.app.mpos.BlankFragment;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.utility.DataHelper;
import com.itertk.app.mpos.utility.LinkeaRequest;
import com.itertk.app.mpos.utility.Utils;
import com.itertk.app.mpos.dbhelper.User;
import com.itertk.app.mpos.dbhelper.UserDao;

import java.util.List;

/*
* 用户列表
* */
public class UserListFragment extends Fragment implements View.OnClickListener{
    static final String TAG="UserListFragment";
    ListView listData = null;
    Button btnAdd = null;

    AddUserFragment addUserFragment = null;
    ModUserFragment modUserFragment = null;
    FragmentTransaction transaction;

    UserAdapter userAdapter = null;

    public void clearItemArea(){
        BlankFragment blankFragment = new BlankFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.itemArea, blankFragment);
        transaction.commit();
    }

    public void addUser(User user){
//        userAdapter.addUser(user);
        LinkeaRequest.addUserRequest(getActivity(),user,userAdapter);
    }

    public void modUser(User user){
        //userAdapter.modUser(user);
        LinkeaRequest.modifyUserRequest(getActivity(), user, userAdapter);
    }

    public void onbtnDelUser(User user){
        //userAdapter.delUser(user);
        LinkeaRequest.deleteUserRequest(getActivity(), user, userAdapter);
    }

    public void onbtnModUser(User user){
        modUserFragment = new ModUserFragment();
        modUserFragment.setParames(this, user);
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.itemArea, modUserFragment);
        transaction.commit();
    }

    private void onbtnAddUser(){
        addUserFragment = new AddUserFragment();
        addUserFragment.setUserListFragment(this);
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.itemArea, addUserFragment);
        transaction.commit();
        userAdapter.preSelect=-1;
        userAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAdd: onbtnAddUser();
        }
    }

    public UserListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listData = (ListView)view.findViewById(R.id.listData);

        btnAdd = (Button)view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        userAdapter = new UserAdapter(this);
        listData.setAdapter(userAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public  class UserAdapter extends BaseAdapter {
        UserListFragment userListFragment;
        final  ListView listView;
        UserDao userManager;
        List<User> userList;
        int preSelect = -1;

        public long addUser(User user){
            long result = 0;
            try {
                result  = userManager.insert(user);
                notifyDataSetChanged();
                clearItemArea();
            }catch (Exception e){
                Log.e(TAG, "insert " + user.toString() + " error:" + e.toString());
                result = -1;
            }

            return result;
        }

        public long delUser(User user){
            long result = 0;
            try {
                userManager.delete(user);
                notifyDataSetChanged();
            }catch (Exception e){
                Log.e(TAG, "delete " + user.toString() + " error:" + e.toString());
                result = -1;
            }

            return result;
        }

        public long modUser(User user){
            long result = 0;

            try {
                userManager.update(user);
                clearItemArea();
                notifyDataSetChanged();
            }catch (Exception e){
                Log.e(TAG, "modify " + user.toString() + " error:" + e.toString());
                result = -1;
            }

            return -1;
        }

        UserAdapter(UserListFragment userListFragment){
            this.userListFragment = userListFragment;
            this.listView = userListFragment.listData;
            this.userManager = ((MPosApplication)getActivity().getApplication()).getDataHelper().getUserManager();
            userList = userManager.queryBuilder().where(UserDao.Properties.Type.notEq(DataHelper.USER_TPYE_MEMBER)).list();
        }


        @Override
        public void notifyDataSetChanged() {
            userList = userManager.queryBuilder().where(UserDao.Properties.Type.notEq(DataHelper.USER_TPYE_MEMBER)).list();
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return userList.size();
        }

        @Override
        public Object getItem(int position) {
            return userList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final UserAdapter adapter = this;
            final int pos = position;
            final User user = userList.get(position);

            View view = convertView;
            if(view == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.user_list_item, null);
            }

            TextView textView = (TextView)view.findViewById(R.id.ItemText);
            textView.setText(user.getClerkName());

            ImageButton btnModify = (ImageButton) view.findViewById(R.id.btnModify);
            btnModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    User user = userList.get(pos);

                    userListFragment.onbtnModUser(user);
                    Log.v("modify click", "position=" + pos);

                    ((ImageButton)v).setImageResource(R.drawable.btn_modify_2);

                    if(preSelect < 0 || preSelect == pos){}
                    else{
                        View preView = listView.getChildAt(preSelect);
                        if(preView != null)
                            ((ImageButton)preView.findViewById(R.id.btnModify)).setImageResource(R.drawable.btn_modify);
                    }
                    preSelect = pos;
                    notifyDataSetChanged();
                }
            });
            if(preSelect == pos) {
                btnModify.setImageResource(R.drawable.btn_modify_2);
            }
            else {
                btnModify.setImageResource(R.drawable.btn_modify);
            }


            ImageButton btnDelete = (ImageButton) view.findViewById(R.id.btnDelete);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = userList.get(pos);
                    userListFragment.onbtnDelUser(user);
                    preSelect=-1;
                    Log.v("btnDelete", "position="+pos);
                }
            });
            btnDelete.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        ((ImageButton)v).setImageResource(R.drawable.btn_delete_2);
                    else
                        ((ImageButton)v).setImageResource(R.drawable.btn_delete);
                    return false;
                }
            });


            return view;
        }

    }
}
