package com.example.airport.main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.airport.R;
import com.example.airport.airQuery.AirQueryView;
import com.example.airport.base.BaseActivity;
import com.example.airport.config.RobotInfo;
import com.example.airport.config.enums.SpecialType;
import com.example.airport.database.manager.AirQueryManager;
import com.example.airport.database.manager.LocalQuestionManager;
import com.example.airport.handleplane.HandlePlaneView;
import com.example.airport.modle.Question;
import com.example.airport.modle.SerialBean;
import com.example.airport.modle.xf.service.Cookbook;
import com.example.airport.modle.xf.service.News;
import com.example.airport.modle.xf.service.Poetry;
import com.example.airport.modle.xf.service.englishEveryday.EnglishEveryday;
import com.example.airport.modle.xf.service.radio.Radio;
import com.example.airport.navigate.NavigateView;
import com.example.airport.presenter.LineSoundPresenter;
import com.example.airport.presenter.SerialPresenter;
import com.example.airport.presenter.SynthesizerPresenter;
import com.example.airport.presenter.ipersenter.ILineSoundPresenter;
import com.example.airport.presenter.ipersenter.ISerialPresenter;
import com.example.airport.presenter.ipersenter.ISynthesizerPresenter;
import com.example.airport.question.QuestionView;
import com.example.airport.service.SerialService;
import com.example.airport.service.event.ReceiveEvent;
import com.example.airport.service.event.ServiceToActivityEvent;
import com.example.airport.service.udp.SocketManager;
import com.example.airport.utils.JumpItent;
import com.example.airport.wechat.WeChatView;
import com.iflytek.cloud.SpeechConstant;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.DatagramPacket;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements IMainView, ISynthesizerPresenter.ITtsView, ILineSoundPresenter.ILineSoundView,
        ISerialPresenter.ISerialView {

    private MainPresenter mainPresenter;
    @BindView(R.id.iv_query)
    ImageView ivQuery;//航班查询
    @BindView(R.id.iv_question)
    ImageView ivQuestion;//问题咨询
    @BindView(R.id.iv_navigation)
    ImageView ivNavigation;//导航
    @BindView(R.id.iv_wechat)
    ImageView ivWechat;//公众号
    @BindView(R.id.iv_handle)
    ImageView ivHandle;//办理登机

    private AirQueryManager airQueryManager;
    private LocalQuestionManager localQuestionManager;


    private SynthesizerPresenter mTtsPresenter;
    private LineSoundPresenter mSoundPresenter;

    private SerialPresenter mSerialPresenter;

    private boolean quit;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mainPresenter = new MainPresenter(this);

        mTtsPresenter = new SynthesizerPresenter(this);
        mTtsPresenter.start();

        mSoundPresenter = new LineSoundPresenter(this);
        mSoundPresenter.start();

        mSerialPresenter = new SerialPresenter(this);
        mSerialPresenter.start();

        airQueryManager = new AirQueryManager();
        localQuestionManager = new LocalQuestionManager();


    }

    @Override
    protected void initData() {
        mainPresenter.addLocalData(airQueryManager);
//        mainPresenter.addLocalVoiceData(localQuestionManager);
    }

    @Override
    protected void setListener() {

    }


    @OnClick({R.id.iv_query, R.id.iv_question, R.id.iv_navigation, R.id.iv_wechat, R.id.iv_handle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_query://航班查询
                AirQueryView.newInstance(this);
                break;
            case R.id.iv_question://问题咨询
                QuestionView.newInstance(this);
                break;
            case R.id.iv_navigation://导航
                NavigateView.newInstance(this);
                break;
            case R.id.iv_wechat://公众号
                WeChatView.newInstance(this);
                break;
            case R.id.iv_handle://办理登机
                HandlePlaneView.newInstance(this);
                break;
        }
    }


    /**
     * @param msg
     * @description: 消息提示，如Toast，Dialog等
     */
    @Override
    public void showMsg(String msg) {

    }

    /**
     * @description: 获取Context
     */
    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("GG", "Main onResume");
        RobotInfo.getInstance().setEngineType(SpeechConstant.TYPE_CLOUD);
        mTtsPresenter.buildTts();
        mSoundPresenter.buildIat();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("GG", "Main onPause");
        mTtsPresenter.stopTts();
        mTtsPresenter.stopHandler();
        mSoundPresenter.stopRecognizerListener();
        mSoundPresenter.stopVoice();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e("GG", "Main onDestroy");
