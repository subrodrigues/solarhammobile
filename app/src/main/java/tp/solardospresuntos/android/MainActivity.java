package tp.solardospresuntos.android;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;

import tp.solardospresuntos.android.base.BaseNavigationActivity;


public class MainActivity extends BaseNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
        setContent();
    }

    private void initViews() {
        showMainFragment();
    }

    private void setContent() {
        setBackgroundDrawable(getDrawable(R.drawable.home_blurred_background));
    }

    public void showMainFragment() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.content_frame, HomeFragment.newInstance(), HomeFragment.class.getName())
                .setCustomAnimations(R.anim.fade_in_anim, R.anim.fade_out_anim)
                .commitAllowingStateLoss();
    }

}
