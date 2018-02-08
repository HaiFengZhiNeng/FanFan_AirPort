package com.example.airport.city;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.airport.R;
import com.example.airport.adapter.CityResultListAdapter;
import com.example.airport.adapter.HotCityAdapter;
import com.example.airport.base.BaseActivity;
import com.example.airport.database.citySelect.CityDBHelper;
import com.example.airport.database.citySelect.CDatabaseHelper;
import com.example.airport.modle.City;
import com.example.airport.utils.CityRetrievalUtil;
import com.example.airport.utils.PingYinUtil;
import com.example.airport.view.CitySelectLetterListView;

import android.widget.AbsListView.OnScrollListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * 城市选择
 *
 * @author Guanluocang
 *         created at 2018/1/24 10:55
 */
public class CitySelectView extends BaseActivity implements OnScrollListener {

    @BindView(R.id.et_inputName)
    EditText etInputName;
    @BindView(R.id.lv_city)
    ListView lvCity;
    @BindView(R.id.lv_searchResult)
    ListView lvSearchResult;
    @BindView(R.id.tv_noresult)
    TextView tvNoresult;
    @BindView(R.id.lv_retrieval)
    CitySelectLetterListView lvRetrieval;// A-Z listview

    private CityResultListAdapter cityResultListAdapter;// 结果Adapter

    private CityRetrievalAdapter cityRetrievalAdapter;//检索Adapter


    private TextView overlay; // 对话框首字母textview


    private Handler handler;
    private OverlayThread overlayThread; // 显示首字母对话框

    private int locateProcess = 1; // 记录当前定位的状态 正在定位-定位成功-定位失败

    private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
    private String[] sections;// 存放存在的汉语拼音首字母

    private ArrayList<City> allCity_lists = new ArrayList<>(); // 所有城市列表
    private ArrayList<City> city_lists = new ArrayList<>();// 城市列表

    private ArrayList<City> city_hot = new ArrayList<>();
    private ArrayList<City> city_result = new ArrayList<>();
    private ArrayList<String> city_history = new ArrayList<>();

    private CDatabaseHelper helper;


    /**
     * 返回当前界面布局文件
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_city_select_view;
    }

    /**
     * 此方法描述的是： 初始化所有view
     */
    @Override
    protected void initView() {

        helper = new CDatabaseHelper(this);
        alphaIndexer = new HashMap<String, Integer>();
        handler = new Handler();
        overlayThread = new OverlayThread();

    }

    /**
     * 此方法描述的是： 初始化所有数据的方法
     */
    @Override
    protected void initData() {

        lvCity.setAdapter(cityRetrievalAdapter);
        lvCity.setOnScrollListener(this);

        cityResultListAdapter = new CityResultListAdapter(this, city_result);
        lvSearchResult.setAdapter(cityResultListAdapter);

        initOverlay();
        cityInit();
        hotCityInit();
//        hisCityInit();
        setAdapter(allCity_lists, city_hot, city_history);
    }