//        if (mPlayServiceConnection != null) {
//            unbindService(mPlayServiceConnection);
//        }
//        stopService(new Intent(this, UdpService.class));
//        stopService(new Intent(this, SerialService.class));
        super.onDestroy();
        mTtsPresenter.finish();
//        mChatPresenter.finish();
        mSoundPresenter.finish();
    }

    @Override
    public void onBackPressed() {
        if (!quit) {
            showToast("再按一次退出程序");
            new Timer(true).schedule(new TimerTask() {
                @Override
                public void run() {
                    quit = false;
                }
            }, 2000);
            quit = true;
        } else {
            super.onBackPressed();
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override
    public void onSpeakBegin() {

    }

    @Override
    public void onRunable() {
        mSoundPresenter.startRecognizerListener();
    }

    /**
     * ----------------------- 声音识别 -------------------------------
     */
    @Override
    public void aiuiForLocal(String result) {
        List<Question> voiceBeanList = localQuestionManager.loadAll();
        if (voiceBeanList != null && voiceBeanList.size() > 0) {
            for (Question voiceBean : voiceBeanList) {
                if (voiceBean.getAnswer().equals(result)) {
                    refHomePage(voiceBean);
                    return;
                }
            }
        }
        mSoundPresenter.onlineResult(result);
    }

    @Override
    public void doAiuiAnwer(String anwer) {
        addSpeakAnswer(anwer);
    }

    @Override
    public void refHomePage(Question voiceBean) {
//        if (voiceBean.getActionData() != null)
//            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, voiceBean.getActionData());
//        if (voiceBean.getExpressionData() != null)
//            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, voiceBean.getExpressionData());
        addSpeakAnswer(voiceBean.getAnswer());
    }

    private void addSpeakAnswer(String messageContent) {
        mTtsPresenter.doAnswer(messageContent);
        // 运动指令
        speakingAddAction(messageContent.length());
    }

    private void speakingAddAction(int length) {
        if (length <= 13) {
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A50C8001AA");
        } else if (length > 13 && length <= 40) {
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A50C8003AA");
        } else {
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A50C8021AA");
        }
    }

    @Override
    public void special(String result, SpecialType type) {
        switch (type) {
            case Story:
                break;
            case Music:
                break;
            case Joke:
                break;
        }
    }

    // 跳转其他页面
    @Override
    public void startPage(SpecialType specialType) {
        switch (specialType) {
            //航班查询
            case AirQuery:
                AirQueryView.newInstance(this);
                break;
            //问题咨询
            case Problem:
                QuestionView.newInstance(this);
                break;
            //办理登机
            case HandlePlance:
                HandlePlaneView.newInstance(this);
                break;
            //公众号
            case WeChat:
                WeChatView.newInstance(this);
                break;
            //导航
            case Navigation:
                NavigateView.newInstance(this);
                break;
        }
    }

    //运动
    @Override
    public void spakeMove(SpecialType type, String result) {
        mTtsPresenter.onCompleted();
        switch (type) {
            case Forward:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038002AA");
                break;
            case Backoff:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038008AA");
                break;
            case Turnleft:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038004AA");
                break;
            case Turnright:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038006AA");
                break;
        }
    }

    // 合成完毕
    @Override
    public void onCompleted() {
        mTtsPresenter.onCompleted();
    }

    @Override
    public void stopAll() {
        mSoundPresenter.stopVoice();
        mTtsPresenter.stopAll();
    }

    @Override
    public void onMoveStop() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(ReceiveEvent event) {
        if (event.isOk()) {
            DatagramPacket packet = event.getBean();
            if (!SocketManager.getInstance().isGetTcpIp) {
                SocketManager.getInstance().setUdpIp(packet.getAddress().getHostAddress(), packet.getPort());
            }
            String recvStr = new String(packet.getData(), 0, packet.getLength());
            if (recvStr.contains("udp")) {
                Log.e("GG", recvStr);
            } else {
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, recvStr);
            }
            Log.e("GG", recvStr);
        } else {
            Log.e("GG", "ReceiveEvent error");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(ServiceToActivityEvent event) {
        if (event.isOk()) {
            SerialBean serialBean = event.getBean();
            mSerialPresenter.onDataReceiverd(serialBean);
        } else {
            Log.e("GG", "ReceiveEvent error");
        }
    }
}
