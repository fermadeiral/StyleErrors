package com.chhd.sharesdk;

import android.content.Context;
import android.view.View;

import com.orhanobut.logger.Logger;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.TitleLayout;
import cn.sharesdk.framework.authorize.AuthorizeAdapter;

/**
 * Created by CWQ on 2017/4/12.
 */

public class AuthPageAdapter extends AuthorizeAdapter {

    public void onCreate() {
        hideShareSDKLogo();//隐藏logo
        disablePopUpAnimation();//去掉启动动画
        //去掉返回按钮后面的分割线
//        TitleLayout llTitle = getTitleLayout();
//        llTitle.getChildAt(1).setVisibility(View.GONE);
    }

}
