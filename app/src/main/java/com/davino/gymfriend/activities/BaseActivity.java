package com.davino.gymfriend.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.davino.gymfriend.R;
import com.davino.gymfriend.util.Constants;

/**
 * Created by davin on 03/06/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyPermissions();
    }

    private int getPermissionCode(String permission){
        switch(permission){
            case Manifest.permission.ACCESS_FINE_LOCATION:
                return Constants.REQUEST_ID_ACCESS_FINE_LOCATION;
            case Manifest.permission.INTERNET:
                return Constants.INTERNET;
            default: throw new IndexOutOfBoundsException();
        }
    }

    private void verifyPermissions() {
        Log.i(TAG, "Checking permissions");
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
        };

        for(String permission : permissions){
            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        getPermissionCode(permission));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_ID_ACCESS_FINE_LOCATION:
                checkPermissionRequested(grantResults);
                break;
            case Constants.INTERNET:
                checkPermissionRequested(grantResults);
                break;
        }
    }

    private void checkPermissionRequested(int[] grantResults){
        if (!(grantResults.length > 0) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showToast(getString(R.string.err_permission_denied));
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    protected void showToast(String message){
        showToast(message, Toast.LENGTH_LONG);
    }

    protected void showToast(String message, int toastLength){
        Toast.makeText(getApplicationContext(), message, toastLength).show();
    }

    /**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     *               - pass null if you don't want icon
     * */
    public void showAlertDialog(Context context, String title, String message,
                                Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        if(status != null)
            // Setting alert dialog icon
            alertDialog.setIcon((status) ? R.drawable.ic_done_black_24dp : R.drawable.ic_highlight_off_black_24dp);

        // Setting OK Button
        alertDialog.setButton(1, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    public void showSnackBar(String message){
        Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
