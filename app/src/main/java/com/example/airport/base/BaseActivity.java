package com.example.airport.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.airport.utils.PhoneUtil;

import butterknife.ButterKnife;

/**
 * Created by dell on 2018/1/18.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    protected Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //方向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setBeforeLayout();
        mContext = this;
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        ButterKnife.bind(this);
        initView();
        initData();
        setListener();
    }


    protected void setBeforeLayout() {
        PhoneUtil.getDispaly(this);
    }

    /**
     * 返回当前界面布局文件
     */
    protected abstract int getLayoutId();

    /**
     * 此方法描述的是： 初始化所有view
     */
    protected abstract void initView();

    /**
     * 此方法描述的是： 初始化所有数据的方法
     */
    protected abstract void initData();

    /**
     * 此方法描述的是： 设置所有事件监听
     */
    protected abstract void setListener();
    /**
     * 显示toast
     *
     * @param resId
     */
    public void showToast(final int resId) {
        showToast(getString(resId));
    }

    /**
     * 显示toast
     *
     * @param resStr
     * @return Toast对象，便于控制toast的显示与关闭
     */
    public void showToast(final String resStr) {

        if (TextUtils.isEmpty(resStr)) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(BaseActivity.this, resStr, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
