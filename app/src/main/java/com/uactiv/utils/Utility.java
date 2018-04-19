package com.uactiv.utils;


import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextPaint;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alexzh.circleimageview.CircleImageView;
import com.felipecsl.gifimageview.library.GifImageView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.uactiv.R;
import com.uactiv.application.UActiveApplication;
import com.uactiv.interfaces.OnAlertClickListener;
import com.uactiv.location.GPSTracker;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by doodleblue-25 on 19-08-2015.
 */
public class Utility {


    public static SimpleDateFormat dateFormatsdf = new SimpleDateFormat("EEE, dd MMM yyyy");
    public static byte[] bytes = null;
    static int position = 0;

    /**
     * null checker
     *
     * @param isNotNull
     * @return
     */

    public static boolean isNullCheck(String isNotNull) {

        if (isNotNull != null) {
            if (!isNotNull.equalsIgnoreCase("") && isNotNull.length() > 0) {
                if (!isNotNull.equalsIgnoreCase("null")) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * To show internetError message
     *
     * @param context
     */

    public static void showInternetError(Context context) {
        Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_LONG).show();
    }

    /**
     * To show message
     *
     * @param context
     */
    public static void showToastMessage(Context context, String message) {

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * date format conversion
     *
     * @param date
     * @return
     */


    public static String dateFormats(String date) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


            Date dat = dateFormat.parse(date);

            //SimpleDateFormat dateFormatsdf = new SimpleDateFormat("EEE, dd MMM yyyy");

            SimpleDateFormat dateFormatsdf = new SimpleDateFormat("dd MMM");

            String convertedDate = dateFormatsdf.format(dat);

            return convertedDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;


    }

    /**
     * date format conversion
     *
     * @param date
     * @return
     */


    public static String dateFormatWithFull(String date) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


            Date dat = dateFormat.parse(date);

            SimpleDateFormat dateFormatsdf = new SimpleDateFormat("EEE, dd MMM yyyy");

            String convertedDate = dateFormatsdf.format(dat);

            return convertedDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;


    }

    /**
     * time format String to 24 hrs format
     *
     * @param mtime
     * @return
     */


    public static String timeFormat24(String mtime) {

        Date time_date = null;
        String newString = "";
        try {
            time_date = new SimpleDateFormat("HH:MM:SS").parse(mtime);
            //  newString = new SimpleDateFormat("HH:mm").format(time_date);
            newString = new SimpleDateFormat("HH:mm").format(time_date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newString;
    }

    /**
     * Time format string to 12 hr foramt
     *
     * @param mtime
     * @return
     */


    public static String timeFormatChanage(String mtime) {

        String time = null;
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            final Date dateObj = sdf.parse(mtime);

            return time = new SimpleDateFormat("hh:mm a").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * UActive - Endtime before 30 min
     *
     * @param startTime
     * @return
     */

    public static boolean endTimelogic(String startTime, long gap) {

        String time = null;
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
            final Date start_time = sdf.parse(startTime);
            Calendar currTime = Calendar.getInstance();
            Date date = currTime.getTime();
            Date currentdate = sdf.parse(sdf.format(date));

            long start_diff = start_time.getTime() - (gap); // 30mins in milliseconds

            Log.d("Time Difference", ":" + gap);

            //long diffInTime = ((int) difference / 1000L / 60L);

            Log.e("TimeDifference", "" + (currentdate.getTime() >= start_diff));

            if (currentdate.getTime() >= start_diff) {
                return true;
            }

           /* return time = new SimpleDateFormat("hh:mm a").format(dateObj);*/
        } catch (final ParseException e) {
            e.printStackTrace();
        }


        return false;
    }


    /**
     * Returns true if the network connection is avail.
     *
     * @param _context
     * @return boolean
     */

    public static boolean isConnectingToInternet(Context _context) {
        ConnectivityManager cm = (ConnectivityManager) UActiveApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        }
        return false;
    }


    /**
     * Get DeviceAndroid ID
     *
     * @param mContext
     * @return
     */

    public static String getDeviceAndroidID(Context mContext) {

        String deviceId = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return deviceId;
    }


    /**
     * calculateAge from Date
     *
     * @param birthDate
     * @param isMsgShow
     * @return
     */

    public static String calculateAge(Date birthDate, Context context, boolean isMsgShow) {
        int years = 0;
        int months = 0;
        int days = 0;
        //create calendar object for birth day
        Calendar birthDay = Calendar.getInstance();
        birthDay.setTimeInMillis(birthDate.getTime());
        //create calendar object for current day
        long currentTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(currentTime);
        //Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthDay.get(Calendar.MONTH) + 1;
        //Get difference between months
        months = currMonth - birthMonth;
        //if month difference is in negative then reduce years by one and calculate the number of months.
        if (months < 0) {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
            years--;
            months = 11;
        }
        //Calculate the days
        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
        } else {
            days = 0;
            if (months == 12) {
                years++;
                months = 0;
            }
        }
        //Create new Age object
        //  Log.e("years", "" + years);
        String age = null;

        if (16 <= years && 19 >= years) {
            age = "Late Teens";
        } else if (20 <= years && 25 >= years) {
            age = "Early 20’s";
        } else if (26 <= years && 29 >= years) {
            age = "Late 20’s";
        } else if (30 <= years && 35 >= years) {
            age = "Early 30’s";
        } else if (36 <= years && 39 >= years) {
            age = "Late 30’s";
        } else if (40 <= years && 45 >= years) {
            age = "Early 40’s";
        } else if (46 <= years && 49 >= years) {
            age = "Late 40’s";
        } else if (50 <= years && 55 >= years) {
            age = "Early 50's";
        } else if (56 <= years && 59 >= years) {
            age = "Late 50's";
        } else if (60 <= years && 65 >= years) {
            age = "Early 60’s";
        } else if (66 <= years && 69 >= years) {
            age = "Late 60’s";
        } else if (70 <= years && 75 >= years) {
            age = "Early 70’s";
        } else if (76 <= years && 79 >= years) {
            age = "Late 70’s";
        } else if (80 <= years && 85 >= years) {
            age = "Early 80’s";
        } else if (86 <= years && 89 >= years) {
            age = "Late 80’s";
        } else if (90 <= years && 95 >= years) {
            age = "Early 90’s";
        } else if (96 <= years && 99 >= years) {
            age = "Late 90’s";
        } else if (years >= 100) {
            age = "above 100’s";
        } else {
            if (isMsgShow) {
                Utility.showToastMessage(context, "Your Not Eligible");
            }
        }

        return age;
    }


    /**
     * check ExternalStorage availability
     *
     * @return
     */


    private static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * update badges
     *
     * @param badge
     * @param imageType
     */

    public static void updateBadge(String badge, ImageView imageType) {

        switch (badge) {
            case "Rookie":
                imageType.setImageResource(R.drawable.batchicon_a);
                break;
            case "Contender":
                imageType.setImageResource(R.drawable.batchicon_b);
                break;
            case "All Star":
                imageType.setImageResource(R.drawable.batchicon_c);
                break;
            case "Ace":
                imageType.setImageResource(R.drawable.batchicon_d);
                break;
            case "Champion":
                imageType.setImageResource(R.drawable.batchicon_e);
                break;
            case "Hercules/Bia":
                imageType.setImageResource(R.drawable.batchicon_f);
                break;
            case "Hercules":
                imageType.setImageResource(R.drawable.batchicon_f);
            default:

                break;
        }

    }

    /**
     * UActive - Event Start Time 30 mins logic to validate date.
     *
     * @param selectedDate
     * @return
     */


    public static boolean isVaildrequestDateSetLogic(Date selectedDate) {

        boolean isValid = false;

        try {
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            Date currentdate = sdf.parse(sdf.format(date));


            if (selectedDate.after(currentdate)) {
                // System.out.println("selected  is after current date");
                Log.e("Acceptable", "Yes");
                isValid = true;
            }
            if (selectedDate.before(currentdate)) {
                //  System.out.println("selected is before current date");
                isValid = false;
            }
            if (selectedDate.equals(currentdate)) {
                // System.out.println("selected is equal current date");
                isValid = true;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return isValid;
    }

    /**
     * UActive - Event Start Time 30 mins logic to validate date with time
     *
     * @param context
     * @param selectedtime
     * @param selecteddate
     * @return
     */


    public static boolean requestTimelogic(Context context, long selectedtime, Date selecteddate) {

        boolean isVaildDate = false;

        Calendar currTime = Calendar.getInstance();
        int hour = currTime.get(Calendar.HOUR_OF_DAY);
        int minut = currTime.get(Calendar.MINUTE);
        Date currDate = currTime.getTime();
        long difference = selectedtime - currTime.getTimeInMillis();
        long diffInTime = ((int) difference / 1000L / 60L);
        Log.e("Difference", "" + diffInTime);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            Date currentDate = sdf.parse(sdf.format(currDate));

            Log.e("CurrentDate", "" + currentDate);
            Log.e("SelectedDate", "" + selecteddate);

            if (selecteddate.equals(currentDate)) {

                Log.e("datesEqual", "" + diffInTime);

                //Changed from 60 min to 30 mins.

                if (diffInTime >= 30) {

                    isVaildDate = true;

                } else {
                    isVaildDate = false;
                }
            } else if (selecteddate.after(currentDate)) {

                Log.e("after", "" + diffInTime);

                isVaildDate = true;

            } else {
                isVaildDate = false;
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return isVaildDate;
    }


    /**
     * Date validation for choosing end time
     *
     * @param selecteddate
     * @param endtime
     * @return
     */


    public static boolean isvalidEndTime(Date selecteddate, Date endtime) {


        try {

            if (endtime.equals(selecteddate)) {

                return true;

            } else if (endtime.after(selecteddate)) {

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Volley imageLoader
     *
     * @param url
     * @param circleImageView
     *//*

    public static void SetUrlImage(String url, CircleImageView circleImageView) {
        UActiveApplication.imageLoader.get(url, ImageLoader.getImageListener(circleImageView, R.drawable.ic_profile, R.drawable.ic_profile));
    }

    *//**
     * Volley imageLoader
     *
     * @param url
     * @param circleImageView
     *//*

    public static void SetUrlImage(String url, ImageView circleImageView) {
        UActiveApplication.imageLoader.get(url, ImageLoader.getImageListener(circleImageView, R.drawable.ic_profile, R.drawable.ic_profile));
    }*/

    /**
     * get image for circleImageView through UniversalImageLoader
     *
     * @param mcontext
     * @param url
     * @param circleImageView
     */

    public static void setImageUniversalLoaders(Context mcontext, String url, CircleImageView circleImageView) {
        ImageAware imageAware = new ImageViewAware(circleImageView, false);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_profile)
                .showImageOnLoading(R.drawable.ic_profile)
                .showImageOnFail(R.drawable.ic_profile)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new SimpleBitmapDisplayer())
                .build();
        UActiveApplication.getInstance().getImageLoader().displayImage(url, imageAware, options);

    }


    public static void setImageUniversalLoader(Context context, String url, final com.uactiv.views.CircularImageViews imageView) {
        ImageAware imageAware = new ImageViewAware(imageView, false);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_profile)
                .showImageOnLoading(R.drawable.ic_profile)
                .showImageOnFail(R.drawable.ic_profile)
                .resetViewBeforeLoading(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        UActiveApplication.getInstance().getImageLoader().displayImage(url, imageAware, options);


    }

    /**
     * get image for imageView through UniversalImageLoader
     *
     * @param mcontext
     * @param url
     * @param imageView
     */

    public static void setImageUniversalLoader(Context mcontext, String url, final ImageView imageView) {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_profile)
                .showImageOnLoading(R.drawable.ic_profile)
                .showImageOnFail(R.drawable.ic_profile)
                .resetViewBeforeLoading(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        UActiveApplication.getInstance().getImageLoader().displayImage(url, imageView, options);


    }
    public static void setcircularImageUniversalLoader(Context mcontext, String url,   de.hdodenhof.circleimageview.CircleImageView imageView) {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_profile)
                .showImageOnLoading(R.drawable.ic_profile)
                .showImageOnFail(R.drawable.ic_profile)
                .resetViewBeforeLoading(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        UActiveApplication.getInstance().getImageLoader().displayImage(url, imageView, options);


    }

    /**
     * scheckvalidLocation
     *

     * @param gpsTracker
     * @return
     */


    public static boolean ischeckvalidLocation(GPSTracker gpsTracker) {

        if (gpsTracker != null) {
            if (gpsTracker.getLatitude() == 0.0 && gpsTracker.getLongitude() == 0.0) {
                showToastMessage(UActiveApplication.getInstance().getApplicationContext(), "We can't get your Location");
                return false;
            } else {
                return true;
            }
        }

        return false;
    }


    /**
     * get rating
     *
     * @param value
     * @param divider
     * @return
     */

    public static BigDecimal round(int value, int divider) {

        BigDecimal bg1, bg2, bg3;

        if (value > 0 && divider > 0) {


            bg1 = new BigDecimal(value);
            bg2 = new BigDecimal(divider);
            // divide bg1 with bg2 with 3 scale
            return bg1.divide(bg2, 1, RoundingMode.DOWN);

        }

        return new BigDecimal(0);
    }


    public static void showProgressDialog(Context context, GifImageView gifImageView) {

        try {
            InputStream is = context.getAssets().open("loader.gif");
            bytes = new byte[is.available()];
            is.read(bytes);
            is.close();
            gifImageView.setBytes(bytes);
            // gifImageView.startAnimation();
        } catch (IOException e) {
            Log.e("showProgressDialog", "" + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e1) {
            Log.e("showProgressDialog", "" + e1.getMessage());
            e1.printStackTrace();
        }
    }


    private static AnimatorSet getAnimation(Context context, final ArrayList<ImageView> target) {
        final AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.flip_animation);
        target.get(0).setVisibility(View.VISIBLE);
        set.setTarget(0);
        set.start();
        return set;
    }

    public static void stopFlipAnimation(Context context, ImageView target) {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.flip_animation);
        set.setTarget(target);
        set.end();
    }


    public static TextPaint getTextPaint(Context context) {
        TextPaint title = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        title.setTextSize(context.getResources().getDimension(R.dimen.dp_62));
        title.setColor(context.getResources().getColor(R.color.colorPrimary));
        title.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Brandon_bld.otf"));
        return title;
    }


    public static void setScreenTracking(Context context, String trackingId) {
        Tracker mTracker = UActiveApplication.getInstance().getDefaultTracker();
        if(!trackingId.contains("com.uactiv.activity")){
            mTracker.setScreenName(trackingId);
            mTracker.enableAdvertisingIdCollection(true);
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }

    }


    public static void setEventTracking(Context context,String category, String trackingId) {
        Tracker mTracker = UActiveApplication.getInstance().getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(trackingId)
                .build());
    }

    /**Nirmal*/
    /**
     * json response string to fetch records through gson
     *
     * @param json
     * @param classType
     * @param <T>
     * @return
     */
    public static <T> T getModelFromJson(String json, Class<T> classType) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
        return gson.fromJson(json, classType);
    }

    public static <T> String getJsonFromModel(T object, Class<T> classType) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
        //Log.i("Gson String", ""+gson.toJson(object,classType));
        return gson.toJson(object, classType);
    }


    /***/
    public static String[] parseAndGetURLsfromText(String text) {
        List<String> links = new ArrayList<>();
        Matcher m = Patterns.WEB_URL.matcher(text);
        while (m.find()) {
            String url = m.group();
            Log.d("UActiv Utility", "URL extracted: " + url);
            links.add(url);
        }
        return links.toArray(new String[links.size()]);
    }


    public static void showAlertForExistingTimeSlot(Context context, String message, final OnAlertClickListener onAlertClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                if (onAlertClickListener != null) {
                    onAlertClickListener.onClickPositive();

                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
                if (onAlertClickListener != null) {
                    onAlertClickListener.onClickNegative();
                }
            }
        });
        AlertDialog dialog = builder.create();
        try {
              dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static String getDateFromMilliSeconds(long dateInString) {
        /*2017-01-17 09:41:45*/
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateInString);
        return AppConstants.NOTIFICATION_DATE_SORT_FORMAT.format(calendar.getTime());
    }


}
