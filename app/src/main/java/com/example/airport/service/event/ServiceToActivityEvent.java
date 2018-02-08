package com.example.airport.service.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.airport.base.event.BaseEvent;
import com.example.airport.modle.SerialBean;


/**
 * Created by android on 2017/12/26.
 */

public class ServiceToActivityEvent extends BaseEvent<SerialBean> {

    public ServiceToActivityEvent(@Nullable String uuid) {
        super(uuid);
    }

    public ServiceToActivityEvent(@Nullable String uuid, @NonNull Integer code, @Nullable SerialBean serialBean) {
        super(uuid, code, serialBean);
    }
}
