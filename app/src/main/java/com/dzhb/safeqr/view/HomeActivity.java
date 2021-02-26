package com.dzhb.safeqr.view;

/**
 * 首页
 */

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.dzhb.safeqr.ActivityCollector;
import com.dzhb.safeqr.BaseActivity;
import com.dzhb.safeqr.R;
import com.dzhb.safeqr.util.PermissionUtils;
import com.dzhb.safeqr.view.checkSafeQR.CheckSafeQRActivity;
import com.dzhb.safeqr.view.makeSafeQR.MakeSafeQRActivity2;
import com.dzhb.safeqr.widget.ImgBtn_horizontal;

public class HomeActivity extends BaseActivity {

    private long exitTime = 0;

    private ImgBtn_horizontal makeSafeQrButton;
    private ImgBtn_horizontal checkSafeQrButton;

    private TextView registerTextView;
    private TextView loginTextView;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.layout_home);
        initView();
        initListener();

        //申请权限
        PermissionUtils.isGrantExternalRW(this, 1);

    }


    private void initView() {
        makeSafeQrButton = findViewById(R.id.home_myImgBtn_makeSafeQR);
        checkSafeQrButton = findViewById(R.id.home_myImgBtn_checkSafeQR);
        registerTextView = findViewById(R.id.home_register_textView);
        loginTextView = findViewById(R.id.home_login_textView);
    }

    private void initListener() {
        makeSafeQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MakeSafeQRActivity2.class);
                startActivity(intent);
            }
        });

        checkSafeQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CheckSafeQRActivity.class);
                startActivity(intent);
            }
        });
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort("还未开发");
            }
        });
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort("还未开发");
            }
        });
    }


    //对activity.requestPermissions（）函数的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String sdCard = Environment.getExternalStorageState();
                    if (sdCard.equals(Environment.MEDIA_MOUNTED)) {
                        Toast.makeText(getApplicationContext(), "获得授权", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "未获得权限", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                ActivityCollector.finishAll();
//                System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


}
