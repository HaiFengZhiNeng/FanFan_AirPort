package com.example.airport.airQuery;

import com.example.airport.base.presenter.BaseView;

/**
 * Created by dell on 2018/1/22.
 */

public interface IAirQueryView extends BaseView {

    void selectNum(boolean isSelect);

    void selectAddress(boolean isSelect);
}
