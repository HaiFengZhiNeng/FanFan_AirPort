package com.example.airport.question;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.airport.R;
import com.example.airport.adapter.QuestionAdapter;
import com.example.airport.addData.AddLocalVoiceView;
import com.example.airport.animator.RegisterAnimation;
import com.example.airport.animator.SlideInOutBottomItemAnimator;
import com.example.airport.base.BaseActivity;
import com.example.airport.base.adapter.BaseRecyclerAdapter;
import com.example.airport.database.manager.LocalQuestionManager;
import com.example.airport.modle.Question;
import com.example.airport.presenter.LocalSoundPresenter;
import com.example.airport.presenter.ipersenter.ILocalSoundPresenter;
import com.example.airport.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 问题查询
 *
 * @author Guanluocang
 *         created at 2018/1/19 9:39
 */
public class QuestionView extends BaseActivity implements ILocalSoundPresenter.ILocalSoundView {

    @BindView(R.id.iv_goBack)
    ImageView ivGoBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_question)
    RecyclerView rvQuestion;
    @BindView(R.id.iv_questionPeople)
    ImageView ivQuestionPeople;
    @BindView(R.id.iv_topRight)
    ImageView ivTopRight;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, QuestionView.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private QuestionAdapter questionAdapter;
    private List<Question> questionList = new ArrayList<>();

    private LocalSoundPresenter mSoundPresenter;

    private LocalQuestionManager localQuestionManager;

    private String speakText;//回答的内容

    @Override
    protected int getLayoutId() {
        return R.layout.activity_question_view;
    }

    @Override
    protected void initView() {
        mSoundPresenter = new LocalSoundPresenter(this);
        mSoundPresenter.start();//开启语音监听

        localQuestionManager = new LocalQuestionManager();

        ivTopRight.setVisibility(View.VISIBLE);
        //设置适配器
        questionAdapter = new QuestionAdapter(QuestionView.this, questionList);
        //布局格式
        rvQuestion.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rvQuestion.setItemAnimator(new SlideInOutBottomItemAnimator(rvQuestion));
        //进场动画
        rvQuestion.setLayoutAnimation(RegisterAnimation.getInstance());
    }

    @Override
    protected void initData() {
        questionList = localQuestionManager.loadAll();
        if (questionList != null && questionList.size() > 0) {
            questionAdapter.refreshData(questionList);
        }
        ivGoBack.setImageResource(R.mipmap.ic_goback_white);
        tvTitle.setTextColor(getResources().getColor(R.color.color_white));
        tvTitle.setText(R.string.string_question);


        rvQuestion.setAdapter(questionAdapter);
    }

    @Override
    protected void setListener() {
        questionAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                refVoice(questionList.get(position));
            }
        });
        questionAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                showNeutralNotitleDialog(position);
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("GG", "question onStart");
        mSoundPresenter.buildTts();
        mSoundPresenter.buildIat();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("GG", "question onResume");
        mSoundPresenter.startRecognizerListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("GG", "question onPause");
        mSoundPresenter.stopTts();
        mSoundPresenter.stopRecognizerListener();
        mSoundPresenter.stopHandler();
    }

    @Override
    protected void onDestroy() {
        Log.e("GG", "question OnDestory");
        super.onDestroy();
        mSoundPresenter.finish();
    }


    @OnClick({R.id.iv_goBack, R.id.iv_questionPeople, R.id.iv_topRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_goBack:
                finish();
                break;
            case R.id.iv_questionPeople:
                showToast("正在连接人工客服");
                break;
            case R.id.iv_topRight:
                AddLocalVoiceView.newInstance(this, AddLocalVoiceView.ADD_VOICE_REQUESTCODE);
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
        List<Question> voiceBeans = localQuestionManager.queryQuestionByName(result);
        if (voiceBeans != null && voiceBeans.size() > 0) {
            Question itemData = null;
            if (voiceBeans.size() == 1) {
                itemData = voiceBeans.get(voiceBeans.size() - 1);
            } else {
                itemData = voiceBeans.get(new Random().nextInt(voiceBeans.size()));
            }
            int index = questionList.indexOf(itemData);
            refVoice(itemData);
        } else {
            if (new Random().nextBoolean()) {
                addSpeakAnswer(resFoFinal(R.array.no_result));
            } else {
                addSpeakAnswer(resFoFinal(R.array.no_voice));
            }
        }
    }

    private void addSpeakAnswer(String messageContent) {
        mSoundPresenter.doAnswer(messageContent);
    }

    private void refVoice(Question itemData) {
        questionAdapter.notifyDataSetChanged();
        speakText = itemData.getAnswer();
//        if (itemData.getActionData() != null) {
//            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, itemData.getActionData());
//        }
//        if (itemData.getExpressionData() != null) {
//            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, itemData.getExpressionData());
//        }
        addSpeakAnswer(speakText);
    }
//    @Override
//    public void stopAll() {
//        super.stopAll();
//        mSoundPresenter.stopTts();
//        mSoundPresenter.stopRecognizerListener();
//        mSoundPresenter.stopHandler();
//        addSpeakAnswer("你好，这里是问题咨询页面，点击上方列表或说出列表中问题可得到答案");
//    }

    public String resFoFinal(int id) {
        String[] arrResult = getResources().getStringArray(id);
        return arrResult[new Random().nextInt(arrResult.length)];
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddLocalVoiceView.ADD_VOICE_REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                questionList = localQuestionManager.loadAll();
                if (questionList != null && questionList.size() > 0) {
                    questionAdapter.refreshData(questionList);
                }
            }
        }
    }

    private void showNeutralNotitleDialog(final int position) {
        DialogUtils.showNeutralNotitleDialog(this, "选择您要执行的操作", "删除所有",
                "删除此条", "修改此条", new DialogUtils.OnNeutralDialogListener() {
                    @Override
                    public void neutralText() {
                        //删除所有
                        if (localQuestionManager.deleteAll()) {
                            questionAdapter.clear();
                            questionList.clear();
                        }
                    }

                    @Override
                    public void negativeText() {
                        // 删除此条
                        if (localQuestionManager.delete(questionList.get(position))) {
                            questionAdapter.removeItem(questionList.get(position));
                            questionList.remove(position);
                        }
                    }

                    @Override
                    public void positiveText() {
                        //修改
//                        Question voiceBean = questionList.get(position);
//                        AddVoiceActivity.newInstance(DataVoiceActivity.this, voiceBean.getId(), AddVoiceActivity.ADD_VOICE_REQUESTCODE);
                    }
                });
    }
}
