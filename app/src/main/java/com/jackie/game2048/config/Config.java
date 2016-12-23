package com.jackie.game2048.config;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by Jackie on 2016/12/23.
 * 游戏的全局配置
 */

public class Config extends Application {

    /**
     * SharedPreferences对象
     */
    public static SharedPreferences mSharedPreferences;

    /**
     * Game Goal
     */
    public static int mGameGoal;

    /**
     * GameView行列数
     */
    public static int mGameLines;

    /**
     * Item宽高
     */
    public static int mItemSize;

    /**
     * 记录分数
     */
    public static int SCORE = 0;

    public static String SP_HIGH_SCORE = "sp_high_score";

    public static String KEY_HIGH_SCORE = "key_high_score";

    public static String KEY_GAME_LINES = "key_game_lines";

    public static String KEY_GAME_GOAL = "key_game_goal";

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences = getSharedPreferences(SP_HIGH_SCORE, 0);
        mGameLines = mSharedPreferences.getInt(KEY_GAME_LINES, 4);
        mGameGoal = mSharedPreferences.getInt(KEY_GAME_GOAL, 2048);
        mItemSize = 0;
    }
}
