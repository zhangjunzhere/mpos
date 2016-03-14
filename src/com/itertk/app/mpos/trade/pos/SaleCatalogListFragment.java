package com.itertk.app.mpos.trade.pos;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.dbhelper.Catalog;
import com.itertk.app.mpos.dbhelper.CatalogDao;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * 销售商品目录列表
 */
public class SaleCatalogListFragment extends Fragment implements AdapterView.OnItemClickListener {
    SaleCatalogAdapter saleCatalogAdapter;
    ListView listData = null;


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("onListItemClick", "" + position);
        Catalog catalog = (Catalog) saleCatalogAdapter.getItem(position);

       // ((PosActivity) getActivity()).onCatalogItemClick(catalog);
    }

    public SaleCatalogListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sale_catalog_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listData = (ListView) view.findViewById(R.id.listData);

        saleCatalogAdapter = new SaleCatalogAdapter();
        listData.setAdapter(saleCatalogAdapter);
        listData.setOnItemClickListener(this);
    }

    private class SaleCatalogAdapter extends BaseAdapter {
        CatalogDao catalogManager;
        List<Catalog> catalogList;

        public SaleCatalogAdapter() {
            this.catalogManager = ((MPosApplication) getActivity().getApplication()).getDataHelper().getCatalogManager();
            catalogList = catalogManager.loadAll();
        }

        @Override
        public int getCount() {
            return catalogList.size();
        }

        @Override
        public Object getItem(int position) {
            return catalogList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            catalogList = catalogManager.loadAll();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            Catalog catalog = catalogList.get(pos);
            View view = convertView;
            ViewHold viewHold;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.sale_catalog_list_item, null);

                viewHold = new ViewHold();
                view.setTag(viewHold);

                viewHold.textCatalog = (TextView) view.findViewById(R.id.ItemText);
                ((ImageView) view.findViewById(R.id.ItemImage)).setImageResource(R.drawable.product);
            } else {
                viewHold = (ViewHold) view.getTag();
            }

            viewHold.textCatalog.setText(catalog.getName());

            return view;
        }

    }

    static class ViewHold {
        ImageView imageView;
        TextView textCatalog;
    }
    public interface OnChangeOrderItemnListener  {
        // TODO: Update argument type and name
        public void onChangeItem(String pams,int position);
    }
}
