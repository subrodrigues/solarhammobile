package tp.solardospresuntos.android.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import tp.solardospresuntos.android.R;

/**
 * Created by eduardo on 17/03/16.
 */
public class PackageUtils {

    private static final String TAG = "PackageUtils";
    public static final String WHATSAPP_PACKAGE_NAME = "com.whatsapp";
    public static final String PACKAGE_STRING = "package:";

    public static boolean hasPackageInstalled(@NonNull Context context, @NonNull String packageName) {
        PackageManager pm = context.getPackageManager();
        boolean appInstalled;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Package " + packageName + " isn't installed!");
            appInstalled = false;
        }

        return appInstalled;
    }

    public static boolean hasMetaData(@NonNull Context context, @NonNull String metaDataName) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.containsKey(metaDataName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        }

        return false;
    }

    public static boolean hasProviderInstalled(@NonNull Context context, @NonNull String providerName) {
        PackageManager pm = context.getPackageManager();

        List<PackageInfo> installedProviders = pm.getInstalledPackages(PackageManager.GET_PROVIDERS);
        for (PackageInfo pack : installedProviders) {
            if(!TextUtils.equals(pack.packageName, context.getPackageName())) continue;

            if (providerFinder(providerName, pack.providers)) return true;
        }

        Log.d(TAG, "No Provider matched: " + providerName + ".");
        return false;
    }

    public static String getPackageKeyHash(@NonNull Context context, @NonNull String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                String keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d(TAG, "KeyHash for " + packageName + ": " + keyHash);

                return keyHash;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Package " + packageName + " doesn't exists!");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "NoSuchAlgorithmException: " + e.getMessage());
        }

        return null;
    }

    private static boolean providerFinder(@NonNull String providerName, ProviderInfo[] providers) {
        if (providers == null) return false;

        for (ProviderInfo provider : providers) {
            String name = provider.name;

            if(TextUtils.equals(name, providerName)) {
                Log.d(TAG, "Provider: " + name + " was found.");
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if there are any apps that can handle the given intent.
     *
     * @param context the context to use
     * @param intent the intent to check
     * @return true if there is one or more apps in the device that can handle the given intent, false if there is none.
     */
    public static boolean isIntentSupported(Context context, Intent intent) {
        boolean result = true;
        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        if (infos.size() <= 0) {
            result = false;
        }
        return result;
    }

    /**
     * Opens the current app settings.
     *
     * @param context the context of the application.
     */
    public static void openAppSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Opens the dialer to start a call.
     *
     * @param phoneNumber The phone number to be dialed.
     *
     * @return {@code true} if a dialer Intent exists to handle the call or {@code false} if there is no Intent.
     */
    public static boolean openDialer(@NonNull Context context, @NonNull String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:".concat(phoneNumber)));

            if(isIntentSupported(context, intent)) {
                context.startActivity(intent);
                return true;
            }
        } catch (ActivityNotFoundException anfe) {
            Log.i(TAG, "There is no application to handle dialer links");
        }

        return false;
    }

    public static boolean openBrowser(@NonNull Context context, @NonNull String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));

            if(isIntentSupported(context, intent)) {
                context.startActivity(intent);
                return true;
            }
        } catch (ActivityNotFoundException anfe) {
            Log.i(TAG, "There is no application to handle URLs.");
        }

        return false;
    }

    /**
     * Opens an intent to an Activity
     *
     * @param context the context to use.
     * @param cls the class of the context to open.
     */
    public static void openIntent(Context context, Class<?> cls) {
        openIntent(context, cls, null);
    }

    /**
     * Opens an intent to an Activity, passing the given arguments.
     *
     * @param context the context to use.
     * @param cls the class of the activity to open.
     * @param bundle the bundle with the arguments to pass.
     */
    public static void openIntent(Context context, Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        if(bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    /**
     * Opens an intent to an Activity, passing the given arguments.
     *  @param activity the context to use.
     * @param cls the class of the activity to open.
     * @param bundle the bundle with the arguments to pass.
     * @param requestCode the request code to use
     */
    public static void openIntentForResult(Activity activity, Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent(activity, cls);
        if(bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Opens an intent to an Activity, passing the given arguments.
     *  @param fragment the context to use.
     * @param cls the class of the activity to open.
     * @param bundle the bundle with the arguments to pass.
     * @param requestCode the request code to use
     */
    public static void openIntentForResult(Fragment fragment, Class<?> cls, Bundle bundle, int requestCode) {
        final FragmentActivity activity = fragment.getActivity();
        if(activity != null) {
            Intent intent = new Intent(activity, cls);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            fragment.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * Opens an intent to an Activity, passing the given arguments.
     *
     * @param activity the context to use.
     * @param cls the class of the activity to open.
     * @param bundle the bundle with the arguments to pass.
     */
    public static void openIntentWithHorizontalSlideAnimation(Activity activity, Class<?> cls, Bundle bundle) {
        if(activity != null) {
            openIntent(activity, cls, bundle);
            activity.overridePendingTransition(R.anim.open_right_next_activity, R.anim.close_right_main_activity);
        }
    }

    /**
     * Opens an intent to an Activity, passing the given arguments.
     *
     * @param activity the context to use.
     * @param cls the class of the activity to open.
     * @param bundle the bundle with the arguments to pass.
     */
    public static void openIntentForResultWithHorizontalSlideAnimation(Activity activity, Class<?> cls, Bundle bundle, int requestCode) {
        if(activity != null) {
            openIntentForResult(activity, cls, bundle, requestCode);
            activity.overridePendingTransition(R.anim.open_right_next_activity, R.anim.close_right_main_activity);
        }
    }

    /**
     * Opens an intent to an Activity.
     *
     * @param activity the context to use.
     * @param cls the class of the activity to open.
     */
    public static void openIntentWithHorizontalSlideAnimation(Activity activity, Class<?> cls) {
        openIntentWithHorizontalSlideAnimation(activity, cls, null);
    }

    /**
     * Opens an intent to an Activity that has a pop up behaviour, passing the given arguments.
     *
     * @param activity the context to use.
     * @param cls the class of the activity to open.
     * @param bundle the bundle with the arguments to pass.
     */
    public static void openIntentWithPopUpAnimation(Activity activity, Class<?> cls, Bundle bundle) {
        if(activity != null) {
            openIntent(activity, cls, bundle);
            activity.overridePendingTransition(R.anim.slide_in_down, R.anim.fade_out_anim);
        }
    }

    /**
     * Opens an intent to an Activity, passing the given arguments.
     *
     * @param activity the context to use.
     * @param cls the class of the activity to open.
     * @param bundle the bundle with the arguments to pass.
     */
    public static void openIntentForResultWithPopUpAnimation(Activity activity, Class<?> cls, Bundle bundle, int requestCode) {
        if(activity != null) {
            openIntentForResult(activity, cls, bundle, requestCode);
            activity.overridePendingTransition(R.anim.slide_in_down, R.anim.fade_out_anim);
        }
    }

    /**
     * Opens an intent to an Activity, passing the given arguments.
     *
     * @param fragment the fragment with the context to use.
     * @param cls the class of the activity to open.
     * @param bundle the bundle with the arguments to pass.
     */
    public static void openIntentForResultWithPopUpAnimation(Fragment fragment, Class<?> cls, Bundle bundle, int requestCode) {
        if(fragment != null) {
            openIntentForResult(fragment, cls, bundle, requestCode);
            fragment.getActivity().overridePendingTransition(R.anim.slide_in_down, R.anim.fade_out_anim);
        }
    }

    /**
     * Opens an intent to an Activity that has a pop up behaviour.
     *
     * @param activity the context to use.
     * @param cls the class of the activity to open.
     */
    public static void openIntentWithPopUpAnimation(Activity activity, Class<?> cls) {
        openIntentWithPopUpAnimation(activity, cls, null);
    }

    /**
     * Creates an intent which leads to the application's settings
     *
     * @param applicationContext the context to use.
     */
    public static Intent getOpenSettingsIntent(Context applicationContext) {
        return new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(PACKAGE_STRING + applicationContext.getPackageName()));
    }
}
