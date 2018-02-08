package com.example.airport.utils;

import android.content.res.Resources;


import com.example.airport.R;
import com.example.airport.config.enums.SpecialType;

import java.util.Arrays;

/**
 * Created by android on 2017/12/19.
 */

public class SpecialUtils {

    private static String[] MusicArray = {"唱歌", "唱歌儿", "唱一首歌", "我想听音乐", "播放音乐", "来首歌曲", "播放歌曲", "唱首歌", "音乐", "音乐播放中..."};
    private static String[] StoryArray = {"故事"};
    private static String[] JokeArray = {"笑话"};

    public static SpecialType doesExist(Resources resources, String speakTxt) {
        if (txtInArray(speakTxt, MusicArray) || Arrays.asList(resFoFinal(resources, R.array.other_misic)).contains(speakTxt)) {
            return SpecialType.Music;
        } else if (txtInArray(speakTxt, StoryArray) || Arrays.asList(resFoFinal(resources, R.array.other_story)).contains(speakTxt)) {
            return SpecialType.Story;
        } else if (txtInArray(speakTxt, JokeArray) || Arrays.asList(resFoFinal(resources, R.array.other_joke)).contains(speakTxt)) {
            return SpecialType.Joke;
        } else if (txtInTxt(resources, speakTxt, R.string.Forward)) {
            return SpecialType.Forward;
        } else if (txtInTxt(resources, speakTxt, R.string.Backoff)) {
            return SpecialType.Backoff;
        } else if (txtInTxt(resources, speakTxt, R.string.Turnleft)) {
            return SpecialType.Turnleft;
        } else if (txtInTxt(resources, speakTxt, R.string.Turnright)) {
            return SpecialType.Turnright;
        } else if (txtInTxt(resources, speakTxt, R.string.StopListener)) {
            return SpecialType.StopListener;
        } else if (txtInTxt(resources, speakTxt, R.string.string_question)) {
            return SpecialType.Problem;
        } else if (txtInTxt(resources, speakTxt, R.string.Seting_up)) {
            return SpecialType.Seting_up;
        } else if (txtInTxt(resources, speakTxt, R.string.string_wechat)) {
            return SpecialType.WeChat;
        } else if (txtInTxt(resources, speakTxt, R.string.string_navigation)) {
            return SpecialType.Navigation;
        } else if (txtInTxt(resources, speakTxt, R.string.string_airquery)) {
            return SpecialType.AirQuery;
        } else if (txtInTxt(resources, speakTxt, R.string.string_handle)) {
            return SpecialType.HandlePlance;
        }
        return SpecialType.NoSpecial;
    }

    private static boolean txtInTxt(Resources resources, String speakTxt, int res) {
        if (speakTxt.endsWith("。")) {
            speakTxt = speakTxt.substring(0, speakTxt.length() - 1);
        }
        return resources.getString(res).equals(speakTxt);
    }

    private static boolean txtInArray(String speakTxt, String[] speakArray) {
        if (speakTxt.endsWith("。")) {
            speakTxt = speakTxt.substring(0, speakTxt.length() - 1);
        }
        return Arrays.asList(speakArray).contains(speakTxt);
    }

    private static String[] resFoFinal(Resources resources, int id) {
        String[] res = resources.getStringArray(id);
        return res;
    }

    public static SpecialType doesExistLocal(Resources resources, String speakTxt) {
        if (txtInTxt(resources, speakTxt, R.string.Forward)) {
            return SpecialType.Forward;
        } else if (txtInTxt(resources, speakTxt, R.string.Backoff)) {
            return SpecialType.Backoff;
        } else if (txtInTxt(resources, speakTxt, R.string.Turnleft)) {
            return SpecialType.Turnleft;
        } else if (txtInTxt(resources, speakTxt, R.string.Turnright)) {
            return SpecialType.Turnright;
        } else if (txtInTxt(resources, speakTxt, R.string.StopListener)) {
            return SpecialType.StopListener;
        } else if (txtInTxt(resources, speakTxt, R.string.Back)) {
            return SpecialType.Back;
        } else if (txtInTxt(resources, speakTxt, R.string.Artificial)) {
            return SpecialType.Artificial;
        } else if (txtInTxt(resources, speakTxt, R.string.Face_lifting_area)) {
            return SpecialType.Face_lifting_area;
        }
//        else if (txtInTxt(resources, speakTxt, R.string.Next)) {
//            return SpecialType.Next;
//        } else if (txtInTxt(resources, speakTxt, R.string.Lase)) {
//            return SpecialType.Lase;
//        }
        return SpecialType.NoSpecial;
    }
}
