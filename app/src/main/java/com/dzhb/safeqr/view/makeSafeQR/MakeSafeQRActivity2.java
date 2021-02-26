package com.dzhb.safeqr.view.makeSafeQR;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.dzhb.safeqr.BaseActivity;
import com.dzhb.safeqr.JoinWatermarkImpl;
import com.dzhb.safeqr.OpenCVNativeManager;
import com.dzhb.safeqr.R;
import com.dzhb.safeqr.util.DialogUtils;
import com.dzhb.safeqr.util.ImgUtils;
import com.dzhb.safeqr.util.QrUtils;
import com.dzhb.safeqr.view.HomeActivity;
import com.dzhb.safeqr.view.checkSafeQR.CheckSafeQRActivity;
import com.dzhb.safeqr.widget.ImgBtn_horizontal;
import com.dzhb.safeqr.widget.TextAndLine;
import com.dzhb.safeqr.widget.TopBar;
import com.google.zxing.Result;

import java.io.FileNotFoundException;

public class MakeSafeQRActivity2 extends BaseActivity {

    private final int IMAGE_REQUEST_CODE_1 = 1;

    private OpenCVNativeManager openCVNativeManager;

    private Dialog loadingDialog;

    private Button withoutQrImageButton;
    private Button withQrImageButton;
    private Button chooseImageButton;
    private Button beganMakeButton;
    private Button saveImageButton;
    private Button reBuildButton;
    private Button makeSafeQrButton;
    private ImageView qrImageView;
    private Bitmap originQrImageBitmap;
    private Bitmap safeQrImageBitmap;
    private TextView chooseMethodTextView;

    private Bitmap testBitmap;

    private LinearLayout linearLayout_chooseMethod;
    private LinearLayout linearLayout_makeWIthSring;
    private RelativeLayout relativeLayout_chooseMethod_Right;

    private TextAndLine textAndLine_text;
    private TextAndLine textAndLine_website;
    private TextAndLine textAndLine_image;
    private TextAndLine textAndLine_file;
    private TextAndLine textAndLine_audio;

    private ScrollView scrollView_textInput;
    private EditText editText_text;
    private EditText editText_website;
    private TextView TextView_file;
    private TextView TextView_file2;
    private TextView TextView_image;
    private TextView TextView_image2;
    private TextView TextVIew_audio;
    private TextView TextVIew_audio2;


    private ImgBtn_horizontal imgBtn_horizontal_checkSafeQr;

