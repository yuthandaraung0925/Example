package com.example.testing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testing.Adapter.TransactionListAdapter;
import com.example.testing.Model.TransactionModel;
import com.example.testing.Utility.Constants;
import com.example.testing.Utility.Dialogs;
import com.example.testing.Utility.HttpUtils;
import com.example.testing.Utility.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class TransactionHistoryActivity extends AppCompatActivity {

    SharedPreferences pref;
    RequestParams requestParams;

    TextView txtBalance;
    TextView txtAccountNo;
    TextView txtAccountHolder;
    Button btnTransfer;
    ExpandableListView lvTransaction;
    TransactionListAdapter transactionListAdapter;

    String token;
    String userName;
    String accountNo;
    String balance;
    List<String> transactionDates;
    Map<String, List<TransactionModel>> transactionDetails;
    List<TransactionModel> transactionList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(null);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        token = Utils.decryptData(pref.getString("Token",""));
        userName = Utils.decryptData(pref.getString("UserName",""));
        accountNo = Utils.decryptData(pref.getString("AccountNo",""));

        requestParams = new RequestParams();
        HttpUtils.setToken(token);

        txtBalance = (TextView) findViewById(R.id.txtBalance);
        txtAccountNo = (TextView) findViewById(R.id.txtAccountNo);
        txtAccountHolder = (TextView) findViewById(R.id.txtAccountHolder);
        lvTransaction = (ExpandableListView) findViewById(R.id.lvTransaction);

        getBalance();
        getTransactionHistory();

        btnTransfer = (Button) findViewById(R.id.btnTransfer);
        btnTransfer.setOnClickListener(transferClickListener);
    }

    private View.OnClickListener transferClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("Balance", Utils.encryptData(balance));
            editor.commit();
            finish();

            Intent intent = new Intent(getApplicationContext(), TransferActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();
            finish();

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void getBalance(){
        HttpUtils.get(Constants.BALANCE_URL, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject serverResponse = new JSONObject(response.toString());
                    balance = serverResponse.getString("balance");
                    double amount = Double.parseDouble(balance);
                    txtBalance.setText(String.format("%s %s", getText(R.string.sgd), String.format("%,.2f", amount)));
                    txtAccountNo.setText(serverResponse.getString("accountNo"));
                    txtAccountHolder.setText(userName);
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject response) {
                try {
                    JSONObject serverResponse = new JSONObject(response.toString());
                    JSONObject errorObject = new JSONObject(serverResponse.getString("error"));
                    Dialogs.showErrorDialog(TransactionHistoryActivity.this, getText(R.string.token_error).toString(), getText(R.string.token_error_message).toString());
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void getTransactionHistory(){
        HttpUtils.get(Constants.TRANSACTIONS_URL, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject jsonObj = new JSONObject(response.toString());
                    JSONArray transactions = jsonObj.getJSONArray("data");
                    int size = transactions.length() - 1;
                    //get top 5 transactions
                    JSONArray showTransactions = new JSONArray();
                    if(size > 5){
                        for (int h = 0; h < 5; h++){
                            JSONObject o = transactions.getJSONObject(h);
                            showTransactions.put(o);
                        }
                        transactions = showTransactions;
                    }


                    String currentTransactionDate = "";
                    transactionDates = new ArrayList<>();
                    transactionDetails = new HashMap<>();
                    transactionList = new ArrayList<TransactionModel>();

                    Log.e("before for loop", String.valueOf(transactions.length()));
                    int sizeShowTransaction = transactions.length();
                    for (int i = 0; i < transactions.length(); i++){
                        JSONObject c = transactions.getJSONObject(i);
                        JSONObject account;
                        if(c.getString("transactionType").equals("transfer")){
                            account = c.getJSONObject("receipient");
                        }else {
                            account = c.getJSONObject("sender");
                        }

                        String transactionDate = c.getString("transactionDate").substring(0, 10);
                        String accountHolder = account.getString("accountHolder");
                        String accountNo = account.getString("accountNo");
                        String amount = c.getString("amount");
                        String transactionType = c.getString("transactionType");

                        if(i == 0){
                            currentTransactionDate = transactionDate;
                            transactionList.add(new TransactionModel(accountHolder, accountNo, amount, transactionType));

                            if(Integer.compare(sizeShowTransaction, i) == 1){
                                addTransactionHistory(currentTransactionDate, transactionList);
                            }
                        }else{
                            if(currentTransactionDate.equals(transactionDate)){
                                transactionList.add(new TransactionModel(accountHolder, accountNo, amount, transactionType));

                                if(Integer.compare(sizeShowTransaction, i) == 1){
                                    addTransactionHistory(currentTransactionDate, transactionList);
                                }

                            }else if(!currentTransactionDate.equals(transactionDate)) {
                                  addTransactionHistory(currentTransactionDate, transactionList);

                                  transactionList = new ArrayList<TransactionModel>();
                                  transactionList.add(new TransactionModel(accountHolder, accountNo, amount, transactionType));
                                  currentTransactionDate = transactionDate;

                                if(Integer.compare(sizeShowTransaction, i) == 1){
                                    addTransactionHistory(currentTransactionDate, transactionList);
                                }

                            }
                        }
                    }
                    Log.e("final date", transactionDates.toString());
                    Log.e("final details", transactionDetails.toString());

                    transactionListAdapter = new TransactionListAdapter(getApplicationContext(), transactionDates, transactionDetails);
                    lvTransaction.setAdapter(transactionListAdapter);
                    for (int j = 0; j < transactionDates.size(); j++){
                        lvTransaction.expandGroup(j);
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject response) {
                try {
                    JSONObject serverResponse = new JSONObject(response.toString());
                    JSONObject errorObject = new JSONObject(serverResponse.getString("error"));
                    Dialogs.showErrorDialog(TransactionHistoryActivity.this, getText(R.string.token_error).toString(), getText(R.string.token_error_message).toString());
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void addTransactionHistory(String transactionDate,  List<TransactionModel> list){
        String stringDate = DateTimeFormatter.ofPattern("d MMM yyyy").format(LocalDate.parse(transactionDate));
        if (!transactionDates.contains(stringDate)){
            transactionDates.add(stringDate);
        }
        transactionDetails.put(stringDate, list);
    }
}