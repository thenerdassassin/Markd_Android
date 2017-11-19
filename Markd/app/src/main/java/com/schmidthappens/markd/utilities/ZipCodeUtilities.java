package com.schmidthappens.markd.utilities;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.schmidthappens.markd.customer_subactivities.ChangeContractorActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by joshua.schmidtibm.com on 11/18/17.
 */

public class ZipCodeUtilities {
    private static final String TAG = "ZipCodeUtilities";

    public static void getZipCodesByRadius(final Activity context, final String zipCode, final String radius, final Response.Listener<JSONObject> successListener, final Response.ErrorListener errorListener) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        String url ="https://www.zipcodeapi.com/rest/9xTgQJatx64XJImaOJdn8DMqCNEOSsm20fiQYskwyoui6QtVzGiIk58WSMLLf8Nf";
        if(radius.equals("0")) {
            url += "/radius.json/" + zipCode + "/1/miles";
        } else {
            url += "/radius.json/" + zipCode + "/" + radius + "/miles";
        }

        // Request a string response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(url, null, successListener, errorListener);

        // Add the request to the RequestQueue.
        queue.add(request);
    }

    public static Map<Double, String> sortZipCodes(JSONArray zipCodes) {
        try {
            Map<Double, String> zipCodeMap = new TreeMap<Double, String>();

            for (int i = 0; i < zipCodes.length(); i++) {
                JSONObject zipCode = zipCodes.getJSONObject(i);
                Double key = zipCode.getDouble("distance");
                while(zipCodeMap.containsKey(key)) {
                    key += 0.0000001;
                }
                zipCodeMap.put(key, zipCode.getString("zip_code"));
            }
            return zipCodeMap;
        } catch (JSONException exception) {
            Log.d(TAG, exception.toString());
            return Collections.emptyMap();
        }
    }
}
