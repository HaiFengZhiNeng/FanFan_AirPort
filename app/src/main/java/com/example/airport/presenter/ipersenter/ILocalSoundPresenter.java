package com.example.airport.presenter.ipersenter;

import com.example.airport.base.presenter.BasePresenter;
import com.example.airport.base.presenter.BaseView;

/**
 * Created by android on 2017/12/20.
 */

public abstract class ILocalSoundPresenter implements BasePresenter {

    private ILocalSoundView mBaseView;

    public ILocalSoundPresenter(ILocalSoundView baseView) {
        mBaseView = baseView;
    }

    public abstract void initTts();

    public abstract void buildTts();

    public abstract void initIat();

    public abstract void buildIat();

    public abstract void stopTts();

    public abstract void doAnswer(String answer);

    public abstract void startRecognizerListener();

    public abstract void stopRecognizerListener();

    public abstract void stopAll();

    public abstract void stopHandler();

    public interface ILocalSoundView extends BaseView {



        /**
         * 停止监听
         */
        void stopListener();

        /**
         * 返回
         */
        void back();

        /**
         * 人工客服
         */
        void artificial();

        /**
         * 普通
         *
         * @param result
         */
        void refLocalPage(String result);
    }
}
