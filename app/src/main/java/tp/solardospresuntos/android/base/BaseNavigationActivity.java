package tp.solardospresuntos.android.base;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import tp.solardospresuntos.android.R;
import tp.solardospresuntos.android.databinding.ActivityBaseNavigationBinding;

public abstract class BaseNavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle mDrawerToggle;
    private ActivityBaseNavigationBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_base_navigation);

        setupActionBar();
        setupDrawerLayout();
    }

    protected void setupDrawerLayout() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mBinding.baseDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {

                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {

                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_DRAGGING) {

                }
            }
        };

        mBinding.baseDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void setupActionBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
    }

    protected void setupActionBar(Drawable drawable) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(drawable);
    }

    protected void onActionBarBackPressed() {
        //for descendant activities to implement
    }

    protected void setSpinnerOptions(ArrayAdapter<?> options, @NonNull AdapterView.OnItemSelectedListener itemSelectedListener) {

    }

    public static boolean validateActivity(Activity activity) {

        if (activity == null) {
            return false;
        }

        boolean isActivityFinishing = activity.isFinishing();
        boolean isActivityDestroyed = false;

        isActivityDestroyed = activity.isDestroyed();

        return !isActivityFinishing || !isActivityDestroyed;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    protected void setBackgroundDrawable(Drawable backgroundDrawable){
        mBinding.baseContentLayout.setBackground(backgroundDrawable);
    }

    protected void setBackgroundColor(int backgroundColor) {
        mBinding.baseDrawerLayout.setBackgroundColor(backgroundColor);
    }

}
