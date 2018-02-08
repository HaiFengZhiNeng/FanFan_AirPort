package com.example.airport.main;

import com.example.airport.R;
import com.example.airport.database.manager.AirQueryManager;
import com.example.airport.database.manager.LocalQuestionManager;
import com.example.airport.modle.AirQuery;
import com.example.airport.modle.Question;
import com.example.airport.utils.PreferencesUtils;

/**
 * Created by dell on 2018/1/25.
 */

public class MainPresenter extends IMainPresnter {

    private IMainView iMainView;

    public MainPresenter(IMainView mainView) {
        super(mainView);
        iMainView = mainView;
    }

    //    添加航班数据
    void addLocalData(AirQueryManager airQueryManager) {
        boolean isSaveCountry = PreferencesUtils.getBoolean(iMainView.getContext(), "airQuery", false);
        if (!isSaveCountry) {
            PreferencesUtils.putBoolean(iMainView.getContext(), "airQuery", true);
            airQueryManager.insert(new AirQuery("东方航空A5244", "07:52", "08:45", "北京首都机场", "10:20", "10:30", "上海虹桥机场", "95%", "1"));
            airQueryManager.insert(new AirQuery("东方航空B213", "07:52", "08:45", "武汉机场", "10:20", "10:30", "北京机场", "95%", "0"));
            airQueryManager.insert(new AirQuery("中国航空E1234", "07:52", "08:45", "重庆机场", "10:20", "10:30", "济南机场", "95%", "0"));
            airQueryManager.insert(new AirQuery("海南航空F1413", "07:52", "08:45", "广州机场", "10:20", "10:30", "上海虹桥机场", "95%", "1"));
            airQueryManager.insert(new AirQuery("国泰航空DF7857", "07:52", "08:45", "上海机场", "10:20", "10:30", "临沂机场", "95%", "0"));
            airQueryManager.insert(new AirQuery("中国航空D124", "07:52", "08:45", "广州机场", "10:20", "10:30", "北京机场", "95%", "1"));
            airQueryManager.insert(new AirQuery("中国航空B213", "07:52", "08:45", "云南机场", "10:20", "10:30", "上海机场", "95%", "1"));
            airQueryManager.insert(new AirQuery("东方航空DF7857", "07:52", "08:45", "临沂机场", "10:20", "10:30", "广州机场", "95%", "0"));
            airQueryManager.insert(new AirQuery("中国航空DF7857", "07:52", "08:45", "北京首都机场", "10:20", "10:30", "上海机场", "95%", "1"));
            airQueryManager.insert(new AirQuery("海南航空A5244", "07:52", "08:45", "上海机场", "10:20", "10:30", "广州机场", "95%", "0"));
            airQueryManager.insert(new AirQuery("海南航空E1234", "07:52", "08:45", "临沂机场", "10:20", "10:30", "广州机场", "95%", "0"));
            airQueryManager.insert(new AirQuery("中国航空D124", "07:52", "08:45", "云南机场", "10:20", "10:30", "临沂机场", "95%", "1"));
        }
    }

    //    添加本地语音数据
    void addLocalVoiceData(LocalQuestionManager localQuestionManager) {
        boolean isSaveCountry = PreferencesUtils.getBoolean(iMainView.getContext(), "localQuestion", false);
        if (!isSaveCountry) {
            PreferencesUtils.putBoolean(iMainView.getContext(), "localQuestion", true);
            String[] question = resArray(R.array.string_local_question);
            String[] answer = resArray(R.array.string_local_answer);
            for (int i = 0; i < question.length; i++) {
                localQuestionManager.insert(new Question(question[i], answer[i]));
            }
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }


    //获取资源文件
    private String[] resArray(int resId) {
        return iMainView.getContext().getResources().getStringArray(resId);
    }
}
