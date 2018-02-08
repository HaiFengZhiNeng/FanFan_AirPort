package com.example.airport.presenter;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.example.airport.AirApplication;
import com.example.airport.R;
import com.example.airport.base.Constants;
import com.example.airport.config.RobotInfo;
import com.example.airport.config.enums.SpecialType;
import com.example.airport.config.instance.SpeakIat;
import com.example.airport.config.instance.SpeakTts;
import com.example.airport.listener.IatListener;
import com.example.airport.listener.TtsListener;
import com.example.airport.presenter.ipersenter.ILocalSoundPresenter;
import com.example.airport.utils.FucUtil;
import com.example.airport.utils.SpecialUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.util.ResourceUtil;

import java.io.File;
import java.util.Random;

/**
 * Created by android on 2017/12/20.
 */

public class LocalSoundPresenter extends ILocalSoundPresenter implements TtsListener.SynListener, IatListener.RecognListener {

    private static final String LOCAL_GRAMMAR_NAME = "local";

    private ILocalSoundPresenter.ILocalSoundView mSoundView;

    private SpeechSynthesizer mTts;
    private SpeechRecognizer mIat;

    private TtsListener mTtsListener;
    private IatListener mIatListener;

    private Handler mHandler = new Handler();

    public LocalSoundPresenter(ILocalSoundView baseView) {
        super(baseView);
        mSoundView = baseView;

        mTtsListener = new TtsListener(this);
        mIatListener = new IatListener(this);
    }

    @Override
    public void start() {
        RobotInfo.getInstance().setEngineType(SpeechConstant.TYPE_LOCAL);
        initTts();
        initIat();
    }

    @Override
    public void finish() {
        RobotInfo.getInstance().setEngineType(SpeechConstant.TYPE_CLOUD);
        mTtsListener = null;
        mIatListener = null;
    }

    @Override
    public void initTts() {
        mTts = SpeakTts.getInstance().mTts();
        if (mTts == null) {
            SpeakTts.getInstance().initTts(AirApplication.getInstance().getApplicationContext(), new InitListener() {
                @Override
                public void onInit(int code) {
                    if (code != ErrorCode.SUCCESS) {
                        Log.e("GG", "初始化失败，错误码：" + code);
                    }
                    Log.e("GG", "local initTts success");
                    mTts = SpeakTts.getInstance().mTts();
                }
            });
        }
    }

    @Override
    public void buildTts() {
        if (mTts == null) {
            throw new NullPointerException(" mTts is null");
        }
        mTts.setParameter(SpeechConstant.PARAMS, null);
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        mTts.setParameter(ResourceUtil.TTS_RES_PATH, FucUtil.getResTtsPath(mSoundView.getContext(), RobotInfo.getInstance().getTtsLocalTalker()));
        mTts.setParameter(SpeechConstant.VOICE_NAME, RobotInfo.getInstance().getTtsLocalTalker());
        mTts.setParameter(SpeechConstant.SPEED, "60");
        mTts.setParameter(SpeechConstant.PITCH, "50");
        mTts.setParameter(SpeechConstant.VOLUME, "100");
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }

    @Override
    public void initIat() {
        mIat = SpeakIat.getInstance().mIat();
        if (mIat == null) {
            SpeakIat.getInstance().initIat(AirApplication.getInstance().getApplicationContext(), new InitListener() {
                @Override
                public void onInit(int code) {
                    if (code != ErrorCode.SUCCESS) {
                        Log.e("GG", "初始化失败，错误码：" + code);
                    }
                    Log.e("GG", "local initIat success");
                    mIat = SpeakIat.getInstance().mIat();
                }
            });
        }
    }

    @Override
    public void buildIat() {
        if (mIat == null) {
            throw new NullPointerException(" mIat is null");
        }
        mIat.setParameter(SpeechConstant.PARAMS, null);
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        mIat.setParameter(ResourceUtil.ASR_RES_PATH, FucUtil.getResAsrPath(mSoundView.getContext()));
        mIat.setParameter(ResourceUtil.GRM_BUILD_PATH, Constants.GRM_PATH);
        mIat.setParameter(SpeechConstant.LOCAL_GRAMMAR, LOCAL_GRAMMAR_NAME);
        mIat.setParameter(SpeechConstant.MIXED_THRESHOLD, "30");
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        mIat.setParameter(SpeechConstant.VAD_BOS, "99000");
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Constants.GRM_PATH + File.separator + "iat.wav");
    }

    @Override
    public void stopTts() {
        if (mTts.isSpeaking()) {
            mTts.stopSpeaking();
        }
    }

    @Override
    public void doAnswer(String answer) {
        mTts.startSpeaking(answer, mTtsListener);
    }

    @Override
    public void startRecognizerListener() {
        mIat.startListening(mIatListener);
    }

    @Override
    public void stopRecognizerListener() {
        mIat.startListening(null);
        mIat.stopListening();
    }

    @Override
    public void stopAll() {
        stopTts();
        doAnswer(resFoFinal(R.array.wake_up));
    }

    @Override
    public void stopHandler() {
        mHandler.removeCallbacks(runnable);
    }

    private String resFoFinal(int id) {
        String[] arrResult = ((Activity) mSoundView).getResources().getStringArray(id);
        return arrResult[new Random().nextInt(arrResult.length)];
    }

    //**********************************************************************************************

    @Override
    public void onCompleted() {
        mHandler.postDelayed(runnable, 0);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startRecognizerListener();
        }
    };

    @Override
    public void onSpeakBegin() {
        stopRecognizerListener();
    }

    //**********************************************************************************************
    @Override
    public void onTranslate(String result) {

    }

    @Override
    public void onRecognResult(String result) {
        Log.e("GG", "本地听写结果" + result);
        SpecialType type = SpecialUtils.doesExistLocal(mSoundView.getContext().getResources(), result);

        //右转
//        if (type == SpecialType.Forward || type == SpecialType.Backoff ||
//                type == SpecialType.Turnleft || type == SpecialType.Turnright) {
//            mSoundView.spakeMove(type, result);
//        } else
        //退出
//        if (type == SpecialType.Logout) {
//            mSoundView.logout();
//        } else
        if (type == SpecialType.StopListener) {
            mSoundView.stopListener();
            //返回
        } else if (type == SpecialType.Back) {
            mSoundView.back();
            //人工客服
        } else if (type == SpecialType.Artificial) {
            mSoundView.artificial();
            //人脸提取
        } else if (type == SpecialType.Face_lifting_area || type == SpecialType.Face_check_in
                || type == SpecialType.Instagram || type == SpecialType.Witness_contrast) {
//            mSoundView.face(type, result);
        } else {
            mSoundView.refLocalPage(result);
        }
//        else if (type == SpecialType.Next || type == SpecialType.Lase) {
//            mSoundView.control(type, result);


    }

    @Override
    public void onErrInfo(int errorCode) {
        Log.e("GG", "onRecognDown total error ：" + errorCode);
        switch (errorCode) {
            case 10118:
                startRecognizerListener();
                break;
            case 20006:
                startRecognizerListener();
                break;
            case 10114:
                startRecognizerListener();
                break;
            case 10108:
                Log.e("GG", "网络差");
                startRecognizerListener();
                break;
            case 20005:
                Log.e("GG", "本地暂无此命令词");
                startRecognizerListener();
                break;
            case 11201:
                Log.e("GG", "授权不足");
                mSoundView.showMsg("授权不足");
                break;
        }
    }

    @Override
    public void onRecognDown() {
        startRecognizerListener();
    }

    @Override
    public void onLevelSmall() {
        onCompleted();
    }
}
