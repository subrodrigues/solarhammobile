package tp.solardospresuntos.android.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by joaocaseiro on 13/12/16.
 */
public class PermissionUtils {
    public static boolean isPermissionGranted(String permissionToCheck, String[] permissions, int[] grantResults) {
        if(permissionToCheck == null || permissions == null || grantResults == null || permissions.length != grantResults.length) {
            return false;
        }

        for(int i = 0 ; i < permissions.length ; i++) {
            if(permissions[i].equals(permissionToCheck)) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }

    /**
     * Check if location permission has been granted.
     *
     * @return true if the permission has been granted, false otherwise.
     */
    public static boolean hasPermission(Context context, String permission) {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(context, permission);
        return hasFineLocationPermission == PackageManager.PERMISSION_GRANTED;
    }
}
