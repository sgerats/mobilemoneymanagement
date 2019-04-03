package com.example.mobilemoneymanagement;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TransactionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TransactionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String transactionName;
    private String transactionDate;
    private String transactionReference;
    private double transactionAmount;

    MainActivity activity;
    private View rootView;
    private OnFragmentInteractionListener mListener;

    TransactionAdapter adapter;
    RecyclerView recyclerView;

    List<Transaction> transactionList;

    public TransactionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransactionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransactionsFragment newInstance(String param1, String param2) {
        TransactionsFragment fragment = new TransactionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("Test", "transactionFragment is created ");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
        activity = (MainActivity) getActivity();
        transactionList = activity.transactionList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout if the view is not created before
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_transactions, container, false);
            recyclerView = rootView.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            //Setting up adapter to show transactions
            adapter = new TransactionAdapter(rootView.getContext(),transactionList);
            recyclerView.setAdapter(adapter);

            //This listener is not used
            adapter.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = recyclerView.getChildAdapterPosition(v);
                    Transaction transaction = transactionList.get(position);
                }
            });
            //Setting up listener for inflating popup menu
            adapter.setButtonClickListener(new TransactionAdapter.OnButtonClickListener() {
                @Override
                public void onButtonClick(View view, final int position) {
                    final Transaction transaction = transactionList.get(position);
                    PopupMenu popupMenu = new PopupMenu(rootView.getContext(), view);
                    popupMenu.getMenuInflater().inflate(R.menu.popup_categories, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            String newCategory = item.getTitle().toString();
                            activity.addToCategory(transaction,"New", newCategory);
                            transactionList.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position,transactionList.size());
                            return true;
                        }
                    });
                    Log.d("Test", "onButtonClick: " + rootView.getContext());
                }
            });
        }
        else Log.d("Test", "transactionFragment rootView = nonnull");

        Log.d("Test", "rootView's Context: "+rootView.getContext());


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
