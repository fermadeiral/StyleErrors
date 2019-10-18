package com.chhd.cniaoshops.ui.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.chhd.cniaoshops.R;
import com.chhd.cniaoshops.bean.Tab;
import com.chhd.cniaoshops.global.AppApplication;
import com.chhd.cniaoshops.ui.base.activity.HideSoftInputActivity;
import com.chhd.cniaoshops.ui.fragment.CartFragment;
import com.chhd.cniaoshops.ui.fragment.CategoryFragment;
import com.chhd.cniaoshops.ui.fragment.HomeFragment;
import com.chhd.cniaoshops.ui.fragment.HotFragment;
import com.chhd.cniaoshops.ui.fragment.MineFragment;
import com.chhd.cniaoshops.ui.widget.CnToolbar;
import com.chhd.cniaoshops.ui.widget.FragmentTabHost;
import com.chhd.cniaoshops.util.LoggerUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends HideSoftInputActivity {

    @BindView(R.id.cn_tool_bar)
    CnToolbar toolbar;
    @BindView(android.R.id.tabcontent)
    FrameLayout frameLayout;
    @BindView(android.R.id.tabhost)
    FragmentTabHost tabHost;

    private List<Tab> tabs = new ArrayList<>();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        AppApplication.isHotRun = true;

        initTab();

    }


    @Override
    public int getLayoutResID() {
        return R.layout.activity_main;
    }


    private void initTab() {

        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        tabs.add(new Tab(R.drawable.selector_icon_home, getString(R.string.tab_home), HomeFragment.class));
        tabs.add(new Tab(R.drawable.selector_icon_hot, getString(R.string.tab_hot), HotFragment.class));
        tabs.add(new Tab(R.drawable.selector_icon_category, getString(R.string.tab_classification), CategoryFragment.class));
        tabs.add(new Tab(R.drawable.selector_icon_cart, getString(R.string.tab_shopping_cart), CartFragment.class));
        tabs.add(new Tab(R.drawable.selector_icon_mine, getString(R.string.tab_me), MineFragment.class));

        for (Tab tab : tabs) {
            TabHost.TabSpec tabSpec = tabHost.newTabSpec(tab.getTitle());
            tabSpec.setIndicator(getIndicator(tab));
            tabHost.addTab(tabSpec, tab.getFragment(), null);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            tabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        }

        tabHost.setOnTabChangedListener(onTabChangeListener);
    }

    private TabHost.OnTabChangeListener onTabChangeListener = new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String tabId) {
            if (tabId.equals(getString(R.string.tab_me))) {
                toolbar.setVisibility(View.GONE);
            } else {
                toolbar.setVisibility(View.VISIBLE);
            }
            if (tabId.equals(getString(R.string.tab_shopping_cart))) {
                toolbar.hideSearchView();
                toolbar.setTitle(R.string.tab_shopping_cart);
            }
        }
    };

    private View getIndicator(Tab tab) {
        View view = View.inflate(context, R.layout.tab_indicator, null);
        ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        ivIcon.setImageResource(tab.getIcon());
        tvTitle.setText(tab.getTitle());
        return view;
    }

}
