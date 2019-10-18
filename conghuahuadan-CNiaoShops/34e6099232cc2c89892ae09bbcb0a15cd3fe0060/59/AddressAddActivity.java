package com.chhd.cniaoshops.ui.activity.address;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.chhd.cniaoshops.R;
import com.chhd.cniaoshops.bean.Address;
import com.chhd.cniaoshops.bean.Province;
import com.chhd.cniaoshops.bean.User;
import com.chhd.cniaoshops.biz.UserLocalData;
import com.chhd.cniaoshops.global.App;
import com.chhd.cniaoshops.http.bmob.SimpleUpdateListener;
import com.chhd.cniaoshops.ui.activity.PlaceChooseActivity;
import com.chhd.cniaoshops.ui.base.activity.BaseActivity;
import com.chhd.cniaoshops.ui.base.activity.HideSoftInputActivity;
import com.chhd.cniaoshops.util.JsonUtil;
import com.chhd.cniaoshops.util.LoggerUtil;
import com.chhd.cniaoshops.util.ToastyUtil;
import com.chhd.per_library.util.AppUtil;
import com.chhd.per_library.util.SoftKeyboardUtil;
import com.chhd.per_library.util.UiUtil;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

public class AddressAddActivity extends BaseAddressActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.add_address);

        etConsignee.setText(App.user.getNickname());
        etNumber.setText(AppUtil.getMobliePhone());

        SoftKeyboardUtil.showSoftInput(etConsignee);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_DEFAULT_ID:
                requestAddAddress();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestAddAddress() {
        final Address address = new Address();
        address.setConsignee(etConsignee.getText().toString());
        address.setNumber(etNumber.getText().toString());
        address.setArea(tvArea.getText().toString());
        address.setDetailAddress(etAddress.getText().toString());
        address.setDefault(App.user.getAddresses().isEmpty());

        List<Address> newList = new ArrayList<>();
        newList.add(address);
        newList.addAll(App.user.getAddresses());

        User newUser = new User();
        newUser.setAddresses(newList);
        newUser.update(BmobUser.getCurrentUser().getObjectId(), new SimpleUpdateListener(this) {
            @Override
            public void success() {
                App.user.getAddresses().add(0, address);
                UserLocalData.putUser(App.user);
                finish();
            }
        });
    }

}
