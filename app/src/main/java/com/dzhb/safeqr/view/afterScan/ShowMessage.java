package com.dzhb.safeqr.view.afterScan;


import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.dzhb.safeqr.BaseActivity;
import com.dzhb.safeqr.R;
import com.dzhb.safeqr.view.HomeActivity;
import com.dzhb.safeqr.widget.TopBar;

/**
 * 检测结束后，如果二维码信息为纯文本，显示文本信息
 */

public class ShowMessage extends BaseActivity {

    private TopBar topBar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.layout_afterscan_showmessage);

        topBar = findViewById(R.id.topBar_afterScan_showMessage);
        textView = findViewById(R.id.afterScan_showMessage_tv);
        //设置textView可选择
        textView.setTextIsSelectable(true);

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");

        if(message != null){
            textView.setText(message);
        }

        topBar.getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        textView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                ClipboardManager cmb = (ClipboardManager) getApplicationContext().getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
//                cmb.setText(textView.getText().toString().trim()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
////        ToastUtils.showTextToast(context,"复制文本成功");
//                return false;
//            }
//        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(ShowMessage.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);

    }
}