    private Result result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_makesafeqr2);

        TopBar topBar = findViewById(R.id.topBar_makeSafeQr2_makeSafeQR);
        topBar.getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initView();
        initData();
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
//                qrImageView.setImageURI(data.getData());
                //将选择的图片存为Bitmap格式
                if(data != null){
                    try {
                        originQrImageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    addWatermark();

                    if(result !=null){
                        chooseMethodTextView.setText("  导入成功！");
                        chooseImageButton.setText("重新选择");
                        relativeLayout_chooseMethod_Right.setVisibility(View.GONE);
                        beganMakeButton.setVisibility(View.GONE);
                        withoutQrImageButton.setVisibility(View.GONE);
                    }

                }


                break;
            default:
                break;
        }
    }

    public void initView() {
        withoutQrImageButton = findViewById(R.id.make_makeSafeQr2_makeSafeQrWithoutQrImage);
        withQrImageButton = findViewById(R.id.make_makeSafeQr2_makeSafeQrWithQrImage);
        chooseImageButton = findViewById(R.id.make_makeSafeQr2_chooseImageButton);
        beganMakeButton = findViewById(R.id.make_makeSafeQr2_beganMakeButton);
        saveImageButton = findViewById(R.id.make_makeSafeQr2_saveButton);
        reBuildButton = findViewById(R.id.make_makeSafeQr2_remakeButton);
        qrImageView = findViewById(R.id.make_makeSafeQr2_qrImageView);
        makeSafeQrButton = findViewById(R.id.make_makeSafeQr2_makeSafeQrButton);
        chooseMethodTextView = findViewById(R.id.make_makeSafeQr2_chooseMethod_textView);

        linearLayout_chooseMethod = findViewById(R.id.make_makeSafeQr2_LinearLayout_chooseMethod);
        linearLayout_makeWIthSring = findViewById(R.id.make_makeSafeQr2_Linearlayout_makeWithString);
        relativeLayout_chooseMethod_Right = findViewById(R.id.make_makeSafeQr2_chooseMethod_right);

        textAndLine_text = findViewById(R.id.make_makeSafeQr2_TextAndLine_text);
        textAndLine_website = findViewById(R.id.make_makeSafeQr2_TextAndLine_website);
        textAndLine_file = findViewById(R.id.make_makeSafeQr2_TextAndLine_file);
        textAndLine_image = findViewById(R.id.make_makeSafeQr2_TextAndLine_image);
        textAndLine_audio = findViewById(R.id.make_makeSafeQr2_TextAndLine_audio);

        scrollView_textInput = findViewById(R.id.make_makeSafeQr2_EditTextScrollView_text);
        editText_text = findViewById(R.id.make_makeSafeQr2_EditText_text);
        editText_website = findViewById(R.id.make_makeSafeQr2_EditText_website);
        TextView_file = findViewById(R.id.make_makeSafeQr2_TextView_file);
        TextView_file2 = findViewById(R.id.make_makeSafeQr2_TextView_file2);
        TextView_image = findViewById(R.id.make_makeSafeQr2_TextView_image);
        TextView_image2 = findViewById(R.id.make_makeSafeQr2_TextView_image2);
        TextVIew_audio = findViewById(R.id.make_makeSafeQr2_TextView_audio);
        TextVIew_audio2 = findViewById(R.id.make_makeSafeQr2_TextView_audio2);


        imgBtn_horizontal_checkSafeQr = findViewById(R.id.make_makeSafeQr2_IBH_checkSafeQR);

    }

    private void initData() {
        openCVNativeManager = new OpenCVNativeManager();
        testBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.test3);
        initTextAndLine();
        initVis();
        linearLayout_makeWIthSring.setVisibility(View.GONE);
        textAndLine_text.setView("文本", R.color.textColor_blue, R.drawable.line_blue);
        scrollView_textInput.setVisibility(View.VISIBLE);
    }

    //初始化导航栏显示
    private void initTextAndLine() {
        textAndLine_text.setView("文本", R.color.textColor_gray, R.drawable.line_gray);
        textAndLine_website.setView("网址", R.color.textColor_gray, R.drawable.line_gray);
        textAndLine_file.setView("文件", R.color.textColor_gray, R.drawable.line_gray);
        textAndLine_image.setView("图片", R.color.textColor_gray, R.drawable.line_gray);
        textAndLine_audio.setView("音视频", R.color.textColor_gray, R.drawable.line_gray);

        makeSafeQrButton.setText(R.string.makeSafeQrActivity2_makeSafeQrButton1);
        originQrImageBitmap = null;
        safeQrImageBitmap = null;
        qrImageView.setImageBitmap(null);
        saveImageButton.setBackgroundResource(R.drawable.round_rectangle4_gray);
        reBuildButton.setBackgroundResource(R.drawable.round_rectangle4_graystroke);


    }

    private void initVis() {
        scrollView_textInput.setVisibility(View.GONE);
        editText_website.setVisibility(View.GONE);
        TextView_file.setVisibility(View.GONE);
        TextView_image.setVisibility(View.GONE);
        TextVIew_audio.setVisibility(View.GONE);
        TextView_file2.setVisibility(View.GONE);
        TextView_image2.setVisibility(View.GONE);
        TextVIew_audio2.setVisibility(View.GONE);

    }

    private void initListener() {
        withQrImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), MakeSafeQrWithQrImageActivity.class);
//                startActivity(intent);
                if (withQrImageButton.getVisibility() == View.VISIBLE) {
                    withQrImageButton.setVisibility(View.INVISIBLE);
                    chooseImageButton.setVisibility(View.VISIBLE);
                    beganMakeButton.setVisibility(View.INVISIBLE);
                    withoutQrImageButton.setVisibility(View.VISIBLE);
                }
            }
        });


        withoutQrImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (withoutQrImageButton.getVisibility() == View.VISIBLE) {
                    withoutQrImageButton.setVisibility(View.INVISIBLE);
                    beganMakeButton.setVisibility(View.VISIBLE);
                    withQrImageButton.setVisibility(View.VISIBLE);
                    chooseImageButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chooseImageButton.getVisibility() == View.VISIBLE) {
                    chooseImage(IMAGE_REQUEST_CODE_1);
                }
            }
        });

        beganMakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), MakeSafeQrWithStringActivity.class);
//                startActivity(intent);
                linearLayout_makeWIthSring.setVisibility(View.VISIBLE);
                linearLayout_chooseMethod.setVisibility(View.GONE);
            }
        });

        saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (safeQrImageBitmap != null)
                    ImgUtils.saveImage(getApplicationContext(), safeQrImageBitmap, null);
            }
        });

        reBuildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                addWatermark();
