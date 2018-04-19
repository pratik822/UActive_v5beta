package com.uactiv.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.belladati.httpclientandroidlib.HttpEntity;
import com.belladati.httpclientandroidlib.entity.mime.HttpMultipartMode;
import com.belladati.httpclientandroidlib.entity.mime.MultipartEntityBuilder;
import com.belladati.httpclientandroidlib.entity.mime.content.ByteArrayBody;
import com.belladati.httpclientandroidlib.entity.mime.content.ContentBody;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.uactiv.controller.ResponseListener;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MultipartRequest extends Request<String> {

    private static final String FILE_PART_NAME = "image";
    private final ResponseListener mListener;
    private final File mFilePart;
    private final Map<String, Object> mStringPart;
    MultipartEntityBuilder entity = MultipartEntityBuilder.create();
    HttpEntity httpentity;
    private int flag;
    Context context;
    String mResponseString = null;

    public MultipartRequest(Context context,String url, ResponseListener listener, File file, Map<String, Object> mStringPart,int flag) {
        super(Method.POST, url, null);
        this.context =context;
        mListener = listener;
        mFilePart = file;
        this.mStringPart = mStringPart;
        this.flag = flag;
        entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        buildMultipartEntity();
    }

    public void addStringBody(String param, String value) {
        mStringPart.put(param, value);
    }

    private void buildMultipartEntity() {

        //Log.e("CompressedImage",""+getCompressedImage(mFilePart.getAbsolutePath()));
       // entity.addPart(FILE_PART_NAME, new FileBody(mFilePart));
        if(getCompressedImage(mFilePart.getAbsolutePath()) != null) {
             entity.addPart(FILE_PART_NAME, (ContentBody) getCompressedImage(mFilePart.getAbsolutePath()));
        }
        for (Map.Entry<String, Object> entry : mStringPart.entrySet()) {
            entity.addTextBody(entry.getKey(), ""+entry.getValue());
            Log.e(entry.getKey()+"",":"+entry.getValue());

        }
    }

    @Override
    public String getBodyContentType() {
        return httpentity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            httpentity = entity.build();
            httpentity.writeTo(bos);
        } catch (IOException e) {
            if (mListener != null) {
                mListener.removeProgress(true);
                mListener.errorResponse(null, flag);
            }
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        Log.e("Response",""+new String(response.data));
        Log.e("StatusCode", "" + response.statusCode);
        try{
            this.mResponseString = "" + new String(response.data);
        }catch (NullPointerException  e){
            e.printStackTrace();
            mResponseString = "";
        }
        return Response.success("Uploaded", getCacheEntry());
    }

    @Override
    protected void deliverResponse(String response) {
        Log.e("deliverResponse", "deliverResponse" + response);
        try {
            mListener.successResponse(""+mResponseString, flag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ByteArrayBody getCompressedImage(String path){

        Bitmap imageBitmap = getBitmap(path);

        if(imageBitmap !=null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ByteArrayBody bab;
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] data = bos.toByteArray();
            bab = new ByteArrayBody(data, "" + System.currentTimeMillis() + "profilepic.jpg");
            return bab;
        }
        return null;
    }

    private Bitmap getBitmap(final String image) {
        ImageLoader imageloader;
        imageloader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        imageloader.init(ImageLoaderConfiguration.createDefault(this.context));
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY).build();
        return imageloader.loadImageSync("file://"+image,options);

    }


    /*private Bitmap getBitmap(String path){

        if(path != null){




            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return  BitmapFactory.decodeFile(path, options);
        }
        return null;
    }*/
}