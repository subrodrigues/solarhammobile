package tp.solardospresuntos.android.utils.listener;

import android.os.SystemClock;
import android.view.View;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * A Debounced OnClickListener
 * Rejects clicks that are too close together in time.
 * This class is safe to use as an OnClickListener for multiple views, and will debounce each one separately.
 * <p>
 * Credits to: http://stackoverflow.com/questions/16534369/avoid-button-multiple-rapid-clicks
 */
public abstract class DebouncedOnClickListener implements View.OnClickListener {

    // the default threshold between clicks
    private final long DEFAULT_CLICK_THRESHOLD_MILLIS = 1000;
    private final long minimumInterval;
    private Map<View, Long> lastClickMap;

    /**
     * The constructor which assumes the default interval of 1000 milliseconds
     */
    public DebouncedOnClickListener() {
        this.minimumInterval = DEFAULT_CLICK_THRESHOLD_MILLIS;
        this.lastClickMap = new WeakHashMap<>();
    }

    /**
     * The constructor with a custom interval in millis.
     *
     * @param minimumIntervalMilliSecs The minimum allowed time between clicks - any click sooner than this after a previous click will be rejected
     */
    public DebouncedOnClickListener(long minimumIntervalMilliSecs) {
        this.minimumInterval = minimumIntervalMilliSecs;
        this.lastClickMap = new WeakHashMap<>();
    }

    /**
     * Implement this in your subclass instead of onClick
     *
     * @param v The view that was clicked
     */
    public abstract void onDebouncedClick(View v);

    @Override
    public void onClick(View clickedView) {
        Long previousClickTimestamp = lastClickMap.get(clickedView);
        long currentTimestamp = SystemClock.uptimeMillis();

        lastClickMap.put(clickedView, currentTimestamp);
        if (previousClickTimestamp == null || (currentTimestamp - previousClickTimestamp > minimumInterval)) {
            onDebouncedClick(clickedView);
        }
    }
}
