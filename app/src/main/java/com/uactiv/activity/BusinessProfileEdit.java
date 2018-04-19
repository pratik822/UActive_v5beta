package com.uactiv.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.api.account.register.RegisterUserClientService;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.User;
import com.belladati.httpclientandroidlib.entity.mime.HttpMultipartMode;
import com.belladati.httpclientandroidlib.entity.mime.MultipartEntityBuilder;
import com.belladati.httpclientandroidlib.entity.mime.content.ByteArrayBody;
import com.belladati.httpclientandroidlib.entity.mime.content.ContentBody;
import com.belladati.httpclientandroidlib.impl.client.SystemDefaultHttpClient;
import com.belladati.httpclientandroidlib.util.EntityUtils;
import com.felipecsl.gifimageview.library.GifImageView;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.soundcloud.android.crop.Crop;
import com.uactiv.R;
import com.uactiv.adapter.SpinnerAdapter;
import com.uactiv.controller.CreatePickupNotifier;
import com.uactiv.controller.IRunTimePermission;
import com.uactiv.controller.ResponseListener;
import com.uactiv.network.ImageUpLoader;
import com.uactiv.network.ResponseHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.RuntimeAccess;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class BusinessProfileEdit extends Activity implements OnClickListener, AppConstants.SharedConstants, AppConstants.urlConstants, ResponseListener, CreatePickupNotifier, IRunTimePermission, AdapterView.OnItemSelectedListener {

    private String TAG = getClass().getSimpleName();
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int PICK_GALLERY_IMAGE_REQUEST_CODE = 200;
    public static CreatePickupNotifier addressPicker = null;
    private TextView tvSave, tvCancel;
    private EditText editDetail, editBusinessName;
    private Spinner spinSkating, spinYoga, spinRunning, spinSquash, spinFive, spinSix;
    private CustomEditText etPhoneNumber, etAddress, etLandineNumber;
    private SpinnerAdapter spinSkateAdapter;
    private ArrayList<String> activitylist = new ArrayList<>();
    private ImageView prfile_pic = null;
    private ImageView mImageSelectPicture = null;
    private GifImageView progressWheel = null;
    private String path;
    private double lat = 0.0;
    private double lng = 0.0;
    private Uri mImageCaptureUri, mCropedImagePath;
    MultipartEntityBuilder builder;
    com.belladati.httpclientandroidlib.client.HttpClient httpClient;
    com.belladati.httpclientandroidlib.client.methods.HttpPost httpPost;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_profile_edit);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        addressPicker = this;
        loadSpinnerValues();
        initializeValues();
        Utility.setScreenTracking(this, AppConstants.SCREEN_TRACKING_ID_EDITBUSINESSPROFILE);
    }


    /**
     * load spinner values
     */
    private void loadSpinnerValues() {
        activitylist.add("Select activity");
        try {
            String skillSet = SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, Api_skill_list);
            JSONArray skillArray = new JSONArray(skillSet);
            if (skillArray != null) {
                try {
                    if (skillArray.length() > 0 && activitylist != null) {
                        for (int k = 0; k < skillArray.length(); k++) {
                            activitylist.add(skillArray.optJSONObject(k).optString("activity"));
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * bind view and show existing user info
     */

    private void initializeValues() {

        progressWheel = (GifImageView) findViewById(R.id.gifLoader);
        Utility.showProgressDialog(BusinessProfileEdit.this, progressWheel);
        tvSave = (TextView) findViewById(R.id.tvSave);
        tvCancel = (TextView) findViewById(R.id.tvCancel);
        prfile_pic = (ImageView) findViewById(R.id.imageView1);
        mImageSelectPicture = (ImageView) findViewById(R.id.imageView2);
        mImageSelectPicture.setOnClickListener(this);

        etPhoneNumber = (CustomEditText) findViewById(R.id.etPhoneNumber);
        etLandineNumber = (CustomEditText) findViewById(R.id.etLandine);
        etAddress = (CustomEditText) findViewById(R.id.etAddress);
        etAddress.setOnClickListener(this);

        editDetail = (EditText) findViewById(R.id.editDetail);
        editBusinessName = (EditText) findViewById(R.id.etBusinessName);

        spinSkating = (Spinner) findViewById(R.id.spinSkating);
        spinYoga = (Spinner) findViewById(R.id.spinYoga);
        spinRunning = (Spinner) findViewById(R.id.spinRunning);
        spinSquash = (Spinner) findViewById(R.id.spinRunnin4);
        spinFive = (Spinner) findViewById(R.id.spinFive);
        spinSix = (Spinner) findViewById(R.id.spinSix);

        spinSkating.setOnItemSelectedListener(this);
        spinYoga.setOnItemSelectedListener(this);
        spinRunning.setOnItemSelectedListener(this);
        spinSquash.setOnItemSelectedListener(this);
        spinFive.setOnItemSelectedListener(this);
        spinSix.setOnItemSelectedListener(this);


        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, latitude))
                && Utility.isNullCheck(SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, longitude))) {
            lat = Double.parseDouble(SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, latitude));
            lng = Double.parseDouble(SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, longitude));

        }

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, businessName))) {
            editBusinessName.setText(SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, businessName));
        }
        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, about_yourself))) {
            editDetail.setText(SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, about_yourself));
        }

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, phoneno))) {
            etPhoneNumber.setText(SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, phoneno));
        }

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, landline))) {
            etLandineNumber.setText(SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, landline));
        }

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, address))) {
            etAddress.setText(SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, address));
        }


        String imageUrl = SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, image);
        if (Utility.isNullCheck(imageUrl)) {
            Utility.setImageUniversalLoader(this, imageUrl, prfile_pic);
        }


        //spinner values
        spinSkateAdapter = new SpinnerAdapter(this, R.layout.custom_spinner_activity, activitylist, 0);
        spinSkating.setAdapter(spinSkateAdapter);

        spinYoga.setAdapter(spinSkateAdapter);
        spinRunning.setAdapter(spinSkateAdapter);
        spinSquash.setAdapter(spinSkateAdapter);
        spinFive.setAdapter(spinSkateAdapter);
        spinSix.setAdapter(spinSkateAdapter);

        //onclick
        tvSave.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        String jsonArray = SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, skills);

        try {
            JSONArray activity = new JSONArray(jsonArray);

            for (int i = 0; i < activity.length(); i++) {

                JSONObject obj = activity.getJSONObject(i);
                String activityText = obj.optString("activity");
                if (i == 0) {
                    spinSkating.setSelection(spinSkateAdapter.getPosition(activityText));
                }
                if (i == 1) {
                    spinYoga.setSelection(spinSkateAdapter.getPosition(activityText));
                }
                if (i == 2) {
                    spinRunning.setSelection(spinSkateAdapter.getPosition(activityText));
                }
                if (i == 3) {
                    spinSquash.setSelection(spinSkateAdapter.getPosition(activityText));
                }
                if (i == 4) {
                    spinFive.setSelection(spinSkateAdapter.getPosition(activityText));
                }
                if (i == 5) {
                    spinSix.setSelection(spinSkateAdapter.getPosition(activityText));
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.imageView2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    RuntimeAccess access = new RuntimeAccess(this, this);
                    access.grandPermission();
                } else {
                    selectImage();
                }
                break;
            case R.id.tvSave:

                String selectedSkill = null;

                if (editBusinessName.length() > 0) {

                    if (spinSkating.getSelectedItemPosition() != 0 || spinYoga.getSelectedItemPosition() != 0 ||
                            spinRunning.getSelectedItemPosition() != 0 || spinSquash.getSelectedItemPosition() != 0 ||
                            spinFive.getSelectedItemPosition() != 0 || spinSix.getSelectedItemPosition() != 0) {

                        selectedSkill = spinSkating.getSelectedItem().toString();

                        if (!spinYoga.getSelectedItem().toString().trim().equals(selectedSkill) ||
                                spinYoga.getSelectedItem().toString().trim().equals("Select activity")) {

                            selectedSkill = selectedSkill + "," + spinYoga.getSelectedItem().toString();

                            String[] tem = selectedSkill.split(",");

                            if (!spinRunning.getSelectedItem().toString().trim().equals(tem[0]) &&
                                    !spinRunning.getSelectedItem().toString().trim().equals(tem[1]) ||
                                    spinRunning.getSelectedItem().toString().trim().equals("Select activity")) {

                                selectedSkill = selectedSkill + "," + spinRunning.getSelectedItem().toString();

                                String[] tem1 = selectedSkill.split(",");

                                if (!spinSquash.getSelectedItem().toString().trim().equals(tem1[0]) &&
                                        !spinSquash.getSelectedItem().toString().trim().equals(tem1[1]) &&
                                        !spinSquash.getSelectedItem().toString().trim().equals(tem1[2]) ||
                                        spinSquash.getSelectedItem().toString().trim().equals("Select activity")) {

                                    selectedSkill = selectedSkill + "," + spinSquash.getSelectedItem().toString();

                                    String[] fourthSpilit = selectedSkill.split(",");

                                    //Validate four'th
                                    if (!spinFive.getSelectedItem().toString().trim().equals(fourthSpilit[0]) &&
                                            !spinFive.getSelectedItem().toString().trim().equals(fourthSpilit[1]) &&
                                            !spinFive.getSelectedItem().toString().trim().equals(fourthSpilit[2]) &&
                                            !spinFive.getSelectedItem().toString().trim().equals(fourthSpilit[3]) ||
                                            spinFive.getSelectedItem().toString().trim().equals("Select activity")) {

                                        selectedSkill = selectedSkill + "," + spinFive.getSelectedItem().toString();

                                        String[] fifithSpilit = selectedSkill.split(",");

                                        //Validate fifth'th

                                        if (!spinSix.getSelectedItem().toString().trim().equals(fifithSpilit[0]) &&
                                                !spinSix.getSelectedItem().toString().trim().equals(fifithSpilit[1]) &&
                                                !spinSix.getSelectedItem().toString().trim().equals(fifithSpilit[2]) &&
                                                !spinSix.getSelectedItem().toString().trim().equals(fifithSpilit[3]) &&
                                                !spinSix.getSelectedItem().toString().trim().equals(fifithSpilit[4]) ||
                                                spinSix.getSelectedItem().toString().trim().equals("Select activity")) {

                                            selectedSkill = selectedSkill + "," + spinSix.getSelectedItem().toString();
                                            new saveTask(selectedSkill).execute();

                                        } else {
                                            Utility.showToastMessage(BusinessProfileEdit.this, "Please select another activity");
                                        }

                                    } else {
                                        Utility.showToastMessage(BusinessProfileEdit.this, "Please select another activity");
                                    }
                                } else {
                                    Utility.showToastMessage(BusinessProfileEdit.this, "Please select another activity");
                                }


                            } else {
                                Utility.showToastMessage(BusinessProfileEdit.this, "Please select another activity");
                            }

                        } else {
                            Utility.showToastMessage(BusinessProfileEdit.this, "Please select another activity");
                        }


                    } else {
                        Utility.showToastMessage(BusinessProfileEdit.this, "Please select activity");
                    }

                } else {

                    Utility.showToastMessage(BusinessProfileEdit.this, "Please enter Business Name");
                }


                break;
            case R.id.tvCancel:
                onBackPressed();
                break;
            case R.id.etAddress:
                startActivity(new Intent(getApplicationContext(), PickChoose.class).putExtra("isFromEdit", true)
                        .putExtra("latitude", lat).putExtra("longitude", lng));
                break;

            default:
                break;
        }


    }


    class saveTask extends AsyncTask<Void, Void, Void> {
        String myskills;

        public saveTask(String skills) {
            this.myskills = skills;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressWheel.startAnimation();
            progressWheel.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                doEditProfile(getFilteredSkill(myskills));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressWheel.stopAnimation();
            progressWheel.setVisibility(View.GONE);
            Utility.showToastMessage(BusinessProfileEdit.this, "Your profile is updated");
        }
    }

    /**
     * edit profile
     *
     * @param selectedSkill selected skill
     */
    private void doEditProfile(String selectedSkill) throws IOException {

        if (Utility.isConnectingToInternet(BusinessProfileEdit.this)) {

            if (lat != 0.0 && lng != 0.0) {

          /*      try {
                    JSONObject param = new JSONObject();
                    param.put("about_yourself", editDetail.getText().toString().trim());
                    param.put("skills", selectedSkill);
                    param.put("latitude", "" + lat);
                    param.put("longitude", "" + lng);
                    param.put("user_type", "1");
                    param.put("landline", etLandineNumber.getText().toString().trim());
                    param.put("business_name", editBusinessName.getText().toString().trim());
                    param.put("phone_no", etPhoneNumber.getText().toString().trim());
                    param.put("address", etAddress.getText().toString());
                    param.put("iduser", SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, userId));
                    param.put("levels", "");
                    Map<String, Object> stringMap = jsonToMap(param);
                    if (stringMap != null) {
                        imageUpload(stringMap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                httpClient = new SystemDefaultHttpClient();
                httpPost = new com.belladati.httpclientandroidlib.client.methods.HttpPost(AppConstants.getBaseUrl(SharedPref.getInstance()
                        .getBooleanValue(BusinessProfileEdit.this, isStaging)) + editprofile);
                builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                builder.addTextBody("about_yourself", editDetail.getText().toString().trim());
                builder.addTextBody("skills", selectedSkill);
                builder.addTextBody("latitude", "" + lat);
                builder.addTextBody("longitude", "" + lng);
                builder.addTextBody("user_type", "1");
                builder.addTextBody("landline", etLandineNumber.getText().toString().trim());
                builder.addTextBody("business_name", editBusinessName.getText().toString().trim());
                builder.addTextBody("phone_no", etPhoneNumber.getText().toString().trim());
                builder.addTextBody("address", etAddress.getText().toString());
                builder.addTextBody("iduser", SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, userId));
                builder.addTextBody("levels", "");


                try {
                    file = new File(path);
                    builder.addPart("image[" + (0) + "]", (ContentBody) getCompressedImage(file.getAbsolutePath()));
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }


                //  imageUpload(param);
                httpPost.setEntity(builder.build());
                com.belladati.httpclientandroidlib.HttpResponse response = httpClient.execute(httpPost);
                com.belladati.httpclientandroidlib.HttpEntity entity = response.getEntity();
                String responce = EntityUtils.toString(entity, "UTF-8");

                Log.d("mynewresp", new Gson().toJson(responce));
                if (responce != null) {
                    try {
                        final JSONObject object = new JSONObject(responce);
                        if (object.optString(resultcheck).equals(KEY_TRUE)) {
                            ResponseHandler.getInstance().storeProfileInfo(BusinessProfileEdit.this, object);
                            Intent pic_change = new Intent(AppConstants.ACTION_PROFILE_PICTURE_CHANGED);
                            LocalBroadcastManager.getInstance(BusinessProfileEdit.this).sendBroadcast(pic_change);
                            updateProfileInfoInApplozic();

                            finish();

                        } else {
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    Utility.showToastMessage(BusinessProfileEdit.this, object.optString(KEY_MSG));
                                }
                            });

                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                } else {

                    Toast.makeText(BusinessProfileEdit.this, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
                }


            } else {
                Toast.makeText(BusinessProfileEdit.this, "Please select location!", Toast.LENGTH_SHORT).show();
            }

        } else {
            Utility.showInternetError(BusinessProfileEdit.this);
        }
    }


    /**
     * get selected skill as string
     *
     * @return
     */

    private Bitmap getBitmap(final String image) {
        ImageLoader imageloader;
        imageloader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        imageloader.init(ImageLoaderConfiguration.createDefault(BusinessProfileEdit.this));
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY).build();
        return imageloader.loadImageSync("file://" + image, options);

    }

    private ByteArrayBody getCompressedImage(String path) {

        Bitmap imageBitmap = getBitmap(path);

        if (imageBitmap != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ByteArrayBody bab;
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] data = bos.toByteArray();
            bab = new ByteArrayBody(data, "" + System.currentTimeMillis() + "profilepic.jpg");
            return bab;
        }
        return null;
    }

    private String getFilteredSkill(String selectedSkill) {

        String finalSelectedSkill = "";
        if (selectedSkill != null) {

            String[] skill = selectedSkill.split(",");

            if (skill != null && skill.length > 0) {

                for (int i = 0; i < skill.length; i++) {

                    if (skill[i] != null && !TextUtils.isEmpty(skill[i]) && !skill[i].equals("Select activity")) {

                        finalSelectedSkill = finalSelectedSkill + "," + skill[i];

                    }
                }
            }
        }
        return finalSelectedSkill.substring(1);
    }


    /**
     * upload images to server
     *
     * @param param values
     */

    private void imageUpload(Map<String, Object> param) {
        //Extras
        param.put("iduser", SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, userId));
        if (Utility.isNullCheck(param.toString())) {
            ImageUpLoader imageUpLoader = new ImageUpLoader(BusinessProfileEdit.this);
            imageUpLoader.setFile(path);
            imageUpLoader.getResponse(AppConstants.getBaseUrl(SharedPref.getInstance()
                    .getBooleanValue(BusinessProfileEdit.this, isStaging))
                    + editprofile, this, param, 0);

        }
    }


    /**
     * convert json object to hasmap
     *
     * @param json input
     * @return
     * @throws JSONException json exception if not valid json
     */
    public Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if (json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }


    /**
     * convert json object to hasmap
     *
     * @param object input
     * @return
     * @throws JSONException json exception if not valid json
     */
    public Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }


    /**
     * convert jsonarray to list
     *
     * @param array input array
     * @return
     * @throws JSONException exception
     */
    public List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }


    @Override
    public void onBackPressed() {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
    }


    /**
     * show choose image alert
     */

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        final String second = String.valueOf(System.currentTimeMillis());
        AlertDialog.Builder builder = new AlertDialog.Builder(BusinessProfileEdit.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    Utility.setEventTracking(BusinessProfileEdit.this,"", AppConstants.EVENT_TRACKING_ID_EDIT_PROFILE);

                    try {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                                "tmp_avatar_" + second + ".jpg"));
                        try {

                            mCropedImagePath = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                                    "crop_tmp_avatar_" + second + ".jpg"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dialog.cancel();

                } else if (items[item].equals("Choose from Library")) {
                    Utility.setEventTracking(BusinessProfileEdit.this,"", AppConstants.EVENT_TRACKING_ID_EDIT_PROFILE);
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_GALLERY_IMAGE_REQUEST_CODE);
                    try {

                        mCropedImagePath = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                                "crop_tmp_avatar_" + second + ".jpg"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                path = mImageCaptureUri.getPath();

                File file = new File(path);

                if (file.exists()) {
                    Crop.of(Uri.fromFile(file), mCropedImagePath)
                            .withAspect(AppConstants.CROP_WIDTH, AppConstants.CROP_HEIGHT)
                            .start(this);
                }

            } else if (resultCode == RESULT_CANCELED) {

                Toast.makeText(BusinessProfileEdit.this,
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {

                Toast.makeText(BusinessProfileEdit.this,
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        }
        switch (requestCode) {

            case PICK_GALLERY_IMAGE_REQUEST_CODE:

                if (data != null) {

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    if (picturePath != null) {

                        path = picturePath;
                        File file = new File(path);
                        if (file.exists()) {
                            Crop.of(Uri.fromFile(file), mCropedImagePath)
                                    .withAspect(AppConstants.CROP_WIDTH, AppConstants.CROP_HEIGHT)
                                    .start(this);
                        }
                    }
                }

                break;
            case Crop.REQUEST_CROP:

                if (resultCode == RESULT_OK) {

                    if (Crop.getOutput(data) != null) {
                        File file = new File(mCropedImagePath.getPath());
                        if (file.exists() && file != null) {
                            Utility.setImageUniversalLoader(BusinessProfileEdit.this, Crop.getOutput(data).toString(), prfile_pic);
                            path = file.getAbsolutePath();
                        } else {
                            Toast.makeText(BusinessProfileEdit.this,
                                    "Sorry! Failed to update image", Toast.LENGTH_SHORT)
                                    .show();
                            path = "";
                        }

                    } else {
                        path = ""; //To clear selected path
                    }
                }
                break;

            default:
                break;


        }

    }


    @Override
    public void successResponse(String successResponse, int flag) {
        switch (flag) {
            case 0:
                if (successResponse != null) {
                    try {
                        JSONObject object = new JSONObject(successResponse);
                        if (object.optString(resultcheck).equals(KEY_TRUE)) {
                            ResponseHandler.getInstance().storeProfileInfo(BusinessProfileEdit.this, object);
                            Intent pic_change = new Intent(AppConstants.ACTION_PROFILE_PICTURE_CHANGED);
                            LocalBroadcastManager.getInstance(BusinessProfileEdit.this).sendBroadcast(pic_change);
                            updateProfileInfoInApplozic();
                            Utility.showToastMessage(BusinessProfileEdit.this, "Your profile is updated");
                            finish();

                        } else {

                            Utility.showToastMessage(BusinessProfileEdit.this, object.optString(KEY_MSG));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                } else {

                    Toast.makeText(BusinessProfileEdit.this, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;

        }

    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag) {

    }

    @Override
    public void errorResponse(String errorResponse, int flag) {

    }

    @Override
    public void removeProgress(Boolean hideFlag) {

    }



    /* display user selected business location  on view
   */

    @Override
    public void mapViewNotifier(LatLng latLng, String Address, String isBooked, String idBusiness, int isBusinessLocatiom) {
        if (latLng != null && Address != null) {
            lat = latLng.latitude;
            lng = latLng.longitude;
            etAddress.setText(String.valueOf(Address));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RuntimeAccess.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

                        ) {
                    onSuccess(0);
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onSuccess(int milis) {
        selectImage();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()) {
            case R.id.spinSkating:
                Utility.setScreenTracking(this, AppConstants.EVENT_TRACKING_ID_EDITUSERACTIVITIES);
                break;
            case R.id.spinYoga:
                Utility.setScreenTracking(this, AppConstants.EVENT_TRACKING_ID_EDITUSERACTIVITIES);
                break;
            case R.id.spinRunning:
                Utility.setScreenTracking(this, AppConstants.EVENT_TRACKING_ID_EDITUSERACTIVITIES);
                break;
            case R.id.spinRunnin4:
                Utility.setScreenTracking(this, AppConstants.EVENT_TRACKING_ID_EDITUSERACTIVITIES);
                break;
            case R.id.spinFive:
                Utility.setScreenTracking(this, AppConstants.EVENT_TRACKING_ID_EDITUSERACTIVITIES);
                break;
            case R.id.spinSix:
                Utility.setScreenTracking(this, AppConstants.EVENT_TRACKING_ID_EDITUSERACTIVITIES);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void updateProfileInfoInApplozic() {


        Log.d(TAG, "profile image : " + SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, image));

        final RegisterUserClientService registerUserClientService = new RegisterUserClientService(BusinessProfileEdit.this);
        final User user = registerUserClientService.getUserDetail();
        user.setDisplayName(SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, firstname));
        user.setImageLink(SharedPref.getInstance().getStringVlue(BusinessProfileEdit.this, image));

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    registerUserClientService.updateRegisteredAccount(user);
                    MobiComUserPreference mobiComUserPreference = MobiComUserPreference.getInstance(getApplicationContext());
                    mobiComUserPreference.setDisplayName(user.getDisplayName());
                    mobiComUserPreference.setImageLink(user.getImageLink());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

}

