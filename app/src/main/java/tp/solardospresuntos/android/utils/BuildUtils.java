package tp.solardospresuntos.android.utils;

import android.os.Build;

/**
 * Created by jcalado on 05/09/16
 */
public class BuildUtils {

    public static boolean hasNougat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static boolean hasMarshmallow() {
        return BuildUtils.hasAndroidVersion(Build.VERSION_CODES.M);
    }

    public static boolean hasLollipop() {
        return BuildUtils.hasAndroidVersion(Build.VERSION_CODES.LOLLIPOP);
    }

    public static boolean hasKitkat() {
        return BuildUtils.hasAndroidVersion(Build.VERSION_CODES.KITKAT);
    }

    public static boolean hasJellyBean() {
        return BuildUtils.hasAndroidVersion(Build.VERSION_CODES.JELLY_BEAN);
    }

    public static boolean hasJellyBeanMR1() {
        return BuildUtils.hasAndroidVersion(Build.VERSION_CODES.JELLY_BEAN_MR1);
    }

    public static boolean hasHoneycombMR1() {
        return BuildUtils.hasAndroidVersion(Build.VERSION_CODES.HONEYCOMB_MR1);
    }

    public static boolean hasAndroidVersion(int buildVersion) {
        return Build.VERSION.SDK_INT >= buildVersion;
    }
}
