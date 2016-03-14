package com.itertk.app.mpos.trade.pos;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.itertk.app.mpos.R;
import com.itertk.app.mpos.SlideDeleteListView;
import com.itertk.app.mpos.dbhelper.SaleOrderItem;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CheckoutFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CheckoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 * 超级收银台列表
 */
public class CheckoutFragment extends Fragment  implements AdapterView.OnItemClickListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public   final  static  int MSG_ADD=1;
    public   final  static  int MSG_SUB=2;
    public  final  static  int MAX_CART=999;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SlideDeleteListView saleOrderListView = null;
    NewSaleOrderAdapter saleOrderAdapter = null;

    ModSaleProductDialog saledlg;
    ModSaleManualDialog manualdlg;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CheckoutFragment newInstance(String param1, String param2) {
        CheckoutFragment fragment = new CheckoutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public CheckoutFragment() {
        // Required empty public constructor
       // saleOrderAdapter=adapter;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }
    public void delSaleItem(int position) {
       // saleOrderAdapter.delSaleOrderItem(position);
        ((PosActivity) getActivity()).delSaleItem(position);
    }
    public void updateTotalPrice()
    {
        ((PosActivity) getActivity()).updateTotalPrice();
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("smile","CartFragment onViewCreated");
        saleOrderListView = (SlideDeleteListView) view.findViewById(R.id.listData);
        saleOrderAdapter=((PosActivity)getActivity()).getSaleAdapter();
        if(saleOrderAdapter ==null)
             saleOrderAdapter = new NewSaleOrderAdapter(getActivity(),null,(PosActivity)getActivity());
        saleOrderListView.setRemoveListener(new SlideDeleteListView.RemoveListener() {
            @Override
            public void removeItem(SlideDeleteListView.RemoveDirection direction, int position) {
                Log.d("payList RemoveListener", "position=" + position);
                delSaleItem(position);
            }
        });
        saleOrderListView.setAdapter(saleOrderAdapter);
        saleOrderListView.setOnItemClickListener(this);

      //  mInputSearchWord.addTextChangedListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public  void setSn(String scancode)
    {
        //mInputSearchWord.setText(scancode);
        setListSelection(saleOrderAdapter.getCount() - 1);
    }
    void setListSelection(final int index)
    {
        saleOrderListView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                saleOrderListView.setSelection(index);
            }
        });
        saleOrderListView.setSelection(ListView.FOCUS_DOWN);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Log.d("smile checkout  onItemClick", "position=" + position);
//        SaleOrderItem saleOrderItem = (SaleOrderItem) saleOrderAdapter.getItem(position);
//        if (saleOrderItem.getProductId() > 0) {
//            if(saledlg==null||!saledlg.isShowing()) {
//                saledlg = new ModSaleProductDialog(this.getActivity(), R.style.MyDialog, saleOrderItem);
//                saledlg.show();
//            }
//        } else {
//            if(manualdlg==null||!manualdlg.isShowing()) {
//                manualdlg = new ModSaleManualDialog(this.getActivity(), R.style.MyDialog, saleOrderItem);
//                manualdlg.show();
//            }
//        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {

        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
