package com.dzhb.safeqr.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzhb.safeqr.R;

public class ImgBtn_vertical extends LinearLayout {
    private ImageView mImgView = null;
    private TextView mTextView = null;
    private Context mContext;


    public ImgBtn_vertical(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        LayoutInflater.from(context).inflate(R.layout.widget_imagebutton_vertical, this, true);
        mContext = context;
        mImgView = (ImageView)findViewById(R.id.imageButtonVertical_image);
        mTextView = (TextView)findViewById(R.id.imageButtonVertical_textView);


    }

    /*设置图片接口*/
    public void setImageResource(int resId){
        mImgView.setImageResource(resId);
    }

    /*设置文字接口*/
    public void setText(String str){
        mTextView.setText(str);
    }
    /*设置文字大小*/
    public void setTextSize(float size){
        mTextView.setTextSize(size);
    }
}
