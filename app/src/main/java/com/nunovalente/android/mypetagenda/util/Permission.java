package com.nunovalente.android.mypetagenda.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class Permission {

    public static void validatePermissions(ArrayList<String> permissions, Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {

            ArrayList<String> listPermissions = new ArrayList<>();

            //Goes through the list to see if permissions were granted
            for (String permission : permissions) {
                boolean hasPermission = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
                if (!hasPermission) {
                    listPermissions.add(permission);
                }
            }

            //if list empty no need to ask for permission
            if (listPermissions.isEmpty()) {
                return;
            }
            String[] newPermissions = new String[listPermissions.size()];
            listPermissions.toArray(newPermissions);

            //ask for permission
            ActivityCompat.requestPermissions(activity, newPermissions, requestCode);

        }
    }
}