//                ToastUtils.showShort("重制完成");
                if(safeQrImageBitmap != null){
                    Intent intent = new Intent(MakeSafeQRActivity2.this, HomeActivity.class);
                    startActivity(intent);
                }


            }
        });

        textAndLine_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTextAndLine();
                textAndLine_text.setView("文本", R.color.textColor_blue, R.drawable.line_blue);
                initVis();
                editText_text.setHint("请输入文本信息");
                editText_text.setText(null);
                scrollView_textInput.setVisibility(View.VISIBLE);
                makeSafeQrButton.setBackgroundResource(R.drawable.round_rectangle5_blue);

            }
        });

        textAndLine_website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTextAndLine();
                textAndLine_website.setView("网址", R.color.textColor_blue, R.drawable.line_blue);
                initVis();
                editText_website.setText("http://");
                editText_website.setVisibility(View.VISIBLE);
                makeSafeQrButton.setBackgroundResource(R.drawable.round_rectangle5_blue);

            }
        });

        textAndLine_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTextAndLine();
                textAndLine_file.setView("文件", R.color.textColor_blue, R.drawable.line_blue);
                initVis();
                TextView_file.setVisibility(View.VISIBLE);
                TextView_file2.setVisibility(View.VISIBLE);

                makeSafeQrButton.setBackgroundResource(R.drawable.round_rectangle4_gray);
            }
        });

        textAndLine_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTextAndLine();
                textAndLine_image.setView("图片", R.color.textColor_blue, R.drawable.line_blue);
                initVis();
                TextView_image.setVisibility(View.VISIBLE);
                TextView_image2.setVisibility(View.VISIBLE);

                makeSafeQrButton.setBackgroundResource(R.drawable.round_rectangle4_gray);

            }
        });

        textAndLine_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTextAndLine();
                textAndLine_audio.setView("音视频", R.color.textColor_blue, R.drawable.line_blue);
                initVis();
                TextVIew_audio.setVisibility(View.VISIBLE);
                TextVIew_audio2.setVisibility(View.VISIBLE);

                makeSafeQrButton.setBackgroundResource(R.drawable.round_rectangle4_gray);

            }
        });

        makeSafeQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scrollView_textInput.getVisibility() == View.VISIBLE) {//文本
                    String text = editText_text.getText().toString();
                    if (text.length() < 1) {
                        Toast.makeText(getApplicationContext(), "文字信息不能为空", Toast.LENGTH_SHORT).show();
                        originQrImageBitmap = null;
                    } else {
                        //生成二维码图像
                        originQrImageBitmap = QrUtils.encodeQrBitmap(text, 400);
                        addWatermark();
                        makeSafeQrButton.setText(R.string.makeSafeQrActivity2_makeSafeQrButton2);
                    }
                } else if(editText_website.getVisibility() == View.VISIBLE){//网址
                    String text = editText_website.getText().toString();
                    if (text.length() < 1) {
                        Toast.makeText(getApplicationContext(), "文字信息不能为空", Toast.LENGTH_SHORT).show();
                        originQrImageBitmap = null;
                    } else {
                        //生成二维码图像
                        originQrImageBitmap = QrUtils.encodeQrBitmap(text, 400);
                        addWatermark();
                        makeSafeQrButton.setText(R.string.makeSafeQrActivity2_makeSafeQrButton2);
                    }
                }
            }
        });

        editText_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeSafeQrButton.setText(R.string.makeSafeQrActivity2_makeSafeQrButton1);
            }
        });

        editText_website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeSafeQrButton.setText(R.string.makeSafeQrActivity2_makeSafeQrButton1);
            }
        });


        imgBtn_horizontal_checkSafeQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MakeSafeQRActivity2.this, CheckSafeQRActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //生成含有水印的图像
    @SuppressLint("ResourceAsColor")
    private void addWatermark() {
        if (originQrImageBitmap == null) {
            Toast.makeText(getApplicationContext(), "二维码图像不能为空", Toast.LENGTH_SHORT).show();
        } else {
            Bitmap bitmap = originQrImageBitmap;
            result = QrUtils.decodeBarcodeRGB(bitmap);//二维码中的信息
            if (result == null) {
                Toast.makeText(getApplicationContext(), "请选择含有二维码的图片", Toast.LENGTH_LONG).show();
                qrImageView.setImageBitmap(null);
                saveImageButton.setBackgroundResource(R.drawable.round_rectangle4_gray);
                reBuildButton.setBackgroundResource(R.drawable.round_rectangle4_graystroke);
                reBuildButton.setTextColor(R.color.textColor_white);
                safeQrImageBitmap = null;
            } else {
                //根据二维码中的信息重新生成新的二维码图片
                originQrImageBitmap = QrUtils.encodeQrBitmap(result.getText(), 400);

                saveImageButton.setBackgroundResource(R.drawable.round_rectangle5_blue);
                reBuildButton.setBackgroundResource(R.drawable.round_rectangle5_bluestroke);
                reBuildButton.setTextColor(R.color.textColor_blue);

                loadingDialog = DialogUtils.createLoadingDialog(MakeSafeQRActivity2.this, "加载中...");
                openCVNativeManager.javaAddQrWatermark(originQrImageBitmap, testBitmap, new JoinWatermarkImpl() {
                    @Override
                    public void join(Bitmap b) {
                        MakeSafeQRActivity2.this.safeQrImageBitmap = b;
                        DialogUtils.closeDialog(loadingDialog);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (safeQrImageBitmap != null) {
                                    qrImageView.setImageBitmap(safeQrImageBitmap);

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

    //保证此界面返回的界面为首页
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(MakeSafeQRActivity2.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);

    }

}
