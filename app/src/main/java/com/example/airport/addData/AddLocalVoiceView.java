package com.example.airport.addData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.airport.R;
import com.example.airport.base.BaseActivity;
import com.example.airport.database.manager.LocalQuestionManager;
import com.example.airport.modle.Question;
import com.example.airport.utils.LocalLexicon;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 添加本地语音数据
 *
 * @author Guanluocang
 *         created at 2018/1/29 17:28
 */
public class AddLocalVoiceView extends BaseActivity implements LocalLexicon.RobotLexiconListener {

    @BindView(R.id.et_question)
    EditText etQuestion;
    @BindView(R.id.et_content)
    EditText etContent;

    public static final String VOICE_ID = "voiceId";
    @BindView(R.id.iv_goBack)
    ImageView ivGoBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_topRight)
    ImageView ivTopRight;
    @BindView(R.id.video_layout)
    LinearLayout videoLayout;
    @BindView(R.id.backdrop)
    RelativeLayout backdrop;
    public static final int ADD_VOICE_REQUESTCODE = 224;

    public static void newInstance(Activity context, int requestCode) {
        Intent intent = new Intent(context, AddLocalVoiceView.class);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    private LocalQuestionManager localQuestionManager;

    /**
     * 返回当前界面布局文件
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_local_voice_view;
    }

    /**
     * 此方法描述的是： 初始化所有view
     */
    @Override
    protected void initView() {

        localQuestionManager = new LocalQuestionManager();
        tvTitle.setText(R.string.string_addlocal);
        ivTopRight.setVisibility(View.VISIBLE);
    }

    /**
     * 此方法描述的是： 初始化所有数据的方法
     */
    @Override
    protected void initData() {
    }

    /**
     * 此方法描述的是： 设置所有事件监听
     */
    @Override
    protected void setListener() {

    }

    public void doAddVoiceData() {
        Question questionModle = new Question();
        String question = etQuestion.getText().toString().trim();
        String answer = etContent.getText().toString().trim();

        List<Question> questionList = localQuestionManager.loadAll();

        List<Question> been = localQuestionManager.queryQuestionByName(etQuestion.getText().toString().trim());

        if (TextUtils.isEmpty(question)) {
            showToast("问题不能为空！");
        } else if (TextUtils.isEmpty(answer)) {
            showToast("答案不能为空！");
        } else if (!been.isEmpty()) {
            showToast("请不要添加相同的问题！");
        } else {
            questionModle.setAnswer(answer);
            questionModle.setQuestion(question);
            //直接添加
            localQuestionManager.insert(questionModle);
            LocalLexicon.getInstance().initDBManager(this).setListener(this).updateContents();
        }


    }

    @Override
    public void onLexiconSuccess() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onLexiconError(String error) {
        showToast(error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_goBack, R.id.iv_topRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_goBack:
                finish();
                break;
            case R.id.iv_topRight:
                doAddVoiceData();
                break;
        }
    }
}
