package com.example.airport;

import android.support.multidex.MultiDexApplication;

import com.example.airport.config.RobotInfo;
import com.example.airport.database.base.BaseManager;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by dell on 2018/1/25.
 */

public class AirApplication extends MultiDexApplication {
    private static AirApplication instance;

    public static AirApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initXunFei();
        BaseManager.initOpenHelper(this);
        //初始化本机
        RobotInfo.getInstance().init(this);
    }

    /**
     * 讯飞
     */
    public void initXunFei() {
        /**
         * 讯飞初始化
         * 原始app_id = 5a27877d
         */
        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID + "=5a27877d" + "," + SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
    }
}
