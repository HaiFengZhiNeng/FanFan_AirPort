package com.example.airport.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.airport.R;
import com.example.airport.base.Constants;
import com.example.airport.config.instance.SpeakIat;
import com.example.airport.database.manager.LocalQuestionManager;
import com.example.airport.modle.Question;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 2018/1/6.
 */

public class LocalLexicon {

    private Context mContext;

    private LocalQuestionManager mVoiceDBManager;

    private RobotLexiconListener mListener;

    private static LocalLexicon mInstance;

    public synchronized static LocalLexicon getInstance() {
        if (mInstance == null) {
            mInstance = new LocalLexicon();
        }
        return mInstance;
    }

    public LocalLexicon() {
    }

    public LocalLexicon initDBManager(Context context) {
        mContext = context;
        mVoiceDBManager = new LocalQuestionManager();
        return this;
    }

    public LocalLexicon initContext(Context context) {
        mContext = context;
        return this;
    }

    public LocalLexicon setListener(RobotLexiconListener listener) {
        mListener = listener;
        return this;
    }

    /**
     * 更新所有
     */
    public void updateContents() {
        StringBuilder lexiconContents = new StringBuilder();
        //本地语音
        List<Question> voiceBeanList = mVoiceDBManager.loadAll();
        for (Question voiceBean : voiceBeanList) {
            lexiconContents.append(voiceBean.getQuestion()).append("\n");
        }

        lexiconContents.append(words2Contents());
        updateLocation(lexiconContents.toString());
    }

    public String words2Contents() {
        StringBuilder sb = new StringBuilder();
        List<String> words = getLocalStrings();
        for (String anArrStandard : words) {
            sb.append(anArrStandard).append("\n");
        }
        return sb.toString();
    }

    @NonNull
    public List<String> getLocalStrings() {

        List<String> words = new ArrayList<>();

        words.add(mContext.getResources().getString(R.string.string_question));
        words.add(mContext.getResources().getString(R.string.Seting_up));
        words.add(mContext.getResources().getString(R.string.string_wechat));
        words.add(mContext.getResources().getString(R.string.string_navigation));
        words.add(mContext.getResources().getString(R.string.StopListener));
        words.add(mContext.getResources().getString(R.string.Back));
        words.add(mContext.getResources().getString(R.string.Forward));
        words.add(mContext.getResources().getString(R.string.Backoff));
        words.add(mContext.getResources().getString(R.string.Turnleft));
        words.add(mContext.getResources().getString(R.string.Turnright));
        words.add(mContext.getResources().getString(R.string.Artificial));
        words.add(mContext.getResources().getString(R.string.Face_lifting_area));
        return words;
    }


    public void updateLocation(String lexiconContents) {
        SpeechRecognizer mIat = SpeakIat.getInstance().mIat();
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置引擎类型
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        // 指定资源路径
        mIat.setParameter(ResourceUtil.ASR_RES_PATH,
                ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "asr/common.jet"));
        // 指定语法路径
        mIat.setParameter(ResourceUtil.GRM_BUILD_PATH, Constants.GRM_PATH);
        // 指定语法名字
        mIat.setParameter(SpeechConstant.GRAMMAR_LIST, "local");
        // 设置文本编码格式
        mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        // lexiconName 为词典名字，lexiconContents 为词典内容，lexiconListener 为回调监听器
        int ret = mIat.updateLexicon("voice", lexiconContents, new LexiconListener() {
            @Override
            public void onLexiconUpdated(String lexiconId, SpeechError error) {
                if (error == null) {
                    Log.e("GG", "词典更新成功");
                    if (mListener != null)
                        mListener.onLexiconSuccess();
                } else {
                    Log.e("GG", "词典更新失败,错误码：" + error.getErrorCode());
                    if (mListener != null)
                        mListener.onLexiconError(error.getErrorDescription());
                }
            }
        });
        if (ret != ErrorCode.SUCCESS) {
            Log.e("GG", "更新词典失败,错误码：" + ret);
        }
    }

    public interface RobotLexiconListener {

        void onLexiconSuccess();

        void onLexiconError(String error);
    }

}
