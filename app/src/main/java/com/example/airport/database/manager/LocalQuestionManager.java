package com.example.airport.database.manager;


import com.example.airport.database.AirQueryDao;
import com.example.airport.database.QuestionDao;
import com.example.airport.database.base.BaseManager;
import com.example.airport.modle.AirQuery;
import com.example.airport.modle.Question;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by dell on 2017/12/21.
 */

public class LocalQuestionManager extends BaseManager<Question, Long> {

    public List<Question> queryQuestionByName(String question) {
        Query<Question> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(QuestionDao.Properties.Question.like("%" + question + "%"))
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
    public AbstractDao<Question, Long> getAbstractDao() {
        return daoSession.getQuestionDao();
    }
}
