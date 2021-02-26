package com.dzhb.safeqr.view.afterScan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.util.RegexUtils;
import com.dzhb.safeqr.R;
import com.dzhb.safeqr.util.HandleMessageUtil;
import com.dzhb.safeqr.util.ImgUtils;
import com.dzhb.safeqr.view.makeSafeQR.MakeSafeQRActivity2;
import com.dzhb.safeqr.widget.ImgBtn_horizontal;
import com.dzhb.safeqr.widget.TopBar;

public class ShowSafeActivity extends AppCompatActivity {

    private Button safeEnter;
    private ImgBtn_horizontal makeSafeQR;

//    private ImageView qrPic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_afterscan_safe);

        Intent intent = getIntent();
        final String message = intent.getStringExtra("message");

        TopBar topBar = findViewById(R.id.topBar_afterScan_safe);
        topBar.getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //获取来自safeCheck的intent
//        Intent intent = getIntent();
//        String result = intent.getStringExtra("result");

//        qrPic = findViewById(R.id.qrCodePic);
//        String imagePath = intent.getStringExtra("imagePath");
//        if(imagePath != null){
////            showBarcodeImage(imagePath);
////            ShowImage.getShowImage(imagePath, qrPic);
////            ShowImage.ShowImage(imagePath, qrPic);
//            ImgUtils.showImage(imagePath, qrPic);
//        }


        safeEnter = findViewById(R.id.safe_btn_enter);
        safeEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandleMessageUtil.handleMessage(getApplicationContext(), message);

            }
        });

        makeSafeQR = findViewById(R.id.make_safeQR);
        makeSafeQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowSafeActivity.this, MakeSafeQRActivity2.class);
                startActivity(intent);
                finish();
            }
        });

    }


}
