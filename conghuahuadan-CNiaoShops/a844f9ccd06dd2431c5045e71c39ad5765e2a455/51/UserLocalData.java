package com.chhd.cniaoshops.biz;

import android.content.Context;

import com.chhd.cniaoshops.bean.User;
import com.chhd.cniaoshops.util.JsonUtil;
import com.chhd.cniaoshops.util.LoggerUtil;
import com.chhd.per_library.util.SpUtil;

/**
 * Created by CWQ on 2017/4/16.
 */

public class UserLocalData {

    private UserLocalData() {
    }

    public static void putUser(User user) {
        LoggerUtil.d("user: " + JsonUtil.toJSON(user));
        SpUtil.putString("user", JsonUtil.toJSON(user));
    }

    public static User getUser() {
        return JsonUtil.fromJson(SpUtil.getString("user", ""), User.class);
    }

    public static void clearUser() {
        SpUtil.putString("user", "");
    }
}
