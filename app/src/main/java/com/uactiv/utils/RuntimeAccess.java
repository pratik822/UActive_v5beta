package com.uactiv.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.uactiv.controller.IRunTimePermission;

import java.util.ArrayList;
import java.util.List;


public class RuntimeAccess {
    String TAG="RuntimeAccess";
    private Activity activity;
    private IRunTimePermission mInterfaceRunTimePermission;
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 123;

    public RuntimeAccess(Activity activity, IRunTimePermission mInterfaceRunTimePermission) {
        this.activity = activity;
        this.mInterfaceRunTimePermission = mInterfaceRunTimePermission;
    }


    /**
     * Used to check multiple permissions and add permissions
     */
    public void grandPermission() {
        Log.e(TAG,"grandPermission");
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermissions(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsNeeded.add("Access Coarse Location");
        if (!addPermissions(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("Access Fine Location");
      if (!addPermissions(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
          permissionsNeeded.add("Read External Storage");
        if (!addPermissions(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write External Storage");

        Log.e("permissionsList.size()", ""+permissionsList.size());

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                }
                            }
                        });
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
            return;
        } else {
            Log.e("Permission granted Else", "Called");
            mInterfaceRunTimePermission.onSuccess(0);
        }
    }


    /**
     * Used to check and add permission
     */
    private boolean addPermissions(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!activity.shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        }
        return true;
    }


    /**
     * Used to show dialog
     */
    public void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(activity);
        mAlertDialog.setMessage(message);
        mAlertDialog.setPositiveButton("OK", okListener);
        mAlertDialog.setNegativeButton("Cancel", null);
        mAlertDialog.create();
        mAlertDialog.show();
    }


//    /***/
//    public void gotoAppSetting() {
//        Intent intent = new Intent();
//        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
//        intent.setData(uri);
//        activity.startActivity(intent);
//    }
}