    /**
     * 此方法描述的是： 设置所有事件监听
     */
    @Override
    protected void setListener() {
        etInputName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString() == null || "".equals(s.toString())) {
                    lvRetrieval.setVisibility(View.VISIBLE);
                    lvCity.setVisibility(View.VISIBLE);
                    lvSearchResult.setVisibility(View.GONE);
                    tvNoresult.setVisibility(View.GONE);
                } else {
                    city_result.clear();
                    lvRetrieval.setVisibility(View.GONE);
                    lvCity.setVisibility(View.GONE);
                    getResultCityList(s.toString());
                    if (city_result.size() <= 0) {
                        tvNoresult.setVisibility(View.VISIBLE);
                        lvSearchResult.setVisibility(View.GONE);
                    } else {
                        tvNoresult.setVisibility(View.GONE);
                        lvSearchResult.setVisibility(View.VISIBLE);
                        cityResultListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lvRetrieval.setOnTouchingLetterChangedListener(new LetterListViewListener());

        // 城市列表点击事件
        lvCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Bundle bundle = new Bundle();
                bundle.putString("cityName", allCity_lists.get(position).getName());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                // 返回intent
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        // 搜索结果 点击事件
        lvSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("cityName", city_result.get(position).getName());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                // 返回intent
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void setAdapter(List<City> list, List<City> hotList,
                            List<String> hisCity) {
        cityRetrievalAdapter = new CityRetrievalAdapter(this, list, hotList, hisCity);
        lvCity.setAdapter(cityRetrievalAdapter);
    }

    private boolean mReady;

    // 初始化汉语拼音首字母弹出提示框
    private void initOverlay() {
        mReady = true;
        LayoutInflater inflater = LayoutInflater.from(this);
        overlay = (TextView) inflater.inflate(R.layout.layout_overlay, null);
        overlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager windowManager = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(overlay, lp);
    }

    private void cityInit() {
//        City city = new City("定位", "0"); // 当前定位城市
//        allCity_lists.add(city);
//         city = new City("最近", "1"); // 最近访问的城市
//        allCity_lists.add(city);
        City city = new City("热门", "0"); // 热门城市
        allCity_lists.add(city);
        city = new City("全部", "1"); // 全部城市
        allCity_lists.add(city);

        city_lists = getCityList();
        allCity_lists.addAll(city_lists);
    }

    /**
     * 热门城市
     */
    public void hotCityInit() {
        City city = new City("上海", "0");
        city_hot.add(city);
        city = new City("北京", "0");
        city_hot.add(city);
        city = new City("广州", "0");
        city_hot.add(city);
        city = new City("深圳", "0");
        city_hot.add(city);
        city = new City("武汉", "0");
        city_hot.add(city);
        city = new City("天津", "0");
        city_hot.add(city);
        city = new City("西安", "0");
        city_hot.add(city);
        city = new City("南京", "0");
        city_hot.add(city);
        city = new City("杭州", "0");
        city_hot.add(city);
        city = new City("成都", "0");
        city_hot.add(city);
        city = new City("重庆", "0");
        city_hot.add(city);
    }

    private void hisCityInit() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "select * from recentcity order by date desc limit 0, 3", null);
        while (cursor.moveToNext()) {
            city_history.add(cursor.getString(1));
        }
        cursor.close();
        db.close();
    }

    @SuppressWarnings("unchecked")
    private ArrayList<City> getCityList() {
        CityDBHelper cityDbHelper = new CityDBHelper(this);
        ArrayList<City> list = new ArrayList<City>();
        try {
            cityDbHelper.createDataBase();
            SQLiteDatabase db = cityDbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from city", null);
            City city;
            while (cursor.moveToNext()) {
                city = new City(cursor.getString(1), cursor.getString(2));
                list.add(city);
            }
            cursor.close();
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(list, comparator);
        return list;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if (scrollState == SCROLL_STATE_TOUCH_SCROLL
                || scrollState == SCROLL_STATE_FLING) {
            isScroll = true;
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (!isScroll) {
            return;
        }

        if (mReady) {
            String text;
            String name = allCity_lists.get(firstVisibleItem).getName();
            String pinyin = allCity_lists.get(firstVisibleItem).getPinyi();
            if (firstVisibleItem < 2) {
                text = name;
            } else {
                text = PingYinUtil.converterToFirstSpell(pinyin)
                        .substring(0, 1).toUpperCase();
            }
            overlay.setText(text);
            overlay.setVisibility(View.VISIBLE);
            handler.removeCallbacks(overlayThread);
            // 延迟一秒后执行，让overlay为不可见
            handler.postDelayed(overlayThread, 1000);
        }
    }

    private boolean isScroll = false;

    private class LetterListViewListener implements
            CitySelectLetterListView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            isScroll = false;
            if (alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);
                lvCity.setSelection(position);
                overlay.setText(s);
                overlay.setVisibility(View.VISIBLE);
                handler.removeCallbacks(overlayThread);
                // 延迟一秒后执行，让overlay为不可见
                handler.postDelayed(overlayThread, 1000);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void getResultCityList(String keyword) {
        CityDBHelper cityDbHelper = new CityDBHelper(this);
        try {
            cityDbHelper.createDataBase();
            SQLiteDatabase db = cityDbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(
                    "select * from city where name like \"%" + keyword
                            + "%\" or pinyin like \"%" + keyword + "%\"", null);
            City city;
            Log.e("info", "length = " + cursor.getCount());
            while (cursor.moveToNext()) {
                city = new City(cursor.getString(1), cursor.getString(2));
                city_result.add(city);
            }
            cursor.close();
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(city_result, comparator);
    }

    /**
     * a-z排序
     */
    @SuppressWarnings("rawtypes")
    Comparator comparator = new Comparator<City>() {
        @Override
        public int compare(City lhs, City rhs) {
            String a = lhs.getPinyi().substring(0, 1);
            String b = rhs.getPinyi().substring(0, 1);
            int flag = a.compareTo(b);
            if (flag == 0) {
                return a.compareTo(b);
            } else {
                return flag;
            }
        }
    };

    // 设置overlay不可见
    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            overlay.setVisibility(View.GONE);
        }
    }

    public class CityRetrievalAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;
        private List<City> list;
        private List<City> hotList;
        private List<String> hisCity;
        final int VIEW_TYPE = 3;

        public CityRetrievalAdapter(Context context, List<City> list,
                                    List<City> hotList, List<String> hisCity) {
            this.inflater = LayoutInflater.from(context);
            this.list = list;
            this.context = context;
            this.hotList = hotList;
            this.hisCity = hisCity;
            alphaIndexer = new HashMap<String, Integer>();
            sections = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                // 当前汉语拼音首字母
                String currentStr = CityRetrievalUtil.getAlpha(list.get(i).getPinyi());
                // 上一个汉语拼音首字母，如果不存在为" "
                String previewStr = (i - 1) >= 0 ? CityRetrievalUtil.getAlpha(list.get(i - 1)
                        .getPinyi()) : " ";
                if (!previewStr.equals(currentStr)) {
                    String name = CityRetrievalUtil.getAlpha(list.get(i).getPinyi());
                    alphaIndexer.put(name, i);
                    sections[i] = name;
                }
            }
        }

