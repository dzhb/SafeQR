package com.dzhb.safeqr.view.checkSafeQR;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.dzhb.safeqr.BaseActivity;
import com.dzhb.safeqr.R;
import com.dzhb.safeqr.util.c;
import com.dzhb.safeqr.view.HomeActivity;
import com.dzhb.safeqr.view.checkSafeQR.scan.CustomCaptureActivity;
import com.dzhb.safeqr.view.makeSafeQR.MakeSafeQRActivity2;
import com.dzhb.safeqr.widget.ImgBtn_horizontal;
import com.dzhb.safeqr.widget.ImgBtn_vertical;
import com.dzhb.safeqr.widget.TopBar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

public class CheckSafeQRActivity extends BaseActivity {

    private TopBar topBar;

    private ImgBtn_vertical openCameraButton;
    private ImgBtn_vertical openAlbumButton;
    private ImgBtn_horizontal makeQRButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_safecheck);
        initView();
        initListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取解析结果
        if (requestCode == REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            Intent intent = new Intent(CheckSafeQRActivity.this, CheckWithScanActivity.class);
            if (result != null) {
                if (result.getContents() == null) {
                    Toast.makeText(this, "取消扫描", Toast.LENGTH_LONG).show();
                    c.clean();
                } else {
//                    Toast.makeText(this, "扫描内容:" + result.getContents(), Toast.LENGTH_LONG).show();
                    //传值“二维码结果”
                    intent.putExtra("result", result.toString());
                    if (result.getBarcodeImagePath() != null) {
                        //显示二维码图片的保存路径
//                        Toast.makeText(this, result.getBarcodeImagePath(), Toast.LENGTH_SHORT).show();
                        //显示二维码图片
                        intent.putExtra("imagePath", result.getBarcodeImagePath());
                    }
                    startActivity(intent);
                }
            }
        }
    }

    private void initView() {
        topBar = findViewById(R.id.topBar_safeCheck);
        openCameraButton = findViewById(R.id.button_openCamera);
        openCameraButton.setText("扫描二维码");
        openCameraButton.setImageResource(R.drawable.camera);
        openAlbumButton = findViewById(R.id.button_openAlbum);
        openAlbumButton.setText("从相册选择二维码");
        openAlbumButton.setImageResource(R.drawable.album);
        makeQRButton = findViewById(R.id.checkSafe_IBH_makeSafeQR);

    }

    private void initListener(){
        topBar.getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        openCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beganScan();
            }
        });
        openAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckSafeQRActivity.this, CheckWithAlbumActivity.class);
                startActivity(intent);
            }
        });
        makeQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckSafeQRActivity.this, MakeSafeQRActivity2.class);
                startActivity(intent);
                finish();
            }
        });
    }


    //开始扫码
    private void beganScan() {
        //创建IntentIntegrator对象
        IntentIntegrator intentIntegrator = new IntentIntegrator(CheckSafeQRActivity.this);

        intentIntegrator.setBarcodeImageEnabled(true)//保存被扫描的二维码图片
                .setCaptureActivity(CustomCaptureActivity.class)//设置自定义扫描Activity
                .setOrientationLocked(false)//如果二维码扫描界面为“完全依赖传感器”，设置方向不会被锁定
                .setPrompt("这里是二维码扫描界面")//设置提示信息
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)//设置扫描条形码格式
                .setTimeout(30000)//设置扫描超时时间(单位：毫秒)
                .initiateScan();//开始扫描
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(CheckSafeQRActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            c.clean();
        }
        return super.onKeyDown(keyCode, event);

    }

}
