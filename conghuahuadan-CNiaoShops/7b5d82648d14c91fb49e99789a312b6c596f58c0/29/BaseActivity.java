package com.chhd.cniaoshops.ui.base.activity;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.chhd.cniaoshops.R;
import com.chhd.cniaoshops.global.Constant;
import com.chhd.per_library.util.UiUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements Constant {

    protected final int MENU_DEFAULT_ID = 10;

    public static List<Activity> activities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResID());

        ButterKnife.bind(this);

        activities.add(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(UiUtils.getColor(getStatusBarColorResId()));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LottieAnimationView emptyAnimView = (LottieAnimationView) findViewById(R.id.empty_animation_view);
        if (emptyAnimView != null && emptyAnimView.isAnimating()) {
            emptyAnimView.cancelAnimation();
        }
        activities.remove(this);
    }

    public abstract int getLayoutResID();

    protected int getStatusBarColorResId() {
        return R.color.colorPrimaryDark;
    }

    protected View getRootView() {
        return getWindow().getDecorView().findViewById(android.R.id.content);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        LottieAnimationView emptyAnimView = (LottieAnimationView) findViewById(R.id.empty_animation_view);
        if (emptyAnimView != null && !emptyAnimView.isAnimating()) {
            emptyAnimView.resumeAnimation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LottieAnimationView emptyAnimView = (LottieAnimationView) findViewById(R.id.empty_animation_view);
        if (emptyAnimView != null && emptyAnimView.isAnimating()) {
            emptyAnimView.pauseAnimation();
        }
    }


    protected int getScreenHeight() {
        return findViewById(android.R.id.content).getHeight();
    }
}
