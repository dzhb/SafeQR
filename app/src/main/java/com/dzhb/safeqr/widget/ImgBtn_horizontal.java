package com.dzhb.safeqr.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzhb.safeqr.R;


public class ImgBtn_horizontal extends LinearLayout {

    private ImageView mImgView = null;
    private TextView mTextView = null;
    private Context mContext;


    public ImgBtn_horizontal(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        LayoutInflater.from(context).inflate(R.layout.widget_imagebutton_horizontal, this, true);
        mContext = context;
        mImgView = (ImageView) findViewById(R.id.imageButtonHorizontal_image);
        mTextView = (TextView) findViewById(R.id.imageButtonHorizontal_text);


        //自定义属性并赋值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImgBtn_horizontal);
        int image = typedArray.getResourceId(R.styleable.ImgBtn_horizontal_image, 0);
        String text = typedArray.getString(R.styleable.ImgBtn_horizontal_text);
        float textSize = typedArray.getDimension(R.styleable.ImgBtn_horizontal_textSize,15);
        int textColor = typedArray.getColor(R.styleable.ImgBtn_horizontal_textColor, 0x38ad5a);
        //释放资源
        typedArray.recycle();

        mImgView.setBackgroundResource(image);
        mTextView.setText(text);
        mTextView.setTextSize(textSize);
        mTextView.setTextColor(textColor);
    }


}