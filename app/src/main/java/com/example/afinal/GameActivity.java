package com.example.afinal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.Random;


public class GameActivity extends Activity {

    private Button playGame, overGame;
    private TextView times;
    private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    private LinearLayout show;

    private final int[] bgAtr = new int[9];  //保存九张带有老鼠的背景图
    private final int[] btnAtr = new int[9]; //保存九个洞对应的按钮
    private final MyHandler myHandler = new MyHandler();
    private SumTime sumTime;
    private GameTime gameTime;
    private GoTime goTime;
    private ClickTime clickTime;

    private int t = 29;
    private int sumMouse = 0; //出現數量
    private int clickMouse = 0; //打到的數量
    private int mouseCheckedId = 0; //圖片序號(第幾個洞)

    private boolean flag = true;  //用于限制开始按钮的有效点击次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        initView(); //初始化控件
        initAtrr(); //添加图片id到数组bgAtrr中
        initListener(); //初始化监听器，将监听器与开始和结束按钮进行绑定
    }

    /**
     * 计时器，游戏开始，该线程sleep一分钟，时间到后，终止弹老鼠进程和计时进程并弹出提示框，传入MyHander的参数为0
     */
    class SumTime extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            gameTime.isStop(); //调用gameTime的isStop方法，使其停止弹出老鼠
            goTime.isStop(); //调用goTime的isStop方法，结束该线程

            Message message = Message.obtain();
            message.what = 0;
            myHandler.sendMessage(message);
        }
    }

    /**
     * 随机弹出老鼠图片，传入到MyHander的参数为1
     */
    class GameTime extends Thread {
        private boolean isStoped = false;

        private void isStop() {
            isStoped = true;
            gameTime.interrupt();
        }

        @Override
        public void run() {
            super.run();
            while (!isStoped) {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message message = Message.obtain(); //获取Message对象
                message.what = 1;
                myHandler.sendMessage(message);
            }
        }
    }

    /**
     * 时间计数,传入到MyHander的参数为2
     */
    class GoTime extends Thread {
        private boolean isStoped = false;

        private void isStop() {
            isStoped = true;
            goTime.interrupt();
        }

        @Override
        public void run() {
            super.run();
            while (!isStoped) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = Message.obtain();
                message.what = 2;
                myHandler.sendMessage(message);
            }
        }
    }

    class ClickTime implements Runnable {


        @Override
        public void run() {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            flag = true;
        }
    }

    /**
     * 根据传入的参数进行处理，传入为0则表示游戏结束，传入为1表示开始游戏，传入2是倒时器开始
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    sumTime.interrupt();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(GameActivity.this);
                    dialog.setTitle("遊戲结束");
                    dialog.setMessage("出現 " + sumMouse + " 隻麋鹿 \n" +
                            "逮到 " + clickMouse + " 隻\n" +
                            "捕獲率：" + clickMouse * 100 / sumMouse + "%"
                    );
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("再試一次", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            times.setText(30 + "");
                            t = 30;
                            sumMouse = 0;
                            clickMouse = 0;
                            onRestart();
                        }
                    });
                    dialog.show();
                    break;
                case 1:
                    Random random = new Random();
                    int index = random.nextInt(9);
                    show.setBackgroundResource(bgAtr[index]);
                    sumMouse++;
                    if (btnAtr[index] == mouseCheckedId) {
                        clickMouse++;  //判断当前老鼠所在的RadioButton的id与用户点击的是否一致，若一致则为打中地鼠
                    }
                    break;
                case 2:
                    times.setText(t + "");
                    t--;
                    break;
            }
        }
    }


    class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mouseCheckedId = v.getId();
            switch (v.getId()) {
                case R.id.bt_play:
                    if (flag) {
                        sumTime = new SumTime();
                        sumTime.start();
                        gameTime = new GameTime();
                        gameTime.start();
                        goTime = new GoTime();
                        goTime.start();
                    }
                    flag = false;
                    clickTime = new ClickTime();
                    new Thread(clickTime).start();
                    break;
                case R.id.bt_over:
                    finish();
                    break;
            }
        }
    }

    private void initListener() {
        ButtonListener listener = new ButtonListener();
        playGame.setOnClickListener(listener);
        overGame.setOnClickListener(listener);

        btn1.setOnClickListener(listener);
        btn2.setOnClickListener(listener);
        btn3.setOnClickListener(listener);
        btn4.setOnClickListener(listener);
        btn5.setOnClickListener(listener);
        btn6.setOnClickListener(listener);
        btn7.setOnClickListener(listener);
        btn8.setOnClickListener(listener);
        btn9.setOnClickListener(listener);
    }

    private void initAtrr() {
        bgAtr[0] = R.drawable.game_background_1;
        bgAtr[1] = R.drawable.game_background_2;
        bgAtr[2] = R.drawable.game_background_3;
        bgAtr[3] = R.drawable.game_background_4;
        bgAtr[4] = R.drawable.game_background_5;
        bgAtr[5] = R.drawable.game_background_6;
        bgAtr[6] = R.drawable.game_background_7;
        bgAtr[7] = R.drawable.game_background_8;
        bgAtr[8] = R.drawable.game_background_9;

        btnAtr[0] = R.id.btn1;
        btnAtr[1] = R.id.btn2;
        btnAtr[2] = R.id.btn3;
        btnAtr[3] = R.id.btn4;
        btnAtr[4] = R.id.btn5;
        btnAtr[5] = R.id.btn6;
        btnAtr[6] = R.id.btn7;
        btnAtr[7] = R.id.btn8;
        btnAtr[8] = R.id.btn9;
    }

    private void initView() {
        times = findViewById(R.id.tv_time);

        playGame = findViewById(R.id.bt_play);
        overGame = findViewById(R.id.bt_over);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);

        show = findViewById(R.id.show);
    }
}



