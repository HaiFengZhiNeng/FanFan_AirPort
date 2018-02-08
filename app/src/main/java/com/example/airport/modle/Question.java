package com.example.airport.modle;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by dell on 2018/1/19.
 */
@Entity
public class Question {
    @Id
    private Long id;
    private String question;
    private String answer;

    @Generated(hash = 560775945)
    public Question(Long id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    public Question(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    @Generated(hash = 1868476517)
    public Question() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

}
