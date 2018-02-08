package com.example.airport.database.manager;


import com.example.airport.database.AirQueryDao;
import com.example.airport.database.base.BaseManager;
import com.example.airport.modle.AirQuery;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by dell on 2017/12/21.
 */

public class AirQueryManager extends BaseManager<AirQuery, Long> {

    public List<AirQuery> queryAirByName(String name) {
        Query<AirQuery> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(AirQueryDao.Properties.AirName.like("%" + name + "%"))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.list();
    }

    public List<AirQuery> queryAirByAddress(String start, String arrive) {
        Query<AirQuery> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(AirQueryDao.Properties.AirStart.like("%" + start + "%"), AirQueryDao.Properties.AirArrive.like("%" + arrive + "%"))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.list();
    }

    /**
     * 获取Dao
     *
     * @return
     */
    @Override
    public AbstractDao<AirQuery, Long> getAbstractDao() {
        return daoSession.getAirQueryDao();
    }
}
