#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <opencv2/imgcodecs.hpp>
#include<iostream>


using namespace std;
using namespace cv;


Mat getTransposeImage(const Mat &input) {
    Mat result;
    transpose(input, result);
    return result;
}


Mat addTextWatermarkSingleChannel(const Mat &input, const string &text) {
    double textSize = 0.0;
    int textWidth = 0;

    int minImgSize = input.rows > input.cols ? input.cols : input.rows;

    if (minImgSize < 150) {
        textSize = 1.0;
        textWidth = 1;
    } else if (minImgSize >= 150 && minImgSize < 300) {
        textSize = 1.5;
        textWidth = 2;
    } else if (minImgSize >= 300 && minImgSize < 400) {
        textSize = 2.5;
        textWidth = 3;
    } else if (minImgSize >= 400 && minImgSize < 650) {
        textSize = 3.0;
        textWidth = 3;
    } else if (minImgSize >= 650 && minImgSize < 1000) {
        textSize = 4.0;
        textWidth = 4;
    } else if (minImgSize >= 1000) {
        textSize = 4.5;;
        textWidth = 5;
    }

    int m = getOptimalDFTSize(input.rows);
    int n = getOptimalDFTSize(input.cols);

    Mat dst;
    copyMakeBorder(input, dst, 0, m - input.rows, 0, n - input.cols, BORDER_CONSTANT,
                   Scalar::all(0));

    Mat planes[] = {Mat_<float>(dst), Mat::zeros(dst.size(), CV_32F)};
    Mat complete;
    merge(planes, 2, complete);
    dft(complete, complete);

    double minv = 0.0, maxv = 0.0;
    double *minp = &minv;
    double *maxp = &maxv;
    minMaxIdx(complete, minp, maxp);

    int meanvalue = mean(complete)[0], num;
    if (meanvalue > 128)
        num = 0;
    else
        num = 255;

    num = 0;
    putText(complete, text, Point(input.cols * 0.2222, input.rows * 0.2222),
            FONT_HERSHEY_PLAIN, textSize, Scalar(num, num, num), textWidth);
    flip(complete, complete, -1);
    putText(complete, text, Point(input.cols * 0.2222, input.rows * 0.2222),
            FONT_HERSHEY_PLAIN, textSize, Scalar(num, num, num), textWidth);
    flip(complete, complete, -1);

    idft(complete, complete);
    split(complete, planes);
    magnitude(planes[0], planes[1], planes[0]);
    Mat result = planes[0];
    result = result(Rect(0, 0, input.cols, input.rows));

    normalize(result, result, 0, 1, CV_MINMAX);

    return result;
}


Mat addTextWatermarkColorImage(const Mat &inputImage, const string &text) {
    int col = inputImage.cols;
    int row = inputImage.rows;
    Mat resultshow;

    if (row < col) {
        Mat temp1 = getTransposeImage(inputImage);
        Mat channelsrc[3], channelresult[3];
        split(temp1, channelsrc);
        channelresult[0] = addTextWatermarkSingleChannel(channelsrc[0], text);
        channelresult[1] = addTextWatermarkSingleChannel(channelsrc[1], text);
        channelresult[2] = addTextWatermarkSingleChannel(channelsrc[2], text);
        Mat temp2;
        merge(channelresult, 3, temp2);
        resultshow = getTransposeImage(temp2);
    } else {
        Mat channelsrc[3], channelresult[3];
        split(inputImage, channelsrc);
        channelresult[0] = addTextWatermarkSingleChannel(channelsrc[0], text);
        channelresult[1] = addTextWatermarkSingleChannel(channelsrc[1], text);
        channelresult[2] = addTextWatermarkSingleChannel(channelsrc[2], text);
        merge(channelresult, 3, resultshow);
    }
    return resultshow;
}


Mat getWaterMarkSingleChannel(const Mat &input) {
    int m = getOptimalDFTSize(input.rows);
    int n = getOptimalDFTSize(input.cols);

    Mat dst;
    copyMakeBorder(input, dst, 0, m - input.rows, 0, n - input.cols, BORDER_CONSTANT,
                   Scalar::all(0));

    Mat planes[] = {Mat_<float>(dst), Mat::zeros(dst.size(), CV_32F)};
    Mat complete;
    merge(planes, 2, complete);

    dft(complete, complete);

    split(complete, planes);
    magnitude(planes[0], planes[1], planes[0]);
    Mat result = planes[0];
    result += 1;
    log(result, result);
    result = result(Rect(0, 0, input.cols, input.rows));
    normalize(result, result, 0, 1, CV_MINMAX);

    return result;
}