        @Override
        public int getViewTypeCount() {
            return VIEW_TYPE;
        }

        @Override
        public int getItemViewType(int position) {
            return position < 2 ? position : 2;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        CityRetrievalAdapter.ViewHolder holder;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final TextView city;
//            if (null != convertView) {
            switch (getItemViewType(position)) {
                case 0:

                    convertView = inflater.inflate(R.layout.layout_recent_city, null);
                    final GridView hotCity = (GridView) convertView
                            .findViewById(R.id.recent_city);
                    hotCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            Bundle bundle = new Bundle();
                            bundle.putString("cityName", city_hot.get(position).getName());
                            Intent intent = new Intent();
                            intent.putExtras(bundle);
                            // 返回intent
                            setResult(RESULT_OK, intent);
                            finish();

                        }
                    });
                    hotCity.setAdapter(new HotCityAdapter(context, this.hotList));
                    TextView hotHint = (TextView) convertView
                            .findViewById(R.id.recentHint);
                    hotHint.setText("热门城市");
                    break;
                case 1:
                    convertView = inflater.inflate(R.layout.layout_total_item, null);
                    break;
                default:
                    if (convertView == null) {
                        convertView = inflater.inflate(R.layout.layout_city_list_item, null);
                        holder = new CityRetrievalAdapter.ViewHolder();
                        holder.alpha = (TextView) convertView
                                .findViewById(R.id.alpha);
                        holder.name = (TextView) convertView
                                .findViewById(R.id.name);
                        convertView.setTag(holder);
                    } else {
                        holder = (CityRetrievalAdapter.ViewHolder) convertView.getTag();
                    }
                    if (position >= 1) {
                        holder.name.setText(list.get(position).getName());
                        String currentStr = CityRetrievalUtil.getAlpha(list.get(position).getPinyi());
                        String previewStr = (position - 1) >= 0 ? CityRetrievalUtil.getAlpha(list
                                .get(position - 1).getPinyi()) : " ";
                        if (!previewStr.equals(currentStr)) {
                            holder.alpha.setVisibility(View.VISIBLE);
                            holder.alpha.setText(currentStr);
                        } else {
                            holder.alpha.setVisibility(View.GONE);
                        }
                    }
                    break;
            }
//            }
            return convertView;
        }

        private class ViewHolder {
            TextView alpha; // 首字母标题
            TextView name; // 城市名字
        }
    }
}
