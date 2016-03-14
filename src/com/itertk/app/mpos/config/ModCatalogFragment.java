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
 *修改商品目录
 */
public class ModCatalogFragment extends Fragment implements View.OnClickListener{
    CatalogListFragment catalogListFragment;
    Catalog catalog;

    Button btnSave = null;
    EditText textCatalogName = null;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave: onbtnSave(); break;
        }
    }

    private void onbtnSave(){
        if (textCatalogName.getText().length() == 0){
            Toast.makeText(getActivity().getApplicationContext(), "名称为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        catalog.setName(textCatalogName.getText().toString());
        catalogListFragment.modCatalog(catalog);
    }

    public ModCatalogFragment(){

    }

    public void setParames(CatalogListFragment catalogListFragment, Catalog catalog) {
        // Required empty public constructor
        this.catalogListFragment = catalogListFragment;
        this.catalog = catalog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mod_catalog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnSave = (Button)view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        TextFocusChangeListener textFocusChangeListener = new TextFocusChangeListener();
        textCatalogName = (EditText)view.findViewById(R.id.textCatalogName);
        textCatalogName.setOnFocusChangeListener(textFocusChangeListener);

        textCatalogName.setText(catalog.getName());
    }
}
