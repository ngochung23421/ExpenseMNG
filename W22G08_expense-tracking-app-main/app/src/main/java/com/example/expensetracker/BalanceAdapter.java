package com.example.expensetracker;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.BalanceViewHolder> {

    List<TransactionModel> categoryItemList;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    OnItemClickListener onItemClickListener;
    DatabaseHelper databaseHelper;
    OnItemClickListener mSelected;

    public BalanceAdapter(List<TransactionModel> categoryItemList) {
        this.categoryItemList = categoryItemList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public BalanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_searchitem, parent, false);
        BalanceViewHolder balanceViewHolder = new BalanceViewHolder(view);
        databaseHelper = new DatabaseHelper(view.getContext());
        return balanceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceViewHolder holder, int position) {
        TransactionModel transaction = categoryItemList.get(position);
        holder.txtViewCategory.setText(transaction.getCategory());
        holder.txtViewDate.setText(dateFormat.format(transaction.getDate()));
        holder.txtViewNote.setText(transaction.getNote());
        double amount = transaction.getAmount();
        holder.txtViewAmount.setText(String.valueOf(amount));
        holder.txtViewAmount.setTextColor(amount > 0 ? Color.BLUE : Color.RED);

        // Set click listener for delete on single tap
        holder.itemView.setOnClickListener(view -> {
            showDeleteDialog(view, position);
        });

        // Set long click listener for edit on long tap
        holder.itemView.setOnLongClickListener(view -> {
            showEditDialog(view, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return categoryItemList.size();
    }

    public class BalanceViewHolder extends RecyclerView.ViewHolder {
        TextView txtViewCategory;
        TextView txtViewDate;
        TextView txtViewNote;
        TextView txtViewAmount;

        public BalanceViewHolder(@NonNull View itemView) {
            super(itemView);
            txtViewCategory = itemView.findViewById(R.id.txtViewCategory);
            txtViewDate = itemView.findViewById(R.id.txtViewDate);
            txtViewNote = itemView.findViewById(R.id.txtViewNote);
            txtViewAmount = itemView.findViewById(R.id.txtViewAmount);
        }
    }

    private void showDeleteDialog(View view, int position) {
        TransactionModel clickedData = categoryItemList.get(position);
        new AlertDialog.Builder(view.getContext())
                .setTitle("Delete data")
                .setMessage("Are you sure you want to delete this data?")
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                    databaseHelper.deleteData(clickedData);
                    Toast.makeText(view.getContext(), "Data deleted", Toast.LENGTH_SHORT).show();
                    // Notify the adapter that the item was deleted
                    categoryItemList.remove(position);
                    notifyItemRemoved(position);
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showEditDialog(View view, int position) {
        TransactionModel transaction = categoryItemList.get(position);

        // Inflate dialog layout
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        View dialogView = inflater.inflate(R.layout.dialog_edit_transaction, null);

        EditText editCategory = dialogView.findViewById(R.id.editCategory);
        EditText editDate = dialogView.findViewById(R.id.editDate);
        EditText editNote = dialogView.findViewById(R.id.editNote);
        EditText editAmount = dialogView.findViewById(R.id.editAmount);

        // Pre-fill current values
        editCategory.setText(transaction.getCategory());
        editDate.setText(dateFormat.format(transaction.getDate()));
        editNote.setText(transaction.getNote());
        editAmount.setText(String.valueOf(transaction.getAmount()));

        // Show edit dialog
        new AlertDialog.Builder(view.getContext())
                .setTitle("Edit Transaction")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    try {
                        // Get the updated values from the EditTexts
                        transaction.setCategory(editCategory.getText().toString());
                        transaction.setDate(dateFormat.parse(editDate.getText().toString()));
                        transaction.setNote(editNote.getText().toString());
                        transaction.setAmount(Double.parseDouble(editAmount.getText().toString()));

                        // Update database
                        boolean success = databaseHelper.editData(transaction);
                        if (success) {
                            Toast.makeText(view.getContext(), "Transaction updated!", Toast.LENGTH_SHORT).show();
                            notifyItemChanged(position);
                        } else {
                            Toast.makeText(view.getContext(), "Failed to update transaction.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(view.getContext(), "Invalid input!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener selected) {
        mSelected = selected;
    }
}
