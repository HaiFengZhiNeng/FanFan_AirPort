package com.example.airport.main;

import com.example.airport.airQuery.IAirQueryView;
import com.example.airport.base.presenter.BasePresenter;

/**
 * Created by dell on 2018/1/25.
 */

public abstract class IMainPresnter implements BasePresenter {

    private IMainView iMainView;

    public IMainPresnter(IMainView mainView) {
        iMainView = mainView;
    }

}
