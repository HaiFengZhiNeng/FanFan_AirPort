package com.example.airport.airQuery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.airport.R;
import com.example.airport.adapter.AirAdapter;
import com.example.airport.animator.RegisterAnimation;
import com.example.airport.animator.SlideInOutBottomItemAnimator;
import com.example.airport.base.BaseActivity;
import com.example.airport.calendar.util.CalendarDay;
import com.example.airport.calendar.util.DialogFragment_Listener;
import com.example.airport.calendar.util.MaterialCalendarView;
import com.example.airport.calendar.util.OnDateSelectedListener;
import com.example.airport.city.CitySelectView;
import com.example.airport.database.manager.AirQueryManager;
import com.example.airport.modle.AirQuery;
import com.example.airport.presenter.LocalSoundPresenter;
import com.example.airport.presenter.ipersenter.ILocalSoundPresenter;
import com.example.airport.question.QuestionView;
import com.example.airport.utils.JumpItent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 航班查询
 *
 * @author Guanluocang
 *         created at 2018/1/18 17:04
 */
public class AirQueryView extends BaseActivity implements DialogFragment_Listener, IAirQueryView, ILocalSoundPresenter.ILocalSoundView {

    public static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    private static final int REQUEST_CODE_LEFT = 200;
    private static final int REQUEST_CODE_RIGHT = 400;
    @BindView(R.id.iv_goBack)
    ImageView ivGoBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_selectNum)
    TextView tvSelectNum;
    @BindView(R.id.tv_selectAdd)
    TextView tvSelectAdd;
    @BindView(R.id.et_selectNum)
    EditText etSelectNum;
    @BindView(R.id.ll_selectTime)
    LinearLayout llSelectTime;
    @BindView(R.id.tv_select)
    TextView tvSelect;
    @BindView(R.id.tv_selectTime)
    TextView tvSelectTime;
    @BindView(R.id.rv_air)
    RecyclerView rvAir;
    @BindView(R.id.iv_changeAdd)
    ImageView ivChangeAdd;
    @BindView(R.id.tv_selectLeft)
    TextView tvSelectLeft;
    @BindView(R.id.rl_selectLeft)
    RelativeLayout rlSelectLeft;
    @BindView(R.id.tv_selectRight)
    TextView tvSelectRight;
    @BindView(R.id.rl_selectRight)
    RelativeLayout rlSelectRight;
    @BindView(R.id.ll_selectAddress)
    LinearLayout llSelectAddress;
    @BindView(R.id.ll_inputNum)
    LinearLayout llInputNum;

    /**
     * 左边tv的镜像view
     */
    private ImageView copyViewLeft;
    /**
     * 右边tv的镜像view
     */
    private ImageView copyViewRight;
    private WindowManager mWindowManager;
    private int[] mLeftLocation;
    private int[] mRightLocation;
    private Bitmap mLeftCacheBitmap;
    private Bitmap mRightCacheBitmap;

    private Animation animation;

    private List<AirQuery> airListQuery = new ArrayList<>();
    private AirAdapter airAdapter;

    private AirQueryPresenter mAirQueryPresenter;

    private AirQueryManager airQueryManager;

    private boolean isSelectWhat = true; //判断当前是根据航班号还是地点查询

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, AirQueryView.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private LocalSoundPresenter mSoundPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_air_query_view;
    }


    @Override
    protected void initView() {


        mWindowManager = getWindowManager();
        mAirQueryPresenter = new AirQueryPresenter(this);

        airQueryManager = new AirQueryManager();
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate);

        mSoundPresenter = new LocalSoundPresenter(this);
        mSoundPresenter.start();//开启语音监听
    }

    @Override
    protected void initData() {


        ivGoBack.setImageResource(R.mipmap.ic_goback_black);
        tvTitle.setTextColor(getResources().getColor(R.color.color_top_title));
        tvTitle.setText(R.string.string_airquery);

        //布局格式
        rvAir.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        rvAir.setItemAnimator(new SlideInOutBottomItemAnimator(rvAir));
        //进场动画
        rvAir.setLayoutAnimation(RegisterAnimation.getInstance());
        airListQuery = airQueryManager.loadAll();
        if (airListQuery != null || airListQuery.size() > 0) {  //设置适配器
            airAdapter = new AirAdapter(AirQueryView.this, airListQuery);
        }
        rvAir.setAdapter(airAdapter);
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @OnClick({R.id.iv_goBack, R.id.tv_selectNum, R.id.tv_selectAdd, R.id.ll_selectTime, R.id.tv_select, R.id.iv_changeAdd, R.id.tv_selectLeft, R.id.tv_selectRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_goBack://返回
                finish();
                break;
            case R.id.tv_selectNum://按照航班号
                isSelectWhat = true;
                mAirQueryPresenter.selectNum(true);
                break;
            case R.id.tv_selectAdd://按照地点
                isSelectWhat = false;
                mAirQueryPresenter.selectNum(false);
                break;
            case R.id.ll_selectTime://选择时间
                new CalendarDialogFragment().show(getSupportFragmentManager(), "test-simple-calendar");
                break;
            case R.id.tv_select://查询
                if (isSelectWhat) {
                    doSelectByNum();
                } else {
                    doSelectByAddress();
                }
                break;
            case R.id.iv_changeAdd:
                doChange();
                ivChangeAdd.setEnabled(false);
                break;
            case R.id.tv_selectLeft:
                JumpItent.jump(AirQueryView.this, CitySelectView.class, REQUEST_CODE_LEFT);
                break;
            case R.id.tv_selectRight:
                JumpItent.jump(AirQueryView.this, CitySelectView.class, REQUEST_CODE_RIGHT);
                break;
        }
    }


    @Override
    public void getDataFrom_DialogFragment(String data) {
        tvSelectTime.setText(data);
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

    // 按照航班号查询
    @Override
    public void selectNum(boolean isSelect) {

        tvSelectNum.setBackgroundResource(R.drawable.back_navigation_select_one);
        tvSelectAdd.setBackgroundResource(R.drawable.back_navigation_select);

        tvSelectNum.setTextColor(getResources().getColor(R.color.color_white));
        tvSelectAdd.setTextColor(getResources().getColor(R.color.color_nav_select_one));

        llInputNum.setVisibility(View.VISIBLE);
        llSelectAddress.setVisibility(View.GONE);
    }

    // 按照起降地查询
    @Override
    public void selectAddress(boolean isSelect) {
        tvSelectNum.setBackgroundResource(R.drawable.back_navigation_select);
        tvSelectAdd.setBackgroundResource(R.drawable.back_navigation_select_one);

        tvSelectNum.setTextColor(getResources().getColor(R.color.color_nav_select_one));
        tvSelectAdd.setTextColor(getResources().getColor(R.color.color_white));

        llInputNum.setVisibility(View.GONE);
        llSelectAddress.setVisibility(View.VISIBLE);
    }

    // 按照航班号查询
    public void doSelectByNum() {
        String airNum = etSelectNum.getText().toString().trim();
        String airTime = tvSelectTime.getText().toString().trim();
        if (!TextUtils.isEmpty(airNum) || !TextUtils.isEmpty(airTime)) {
            airListQuery = mAirQueryPresenter.doSelectByNum(airNum);
            if (airListQuery != null && airListQuery.size() > 0) {
                airAdapter.refreshData(airListQuery);
            } else {
                showToast("暂无相应航班");
            }
        } else {
            showToast("请输入航班号或出发时间");
        }
    }

    //   按照 地点 查询
    public void doSelectByAddress() {
        String airTime = tvSelectTime.getText().toString().trim();
        String start = tvSelectLeft.getText().toString().trim();
        String end = tvSelectRight.getText().toString().trim();
        if (!TextUtils.isEmpty(airTime)) {
            airListQuery = mAirQueryPresenter.doSelectByAddress(start, end);
            if (airListQuery != null && airListQuery.size() > 0) {
                airAdapter.refreshData(airListQuery);
            } else {
                showToast("暂无相应航班");
            }
        } else {
            showToast("请输入航班号或出发时间");
        }
    }

    // 出发地和目的地进行交换

    /**
     * 获取tv的属性,计算偏移量,
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void doChange() {

        //获取tv控件距离父控件的位置
        int leftRight = tvSelectLeft.getRight();
        int rightLeft = tvSelectRight.getLeft();

        //包裹右侧tv距离父控件的距离
        int rlRight = rlSelectRight.getRight();
        int rlLeft = rlSelectRight.getLeft();
        //在哪里设的padding就要用哪个控件来获取padding值
        int paddingStart = llSelectAddress.getPaddingStart();

        //左侧textview需要移动的距离
        int leftOffset = rlRight - leftRight - paddingStart;
        //右侧textview需要移动的距离
        int rightOffset = rlLeft + rightLeft - paddingStart;

        //创建出镜像view
        createCopyView();

        //隐藏掉两边的tv
        tvSelectLeft.setVisibility(View.INVISIBLE);
        tvSelectRight.setVisibility(View.INVISIBLE);

        //开启镜像view的动画
        leftAnim(leftOffset, mLeftLocation[0]);
        rightAnim(rightOffset, mRightLocation[0]);

        //箭头旋转动画
        ivChangeAdd.startAnimation(animation);
    }

    /**
     * 创建镜像view
     */
    private void createCopyView() {
        mLeftLocation = new int[2];
        mRightLocation = new int[2];

        //获取相对window的坐标
        tvSelectLeft.getLocationInWindow(mLeftLocation);
        tvSelectRight.getLocationInWindow(mRightLocation);

        //获取左边tv的缓存bitmap
        tvSelectLeft.setDrawingCacheEnabled(true);
        mLeftCacheBitmap = Bitmap.createBitmap(tvSelectLeft.getDrawingCache());
        tvSelectLeft.destroyDrawingCache();

        //获取右边tv的缓存bitmap
        tvSelectRight.setDrawingCacheEnabled(true);
        mRightCacheBitmap = Bitmap.createBitmap(tvSelectRight.getDrawingCache());
        tvSelectRight.destroyDrawingCache();

        //创建出两个镜像view
        copyViewLeft = createCopyView(mLeftLocation[0], mLeftLocation[1], mLeftCacheBitmap);
        copyViewRight = createCopyView(mRightLocation[0], mRightLocation[1], mRightCacheBitmap);
        //释放bitmap资源...这我不确定是不是这么做
        mLeftCacheBitmap = null;
        mRightCacheBitmap = null;
    }

    /**
     * 左侧镜像view的动画
     *
     * @param offset 偏移量
     * @param defX   原始位置的x
     */
    private void leftAnim(int offset, final int defX) {
        ValueAnimator leftAnimV = ValueAnimator.ofInt(0, offset);
        leftAnimV.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int animatedValue = (int) valueAnimator.getAnimatedValue();
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) copyViewLeft.getLayoutParams();
                //往右边移动所以x是变大的
                layoutParams.x = defX + animatedValue;
                mWindowManager.updateViewLayout(copyViewLeft, layoutParams);
            }
        });
        leftAnimV.setDuration(600);
        leftAnimV.start();
        //左侧动画监听
        leftAnimV.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //改变值
                String sLeft = tvSelectLeft.getText().toString();
                String sRight = tvSelectRight.getText().toString();
                tvSelectLeft.setText(sRight);
                tvSelectRight.setText(sLeft);
                tvSelectLeft.setVisibility(View.VISIBLE);
                mWindowManager.removeView(copyViewLeft);
                copyViewLeft = null;
                ivChangeAdd.setEnabled(true);
            }
        });
    }

    /**
     * 右侧镜像view动画
     *
     * @param offset 偏移量
     * @param defX   原始位置的x
     */
    private void rightAnim(int offset, final int defX) {
        ValueAnimator rightAnimV = ValueAnimator.ofInt(0, offset);
        rightAnimV.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int animatedValue = (int) valueAnimator.getAnimatedValue();
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) copyViewRight.getLayoutParams();
                layoutParams.x = defX - animatedValue;
                mWindowManager.updateViewLayout(copyViewRight, layoutParams);
            }
        });
        rightAnimV.setDuration(600);
        rightAnimV.start();
        rightAnimV.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                tvSelectRight.setVisibility(View.VISIBLE);
                mWindowManager.removeView(copyViewRight);
                copyViewRight = null;
            }
        });
    }

    /**
     * 创建镜像view
     *
     * @param x
     * @param y
     * @param bitmap
     */
    private ImageView createCopyView(int x, int y, Bitmap bitmap) {
        WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.format = PixelFormat.TRANSLUCENT;            //图片之外其他地方透明
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mLayoutParams.x = x;   //设置imageView的原点
        mLayoutParams.y = y;
        mLayoutParams.alpha = 1f;                                //设置透明度
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        ImageView copyView = new ImageView(this);
        copyView = new ImageView(this);
        copyView.setImageBitmap(bitmap);
        mWindowManager.addView(copyView, mLayoutParams);   //添加该iamgeView到window
        return copyView;
    }

    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    private static int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
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


    public static class CalendarDialogFragment extends AppCompatDialogFragment implements OnDateSelectedListener {
        private TextView textView;

        private String dataTime = "";
        private DialogFragment_Listener myDialogFragment_Listener;
        // 回调接口，用于传递数据给Activity -------

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            try {
                myDialogFragment_Listener = (DialogFragment_Listener) activity;
            } catch (ClassCastException e) {

                throw new ClassCastException(activity.toString());
            }
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();

            //inflate custom layout and get views
            //pass null as parent view because will be in dialog layout
            View view = inflater.inflate(R.layout.dialog_basic, null);

            textView = (TextView) view.findViewById(R.id.textView);

            MaterialCalendarView widget = (MaterialCalendarView) view.findViewById(R.id.calendarView);

            widget.setOnDateChangedListener(this);

            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.string_select_time)
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create();
        }

        // DialogFragment关闭时回传数据给Activity
        @Override
        public void onDestroy() {
            // 通过接口回传数据给activity
            if (myDialogFragment_Listener != null || dataTime != null || !"".equals(dataTime)) {
                myDialogFragment_Listener.getDataFrom_DialogFragment(dataTime);
            }
            super.onDestroy();
        }

        @Override
        public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
            dataTime = FORMATTER.format(date.getDate());
            textView.setText(dataTime);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LEFT) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    tvSelectLeft.setText(extras.getString("cityName"));
                }
            }
        } else if (requestCode == REQUEST_CODE_RIGHT) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    tvSelectRight.setText(extras.getString("cityName"));
                }
            }
        }
    }
}
