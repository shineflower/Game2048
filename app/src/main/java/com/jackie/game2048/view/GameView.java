package com.jackie.game2048.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.GridLayout;

import com.jackie.game2048.activity.GameActivity;
import com.jackie.game2048.bean.GameItem;
import com.jackie.game2048.config.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jackie on 2016/12/23.
 * 游戏面板
 */

public class GameView extends GridLayout implements View.OnTouchListener {
    //GameView对应的矩阵
    private GameItem[][] mGameMatrix;
    //空格List
    private List<Point> mGameBlanks;
    // 矩阵行列数
    private int mGameLines;
    // 记录坐标
    private int mStartX, mStartY, mEndX, mEndY;
    // 辅助数组
    private List<Integer> mAssistList;
    private int mKeyItemNum = -1;
    // 历史记录数组
    private int[][] mGameMatrixHistory;
    // 历史记录分数
    private int mScoreHistory;
    // 最高记录
    private int mHighScore;
    // 目标分数
    private int mTarget;


    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initGameMatrix();
    }

    private void initGameMatrix() {
        //初始化矩阵
        removeAllViews();;

        mScoreHistory = 0;
        Config.SCORE = 0;
        Config.mGameLines = Config.mSharedPreferences.getInt(Config.KEY_GAME_LINES, 4);
        mGameLines = Config.mGameLines;

        mGameMatrix = new GameItem[mGameLines][mGameLines];
        mGameMatrixHistory = new int[mGameLines][mGameLines];
        mAssistList = new ArrayList<>();
        mGameBlanks = new ArrayList<>();
        mHighScore = Config.mSharedPreferences.getInt(Config.KEY_HIGH_SCORE, 0);
        setColumnCount(mGameLines);
        setRowCount(mGameLines);
        setOnTouchListener(this);
        //初始化View参数
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        display.getMetrics(displayMetrics);
        Config.mItemSize = displayMetrics.widthPixels / Config.mGameLines;
        initGameView(Config.mItemSize);
    }

    public void startGame() {
        initGameMatrix();
        initGameView(Config.mItemSize);
    }

    private void initGameView(int itemSize) {
        removeAllViews();
        GameItem gameItem;

        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                gameItem = new GameItem(getContext(), 0);
                addView(gameItem, itemSize, itemSize);
                //初始化GameMatrix全部为0 空格List为所有
                mGameMatrix[i][j] = gameItem;
                mGameBlanks.add(new Point(i, j));
            }
        }

        //随机添加数字
        addRandomNum();
        addRandomNum();
    }


    /**
     * 撤销上次移动
     */
    public void revertGame() {
        // 第一次不能撤销
        int sum = 0;
        for (int[] element : mGameMatrixHistory) {
            for (int i : element) {
                sum += i;
            }
        }

        if (sum != 0) {
            GameActivity.getGameActivity().setScore(mScoreHistory, 0);
            Config.SCORE = mScoreHistory;
            for (int i = 0; i < mGameLines; i++) {
                for (int j = 0; j < mGameLines; j++) {
                    mGameMatrix[i][j].setNum(mGameMatrixHistory[i][j]);
                }
            }
        }
    }

    /**
     * 添加随机数字
     */
    private void addRandomNum() {
        getGameBlanks();

        if (mGameBlanks.size() > 0) {
            int randomNum = (int) (Math.random() * mGameBlanks.size());
            Point randomPoint = mGameBlanks.get(randomNum);
            mGameMatrix[randomPoint.x][randomPoint.y].setNum(Math.random() > 0.2d ? 2 : 4);
        }
    }

    /**
     * super模式下添加一个指定数字
     */
    private void addSuperNum(int num) {
        if (checkSuperNum(num)) {
            getGameBlanks();

            if (mGameBlanks.size() > 0) {
                int randomNum = (int) (Math.random() * mGameBlanks.size());
                Point randomPoint = mGameBlanks.get(randomNum);
                mGameMatrix[randomPoint.x][randomPoint.y].setNum(num);
                animCreate(mGameMatrix[randomPoint.x][randomPoint.y]);
            }
        }
    }

    /**
     * 生成动画
     *
     * @param target GameItem
     */
    private void animCreate(GameItem target) {
        ScaleAnimation sa = new ScaleAnimation(0.1f, 1, 0.1f, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(100);
        target.setAnimation(null);
        target.getItemView().startAnimation(sa);
    }


    /**
     * 检查添加的数是否是指定的数
     *
     * @param num num
     * @return 添加的数
     */
    private boolean checkSuperNum(int num) {
        boolean flag = (num == 2 || num == 4 || num == 8 || num == 16
                || num == 32 || num == 64 || num == 128 || num == 256
                || num == 512 || num == 1024);
        return flag;
    }


    /**
     * 获取空格Item数组
     */
    private void getGameBlanks() {
        mGameBlanks.clear();

        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrix[i][j].getNum() == 0) {
                    mGameBlanks.add(new Point(i, j));
                }
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                saveHistoryMatrix();

                mStartX = (int) motionEvent.getX();
                mStartY = (int) motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                mEndX = (int) motionEvent.getX();
                mEndY = (int) motionEvent.getY();

                judgeDirection(mEndX - mStartX, mEndY - mStartY);
                if (isMoved()) {
                    addRandomNum();
                   //修改显示分数
                    GameActivity.getGameActivity().setScore(Config.SCORE, 0);
                }

                checkCompleted();
                break;
        }

        return true;
    }

    /**
     * 保留历史矩阵
     */
    private void saveHistoryMatrix() {
        mScoreHistory = Config.SCORE;

        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                mGameMatrixHistory[i][j] = mGameMatrix[i][j].getNum();
            }
        }
    }

    /**
     * 根据偏移量判断移动方向
     * @param offsetX offsetX
     * @param offsetY offsetY
     */
    private void judgeDirection(int offsetX, int offsetY) {
        int density = getDeviceDensity();
        int slideDistance = 5 * density;
        int maxDistance = 200 * density;

        boolean flagNormal = (Math.abs(offsetX) > slideDistance ||
                Math.abs(offsetY) > slideDistance) &&
                (Math.abs(offsetX) < maxDistance) &&
                (Math.abs(offsetY) < maxDistance);

        boolean flagSuper = Math.abs(offsetX) > maxDistance ||
                Math.abs(offsetY) > maxDistance;

        if (flagNormal && !flagSuper) {
            if (Math.abs(offsetX) > Math.abs(offsetY)) {
                if (offsetX > slideDistance) {
                    swipeRight();
                } else {
                    swipeLeft();
                }
             } else {
                if (offsetY > slideDistance) {
                    swipeDown();
                } else {
                    swipeUp();
                }
            }
        } else if (flagSuper) {  //启动超级用户权限来添加自定义数字
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final EditText editText = new EditText(getContext());
            builder.setTitle("Back Door").setView(editText).setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!TextUtils.isEmpty(editText.getText())) {
                                addSuperNum(Integer.parseInt(editText.getText().toString()));
                                checkCompleted();
                            }
                        }
                    }).setNegativeButton("ByeBye", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        }
    }

    private int getDeviceDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return (int) metrics.density;
    }

    /**
     * 检测所有数字 看是否有满足条件的
     *
     * @return 0:结束 1:正常 2:成功
     */
    private int checkNums() {
        getGameBlanks();

        if (mGameBlanks.size() == 0) {
            for (int i = 0; i < mGameLines; i++) {
                for (int j = 0; j < mGameLines; j++) {
                    if (j < mGameLines - 1) {
                        if (mGameMatrix[i][j].getNum() == mGameMatrix[i][j + 1].getNum()) {
                            return 1;
                        }
                    }

                    if (i < mGameLines - 1) {
                        if (mGameMatrix[i][j].getNum() == mGameMatrix[i + 1][j].getNum()) {
                            return 1;
                        }
                    }
                }
            }

            return 0;
        }

        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrix[i][j].getNum() == mTarget) {
                    return 2;
                }
            }
        }

        return 1;
    }


    /**
     * 判断是否结束
     * <p/>
     * 0:结束 1:正常 2:成功
     */
    private void checkCompleted() {
        int result = checkNums();
        if (result == 0) {
            if (Config.SCORE > mHighScore) {
                SharedPreferences.Editor editor = Config.mSharedPreferences.edit();
                editor.putInt(Config.KEY_HIGH_SCORE, Config.SCORE);
                editor.apply();
                GameActivity.getGameActivity().setScore(Config.SCORE, 1);
                Config.SCORE = 0;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Game Over").setPositiveButton("Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    startGame();
                }
            }).create().show();
            Config.SCORE = 0;
        } else if (result == 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Mission Accomplished").setPositiveButton("Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // 重新开始
                    startGame();
                }
            }).setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // 继续游戏 修改target
                    SharedPreferences.Editor editor = Config.mSharedPreferences.edit();
                    if (mTarget == 1024) {
                        editor.putInt(Config.KEY_GAME_GOAL, 2048);
                        mTarget = 2048;
                        GameActivity.getGameActivity().setGoal(2048);
                    } else if (mTarget == 2048) {
                        editor.putInt(Config.KEY_GAME_GOAL, 4096);
                        mTarget = 4096;
                        GameActivity.getGameActivity().setGoal(4096);
                    } else {
                        editor.putInt(Config.KEY_GAME_GOAL, 4096);
                        mTarget = 4096;
                        GameActivity.getGameActivity().setGoal(4096);
                    }

                    editor.apply();
                }
            }).create().show();

            Config.SCORE = 0;
        }
    }


    /**
     * 判断是否移动过(是否需要新增Item)
     * @return 是否移动
     */
    private boolean isMoved() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrixHistory[i][j] != mGameMatrix[i][j].getNum()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 滑动事件：上
     */
    private void swipeUp() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                int currentNum = mGameMatrix[j][i].getNum();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mAssistList.add(mKeyItemNum * 2);
                            Config.SCORE += mKeyItemNum * 2;
                            mKeyItemNum = -1;
                        } else {
                            mAssistList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }

            if (mKeyItemNum != -1) {
                mAssistList.add(mKeyItemNum);
            }

            // 改变Item值
            for (int j = 0; j < mAssistList.size(); j++) {
                mGameMatrix[j][i].setNum(mAssistList.get(j));
            }

            for (int m = mAssistList.size(); m < mGameLines; m++) {
                mGameMatrix[m][i].setNum(0);
            }

            // 重置行参数
            mKeyItemNum = -1;
            mAssistList.clear();
        }
    }

    /**
     * 滑动事件：下
     */
    private void swipeDown() {
        for (int i = mGameLines - 1; i >= 0; i--) {
            for (int j = mGameLines - 1; j >= 0; j--) {
                int currentNum = mGameMatrix[j][i].getNum();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mAssistList.add(mKeyItemNum * 2);
                            Config.SCORE += mKeyItemNum * 2;
                            mKeyItemNum = -1;
                        } else {
                            mAssistList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }

            if (mKeyItemNum != -1) {
                mAssistList.add(mKeyItemNum);
            }
            // 改变Item值
            for (int j = 0; j < mGameLines - mAssistList.size(); j++) {
                mGameMatrix[j][i].setNum(0);
            }

            int index = mAssistList.size() - 1;
            for (int m = mGameLines - mAssistList.size(); m < mGameLines; m++) {
                mGameMatrix[m][i].setNum(mAssistList.get(index));
                index--;
            }

            // 重置行参数
            mKeyItemNum = -1;
            mAssistList.clear();
            index = 0;
        }
    }

    /**
     * 滑动事件：左
     */
    private void swipeLeft() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                int currentNum = mGameMatrix[i][j].getNum();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mAssistList.add(mKeyItemNum * 2);
                            Config.SCORE += mKeyItemNum * 2;
                            mKeyItemNum = -1;
                        } else {
                            mAssistList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }

            if (mKeyItemNum != -1) {
                mAssistList.add(mKeyItemNum);
            }

            // 改变Item值
            for (int j = 0; j < mAssistList.size(); j++) {
                mGameMatrix[i][j].setNum(mAssistList.get(j));
            }

            for (int m = mAssistList.size(); m < mGameLines; m++) {
                mGameMatrix[i][m].setNum(0);
            }

            // 重置行参数
            mKeyItemNum = -1;
            mAssistList.clear();
        }
    }

    /**
     * 滑动事件：右
     */
    private void swipeRight() {
        for (int i = mGameLines - 1; i >= 0; i--) {
            for (int j = mGameLines - 1; j >= 0; j--) {
                int currentNum = mGameMatrix[i][j].getNum();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mAssistList.add(mKeyItemNum * 2);
                            Config.SCORE += mKeyItemNum * 2;
                            mKeyItemNum = -1;
                        } else {
                            mAssistList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }

            if (mKeyItemNum != -1) {
                mAssistList.add(mKeyItemNum);
            }

            // 改变Item值
            for (int j = 0; j < mGameLines - mAssistList.size(); j++) {
                mGameMatrix[i][j].setNum(0);
            }

            int index = mAssistList.size() - 1;
            for (int m = mGameLines - mAssistList.size(); m < mGameLines; m++) {
                mGameMatrix[i][m].setNum(mAssistList.get(index));
                index--;
            }

            // 重置行参数
            mKeyItemNum = -1;
            mAssistList.clear();
            index = 0;
        }
    }
}
