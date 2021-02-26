package com.dzhb.safeqr.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzhb.safeqr.R;

public class TextAndLine extends LinearLayout {

    public TextView textView = null;
    public ImageView imageView = null;


    public TextAndLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.widget_text_and_line, this, true);
        textView = findViewById(R.id.widget_TextAndLine_text);
        imageView = findViewById(R.id.widget_TextAndLine_iamge);
        imageView.setBackgroundResource(R.drawable.line_gray);
        textView.setText("文本");
        textView.setTextColor(this.getResources().getColor(R.color.textColor_gray));
    }

    public void setView(String text, int textColor, int imageResource){
        setText(text);
        setTextColor(textColor);
        setImage(imageResource);
    }




    public void setTextColor(int textColor) {
        this.textView.setTextColor(this.getResources().getColor(textColor));
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    public void setImage(int imageReource){
        this.imageView.setBackgroundResource(imageReource);
    }


}
