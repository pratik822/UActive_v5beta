package com.uactiv.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.uactiv.controller.ResponseListener;
import com.uactiv.utils.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by jeeva on 10/5/2015.
 */
public class RequestHandler implements AppConstants.SharedConstants {

    private String TAG = "RequestHandler";
    private static RequestHandler requestHandler = null;

    public static RequestHandler getInstance() {
        if (requestHandler != null) {
            return requestHandler;
        } else {
            requestHandler = new RequestHandler();
            return requestHandler;
        }
    }


    /**
     * request for string response
     *
     * @param context
     * @param url
     * @param params
     */


    public void stringRequestVolley(Context context, String url, final Map<String, String> params, final ResponseListener listener, final int flag) {

        Log.d(TAG, "url :" + url);

        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("RequestHandler String", response);
                if (listener != null)
                    try {
                        listener.successResponse(response, flag);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                if (listener != null)
                    listener.removeProgress(true);
                    listener.errorResponse(error.getMessage(), flag);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params1 = new HashMap<String, String>();
                params1 = params;
                Log.e("Param", ":" + params1.toString());
                return params1;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded;application/json");
                return params;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(req);

    }


    /**
     * Post Request handling method without header.
     *
     * @param context
     * @param url
     * @param jsonObject
     * @param listener
     * @param flag
     */


    public void postVolleytrt(final Context context, String url, JSONObject jsonObject, final
    ResponseListener listener, final int flag) {

        Log.e("URL", ":" + url);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject responseJson) {


                System.out.println("jsonObject" + responseJson.toString());

                /** Success Response
                 *
                 */
                if (listener != null) {
                    listener.successResponse(responseJson, flag);
                }
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                /** Error Response
                 *
                 */
                VolleyLog.d("Error", "Error Response");

                NetworkResponse networkResponse = error.networkResponse;

                if (networkResponse != null) {

                    if (networkResponse.statusCode != 200) {

                        VolleyLog.d("Error", "Response Code:" + networkResponse.statusCode);
                    }
                }

                if (listener != null) {
                    listener.removeProgress(true);
                    listener.errorResponse(null, flag);
                }


            }
        }) {

            /** Setting the content type
             *
             */

            @Override
            public String getBodyContentType() {
                return "application/json;charset=UTF-8";
            }

        };


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);


    }


    public void getVolleyArray(final Context context, String url, final
    ResponseListener listener, final int flag) {


        System.out.println("url::" + url);

        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {


                System.out.println("Array::");

                VolleyLog.d("JSonArray", "" + jsonArray.toString());


                try {
                    listener.successResponse(jsonArray.toString(), flag);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("Error::");

                listener.removeProgress(true);

            }
        }) {

            /** Setting the content type
             *
             */

            @Override
            public String getBodyContentType() {
                return "application/json;charset=UTF-8";
            }


        };

        request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleySingleton.getInstance(context).addToRequestQueue(request);


    }


    /**
     * GET Request handling method, Return the response in string.
     *
     * @param context
     * @param url
     * @param listener
     * @param flag
     */


    public void getVolley(final Context context, String url, final ResponseListener listener, final int flag) {

        System.out.println("url::" + url);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {

                System.out.println("111 jsonObject::" + jsonObject.toString());

                /** Success Response
                 *
                 */
                listener.successResponse(jsonObject, flag);

            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                /** Error Response
                 *
                 */
                listener.errorResponse(error.getMessage(), flag);
                listener.removeProgress(true);

            }
        }) {

            /** Setting the content type
             *
             */

            @Override
            public String getBodyContentType() {
                return "application/json;charset=UTF-8";
            }


        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);

    }


    /**
     * DELETE Request handling method, Return the response in string.
     *
     * @param context
     * @param url
     * @param listener
     * @param flag
     */


    public void deleteVolley(final Context context, String url, JSONObject jsonObject, final ResponseListener listener, final int flag) {


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE, url, jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {

                /** Success Response
                 *
                 */

                listener.successResponse(jsonObject, flag);

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                /** Error Response
                 *
                 */

                listener.removeProgress(true);


            }

        }) {

            /** Setting the content type
             *
             */

            @Override
            public String getBodyContentType() {
                return "application/json;charset=UTF-8";
            }


        };

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);

    }


    /**
     * requestJSON Object with Map params
     *
     * @param context
     * @param url
     * @param param
     * @param listener
     * @param flag
     */

    public void postVolleyMapNOT(Context context, String url, final Map<String, String> param, final ResponseListener listener, final int flag) {


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("RequestHandler", response.toString());
                        listener.successResponse(response, flag);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                listener.removeProgress(true);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Log.d("params", "" + param);
                return param;
            }

        };

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);

    }

}
