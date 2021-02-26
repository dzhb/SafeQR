package com.dzhb.safeqr.view.afterScan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dzhb.safeqr.ActivityCollector;
import com.dzhb.safeqr.R;
import com.dzhb.safeqr.util.HandleMessageUtil;
import com.dzhb.safeqr.util.ImgUtils;
import com.dzhb.safeqr.view.HomeActivity;
import com.dzhb.safeqr.view.makeSafeQR.MakeSafeQRActivity2;
import com.dzhb.safeqr.widget.ImgBtn_horizontal;
import com.dzhb.safeqr.widget.TopBar;

public class ShowDangerActivity extends AppCompatActivity {

    private TopBar topBar;

    private Button reject;
    private ImgBtn_horizontal makeSafeQR;
    private TextView acceptTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_afterscan_danger);

        topBar = findViewById(R.id.topBar_afterScan_danger);
        topBar.getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        acceptTextView = findViewById(R.id.danger_tv2);

        final Intent intent = getIntent();
        final String message = intent.getStringExtra("message");

        //获取来自checkSafeQR的intent
//        Intent intent = getIntent();
//        String result = intent.getStringExtra("result");
//
//        String imagePath = intent.getStringExtra("imagePath");
//        if(imagePath != null){
////            ShowImage.ShowImage(imagePath, (ImageView)findViewById(R.id.qrCodePic2));
//            ImgUtils.showImage(imagePath, (ImageView)findViewById(R.id.qrCodePic2));
//        }


        reject = findViewById(R.id.danger_btn_reject);
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ShowDangerActivity.this, HomeActivity.class);
                startActivity(intent1);
                finish();
            }
        });

        acceptTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandleMessageUtil.handleMessage(getApplicationContext(), message);
            }
        });
//        if (acceptTextView.isSelected()) {
//            acceptTextView.setSelected(false);
//        } else {
//            acceptTextView.setSelected(true);
//        }

        makeSafeQR = findViewById(R.id.make_safeQR);
        makeSafeQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowDangerActivity.this, MakeSafeQRActivity2.class);
                startActivity(intent);
                finish();
            }
        });


    }


}
