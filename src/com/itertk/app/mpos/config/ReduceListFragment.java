package com.itertk.app.mpos.config;



import android.app.FragmentTransaction;
import android.database.Cursor;
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

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.dbhelper.Reduce;
import com.itertk.app.mpos.dbhelper.ReduceDao;
import com.itertk.app.mpos.dbhelper.SaleOrderItemReduceDao;
import com.itertk.app.mpos.dbhelper.SaleOrderReduceDao;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *优惠列表
 */
public class ReduceListFragment extends Fragment implements View.OnClickListener{
    static final String TAG = "ReduceListFragment";
    ListView listData = null;
    Button btnAdd = null;

    AddReduceFragment addReduceFragment;
    ModReduceFragment modReduceFragment;
    FragmentTransaction transaction;

    ReduceAdapter reduceAdapter;

    public void addReduce(Reduce reduce){
        reduceAdapter.addReduce(reduce);
    }

    public void modReduce(Reduce reduce){
        reduceAdapter.modReduce(reduce);
    }

    private void onbtnAddReduce(){
        addReduceFragment = new AddReduceFragment();
        addReduceFragment.setReduceListFragment(this);
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.itemArea, addReduceFragment);
        transaction.commit();
    }

    private void onbtnModReduce(Reduce reduce){
        modReduceFragment = new ModReduceFragment( );
        modReduceFragment.setParames(this, reduce);
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.itemArea, modReduceFragment);
        transaction.commit();
    }

    private void onbtnDelReduce(Reduce reduce){
        reduceAdapter.delReduce(reduce);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAdd: onbtnAddReduce(); break;
        }
    }

    public ReduceListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reduce_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listData = (ListView)view.findViewById(R.id.listData);

        btnAdd = (Button)view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        reduceAdapter = new ReduceAdapter();
        listData.setAdapter(reduceAdapter);
    }

    private class ReduceAdapter extends BaseAdapter {
        final ListView listView;
        ReduceDao reduceManager;
        SaleOrderReduceDao saleOrderReduceManager;
        SaleOrderItemReduceDao saleOrderItemReduceManager;
        List<Reduce> reduceList;
        int preSelect = -1;


        public long addReduce(Reduce reduce){
            long result = 0;
            try {
                result  = reduceManager.insert(reduce);
                notifyDataSetChanged();
            }catch (Exception e){
                Log.e(TAG, "insert " + reduce.toString() + " error:" + e.toString());
                result = -1;
            }

            return result;
        }

        public long modReduce(Reduce reduce){
            long result = 0;
            try {
                reduceManager.update(reduce);
                notifyDataSetChanged();
            }catch (Exception e){
                Log.e(TAG, "modify " + reduce.toString() + " error:" + e.toString());
                result = -1;
            }

            return result;
        }

        public long delReduce(Reduce reduce){
            long result = 0;
            try {
                saleOrderReduceManager.queryBuilder()
                        .where(SaleOrderReduceDao.Properties.ReduceId.eq(reduce.getReduceId())).buildDelete().executeDeleteWithoutDetachingEntities();
                saleOrderItemReduceManager.queryBuilder()
                        .where(SaleOrderItemReduceDao.Properties.ReduceId.eq(reduce.getReduceId())).buildDelete().executeDeleteWithoutDetachingEntities();
                reduceManager.delete(reduce);
                notifyDataSetChanged();
            }catch (Exception e){
                Log.e(TAG, "delete " + reduce.toString() + " error:" + e.toString());
                result = -1;
            }

            return result;
        }

        ReduceAdapter(){
            this.listView = ReduceListFragment.this.listData;
            this.reduceManager = ((MPosApplication)getActivity().getApplication()).getDataHelper().getReduceManager();
            this.saleOrderReduceManager = ((MPosApplication)getActivity().getApplication()).getDataHelper().getSaleOrderReduceManager();
            this.saleOrderItemReduceManager = ((MPosApplication)getActivity().getApplication()).getDataHelper().getSaleOrderItemReduceManager();
           reduceList = reduceManager.loadAll();
        }


        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            reduceList = reduceManager.loadAll();
        }

        @Override
        public int getCount() {
            return reduceList.size();
        }

        @Override
        public Object getItem(int position) {
           return reduceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
           Reduce reduce = reduceList.get(pos);
            View view = convertView;
            ViewHold viewHold;
            if(view == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.reduce_list_item, null);

                viewHold = new ViewHold();
                view.setTag(viewHold);

                viewHold.textReduce = (TextView)view.findViewById(R.id.ItemText);

                viewHold.btnModify = (ImageButton) view.findViewById(R.id.btnModify);
                viewHold.btnModify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Reduce reduce = reduceList.get(pos);

                        ReduceListFragment.this.onbtnModReduce(reduce);
                        Log.d("modify click", "position=" + pos);

                        ((ImageButton)v).setImageResource(R.drawable.btn_modify_2);

                        if(preSelect < 0 || preSelect == pos){}
                        else{
                            View preView = listView.getChildAt(preSelect);
                            if(preView != null)
                                ((ImageButton)preView.findViewById(R.id.btnModify)).setImageResource(R.drawable.btn_modify);
                        }
                        preSelect = pos;
                    }
                });

                viewHold.btnDelete = (ImageButton) view.findViewById(R.id.btnDelete);
                viewHold.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Reduce reduce = reduceList.get(pos);

                        ReduceListFragment.this.onbtnDelReduce(reduce);
                        Log.v("btnDelete", "position="+pos);
                    }
                });
                viewHold.btnDelete.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN)
                            ((ImageButton)v).setImageResource(R.drawable.btn_delete_2);
                        else
                            ((ImageButton)v).setImageResource(R.drawable.btn_delete);
                        return false;
                    }
                });

            }else{
                viewHold = (ViewHold)view.getTag();
            }

            viewHold.textReduce.setText(reduce.getName());

            return view;
        }

    }

    static class ViewHold{
        TextView textReduce;
        ImageButton btnModify;
        ImageButton btnDelete;
    }
}
