package com.example.airport.database;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.example.airport.modle.AirQuery;
import com.example.airport.modle.Music;
import com.example.airport.modle.Question;

import com.example.airport.database.AirQueryDao;
import com.example.airport.database.MusicDao;
import com.example.airport.database.QuestionDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig airQueryDaoConfig;
    private final DaoConfig musicDaoConfig;
    private final DaoConfig questionDaoConfig;

    private final AirQueryDao airQueryDao;
    private final MusicDao musicDao;
    private final QuestionDao questionDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        airQueryDaoConfig = daoConfigMap.get(AirQueryDao.class).clone();
        airQueryDaoConfig.initIdentityScope(type);

        musicDaoConfig = daoConfigMap.get(MusicDao.class).clone();
        musicDaoConfig.initIdentityScope(type);

        questionDaoConfig = daoConfigMap.get(QuestionDao.class).clone();
        questionDaoConfig.initIdentityScope(type);

        airQueryDao = new AirQueryDao(airQueryDaoConfig, this);
        musicDao = new MusicDao(musicDaoConfig, this);
        questionDao = new QuestionDao(questionDaoConfig, this);

        registerDao(AirQuery.class, airQueryDao);
        registerDao(Music.class, musicDao);
        registerDao(Question.class, questionDao);
    }
    
    public void clear() {
        airQueryDaoConfig.clearIdentityScope();
        musicDaoConfig.clearIdentityScope();
        questionDaoConfig.clearIdentityScope();
    }

    public AirQueryDao getAirQueryDao() {
        return airQueryDao;
    }

    public MusicDao getMusicDao() {
        return musicDao;
    }

    public QuestionDao getQuestionDao() {
        return questionDao;
    }

}
