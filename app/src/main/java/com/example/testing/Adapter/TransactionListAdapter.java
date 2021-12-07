package com.example.testing.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.testing.Model.TransactionModel;
import com.example.testing.R;

import java.util.List;
import java.util.Map;

public class TransactionListAdapter extends BaseExpandableListAdapter {
    Context context;
    List<String> transactionDates;
    Map<String, List<TransactionModel>> transactionDetails;

    public TransactionListAdapter(Context context, List<String> transactionDates, Map<String, List<TransactionModel>> transactionDetails){
        this.context = context;
        this.transactionDates = transactionDates;
        this.transactionDetails = transactionDetails;
    }

    @Override
    public int getGroupCount() {
        return transactionDates.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return transactionDetails.get(transactionDates.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return transactionDates.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return transactionDetails.get(transactionDates.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String date = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group_transaction, parent, false);
        }

        TextView transactionDate = (TextView) convertView.findViewById(R.id.txtTransactionDate);
        transactionDate.setText(date);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TransactionModel model = (TransactionModel) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_transaction, parent, false);
        }

        TextView accountHolder = (TextView)convertView.findViewById(R.id.txtAccountHolder);
        TextView amount = (TextView) convertView.findViewById(R.id.txtAmount);
        TextView accountNo = (TextView) convertView.findViewById(R.id.txtAccountNo);

        accountHolder.setText(model.getAccountHolder());
        accountNo.setText(model.getAccountNo());
        double amountValue = Double.parseDouble(model.getAmount());

        if(model.getTransactionType().equals("transfer")){
            amount.setText(String.format("- %s", String.format("%,.2f", amountValue)));
            amount.setTextColor(ContextCompat.getColor(context, R.color.grey));
        }else{
            amount.setText(String.format("%,.2f", amountValue));
            amount.setTextColor(ContextCompat.getColor(context, R.color.green));
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
