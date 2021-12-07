package com.example.testing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.testing.Utility.Constants;
import com.example.testing.Utility.Dialogs;
import com.example.testing.Utility.HttpUtils;
import com.example.testing.Utility.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class TransferActivity extends AppCompatActivity {
    SharedPreferences pref;
    RequestParams requestParams;

    Spinner cboPayees;
    EditText txtAmount;
    EditText txtDescription;
    Button btnTransferNow;

    String token;
    String balance;
    String changeAccountNo;

    private ArrayList<String> colAccountNo = new ArrayList<String>();
    private ArrayList<String> colAccountHolder = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.transfer);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        token = Utils.decryptData(pref.getString("Token",""));
        balance = Utils.decryptData(pref.getString("Balance",""));

        requestParams = new RequestParams();
        HttpUtils.setToken(token);

        txtAmount = (EditText) findViewById(R.id.txtAmount);
        txtDescription = (EditText) findViewById(R.id.txtDescription);
        cboPayees = (Spinner) findViewById(R.id.cboPayee);
        getPayees();

        btnTransferNow = (Button) findViewById(R.id.btnTransferNow);
        btnTransferNow.setOnClickListener(transferNowClickListener);
    }

    private View.OnClickListener transferNowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(checkDataValidation()) {
                int amount = Integer.parseInt(txtAmount.getText().toString());
                volleyPost(changeAccountNo, amount,  txtDescription.getText().toString());
            }
        }
    };

    public void volleyPost(String receipientAccountNo, int amount, String description){
        String postUrl = String.format("%s%s", Constants.BASE_URL, Constants.TRANSFER_URL);
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject postData = new JSONObject();
        try {
            postData.put("receipientAccountNo", receipientAccountNo);
            postData.put("amount", amount);
            postData.put("description", description);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject serverResponse = new JSONObject(response.toString());
                    if(serverResponse.get("status").toString().equals("success")){
                        Log.e("response", response.toString());
                        AlertDialog alertDialog = new AlertDialog.Builder(TransferActivity.this).create();
                        alertDialog.setTitle(serverResponse.getString("status"));
                        alertDialog.setMessage(getText(R.string.transfer_successful).toString());
                        alertDialog.setIcon(R.drawable.success_icon);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        Intent mIntent = new Intent(TransferActivity.this, TransactionHistoryActivity.class);
                                        startActivity(mIntent);
                                    }
                                });
                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("responseError", error.toString());
                error.printStackTrace();
                Dialogs.showErrorDialog(TransferActivity.this, getText(R.string.token_error).toString(), getText(R.string.token_error_message).toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getPayees(){
        HttpUtils.get(Constants.PAYEES_URL, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject jsonObj = new JSONObject(response.toString());
                    JSONArray payees = jsonObj.getJSONArray("data");

                    for (int i = 0; i < payees.length(); i++){
                        JSONObject obj = payees.getJSONObject(i);
                        colAccountNo.add(obj.getString("accountNo"));
                        colAccountHolder.add(obj.getString("name"));
                    }

                    ArrayAdapter<String> a = new ArrayAdapter<String>(TransferActivity.this, R.layout.support_simple_spinner_dropdown_item, colAccountHolder);
                    cboPayees.setAdapter(a);

                    cboPayees.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            int index = parentView.getSelectedItemPosition();
                            changeAccountNo = colAccountNo.get(index).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            cboPayees.setSelection(0);
                        }
                    });
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject response) {
                try {
                    JSONObject serverResponse = new JSONObject(response.toString());
                    JSONObject errorObject = new JSONObject(serverResponse.getString("error"));
                    Dialogs.showErrorDialog(TransferActivity.this, getText(R.string.token_error).toString(), getText(R.string.token_error_message).toString());
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean checkDataValidation(){
        boolean flag = true;
        if (txtAmount.getText().toString().length() == 0){
            txtAmount.requestFocus();
            txtAmount.setError(getText(R.string.require_amount));
            flag = false;
        }
        else if (Integer.parseInt(txtAmount.getText().toString()) <= 0){
            txtAmount.requestFocus();
            txtAmount.setError(getText(R.string.amount_greater_zero));
            flag = false;
        }
        else if (Integer.parseInt(txtAmount.getText().toString()) > Integer.parseInt(balance)){
            txtAmount.requestFocus();
            txtAmount.setError(getText(R.string.amount_compare_balance));
            flag = false;
        }
        else if (txtDescription.getText().toString().length() == 0){
            txtDescription.setError(getText(R.string.require_description));
            flag = false;
        }
        return flag;
    }
}