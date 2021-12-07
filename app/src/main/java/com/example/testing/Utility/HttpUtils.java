package com.example.testing.Utility;

import com.loopj.android.http.*;

public class HttpUtils {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void setToken(String token){
        //token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI2MWFhMWYzN2M0MDg0MTAyMjhmOWEyYzEiLCJ1c2VybmFtZSI6InRlc3R1c2VyIiwiYWNjb3VudE5vIjoiNTc1My05NTEtNTgzMyIsImlhdCI6MTYzODY4ODg3NCwiZXhwIjoxNjM4Njk5Njc0fQ.B5Vlvqa-3N8fqzOkbgYgLoHogLjXDAq_D5H3AB7YKk";
        client.addHeader("Authorization", token);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return Constants.BASE_URL + relativeUrl;
    }
}
