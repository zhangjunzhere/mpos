package com.itertk.app.mpos.config;



import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.dbhelper.CatalogDao;
import com.itertk.app.mpos.dbhelper.Catalog;


import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * 商品目录列表
 */
public class CatalogListFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{
    static final String TAG = "CatalogListFragment";
    ListView listData = null;
    Button btnAdd = null;

    AddCatalogFragment addCatalogFragment = null;
    ModCatalogFragment modCatalogFragment = null;
    FragmentTransaction transaction = null;

    AddProductFragment addProductFragment=null;

    //CatalogManager catalogManager = null;
    CatalogAdapter catalogAdapter = null;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("onListItemClick", "" + position);
        Catalog catalog = (Catalog)catalogAdapter.getItem(position);

        ((ProductConfigActivity)getActivity()).onCatalogItemClick(catalog);
    }

    public void addCatalog(Catalog catalog){
        catalogAdapter.addCatalog(catalog);
    }

    public void modCatalog(Catalog catalog){
        catalogAdapter.modCatalog(catalog);
    }

    private void onbtnAddCatalog(){
        addCatalogFragment = new AddCatalogFragment();
        addCatalogFragment.setCatalogListFragment(this);
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.itemArea, addCatalogFragment);
        transaction.commit();
    }

    private void onbtnModCatalog(Catalog catalog){
        modCatalogFragment = new ModCatalogFragment();
        modCatalogFragment.setParames(this, catalog);
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.itemArea, modCatalogFragment);
        transaction.commit();
    }

    private void onbtnAddProduct(){
        addProductFragment = new AddProductFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.itemArea, addProductFragment);
        transaction.commit();
    }

    private void onbtnDelCatalog(Catalog catalog){
        catalogAdapter.delCatalog(catalog);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAdd: onbtnAddProduct();
        }
    }

    public CatalogListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_catalog_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listData = (ListView)view.findViewById(R.id.listData);

        btnAdd = (Button)view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        //catalogManager = new CatalogManager(view.getContext());
        catalogAdapter = new CatalogAdapter();
        listData.setAdapter(catalogAdapter);
        listData.setOnItemClickListener(this);
    }

    private class CatalogAdapter extends BaseAdapter {
        final  ListView listView;
        CatalogDao catalogManager;
        List<Catalog> catalogList;
        int preSelect = -1;


        public long addCatalog(Catalog catalog){
            long result = 0;

            try {
                result = catalogManager.insert(catalog);
                notifyDataSetChanged();
            }catch (Exception e){
                Log.e(TAG, "insert " + catalog.toString() + " error:" + e.toString());
                result = -1;
            }

            return result;
        }

        public long modCatalog(Catalog catalog){
            long result = 0;

            try {
                catalogManager.update(catalog);
                notifyDataSetChanged();
            }catch (Exception e){
                Log.e(TAG, "modify " + catalog.toString() + " error:" + e.toString());
                result = -1;
            }

            return result;
        }

        public long delCatalog(Catalog catalog){
            long result = 0;

            try {
                catalogManager.delete(catalog);
                notifyDataSetChanged();
            }catch (Exception e){
                Log.e(TAG, "delete " + catalog.toString() + " error:" + e.toString());
                result = -1;
            }

            return result;
        }

        CatalogAdapter(){
            listView = CatalogListFragment.this.listData;
            catalogManager = ((MPosApplication)getActivity().getApplication()).getDataHelper().getDaoSession().getCatalogDao();
            catalogList = catalogManager.loadAll();
        }


        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            catalogList = catalogManager.loadAll();
        }

        @Override
        public int getCount() {
            return catalogList.size();
        }

        @Override
        public Object getItem(int position) {
            return  catalogList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            Catalog catalog = catalogList.get(pos);
            View view = convertView;
            ViewHold viewHold;
            if(view == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.catalog_list_item, null);

                viewHold = new ViewHold();
                view.setTag(viewHold);

                viewHold.textCatalog = (TextView)view.findViewById(R.id.ItemText);

                viewHold.btnModify = (ImageButton) view.findViewById(R.id.btnModify);
                viewHold.btnModify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Catalog catalog = catalogList.get(pos);

                        CatalogListFragment.this.onbtnModCatalog(catalog);
                        Log.v("modify click", "position=" + pos);

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

                viewHold.btnDelete =  (ImageButton) view.findViewById(R.id.btnDelete);
                viewHold.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Catalog catalog = catalogList.get(pos);

                        CatalogListFragment.this.onbtnDelCatalog(catalog);
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

            viewHold.textCatalog.setText(catalog.getName());

            return view;
        }

    }

    static class ViewHold{
        TextView textCatalog;
        ImageButton btnModify;
        ImageButton btnDelete;
    }
}
