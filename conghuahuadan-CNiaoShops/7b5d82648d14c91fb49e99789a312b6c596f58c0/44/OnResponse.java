package com.chhd.cniaoshops.http;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chhd.cniaoshops.R;
import com.chhd.cniaoshops.global.Config;
import com.chhd.cniaoshops.global.Constant;
import com.chhd.cniaoshops.util.DialogUtils;
import com.chhd.cniaoshops.util.LoggerUtils;
import com.chhd.cniaoshops.util.ToastyUtils;
import com.orhanobut.logger.Logger;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by CWQ on 2017/3/21.
 */

public abstract class OnResponse<T> implements OnResponseListener<T>, Constant {

    private boolean isToastError = true;
    private int delayMillis = DELAYMILLIS_FOR_RQUEST_FINISH;
    private long startTimeMillis;
    private Context progressDialog;
    private MaterialDialog dialog;

    public OnResponse() {
    }

    public OnResponse(boolean isToastError) {
        this.isToastError = isToastError;
    }

    public OnResponse(int delayMillis) {
        this.delayMillis = delayMillis;
    }

    public OnResponse(Context progressDialog) {
        this.progressDialog = progressDialog;
    }

    @Override
    public final void onStart(int what) {
        startTimeMillis = System.currentTimeMillis();
        if (progressDialog != null && progressDialog instanceof Activity) {
            dialog = DialogUtils.newProgressDialog(progressDialog);
            dialog.show();
        }
        start(what);
    }

    @Override
    public final void onSucceed(final int what, final Response response) {
        d(response);
        long timeDif = getTimeDif();
        if (timeDif > delayMillis) {
            succeed(what, response);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    succeed(what, response);
                }
            }, delayMillis - timeDif);
        }
    }

    @Override
    public final void onFailed(final int what, final Response response) {
        e(response);
        long timeDif = getTimeDif();
        if (timeDif > delayMillis) {
            failed(what, response);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    failed(what, response);
                }
            }, delayMillis - timeDif);
        }
    }

    @Override
    public final void onFinish(final int what) {
        long timeDif = getTimeDif();
        if (timeDif > delayMillis) {
            finish(what);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish(what);
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }, delayMillis - timeDif);
        }
    }

    private long getTimeDif() {
        return System.currentTimeMillis() - startTimeMillis;
    }

    public void start(int what) {

    }

    public abstract void succeed(int what, Response<T> response);

    public void failed(int what, Response<T> response) {
        if (isToastError) {
            ToastyUtils.error(R.string.network_connect_fail);
        }
    }

    public void finish(int what) {
    }

    /**
     * 打印NOHttp请求成功信息
     *
     * @param response
     */
    private void d(Response<?> response) {
        if (Config.isDebug) {
            Set<Map.Entry<String, List<Object>>> entries = response.request().getParamKeyValues().entrySet();
            String params = entries.isEmpty() ? "" : "params:\t" + formatParamsStr(entries) + "\n\n";
            String message =
                    "url:\t\t" + response.request().url()
                            + "\n\n"
                            + params
                            + "json:\t" + response.get();
            Logger.d(message);
        }
    }

    /**
     * 打印NOHttp请求失败信息
     *
     * @param response
     */
    private void e(Response<?> response) {
        if (Config.isDebug) {
            Set<Map.Entry<String, List<Object>>> entries = response.request().getParamKeyValues().entrySet();
            String params = entries.isEmpty() ? "" : "params:\t" + formatParamsStr(entries) + "\n\n";
            String message =
                    "url:\t\t" + response.request().url()
                            + "\n\n"
                            + params
                            + "xml:\t" + response.get()
                            + "\n\n"
                            + "error";
            Logger.e(response.getException(), message);
        }
    }

    private String formatParamsStr(Set<Map.Entry<String, List<Object>>> entries) {
        return entries.toString().replace("[", "").replace("]", "").replace(", ", "\n" + "params:\t");
    }
}
