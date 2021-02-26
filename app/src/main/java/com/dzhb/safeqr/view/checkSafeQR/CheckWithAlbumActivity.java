package com.dzhb.safeqr.view.checkSafeQR;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dzhb.safeqr.BaseActivity;
import com.dzhb.safeqr.CheckWatermarkImpl;
import com.dzhb.safeqr.OpenCVNativeManager;
import com.dzhb.safeqr.R;
import com.dzhb.safeqr.util.DialogUtils;
import com.dzhb.safeqr.util.ImgUtils;
import com.dzhb.safeqr.util.QrUtils;
import com.dzhb.safeqr.util.SimilarityCallBack;
import com.dzhb.safeqr.util.SimilarityHelper;
import com.dzhb.safeqr.util.c;
import com.dzhb.safeqr.view.afterScan.ShowDangerActivity;
import com.dzhb.safeqr.view.afterScan.ShowSafeActivity;
import com.dzhb.safeqr.widget.TopBar;
import com.google.zxing.Result;

import java.io.FileNotFoundException;

/**
 * 从相册选择待检测图像
 */

public class CheckWithAlbumActivity extends BaseActivity {

    private final int IMAGE_REQUEST_CODE_1 = 1;

    private OpenCVNativeManager openCVNativeManager;

    private TopBar topBar;
    private ImageView safeImageView;
    //    private ImageView waterMarkImageView;
//    private Button chooseImageButton;
//    private Button checkQrButton;
    private Bitmap safeImageBitmap;
    private Bitmap waterMarkBitmap;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_checktext_album);

        initView();
        initData();
        chooseImage(IMAGE_REQUEST_CODE_1);
        initListener();


    }

    //选择图片
    private void chooseImage(int iamge_request_code) {
        Intent intent;
        if(Build.VERSION.SDK_INT < 19){
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        }else{
            intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        }
        startActivityForResult(intent, iamge_request_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_REQUEST_CODE_1:
                //设置图片显示
                if(data!=null){
                    safeImageView.setImageURI(data.getData());

                    //将选择的图片存为Bitmap格式
                    try {
                        safeImageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    setContentView(R.layout.test_loading_background);
                    loadingDialog = DialogUtils.createLoadingDialog(CheckWithAlbumActivity.this, "检测中...");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            check();
                        }
                    }, 500);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 3000);
                }else{
                    finish();
                }



                break;
            default:
                break;
        }
    }

    private void initView() {
        topBar = findViewById(R.id.topBar_checkSafeQr_check_album);
        safeImageView = findViewById(R.id.checkSafeQr_check_album_safeImageView);
//        waterMarkImageView = findViewById(R.id.checkSafeQr_check_album_waterMarkImageView);
//        chooseImageButton = findViewById(R.id.checkSafeQr_check_album_chooseImageButton);
//        checkQrButton = findViewById(R.id.checkSafeQr_check_album_checkQrButton);

    }

    private void initData() {
        openCVNativeManager = new OpenCVNativeManager();
    }

    private void initListener() {

        topBar.getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        chooseImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chooseImage(IMAGE_REQUEST_CODE_1);
//            }
//        });
//
//
//        checkQrButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                check();
//            }
//        });

    }

    private void check() {
        if (safeImageBitmap == null) {
            Toast.makeText(getApplicationContext(), "请选择要检测的图像", Toast.LENGTH_SHORT).show();
        } else {
            //判断图片中是否含有二维码
            Bitmap bitmap = safeImageBitmap;
            final Result result = QrUtils.decodeBarcodeRGB(bitmap);//二维码中的信息
            if (result == null) {
                Toast.makeText(getApplicationContext(), "图片中不含有二维码", Toast.LENGTH_SHORT).show();
                c.clean();
            } else {
//                loadingDialog = DialogUtils.createLoadingDialog(CheckWithAlbumActivity.this, "检测中...");
                openCVNativeManager.javaCheckQrWatermark(safeImageBitmap, new CheckWatermarkImpl() {
                    @Override
                    public void check(final Bitmap b) {
                        CheckWithAlbumActivity.this.waterMarkBitmap = b;
                        DialogUtils.closeDialog(loadingDialog);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (waterMarkBitmap != null) {
//                                            waterMarkImageView.setImageBitmap(waterMarkBitmap);
                                    //检测相似度
                                    ImgUtils.checkSimilarity(getApplicationContext(), waterMarkBitmap, result.getText());
                                } else {
                                    Toast.makeText(getApplicationContext(), "检测失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }
    }



}
