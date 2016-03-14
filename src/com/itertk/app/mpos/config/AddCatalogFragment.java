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
import com.itertk.app.mpos.dbhelper.Catalog;

/**
 * A simple {@link Fragment} subclass.
 *添加商品目录
 */
public class AddCatalogFragment extends Fragment implements View.OnClickListener{
    CatalogListFragment catalogListFragment;
    EditText textCatalogName = null;
    Button btnSave = null;


    private void onbtnSave(){
        if (textCatalogName.getText().length() == 0){
           Toast.makeText(getActivity().getApplicationContext(), "名称为空",
                   Toast.LENGTH_SHORT).show();
           return;
        }
        Catalog catalog = new Catalog(AddProductFragment.DEFAULT_CATALOG_ID, textCatalogName.getText().toString(),"");
        catalogListFragment.addCatalog(catalog);
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave: onbtnSave(); break;
        }
    }


    public AddCatalogFragment() {
        // Required empty public constructor

    }

    public void setCatalogListFragment(CatalogListFragment catalogListFragment){
        this.catalogListFragment = catalogListFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_catalog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextFocusChangeListener textFocusChangeListener = new TextFocusChangeListener();
        textCatalogName = (EditText)view.findViewById(R.id.textCatalogName);
        textCatalogName.setOnFocusChangeListener(textFocusChangeListener);

        btnSave = (Button)view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
    }
}
