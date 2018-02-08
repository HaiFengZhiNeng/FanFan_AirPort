package com.example.airport.service.udp;

import android.support.annotation.NonNull;
import android.util.Log;


import com.example.airport.service.event.ReceiveEvent;
import com.example.airport.utils.UUIDGenerator;

import org.greenrobot.eventbus.EventBus;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by zhangyuanyuan on 2017/10/20.
 */

public class UDPReceiveRunnable implements Runnable {


    private DatagramSocket mServer;


    private boolean udpLife = true;     //udp生命线程

    private DatagramPacket dpRcv;
    private byte[] msgRcv = new byte[1024];

    public UDPReceiveRunnable(@NonNull DatagramSocket datagramSocket) {
        this.mServer = datagramSocket;
    }


    @Override
    public void run() {
        String uuid = UUIDGenerator.getUUID();
        ReceiveEvent event = new ReceiveEvent(uuid);
        try {
            dpRcv = new DatagramPacket(msgRcv, msgRcv.length);
            while (udpLife) {
                mServer.receive(dpRcv);

                EventBus.getDefault().post(event.setEvent(200, dpRcv));
            }

        } catch (Exception e) {
            Log.e("GG","RecveviceThread start fail");
            e.printStackTrace();
            EventBus.getDefault().post(event.setEvent(-1, null));
            mServer.close();
        }
        Log.e("GG","Thread.interrupted");
    }

}
