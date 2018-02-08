package com.example.airport.presenter.ipersenter;


import com.example.airport.base.presenter.BasePresenter;
import com.example.airport.base.presenter.BaseView;
import com.example.airport.config.enums.SpecialType;
import com.example.airport.modle.Question;
import com.example.airport.modle.xf.service.Cookbook;
import com.example.airport.modle.xf.service.News;
import com.example.airport.modle.xf.service.Poetry;
import com.example.airport.modle.xf.service.englishEveryday.EnglishEveryday;
import com.example.airport.modle.xf.service.radio.Radio;

/**
 * Created by android on 2018/1/6.
 */

public abstract class ILineSoundPresenter implements BasePresenter {

    private ILineSoundView mBaseView;

    public ILineSoundPresenter(ILineSoundView baseView) {
        mBaseView = baseView;
    }

    public abstract void initIat();

    public abstract void initAiui();

    public abstract void buildIat();

    public abstract void startRecognizerListener();

    public abstract void stopRecognizerListener();

    public abstract void onlineResult(String result);

    public abstract void aiuiWriteText(String text);

    public abstract void stopVoice();

    public abstract void setSpeech(boolean speech);

    public interface ILineSoundView extends BaseView {


        void aiuiForLocal(String result);

        void doAiuiAnwer(String anwer);

        void refHomePage(Question voiceBean);

        void special(String result, SpecialType type);

        void startPage(SpecialType specialType);

        void spakeMove(SpecialType specialType, String result);


        void onCompleted();
    }


}
