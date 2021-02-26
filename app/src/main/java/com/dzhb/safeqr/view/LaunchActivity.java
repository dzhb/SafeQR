package com.dzhb.safeqr.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.dzhb.safeqr.ActivityCollector;
import com.dzhb.safeqr.BaseActivity;
import com.dzhb.safeqr.R;

/**
 * 启动界面
 */

public class LaunchActivity extends BaseActivity {

    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.layout_launch);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            System.exit(0);

        return super.onKeyDown(keyCode, event);
    }

}

