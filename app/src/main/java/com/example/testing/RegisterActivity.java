package com.example.testing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.testing.Utility.Constants;
import com.example.testing.Utility.Dialogs;
import com.example.testing.Utility.HttpUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity {

    EditText txtUsername;
    EditText txtPassword;
    EditText txtConfirmPassword;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.register);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(registerClickListener);
    }

    private View.OnClickListener registerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            txtUsername = (EditText) findViewById(R.id.txtUsername);
            txtPassword = (EditText) findViewById(R.id.txtPassword);
            txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);

            if(checkDataValidation()){
                RequestParams requestParams = new RequestParams();
                requestParams.add("username", txtUsername.getText().toString());
                requestParams.add("password", txtPassword.getText().toString());

                HttpUtils.post(Constants.REGISTER_URL, requestParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            JSONObject serverResponse = new JSONObject(response.toString());

                            AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                            alertDialog.setTitle(serverResponse.getString("status"));
                            alertDialog.setMessage(getText(R.string.register_successful).toString());
                            alertDialog.setIcon(R.drawable.success_icon);
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                            Intent mIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(mIntent);
                                        }
                                    });
                            alertDialog.show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject response) {
                        try {
                            JSONObject serverResponse = new JSONObject(response.toString());
                            Dialogs.showErrorDialog(RegisterActivity.this, serverResponse.getString("status"), getText(R.string.register_error).toString());
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean checkDataValidation(){
        boolean flag = true;

        if (txtUsername.getText().toString().length() == 0){
            txtUsername.requestFocus();
            txtUsername.setError(getText(R.string.require_username));
            flag = false;
        }
        else if (txtPassword.getText().toString().length() == 0){
            txtPassword.requestFocus();
            txtPassword.setError(getText(R.string.require_password));
            flag = false;
        }
        else if (txtConfirmPassword.getText().toString().length() == 0){
            txtConfirmPassword.requestFocus();
            txtConfirmPassword.setError(getText(R.string.require_confirm_password));
            flag = false;
        }
        else if (!txtConfirmPassword.getText().toString().equals(txtPassword.getText().toString())){
            txtConfirmPassword.requestFocus();
            txtConfirmPassword.setError(getText(R.string.match_confirm_password));
            flag = false;
        }
        return flag;
    }
}