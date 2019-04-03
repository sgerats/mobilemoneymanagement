package com.example.mobilemoneymanagement;


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


public class InfoFragment extends Fragment {

    private String mParam1;
    private String mParam2;

    private String transactionName;
    private String transactionDate;
    private String transactionReference;
    private double transactionAmount;

    private String thisCategory = "null";
    List<Transaction> transactionGroup;
    MainActivity activity;
    private View rootView;
    RecyclerView recyclerView;
    TransactionAdapter adapter;

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Test with category Coffee
        thisCategory = "Coffee";
        activity.getTransactions(thisCategory);
        transactionGroup = activity.transactionGroup;
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_info, container, false);
        recyclerView = rootView.findViewById(R.id.info_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Setting up adapter to show transactions
        adapter = new TransactionAdapter(rootView.getContext(),transactionGroup);
        recyclerView.setAdapter(adapter);

        //This listener is not used
        adapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = recyclerView.getChildAdapterPosition(v);
                Transaction transaction = transactionGroup.get(position);
                Log.v("CLICKED", "Clicking on item(" + position + ", " + transaction + ")");
            }
        });

        //Setting up listener for inflating popup menu
        adapter.setButtonClickListener(new TransactionAdapter.OnButtonClickListener() {
            @Override
            public void onButtonClick(View view, final int position) {
                final Transaction transaction = transactionGroup.get(position);
                PopupMenu popupMenu = new PopupMenu(rootView.getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.popup_categories, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String newCategory = item.getTitle().toString();
                        activity.addToCategory(transaction,thisCategory,newCategory);
                        transactionGroup.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position,transactionGroup.size());
                        return true;
                    }
                });
            }
        });

        return rootView;
    }

    public void setCategory(String category) {
        thisCategory = category;
    }

}
