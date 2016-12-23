package com.jackie.game2048.bean;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jackie.game2048.config.Config;

/**
 * Created by Jackie on 2016/12/22.
 * Game小方块
 */

public class GameItem extends FrameLayout {
    //Item显示的数字
    private int mCardShowNum;
    //数字Title
    private TextView mTvNum;
    private LayoutParams mParams;

    public GameItem(Context context, int cardShowNum) {
        super(context);

        this.mCardShowNum = cardShowNum;

        //初始化Item
        initCardItem();
    }

    /**
     * 初始化Item
     */
    private void initCardItem() {
        //设置面板背景色，是由Frame拼起来
        setBackgroundColor(Color.GRAY);
        mTvNum = new TextView(getContext());
        setNum(mCardShowNum);
        //修改5 * 5时字体太大
        int gameLines = Config.mSharedPreferences.getInt(Config.KEY_GAME_LINES, 4);
        if (gameLines == 4) {
            mTvNum.setTextSize(35);
        } else if (gameLines == 5) {
            mTvNum.setTextSize(25);
        } else {
            mTvNum.setTextSize(20);
        }

        TextPaint paint = mTvNum.getPaint();
        paint.setFakeBoldText(true);
        mTvNum.setGravity(Gravity.CENTER);

        mParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mParams.setMargins(5, 5, 5, 5);
        addView(mTvNum, mParams);
    }

    public View getItemView() {
        return mTvNum;
    }

    public int getNum() {
        return mCardShowNum;
    }

    public void setNum(int num) {
        this.mCardShowNum = num;

        if (num == 0) {
            mTvNum.setText("");
        } else {
            mTvNum.setText(num + "");
        }

        //设置背景颜色
        switch (num) {
            case 0:
                mTvNum.setBackgroundColor(0x00000000);
                break;
            case 2:
                mTvNum.setBackgroundColor(0xffeee5db);
                break;
            case 4:
                mTvNum.setBackgroundColor(0xffeee0ca);
                break;
            case 8:
                mTvNum.setBackgroundColor(0xfff2c17a);
                break;
            case 16:
                mTvNum.setBackgroundColor(0xfff59667);
                break;
            case 32:
                mTvNum.setBackgroundColor(0xfff68c6f);
                break;
            case 64:
                mTvNum.setBackgroundColor(0xfff66e3c);
                break;
            case 128:
                mTvNum.setBackgroundColor(0xffedcf74);
                break;
            case 256:
                mTvNum.setBackgroundColor(0xffedcc64);
                break;
            case 512:
                mTvNum.setBackgroundColor(0xffedc854);
                break;
            case 1024:
                mTvNum.setBackgroundColor(0xffedc54f);
                break;
            case 2048:
                mTvNum.setBackgroundColor(0xffedc32c);
                break;
            default:
                mTvNum.setBackgroundColor(0xff3c4a34);
                break;
        }
    }
}