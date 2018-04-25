package zhufengjie.com.drysister2.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * 描述：网络相关工具类
 *
 * @author coder-pig： 2016/08/22 10:24
 */
public class NetworkUtils {
    /** 获取网络信息 */
    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /** 判断网络是否可用 */
    public static boolean isAvailable(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        NetworkInfo.DetailedState state = info.getDetailedState();
        Log.e("网络",state.toString());
        Log.e("网络信息",info.toString());
        return info != null && info.isAvailable();
    }
}
