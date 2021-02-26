package com.dzhb.safeqr.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;


import com.blankj.utilcode.util.ImageUtils;
import com.dzhb.safeqr.ActivityCollector;
import com.dzhb.safeqr.R;
import com.dzhb.safeqr.view.afterScan.ShowDangerActivity;
import com.dzhb.safeqr.view.afterScan.ShowSafeActivity;
import com.dzhb.safeqr.view.checkSafeQR.CheckWithAlbumActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

public class ImgUtils {

    /**
     * 保存文件到指定路径
     */
    public static void saveImage(final Context context, Bitmap bitmap, @Nullable String imageName) {
        //首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "safeQr");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = null;
        if (imageName == null) {
            fileName = System.currentTimeMillis() + ".jpg";
        } else {
            fileName = imageName;
        }
        File file = new File(appDir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
                }
            }).start();
        }

        //其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //最后通知图库更新
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);//扫描单个文件
        intent.setData(Uri.fromFile(file));//给图片的绝对路径
        context.sendBroadcast(intent);

        Toast.makeText(context, "保存成功" + Uri.fromFile(file), Toast.LENGTH_LONG).show();
    }


    /**
     * 由图片路径获取图片并在imageView上显示图片
     */
    public static void showImage(String imagePath, ImageView imageView) {

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(imagePath));

            //压缩图片
            Bitmap cBitmap = ImageUtils.compressBySampleSize(BitmapFactory.decodeStream(fis), imageView.getWidth());

//            imageView.setImageBitmap(BitmapFactory.decodeStream(fis));//未压缩
            imageView.setImageBitmap(cBitmap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 检测水印图片与测试图片的相似度，判断是否安全并跳转到相应界面
     * @param context
     * @param waterMarkBitmap 提取的二维码图像
     * @param qrMessage 解析出的二维码内容
     */
    public static void checkSimilarity(final Context context, Bitmap waterMarkBitmap, final String qrMessage) {

        Bitmap testBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.test3);
        SimilarityHelper similarityHelper = SimilarityHelper.instance();

        similarityHelper.similarity(testBitmap, waterMarkBitmap, new SimilarityCallBack() {

            @Override
            public void onSimilarityStart() {

            }

            @Override
            public void onSimilaritySuccess(int similarity, int different) {
//                        tvContent.setText("匹配度为 ： " + ((float)similarity / (similarity + different) * 100) + " %");
                float similariValue = ((float) similarity / (similarity + different) * 100);
//                Toast.makeText(context, "相似度为 ： " + similariValue + " %", Toast.LENGTH_SHORT).show();
                if (similariValue >= 75) {
//                    Toast.makeText(context, "该二维码含有安全水印", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, ShowSafeActivity.class);
                    intent.putExtra("message", qrMessage);
                    startActivity(intent);
                } else {
                    if(c.count_up == 1){
                        Intent intent = new Intent(context, ShowSafeActivity.class);
                        intent.putExtra("message", qrMessage);
                        startActivity(intent);
                        c.clean();

                    }else if(c.count_down == 1){
                        c.clean();
                        ActivityCollector.finishAll();

                    }else {
                        //                    Toast.makeText(context, "该二维码不含有安全水印", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, ShowDangerActivity.class);
                        intent.putExtra("message", qrMessage);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onSimilarityError(String reason) {
                Toast.makeText(context, "检测失败", Toast.LENGTH_LONG).show();

            }
        }, context);
    }


}


