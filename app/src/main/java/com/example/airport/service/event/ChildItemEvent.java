package com.example.airport.service.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.airport.base.event.BaseEvent;
import com.example.airport.utils.BaseItemData;


/**
 * Created by android on 2017/12/20.
 */

public class ChildItemEvent extends BaseEvent<BaseItemData> {

    public ChildItemEvent() {
        super(null);
    }

    public ChildItemEvent(@Nullable String uuid, @NonNull Integer code, @Nullable BaseItemData baseItemData) {
        super(uuid, code, baseItemData);
    }
}
