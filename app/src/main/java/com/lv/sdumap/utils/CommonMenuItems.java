package com.lv.sdumap.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.lv.sdumap.BuildConfig;
import com.lv.sdumap.R;
import com.lv.sdumap.ui.logout.LogoutActivity;
import com.lv.sdumap.ui.share.ShareActivity;

/**
 * 处理公共菜单项
 */
public class CommonMenuItems {
    /**
     * 处理选中某个菜单项
     * @param context context
     * @param item 菜单项
     * @return 返回该事件是否被处理
     */
    public static boolean handleSelected(Context context, @NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout(context);
            return true;
        } else if (item.getItemId() == R.id.action_share) {
            share(context);
            return true;
        } else if (item.getItemId() == R.id.action_about) {
            about(context);
            return true;
        }
        return false;
    }

    /**
     * 退出登录
     * @param context context
     */
    public static void logout(Context context) {
        Intent myIntent = new Intent(context, LogoutActivity.class);
        context.startActivity(myIntent);
    }

    /**
     * 分享
     * @param context context
     */
    public static void share(Context context) {
        Intent myIntent = new Intent(context, ShareActivity.class);
        context.startActivity(myIntent);
    }

    /**
     * 关于
     * @param context context
     */
    public static void about(Context context) {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
        final String[] arrayOfString = {"GitHub 主页", "开源许可", "检查更新"};
        localBuilder.setTitle("关于 MySdu v"+ BuildConfig.VERSION_NAME).setItems(arrayOfString, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    String url = Storage.getSingleton().getString(Storage.KEY_CLOUD_GITHUB_LINK, "https://github.com/lyuxi99/MySdu");
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    context.startActivity(intent);
                } else if (which == 1) {
                    AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
                    localBuilder.setTitle("开源许可")
                            .setMessage(R.string.str_copyright)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                } else if (which == 2) {
                    Toast.makeText(context, "开源版本无法检查更新。",Toast.LENGTH_LONG).show();
                    String url = Storage.getSingleton().getString(Storage.KEY_CLOUD_GITHUB_LINK, "https://github.com/lyuxi99/MySdu");
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    context.startActivity(intent);
                }
                dialog.dismiss();
            }
        }).create().show();
    }
}
