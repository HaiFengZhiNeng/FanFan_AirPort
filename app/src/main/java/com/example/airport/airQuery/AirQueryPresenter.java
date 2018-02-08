package com.example.airport.airQuery;

import com.example.airport.base.presenter.BasePresenter;
import com.example.airport.database.manager.AirQueryManager;
import com.example.airport.modle.AirQuery;
import com.example.airport.modle.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2018/1/22.
 */

public class AirQueryPresenter extends IAirQueryPresenter {

    private IAirQueryView iAirQueryView;

    private AirQueryManager airQueryManager;

    public AirQueryPresenter(IAirQueryView airQueryView) {
        super(airQueryView);
        iAirQueryView = airQueryView;
        airQueryManager = new AirQueryManager();
    }


    // 按照航班号 目的地 查询
    void selectNum(boolean isSelect) {
        if (isSelect) {
            iAirQueryView.selectNum(true);
        } else {
            iAirQueryView.selectAddress(true);
        }

    }

    // 按照航班号查询
    public List<AirQuery> doSelectByNum(String num) {
        List<AirQuery> cities = new ArrayList<>();
        cities = airQueryManager.queryAirByName(num);
        if (cities != null || cities.size() > 0) {
            return cities;
        }
        return null;
    }

    //   按照地点查询
    public List<AirQuery> doSelectByAddress(String start, String arrive) {
        List<AirQuery> cities = new ArrayList<>();
        cities = airQueryManager.queryAirByAddress(start, arrive);
        if (cities != null || cities.size() > 0) {
            return cities;
        }
        return null;
    }


    @Override

    public void start() {

    }

    @Override
    public void finish() {

    }
}
