package com.example.mobilemoneymanagement;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements DashboardFragment.OnFragmentInteractionListener, TransactionsFragment.OnFragmentInteractionListener {

    List<Transaction> transactionList = new ArrayList<>();

    public DatabaseReference ref;
    private TaskCompletionSource<DataSnapshot> dbSource;

    private String transactionName;
    private String transactionDate;
    private String transactionReference;
    private double transactionAmount;
    private String[] categories = {"Coffee","Food","Study","Activities","Products"};
    public double[] sum = new double[5];
    private Map<String, String> remembered = new HashMap<>();
    public List<Transaction> incomeList = new ArrayList<>();
    public List<Transaction> transactionGroup = new ArrayList<>();


    private TextView mTextMessage;
    public TransactionsFragment transactionsFragment;
    public DashboardFragment dashboardFragment;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_transactions:
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    mTextMessage.setText(R.string.title_transactions);
                    if (transactionsFragment == null) {
                        transactionsFragment = new TransactionsFragment();
                        fragmentTransaction.add(R.id.fragment_container,transactionsFragment);
                    }
                    else fragmentTransaction.attach(transactionsFragment);
                    if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) != null)
                        fragmentTransaction.detach(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_dashboard:
                    FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                    mTextMessage.setText(R.string.title_dashboard);
                    if (dashboardFragment == null) {
                        dashboardFragment = new DashboardFragment();
                        fragmentTransaction1.add(R.id.fragment_container, dashboardFragment).detach(transactionsFragment);
                    }
                    else fragmentTransaction1.attach(dashboardFragment).detach(transactionsFragment);
                    if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) != null)
                        fragmentTransaction1.detach(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
                    fragmentTransaction1.commit();
                    return true;
                case R.id.navigation_more:
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    String category = "Coffee";
                    mTextMessage.setText(R.string.title_more);
                    InfoFragment infoFragment = new InfoFragment();
                    infoFragment.setCategory(category);
                    fragmentTransaction2.add(R.id.fragment_container,infoFragment);
                    infoFragment.setCategory(category);
                    if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) != null)
                        fragmentTransaction2.detach(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
                    fragmentTransaction2.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onFragmentInteraction(Uri uri){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ref = FirebaseDatabase.getInstance().getReference();
        addIncome();
        listNewTransactions("New");
        createSums();
    }

    public void addIncome() {
        Log.d("Test", "addIncome ref: "+ref.child("New"));
        ref.child("New").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    transactionAmount = Double.parseDouble(child.child("Amount").getValue().toString());
                    if (transactionAmount >= 0) {
                        transactionName = child.child("Name").getValue().toString();
                        transactionReference = child.child("Reference").getValue().toString();
                        transactionDate = (String) child.child("Date").getValue();
                        Transaction transaction = new Transaction(transactionName, transactionDate, transactionReference, transactionAmount);
                        incomeList.add(transaction);
                        addToCategory(transaction,"New","Income");
                    }
                }
                transactionsFragment = new TransactionsFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.fragment_container,transactionsFragment);
                fragmentTransaction.commit();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Cancelled: ", databaseError.toException());
            }
        });
    }


    public void addToCategory(Transaction transaction, String oldCategory, String newCategory) {
        ref.child(oldCategory).child(transaction.getReference()).removeValue();
        Map<String, Object> thisTransaction = new HashMap<>();
        thisTransaction.put("Name",transaction.getName());
        thisTransaction.put("Date",transaction.getDate());
        thisTransaction.put("Amount",transaction.getAmount());
        thisTransaction.put("Reference",transaction.getReference());
        ref.child(newCategory).child(transaction.getReference()).setValue(thisTransaction);
    }

    public void getTransactions(String category) {
        ref.child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactionGroup.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    transactionName = child.child("Name").getValue().toString();
                    transactionReference = child.child("Reference").getValue().toString();
                    transactionAmount = Double.parseDouble(child.child("Amount").getValue().toString());
                    transactionDate = (String) child.child("Date").getValue();
                    Transaction transaction = new Transaction(transactionName,transactionDate,transactionReference,transactionAmount);
                    transactionGroup.add(transaction);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void listNewTransactions(String category) {
        ref.child(category).addValueEventListener(new ValueEventListener() {
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Cancelled: ", databaseError.toException());
            }
        });
    }

    public void createSums() {
        for (int i=0; i < categories.length; i++) {
            final int j = i;
            ref.child(categories[i]).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    sum[j] = 0;
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    for (DataSnapshot child : children) {
                        double transactionAmount = Double.parseDouble(child.child("Amount").getValue().toString());
                        sum[j] = sum[j] + transactionAmount;
                    }
                    Log.d("Test", "Sum: "+sum[j]);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("Cancelled: ", databaseError.toException());
                }
            });
        }
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
