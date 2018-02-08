package com.example.airport.presenter;


import android.util.Log;

import com.example.airport.modle.SerialBean;
import com.example.airport.presenter.ipersenter.ISerialPresenter;
import com.example.airport.service.SerialService;
import com.example.airport.service.event.ActivityToServiceEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by android on 2017/12/26.
 */

public class SerialPresenter extends ISerialPresenter {

    private ISerialView mSerialView;

    public SerialPresenter(ISerialView baseView) {
        super(baseView);
        this.mSerialView = baseView;
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }

    @Override
    public void receiveMotion(int type, String motion) {
        Log.e("GG","send " + motion);
        SerialBean serialBean = new SerialBean();
        serialBean.setBaudRate(type);
        serialBean.setMotion(motion);
        ActivityToServiceEvent serialEvent = new ActivityToServiceEvent("");
        serialEvent.setEvent(200, serialBean);
        EventBus.getDefault().post(serialEvent);
    }


    @Override
    public void onDataReceiverd(SerialBean serialBean) {
        int iBaudRate = serialBean.getBaudRate();
        String motion = serialBean.getMotion();
        if (iBaudRate == SerialService.DEV_BAUDRATE) {

        } else if (iBaudRate == SerialService.VOICE_BAUDRATE) {
            if (motion.toString().contains("WAKE UP!")) {

                mSerialView.stopAll();
                if (motion.toString().contains("##### IFLYTEK")) {

                    String str = motion.toString().substring(motion.toString().indexOf("angle:") + 6, motion.toString().indexOf("##### IFLYTEK"));
                    int angle = Integer.parseInt(str.trim());
                    Log.e("GG","解析到应该旋转的角度 : " + angle);
                    if (0 <= angle && angle < 30) {
                        receiveMotion(SerialService.DEV_BAUDRATE, "A521821EAA");
                    } else if (30 <= angle && angle <= 60) {
                        receiveMotion(SerialService.DEV_BAUDRATE, "A521823CAA");
                    } else if (120 <= angle && angle <= 150) {
                        receiveMotion(SerialService.DEV_BAUDRATE, "A5218278AA");
                    } else if (150 < angle && angle <= 180) {
                        receiveMotion(SerialService.DEV_BAUDRATE, "A5218296AA");
                    }
                }
            }

        } else if (iBaudRate == SerialService.CRUISE_BAUDRATE) {
            if (motion.toString().trim().equals("first") || motion.toString().trim().equals("second") || motion.toString().trim().equals("third")
                    || motion.toString().trim().equals("fifth") || motion.toString().trim().equals("sixth")) {

                receiveMotion(SerialService.DEV_BAUDRATE, "A50C8001AA");
                mSerialView.onMoveStop();
            }
        }
    }


}
