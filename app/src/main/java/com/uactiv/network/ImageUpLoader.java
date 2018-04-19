package com.uactiv.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.uactiv.controller.ResponseListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ImageUpLoader {

    private final Context mContext;
    private ResponseListener listner;
    private boolean isFile = false;
    private String file_path = "";
    private int flag ;

    public ImageUpLoader(Context cntx) {
        mContext = cntx;
    }

    public void getResponse(String url, ResponseListener mParseListener,int flag) {
        getResponse(url,  mParseListener, null,flag);
    }

    public void getResponse(String url, ResponseListener mParseListener, Map<String,Object> params,int flag) {
        this.listner = mParseListener;
        this.flag = flag;

        if (!isFile) {
            Log.e("isFile","false");

            Map<String,String> params_string = new HashMap<>();
            params_string = (Map<String, String>) (Object) params;
            RequestHandler.getInstance().stringRequestVolley(mContext,url,params_string,listner,flag);

        } else {

            Log.e("isFile","true" +file_path);

            if (file_path != null) {

                File mFile = new File(file_path);

                if(mFile.exists()) {

                    try {
                        params.remove("image[0]");
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    Log.e("File exist","true");

                    MultipartRequest multipartRequest = new MultipartRequest(mContext,url, listner, mFile, params, flag);
                    multipartRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    VolleySingleton.getInstance(mContext).addToRequestQueue(multipartRequest);
                }else {
                    Log.e("File Doesnot exist","true");
                }
            }
        }
    }

    public boolean isFile() {
        return isFile;
    }


    public void setFile(String path) {
        if (path != null ) {
            file_path = path;
            File file =new File(file_path);
            if(file.exists()){
                this.isFile = true;
            }


        }
    }

}