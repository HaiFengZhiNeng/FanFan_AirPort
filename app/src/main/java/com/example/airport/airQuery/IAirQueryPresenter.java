package com.example.airport.airQuery;

import com.example.airport.base.presenter.BasePresenter;

/**
 * Created by dell on 2018/1/22.
 */

public abstract class IAirQueryPresenter implements BasePresenter {

    private IAirQueryView iAirQueryView;

    public IAirQueryPresenter(IAirQueryView airQueryView) {
        iAirQueryView = airQueryView;
    }

}
