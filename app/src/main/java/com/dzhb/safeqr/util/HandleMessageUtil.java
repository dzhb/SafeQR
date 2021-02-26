package com.dzhb.safeqr.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.blankj.utilcode.util.RegexUtils;
import com.dzhb.safeqr.view.afterScan.ShowMessage;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

public class HandleMessageUtil {


    public static void handleMessage(Context context, String message){
        if (RegexUtils.isMobileSimple(message) || RegexUtils.isTel(message)) {
            HandleMessageUtil.phone(message);
        } else if(RegexUtils.isEmail(message)) {
            HandleMessageUtil.sendEmail(message);
        } else if(RegexUtils.isURL(message)){
            HandleMessageUtil.openURL(message);
        } else{
            showMessages(context, message);
        }
    }

    //拨打电话
    public static void phone(String message){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + message);
        intent.setData(data);
        startActivity(intent);
    }

    //发送邮件
    public static void sendEmail(String message){
        Intent i = new Intent(Intent.ACTION_SEND);
        // i.setType("text/plain"); //模拟器请使用这行
        i.setType("message/rfc822"); // 真机上使用这行
        i.putExtra(Intent.EXTRA_EMAIL,
                new String[] { message });
//                    i.putExtra(Intent.EXTRA_SUBJECT, "您的建议");
//                    i.putExtra(Intent.EXTRA_TEXT, "我们很希望能得到您的建议！！！");
        startActivity(Intent.createChooser(i,
                "Select email application."));
    }

    //打开网址
    public static void openURL(String message){
        Uri uri = Uri.parse(message);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    //显示字符串信息
    public static void showMessages(Context context, String message){
        Intent intent = new Intent(context, ShowMessage.class);
        intent.putExtra("message", message);
        startActivity(intent);
    }

}
