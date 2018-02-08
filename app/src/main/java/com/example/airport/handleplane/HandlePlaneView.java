package com.example.airport.handleplane;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.airport.R;
import com.example.airport.airQuery.IAirQueryView;
import com.example.airport.base.BaseActivity;
import com.example.airport.presenter.LocalSoundPresenter;
import com.example.airport.presenter.ipersenter.ILocalSoundPresenter;
import com.example.airport.question.QuestionView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 办理登机
 *
 * @author Guanluocang
 *         created at 2018/1/19 9:38
 */
public class HandlePlaneView extends BaseActivity implements ILocalSoundPresenter.ILocalSoundView {

    @BindView(R.id.iv_goBack)
    ImageView ivGoBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, HandlePlaneView.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private LocalSoundPresenter mSoundPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_handle_plane_view;
    }

    @Override
    protected void initView() {

        mSoundPresenter = new LocalSoundPresenter(this);
        mSoundPresenter.start();//开启语音监听
    }

    @Override
    protected void initData() {
        ivGoBack.setImageResource(R.mipmap.ic_goback_black);
        tvTitle.setTextColor(getResources().getColor(R.color.color_top_title));
        tvTitle.setText(R.string.string_handle);
    }

    @Override
    protected void setListener() {

    }
    @Override
    public void onStart() {
        super.onStart();
        mSoundPresenter.startRecognizerListener();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSoundPresenter.buildTts();
        mSoundPresenter.buildIat();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSoundPresenter.stopTts();
        mSoundPresenter.stopRecognizerListener();
        mSoundPresenter.stopHandler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSoundPresenter.finish();
    }
    /**
     * 停止监听
     */
    @Override
    public void stopListener() {

    }

    /**
     * 返回
     */
    @Override
    public void back() {
        finish();
    }

    /**
     * 人工客服
     */
    @Override
    public void artificial() {

    }

    /**
     * 普通
     *
     * @param result
     */
    @Override
    public void refLocalPage(String result) {

    }

    @OnClick(R.id.iv_goBack)
    public void onViewClicked() {
        finish();
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
}
