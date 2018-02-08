package com.example.airport.splash;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.airport.R;
import com.example.airport.base.BaseActivity;
import com.example.airport.base.handler.BaseHandler;
import com.example.airport.main.MainActivity;
import com.example.airport.utils.JumpItent;
import com.example.airport.utils.PermissionsChecker;

public class SplashView extends BaseActivity implements BaseHandler.HandleMessage {

    //权限列表
    static final String[] PERMISSIONS = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA
    };

    //权限
    private PermissionsChecker mChecker;
    private static final int PERMISSION_REQUEST_CODE = 0; // 系统权限管理页面的参数

    //handler
    private static final int REFRESH_COMPLETE = 0X153;
    private Handler outHandler = new BaseHandler<>(SplashView.this);

    /**
     * 返回当前界面布局文件
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash_view;
    }

    /**
     * 此方法描述的是： 初始化所有view
     */
    @Override
    protected void initView() {
        mChecker = new PermissionsChecker(this);
        if (mChecker.lacksPermissions(PERMISSIONS)) {
            //请求权限
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        } else {
            // 全部权限都已获取
            JumpItent.jump(SplashView.this, MainActivity.class);
            finish();
        }
    }

    /**
     * 此方法描述的是： 初始化所有数据的方法
     */
    @Override
    protected void initData() {

    }

    /**
     * 此方法描述的是： 设置所有事件监听
     */
    @Override
    protected void setListener() {

    }//请求权限结果

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
//            startAnim();
            JumpItent.jump(SplashView.this, MainActivity.class);
            finish();
        } else {
            outHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000);
        }
    }


    //含有全部的权限
    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case REFRESH_COMPLETE:
                finish();
                break;
        }
    }

}
