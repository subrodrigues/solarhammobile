package tp.solardospresuntos.android.utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by filiperodrigues on 22/05/17.
 */

public class Utils {

    public static Snackbar showSnackbar(View parent, String message) {
        return showSnackbar(parent, message, null, Snackbar.LENGTH_LONG, null);
    }

    public static Snackbar showSnackbar(Activity activity, String message, String action, View.OnClickListener actionClickListener) {
        return showSnackbar(activity, message, action, Snackbar.LENGTH_LONG, actionClickListener);
    }

    public static Snackbar showSnackbar(Activity activity, String message, String action, int duration, View.OnClickListener actionClickListener) {
        return showSnackbar(activity.findViewById(android.R.id.content), message, action, duration, actionClickListener);
    }

    public static Snackbar showSnackbar(View parent, String message, String action, int duration, View.OnClickListener actionClickListener) {
        Context context = parent.getContext();

        Snackbar snackbar = Snackbar.make(parent, message, duration);

        if(action != null) {
            snackbar.setAction(action, actionClickListener);
        }

        snackbar.show();
        return snackbar;
    }

    public static Snackbar showSnackbar(Activity activity, String message){
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();

        return snackbar;
    }

}
