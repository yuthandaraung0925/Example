package com.example.testing;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.testing.Utility.Constants;
import com.example.testing.Utility.Dialogs;
import com.example.testing.Utility.HttpUtils;
import com.example.testing.Utility.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences pref;

    EditText txtUsername;
    EditText txtPassword;
    Button btnLogin;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = Utils.decryptData(pref.getString("UserName",""));

        if(userName == ""){
            setContentView(R.layout.activity_login);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.login);

            btnLogin = (Button) findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(loginClickListener);

            btnRegister = (Button) findViewById(R.id.btnRegister);
            btnRegister.setOnClickListener(registerClickListener);
        }
        else{
            Intent mIntent = new Intent(LoginActivity.this, TransactionHistoryActivity.class);
            startActivity(mIntent);
        }
    }

    private View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            txtUsername = (EditText) findViewById(R.id.txtUsername);
            txtPassword = (EditText) findViewById(R.id.txtPassword);

            if(checkDataValidation()) {
                RequestParams requestParams = new RequestParams();
                requestParams.add("username", txtUsername.getText().toString());
                requestParams.add("password", txtPassword.getText().toString());

                HttpUtils.post(Constants.LOGIN_URL, requestParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            JSONObject serverResponse = new JSONObject(response.toString());

                            //store in preference
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("Token", Utils.encryptData(serverResponse.getString("token")));
                            editor.putString("UserName", Utils.encryptData(serverResponse.getString("username")));
                            editor.putString("AccountNo", Utils.encryptData(serverResponse.getString("accountNo")));
                            editor.commit();
                            finish();

                            //call history page
                            Intent mIntent = new Intent(LoginActivity.this, TransactionHistoryActivity.class);
                            startActivity(mIntent);
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject response) {
                        try {
                            JSONObject serverResponse = new JSONObject(response.toString());
                            Dialogs.showErrorDialog(LoginActivity.this, serverResponse.getString("status"), getText(R.string.login_error).toString());
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    };

    private View.OnClickListener registerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        }
    };

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
        return flag;
    }
}