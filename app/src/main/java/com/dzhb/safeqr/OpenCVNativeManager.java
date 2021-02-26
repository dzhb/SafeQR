package com.dzhb.safeqr;


import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class OpenCVNativeManager {


    static {
        System.loadLibrary("native-lib");
    }

    /**
     * 添加文字水印
     *
     * @param matAddrSrcImage  原始图像
     * @param matAddrDestImage 含有水印的图像
     * @param text             要添加的文本内容
     */
    public native void addTextWatermark(long matAddrSrcImage, long matAddrDestImage, String text);

    public void javeAddTextWatermark(final Bitmap origImage, final String text, final JoinWatermarkImpl join) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Mat origMat = new Mat();
                Mat destMat = new Mat();
                Utils.bitmapToMat(origImage, origMat);//Bitmap转OpenCV的Mat
                addTextWatermark(origMat.getNativeObjAddr(), destMat.getNativeObjAddr(), text);
                Bitmap bitImage = Bitmap.createBitmap(destMat.width(), destMat.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(destMat, bitImage);//OpenCV的Mat转Bitmap显示
                join.join(bitImage);
            }
        }).start();
    }


    /**
     * 检测文本水印
     *
     * @param matAddrSrcImage
     * @param matAddrDestImage
     */
    public native void checkTextWatermark(long matAddrSrcImage, long matAddrDestImage);

    public void javeCheckTextWatermark(final Bitmap origImage, final CheckWatermarkImpl checkWatermark) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Mat origMat = new Mat();
                Mat destMat = new Mat();
                Utils.bitmapToMat(origImage, origMat);//Bitmap转OpenCV的Mat
                checkTextWatermark(origMat.getNativeObjAddr(), destMat.getNativeObjAddr());
                Bitmap bitImage = Bitmap.createBitmap(destMat.width(), destMat.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(destMat, bitImage);//OpenCV的Mat转Bitmap显示
                checkWatermark.check(bitImage);
            }
        }).start();

    }

    /**
     * 添加二维码水印
     *
     * @param matAddrSrcImageA 原始载体图像
     * @param matAddrSrcImageB 原始二维码图像
     * @param matAddrDestImage 添加了二维码水印的图像
     */
    public native void addQrWatermark(long matAddrSrcImageA, long matAddrSrcImageB, long matAddrDestImage);

    public void javaAddQrWatermark(final Bitmap origImageA, final Bitmap origImageB, final JoinWatermarkImpl joinWatermark) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Bitmap bitImage = origImageA;

                for (int i = 0; i < 5; i++) {
                    Mat origMatA = new Mat();
                    Mat origMatB = new Mat();
                    Mat destMat = new Mat();

//                    Utils.bitmapToMat(origImageA, origMatA);//Bitmap转OpenCV的Mat
                    Utils.bitmapToMat(bitImage, origMatA);

                    Utils.bitmapToMat(origImageB, origMatB);
                    addQrWatermark(origMatA.getNativeObjAddr(), origMatB.getNativeObjAddr(), destMat.getNativeObjAddr());
                    bitImage = Bitmap.createBitmap(destMat.width(), destMat.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(destMat, bitImage);//OpenCV的Mat转Bitmap显示
//        return bitImage;
                }
                joinWatermark.join(bitImage);

            }
        }).start();

    }

    /**
     * 检测二维码水印
     *
     * @param matAddrSrcImage
     * @param matAddrDestImage
     */
    public native void checkQRWatermark(long matAddrSrcImage, long matAddrDestImage);

    public void javaCheckQrWatermark(final Bitmap originImage, final CheckWatermarkImpl checkWatermark) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Mat origMat = new Mat();
                Mat destMat = new Mat();
                Utils.bitmapToMat(originImage, origMat);//Bitmap转OpenCV的Mat
                checkQRWatermark(origMat.getNativeObjAddr(), destMat.getNativeObjAddr());
                Bitmap bitImage = Bitmap.createBitmap(destMat.width(), destMat.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(destMat, bitImage);//OpenCV的Mat转Bitmap显示
                checkWatermark.check(bitImage);
            }
        }).start();
    }
}
