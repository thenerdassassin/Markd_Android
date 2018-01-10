package com.schmidthappens.markd.utilities;

import android.app.Activity;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by joshua.schmidtibm.com on 1/9/18.
 */

public class SendEmail {
    private final static String TAG = "SendEmail";
    private final static String TO = "schmidt.uconn@gmail.com";
    private final static String DOMAIN = "sandbox11b0c4f4831d4edaa9d23ba61c6d125f.mailgun.org";
    private final static String API_KEY = "key-98ae211f5d8dd12c4639ab41ef6ccf9a";

    public static void sendMessage(final Activity context, String from, String message, final Response.Listener<JSONObject> successListener, final Response.ErrorListener errorListener) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.mailgun.net/v3/" + DOMAIN + "/messages";
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("to", TO);
        paramsMap.put("from", from);
        paramsMap.put("subject", "Markd Help");
        paramsMap.put("text", message);
        try {
            url = addQueryParams(url, paramsMap);
        } catch(IOException error) {
            Log.e(TAG, error.toString());
            return;
        }

        // Request a JSONOBject response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, successListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                try {
                    Map<String, String> map = new HashMap<String, String>();
                    String key = "Authorization";
                    String encodedString = Base64.encodeToString(String.format("%s:%s", "api", API_KEY).getBytes(), Base64.NO_WRAP);
                    String value = String.format("Basic %s", encodedString);
                    map.put(key, value);
                    return map;
                } catch (Exception e) {
                    Log.e(TAG, "Authentication Failure");
                }
                return super.getParams();
            }
        };
        queue.add(request);
    }

    private static String addQueryParams(String url, Map<String, String> params) throws IOException{
        if(params == null || params.isEmpty()) {
            return url;
        }
        for(Map.Entry<String, String> param: params.entrySet()) {
            url = addQueryParam(url, param.getKey(), param.getValue());
        }
        return url;
    }
    private static String addQueryParam(String url, String param, String value) throws IOException{
        if(!url.contains("?")) {
            return url + "?" + encodedString(param) + "=" + encodedString(value);
        } else {
            return url + "&" + encodedString(param) + "=" + encodedString(value);
        }
    }
    private static String encodedString(String valueToEncode) throws IOException {
        return URLEncoder.encode(valueToEncode, "UTF-8");
    }
}
