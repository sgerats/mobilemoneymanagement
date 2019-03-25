package com.example.mobilemoneymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TransactionAdapter adapter;
    List<Transaction> transactionList;

    private DatabaseReference ref;

    private String transactionName;
    private String transactionDate;
    private String transactionReference;
    private double transactionAmount;
    private List<String> categoryList = new ArrayList<>();
    private Map<String, String> remembered = new HashMap<>();

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_transactions:
                    mTextMessage.setText(R.string.title_transactions);
                    Intent intent1 = new Intent();
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    Intent intent2 = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(intent2);
                    return true;
                case R.id.navigation_more:
                    mTextMessage.setText(R.string.title_more);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Firebase Database
        ref = FirebaseDatabase.getInstance().getReference();

        showNewTransactions();

        //For the new Recyclerview
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionList = new ArrayList<>();
    }

    private void addToCategory(Transaction transaction, String thisCategory) {
        ref.child("New").child(transaction.getReference()).removeValue();
        Map<String, Object> thisTransaction = new HashMap<>();
        thisTransaction.put("Name",transaction.getName());
        thisTransaction.put("Date",transaction.getDate());
        thisTransaction.put("Amount",transaction.getAmount());
        thisTransaction.put("Reference",transaction.getReference());
        ref.child(thisCategory).child(transaction.getReference()).setValue(thisTransaction);
    }

    private void showNewTransactions() {
        ref.child("New").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Log.d("Iterable", children.toString());
                for (DataSnapshot child : children) {
                    transactionName = child.child("Name").getValue().toString();
                    transactionReference = child.child("Reference").getValue().toString();
                    transactionAmount = Double.parseDouble(child.child("Amount").getValue().toString());
                    transactionDate = (String) child.child("Date").getValue();
                    Transaction transaction = new Transaction(transactionName,transactionDate,transactionReference,transactionAmount);
                    transactionList.add(transaction);
                    Log.d("Test", "transaction: "+transaction.getReference());
                }

                adapter = new TransactionAdapter(MainActivity.this,transactionList);
                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = recyclerView.getChildAdapterPosition(v);
                        Transaction transaction = transactionList.get(position);
                        Log.v("CLICKED", "Clicking on item(" + position + ", " + transaction + ")");
                    }
                });
                adapter.setButtonClickListener(new TransactionAdapter.OnButtonClickListener() {
                    @Override
                    public void onButtonClick(View view, final int position) {
                        final Transaction transaction = transactionList.get(position);
                        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
                        popupMenu.getMenuInflater().inflate(R.menu.popup_categories, popupMenu.getMenu());
                        popupMenu.show();
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                String thisCategory = item.getTitle().toString();
                                addToCategory(transaction,thisCategory);
                                transactionList.remove(position);
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeChanged(position,transactionList.size());
                                return true;
                            }
                        });
                        Log.d("Test", "onButtonClick: " + MainActivity.this);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Cancelled: ", databaseError.toException());
            }
        });
    }

    /*private void resetData() {
        db.collection("transactions")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete();
                    }
                }
            }
        });

        db.collection("Coffee")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete();
                    }
                }
            }
        });


        for(int i = 0; i < 10; i++) {
            Map<String, Object> newTransaction = new HashMap<>();
            newTransaction.put("Name","DE Coffee Corner");
            newTransaction.put("Date","19/3/2019");
            newTransaction.put("Amount", ThreadLocalRandom.current().nextDouble(0.50, 10.00));
            db.collection("transactions")
                    .add(newTransaction);
        }
    }*/




    /*private void listCategories() {
        db.collection("transactions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String category = document.getString("Category");
                                if (!categoryList.contains(category)) {
                                    categoryList.add(category);
                                }
                            }

                        }
                    }
                });
    }

    /*private void groupTransactions() {
        db.collection("transactions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Map<String, String>> transactionList = new ArrayList<>();
                            // save transaction data to variable temporarily
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getData() != null) {
                                    Map<String, String> transaction = new HashMap<>();

                                    transactionName = document.getString("Name");
                                    transactionDate = document.getString("Date");
                                    transactionCategory = document.getString("Category");
                                    transactionAmount = Double.toString(document.getDouble("Amount"));

                                    //set variable value to internal
                                    transaction.put("ID", document.getId());
                                    transaction.put("Name", transactionName);
                                    transaction.put("Date", transactionDate);
                                    transaction.put("Category", transactionCategory);
                                    transaction.put("Amount", transactionAmount);

                                    //Add to the remembered list if new
                                    if (!remembered.containsKey(transactionName)) {
                                        remembered.put(transactionName, transactionCategory);
                                    }

                                    //Test output
                                    Log.d("remembered", remembered.toString());
                                    Log.d("Hashmap output", transaction.toString());

                                    //Add to list
                                    transactionList.add(transaction);
                                }
                            }
                            Log.d("Number of Categories = ", Integer.toString(categoryList.size()));
                            //Add groups of transactions to the groupedList
                            for (int i = 0; i < categoryList.size(); i++) {
                                String thisCategory = categoryList.get(i);
                                Log.d("thisCategory = ", thisCategory);
                                ArrayList<Map<String,String>> transactionGroup = new ArrayList<>();
                                for (int j = 0; j < transactionList.size(); j++) {
                                    Map<String, String> thisTransaction = transactionList.get(j);
                                    if (thisTransaction.get("Category").equals(thisCategory)) {
                                        transactionGroup.add(thisTransaction);
                                    }
                                }
                                groupedList.add(transactionGroup);
                            }
                            Log.d("groupedList Output", groupedList.toString());


                            //updateList(groupedList);

                            //for (QueryDocumentSnapshot document : task.getResult()) {
                            //  String id = document.getId();
                            //Map<String, String> data = document.getData();
                            //Log.d("Firestore", id + " => " + data);

                        }
                        else {
                            Log.w("Firestore", "Error getting documents.",
                                    task.getException());
                        }
                    }
                });
    }*/
}
