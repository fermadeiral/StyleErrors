package com.chhd.cniaoshops.util;

import com.chhd.cniaoshops.global.Config;
import com.chhd.cniaoshops.global.Constant;
import com.lzy.okgo.model.HttpParams;
import com.orhanobut.logger.Logger;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by CWQ on 2016/11/2.
 */
public class LoggerUtils implements Constant {

    private static boolean isDebug = Config.isDebug;

    private LoggerUtils() {

    }

    public static void v(String message) {
        if (isDebug) {
            Logger.v(message);
        }
    }

    public static void d(String message) {
        if (isDebug) {
            Logger.d(message);
        }
    }

    public static void i(String message) {
        if (isDebug) {
            Logger.i(message);
        }
    }

    public static void e(Throwable throwable) {
        if (isDebug) {
            Logger.e(throwable, "error");
        }
    }

    /**
     * 打印NOHttp Json解析异常信息
     *
     * @param throwable
     * @param response
     */
    public static void e(Throwable throwable, Response<?> response) {
        if (isDebug) {
            Set<Map.Entry<String, List<Object>>> entries = response.request().getParamKeyValues().entrySet();
            String params = entries.isEmpty() ? "" : "params:\t" + entries.toString().replace("[", "").replace("]", "").replace(", ", "\n" + "params:\t") + "\n\n";
            String message =
                    "url:\t\t" + response.request().url()
                            + "\n\n"
                            + params
                            + "json:\t" + response.get()
                            + "\n\n"
                            + "error";
            Logger.e(throwable, message);
        }
    }

    public static void e(String message) {
        if (isDebug) {
            Logger.e(message);
        }
    }


}
