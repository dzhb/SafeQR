package com.dzhb.safeqr.view.checkSafeQR.scan;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dzhb.safeqr.BaseActivity;
import com.dzhb.safeqr.R;
import com.dzhb.safeqr.util.c;
import com.dzhb.safeqr.view.HomeActivity;
import com.dzhb.safeqr.view.checkSafeQR.CheckSafeQRActivity;
import com.dzhb.safeqr.view.checkSafeQR.CheckWithAlbumActivity;
import com.dzhb.safeqr.view.makeSafeQR.MakeSafeQRActivity2;
import com.dzhb.safeqr.widget.ImgBtn_horizontal;
import com.dzhb.safeqr.widget.TopBar;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;


/**
 * 扫描二维码界面
 */

public class CustomCaptureActivity extends BaseActivity implements DecoratedBarcodeView.TorchListener{

    //条形码扫描管理器
    private CaptureManager mCaptureManager;

    //条形码扫描视图
    private DecoratedBarcodeView mBarcodeView;

    //闪光灯
//    private Button switchLight;

    private boolean isLightOn = false;

    private ImgBtn_horizontal chooseImageButton;
    private ImgBtn_horizontal makeSafeQrImage;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_zxing_layout);

        TopBar topBar = findViewById(R.id.topbar_scan);
        topBar.getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chooseImageButton = findViewById(R.id.scan_chooseImageButton);
        makeSafeQrImage = findViewById(R.id.scan_IBH_makeSafeQR);

        mBarcodeView = (DecoratedBarcodeView) findViewById(com.google.zxing.client.android.R.id.zxing_barcode_scanner);
//        switchLight = (Button) findViewById(R.id.flashlight);




        mBarcodeView.setTorchListener(this);

        //如果没有闪光灯功能，就去掉相关按钮
//        if(!hasFlash()){
//            switchLight.setVisibility(View.GONE);
//        }

        mCaptureManager = new CaptureManager(this, mBarcodeView);
        mCaptureManager.initializeFromIntent(getIntent(), savedInstanceState);
        mCaptureManager.decode();

        //选择闪光灯
//        switchLight.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                if(isLightOn){
//                    mBarcodeView.setTorchOff();
//                } else{
//                    mBarcodeView.setTorchOn();
//                }
//            }
//        });

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomCaptureActivity.this, CheckWithAlbumActivity.class);
                startActivity(intent);
            }
        });

        makeSafeQrImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomCaptureActivity.this, MakeSafeQRActivity2.class);
                startActivity(intent);
            }
        });
    }



    @Override
    protected void onResume(){
        super.onResume();
        mCaptureManager.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mCaptureManager.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mCaptureManager.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mCaptureManager.onSaveInstanceState(outState);
    }

    /**
     * 权限处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permission[], @NonNull int[] grantResults){
        mCaptureManager.onRequestPermissionsResult(requestCode, permission, grantResults);
    }

    /**
     * 按键处理
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode) {

            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                setTorchOff();
                c.c_down();
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
//                setTorchOn();
                c.c_up();
                return true;
        }
//        return mBarcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
        return super.onKeyDown(keyCode, event);

    }



    //torch手电筒
    @Override
    public void onTorchOn(){
        Toast.makeText(this, "torch on", Toast.LENGTH_SHORT).show();
        isLightOn = true;

    }

    @Override
    public void onTorchOff(){
        Toast.makeText(this, "torch off", Toast.LENGTH_SHORT).show();
        isLightOn = false;

    }

    //判断是否有闪光灯功能
    private boolean hasFlash(){
        return getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }
}