Mat getWatermarkColorImage(const Mat &input) {
    Mat channelsrc[3], channelresult[3];
    split(input, channelsrc);

    channelresult[0] = getWaterMarkSingleChannel(channelsrc[0]);
    channelresult[1] = getWaterMarkSingleChannel(channelsrc[1]);
    channelresult[2] = getWaterMarkSingleChannel(channelsrc[2]);

    Mat resultshow;
    merge(channelresult, 3, resultshow);

    return resultshow;
}

void ss(Mat &a) {
    for (int i = 0; i < a.cols * a.rows; i++) {
        if (a.data[i] != 0)
            a.data[i] = 255;
    }
}


//CheckMode: 0代表去除黑区域，1代表去除白区域; NeihborMode：0代表4邻域，1代表8邻域;
Mat RemoveSmallRegion(Mat &Src, Mat &Dst, int AreaLimit, int CheckMode, int NeihborMode) {
    int RemoveCount = 0;       //记录除去的个数
    //记录每个像素点检验状态的标签，0代表未检查，1代表正在检查,2代表检查不合格（需要反转颜色），3代表检查合格或不需检查
    Mat Pointlabel = Mat::zeros(Src.size(), CV_8UC1);

    if (CheckMode == 1) {
        cout << "Mode: 去除小区域. ";
        for (int i = 0; i < Src.rows; ++i) {
            uchar *iData = Src.ptr<uchar>(i);
            uchar *iLabel = Pointlabel.ptr<uchar>(i);
            for (int j = 0; j < Src.cols; ++j) {
                if (iData[j] < 10) {
                    iLabel[j] = 3;
                }
            }
        }
    } else {
        cout << "Mode: 去除孔洞. ";
        for (int i = 0; i < Src.rows; ++i) {
            uchar *iData = Src.ptr<uchar>(i);
            uchar *iLabel = Pointlabel.ptr<uchar>(i);
            for (int j = 0; j < Src.cols; ++j) {
                if (iData[j] > 10) {
                    iLabel[j] = 3;
                }
            }
        }
    }

    vector<Point2i> NeihborPos;  //记录邻域点位置
    NeihborPos.push_back(Point2i(-1, 0));
    NeihborPos.push_back(Point2i(1, 0));
    NeihborPos.push_back(Point2i(0, -1));
    NeihborPos.push_back(Point2i(0, 1));
    if (NeihborMode == 1) {
        cout << "Neighbor mode: 8邻域." << endl;
        NeihborPos.push_back(Point2i(-1, -1));
        NeihborPos.push_back(Point2i(-1, 1));
        NeihborPos.push_back(Point2i(1, -1));
        NeihborPos.push_back(Point2i(1, 1));
    } else cout << "Neighbor mode: 4邻域." << endl;
    int NeihborCount = 4 + 4 * NeihborMode;
    int CurrX = 0, CurrY = 0;
    //开始检测
    for (int i = 0; i < Src.rows; ++i) {
        uchar *iLabel = Pointlabel.ptr<uchar>(i);
        for (int j = 0; j < Src.cols; ++j) {
            if (iLabel[j] == 0) {
                //********开始该点处的检查**********
                vector<Point2i> GrowBuffer;                                      //堆栈，用于存储生长点
                GrowBuffer.push_back(Point2i(j, i));
                Pointlabel.at<uchar>(i, j) = 1;
                int CheckResult = 0;                                               //用于判断结果（是否超出大小），0为未超出，1为超出

                for (int z = 0; z < GrowBuffer.size(); z++) {

                    for (int q = 0;
                         q < NeihborCount; q++)                                      //检查四个邻域点
                    {
                        CurrX = GrowBuffer.at(z).x + NeihborPos.at(q).x;
                        CurrY = GrowBuffer.at(z).y + NeihborPos.at(q).y;
                        if (CurrX >= 0 && CurrX < Src.cols && CurrY >= 0 &&
                            CurrY < Src.rows)  //防止越界
                        {
                            if (Pointlabel.at<uchar>(CurrY, CurrX) == 0) {
                                GrowBuffer.push_back(Point2i(CurrX, CurrY));  //邻域点加入buffer
                                Pointlabel.at<uchar>(CurrY,
                                                     CurrX) = 1;           //更新邻域点的检查标签，避免重复检查
                            }
                        }
                    }

                }
                if (GrowBuffer.size() > AreaLimit)
                    CheckResult = 2;                 //判断结果（是否超出限定的大小），1为未超出，2为超出
                else {
                    CheckResult = 1;
                    RemoveCount++;
                }
                for (int z = 0; z < GrowBuffer.size(); z++)                         //更新Label记录
                {
                    CurrX = GrowBuffer.at(z).x;
                    CurrY = GrowBuffer.at(z).y;
                    Pointlabel.at<uchar>(CurrY, CurrX) += CheckResult;
                }
                //********结束该点处的检查**********
            }
        }
    }

    CheckMode = 255 * (1 - CheckMode);
    //开始反转面积过小的区域
    for (int i = 0; i < Src.rows; ++i) {
        uchar *iData = Src.ptr<uchar>(i);
        uchar *iDstData = Dst.ptr<uchar>(i);
        uchar *iLabel = Pointlabel.ptr<uchar>(i);
        for (int j = 0; j < Src.cols; ++j) {
            if (iLabel[j] == 2) {
                iDstData[j] = CheckMode;
            } else if (iLabel[j] == 3) {

                iDstData[j] = iData[j];
            }
        }
    }

    cout << RemoveCount << " objects removed." << endl;
    return Dst;
}

