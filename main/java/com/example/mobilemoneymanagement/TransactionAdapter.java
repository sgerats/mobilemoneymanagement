package com.example.mobilemoneymanagement;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    public Context mCtx;
    List<Transaction> transactionList;

    View.OnClickListener clickListener;
    OnButtonClickListener buttonClickListener;

    public interface OnButtonClickListener {
        void onButtonClick(View view, int position);
    }

    public void setOnItemClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setButtonClickListener(OnButtonClickListener buttonClickListener) {
        this.buttonClickListener = buttonClickListener;
    }

    public TransactionAdapter(Context mCtx, List<Transaction> transactionList) {
        this.mCtx = mCtx;
        this.transactionList = transactionList;
        Log.d("Test", "Transactionlist: "+transactionList);
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.list_item,
                parent, false);
        TransactionViewHolder TransactionViewHolder = new TransactionViewHolder(view);
        return TransactionViewHolder;
    }

    @Override
    public void onBindViewHolder(TransactionViewHolder holder, final int position) {
        Transaction transaction = transactionList.get(position);

        holder.name.setText(transaction.getName());
        holder.date.setText(transaction.getDate());
        holder.amount.setText(String.format("%.2f",transaction.getAmount()));

        if (clickListener != null) {
            holder.imageButton.setOnClickListener(clickListener);
        }

        if (buttonClickListener != null) {
            TransactionViewHolder myHolder = (TransactionViewHolder) holder;
            myHolder.getImageButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonClickListener.onButtonClick(v, position);
                }
            }); ;
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(ImageButton imageButton);
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView name, date, amount;
        ImageButton imageButton;
        private RecyclerViewClickListener listener;

        public TransactionViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
            imageButton = itemView.findViewById(R.id.add_button);
        }

        public ImageButton getImageButton() {
            return imageButton;
        }
    }
}
