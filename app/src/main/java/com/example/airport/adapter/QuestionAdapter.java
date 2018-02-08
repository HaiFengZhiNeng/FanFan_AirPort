package com.example.airport.adapter;

import android.content.Context;

import com.example.airport.R;
import com.example.airport.base.adapter.BaseAdapter;
import com.example.airport.base.adapter.BaseRecyclerViewHolder;
import com.example.airport.modle.Question;

import java.util.List;

/**
 * Created by dell on 2018/1/19.
 */

public class QuestionAdapter extends BaseAdapter<Question> {

    public QuestionAdapter(Context context, List<Question> questionList) {
        super(context, R.layout.layout_question_item, questionList);
    }

    @Override
    protected void convert(BaseRecyclerViewHolder viewHolder, Question item, int pos) {
        viewHolder.getTextView(R.id.tv_question).setText(item.getQuestion());
    }

}