Mat Arnold (Mat a)
{
    Mat b;
    a.copyTo (b);
    for (int i = 0; i < a.cols; i++)
    {
        for (int j = 0; j < a.rows; j++)
        {
            b.at<uchar> ((i + 2 * j) % a.cols, (i + j) % a.cols) = a.at<uchar> (j, i);
        }
    }
    return b;
}

Mat iArnold (Mat a)
{
    Mat b;
    a.copyTo (b);
    for (int i = 0; i < a.cols; i++)
    {
        for (int j = 0; j < a.rows; j++)
        {
            b.at<uchar> ((-i + j + a.cols) % a.cols, (2 * i - j + a.cols) % a.cols) = a.at<uchar> (j, i);
        }
    }
    return b;
}


extern "C" JNIEXPORT jstring

JNICALL
Java_com_dzhb_safeqr_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_dzhb_safeqr_OpenCVNativeManager_addTextWatermark(JNIEnv *env, jobject instance,
                                                          jlong matAddrSrcImage,
                                                          jlong matAddrDestImage, jstring text_) {
    const char *text = env->GetStringUTFChars(text_, 0);

    Mat &srcImage = *(Mat *) matAddrSrcImage;
    Mat &destImage = *(Mat *) matAddrDestImage;
    cv::cvtColor(srcImage, srcImage, CV_BGR2RGB);

    //添加水印
    Mat re = addTextWatermarkColorImage(srcImage, text);
    re *= 255;
    re.convertTo(re, CV_8UC3);

    cv::cvtColor(re, re, CV_RGB2BGR);
    destImage = re;

    env->ReleaseStringUTFChars(text_, text);
}extern "C"
JNIEXPORT void JNICALL
Java_com_dzhb_safeqr_OpenCVNativeManager_addQrWatermark(JNIEnv *env, jobject instance,
                                                        jlong matAddrSrcImageA,
                                                        jlong matAddrSrcImageB,
                                                        jlong matAddrDestImage) {

    // TODO
    Mat &srcImageA = *(Mat *) matAddrSrcImageA;
    Mat &srcImageB = *(Mat *) matAddrSrcImageB;
    Mat &destImage = *(Mat *) matAddrDestImage;
    cv::cvtColor(srcImageA, srcImageA, CV_BGR2RGB);
    cv::cvtColor(srcImageB, srcImageB, CV_BGR2RGB);

//读入宿主图像a和水印图像b
    Mat a = srcImageA;
    Mat b = srcImageB;
    cvtColor(b, b, CV_BGR2GRAY);//将图像b转为单通道灰度图

    resize(b, b, Size(60, 60));

    //TODO
    for(int i = 0; i < 30; i++) b = Arnold(b);

    resize(a, a, Size(480, 480));


    //将宿主图像转为YUV格式，只处理Y（亮度）通道
    Mat aYUV;
    cvtColor(a, aYUV, CV_BGR2YUV);
    aYUV.convertTo(aYUV, CV_32FC3);//dct变换对图片的格式需求
    vector<Mat> channels;
    split(aYUV, channels);
    Mat src = channels[0];

    //分块dct，每块的大小是8×8
    Rect rect;
    rect.height = 8;
    rect.width = 8;

    Mat dst;
    src.copyTo(dst);


    //嵌入水印，每一块处理一次，每一块隐藏一个像素
    for (int i = 0; i < a.cols / 8; i++) {
        for (int j = 0; j < a.rows / 8; j++) {
            rect.x = 8 * i;
            rect.y = 8 * j;

            dct(src(rect), dst(rect));

            //r1>r2记录为黑 反之为白
            float r1 = dst(rect).at<float>(3, 4);
            float r2 = dst(rect).at<float>(4, 3);
            float k = abs(r1 - r2) + 5.0f;

            if (b.at<uchar>(j, i) >= 128) {
                if (r1 >= r2)
                    dst(rect).at<float>(3, 4) -= k;
            } else {
                if (r1 <= r2)
                    dst(rect).at<float>(3, 4) += k;
            }
        }
    }

    //逆dct变换
    for (int i = 0; i < a.cols / 8; i++) {
        for (int j = 0; j < a.rows / 8; j++) {
            rect.x = 8 * i;
            rect.y = 8 * j;
            idct(dst(rect), src(rect));
        }
    }

    //变回正常的图像，然后保存
    channels[0] = src;
    merge(channels, aYUV);
    aYUV.convertTo(aYUV, CV_8UC3);
    cvtColor(aYUV, a, CV_YUV2BGR);


    cvtColor(a, a, CV_RGB2BGR);//恢复颜色
    destImage = a;


}extern "C"
JNIEXPORT void JNICALL
Java_com_dzhb_safeqr_OpenCVNativeManager_checkTextWatermark(JNIEnv *env, jobject instance,
                                                            jlong matAddrSrcImage,
                                                            jlong matAddrDestImage) {

    // TODO
    Mat &srcImage = *(Mat *) matAddrSrcImage;
    Mat &destImage = *(Mat *) matAddrDestImage;
    cv::cvtColor(srcImage, srcImage, CV_BGR2RGB);


    Mat re2 = getWatermarkColorImage(srcImage);
    re2 *= 255;
    re2.convertTo(re2, CV_8UC3);
    cvtColor(re2, re2, CV_BGR2GRAY);
    Mat re3 = re2.clone();
    GaussianBlur(re2, re2, Size(3, 3), 0);


    Canny(re2, re2, 100, 100);
    GaussianBlur(re2, re2, Size(3, 3), 0);


    Mat ele = getStructuringElement(MORPH_DILATE, Size(3, 3));
    morphologyEx(re2, re2, MORPH_OPEN, ele);
    ss(re2);

    re2 = RemoveSmallRegion(re2, re2, 30, 1, 1);


    re2 = re2(Rect(0, 0, re2.cols, re2.rows / 2));

    vector<vector<Point> > contours;
    vector<Vec4i> hierarchy;
    Canny(re2, re2, 100, 300);


    findContours(re2, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE);

    vector<Point> Points;
    for (auto pts : contours) {
        Points.insert(Points.end(), pts.begin(), pts.end());
    }
    auto box = minAreaRect(Points);


//    box就是最后检测到水印区域
    Mat re4 = re3(box.boundingRect());
    rectangle(re3, box.boundingRect(), Scalar(255, 0, 0));

    destImage = re4;
}extern "C"
JNIEXPORT void JNICALL
Java_com_dzhb_safeqr_OpenCVNativeManager_checkQRWatermark(JNIEnv *env, jobject instance,
                                                          jlong matAddrSrcImage,
                                                          jlong matAddrDestImage) {

    // TODO
    Mat &srcImage = *(Mat *) matAddrSrcImage;
    Mat &destImage = *(Mat *) matAddrDestImage;
    cv::cvtColor(srcImage, srcImage, CV_BGR2RGB);

    Mat a;
    a = srcImage;
    Mat aYUV;
    vector<Mat> channels;

    //分块dct，每块的大小是8×8
    Rect rect;
    rect.height = 8;
    rect.width = 8;

    cvtColor(a, aYUV, CV_RGB2YUV);
    aYUV.convertTo(aYUV, CV_32FC3);
    split(aYUV, channels);
    Mat src;
    src = channels[0];
    Mat dst;
    src.copyTo(dst);

    Mat re(60, 60, CV_8UC1, Scalar(0));
    for (int i = 0; i < a.cols / 8; i++) {
        for (int j = 0; j < a.rows / 8; j++) {
            rect.x = 8 * i;
            rect.y = 8 * j;

            dct(src(rect), dst(rect));

            //r1>r2记录为黑 反之为白
            float r1 = dst(rect).at<float>(3, 4);
            float r2 = dst(rect).at<float>(4, 3);
            if (r1 > r2)
                re.at<uchar>(j, i) = 0;
            else
                re.at<uchar>(j, i) = 255;
        }
    }

    for(int i = 0; i < 30; i++) re = iArnold(re);

    resize(re, re, re.size() * 8, 0.0, 0.0, 0);

    destImage = re;

}