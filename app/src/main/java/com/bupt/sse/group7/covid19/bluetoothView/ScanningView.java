package com.bupt.sse.group7.covid19.bluetoothView;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bupt.sse.group7.covid19.R;

import java.util.Timer;
import java.util.TimerTask;


public class ScanningView extends FrameLayout {

    private static final String TAG = "ScanningView";

    /**
     * 指针
     */
    private ImageView ivNeedle;

    /**
     * 波纹
     */
    private ImageView ivRipple;

    /**
     * 中间文字
     */
    private TextView tvTitle;

    /**
     * 装波纹的容器
     */
    private FrameLayout fl_move_circle;


    private Context context;

    // 动画
    private AnimatorSet pointerAnimatorSet;
    private AnimatorSet outCircleAnimatorSet;
    private AnimatorSet moveCircleAnimatorSet;

    //定时器
    private Timer moveCircleTimer;

    //定时器任务
    private TimerTask timerTask;

    public ScanningView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ScanningView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    /**
     * 初始化View
     */
    private void initView(){
        View v = LayoutInflater.from(getContext()).inflate(R.layout.rotate_view,null);
        ivNeedle = v.findViewById(R.id.iv_btn);
        ivRipple = v.findViewById(R.id.iv_out_circle);
        tvTitle = v.findViewById(R.id.tv_title);
        fl_move_circle = v.findViewById(R.id.fl_move_circle);
        addView(v, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        initPointAnimation();
        initOutCircleAnim();

        outCircleAnimatorSet.start();
    }



    @SuppressLint("HandlerLeak")
    private  Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.d(TAG, "handleMessage：收到扩大波纹消息");
                    addOneMoveCircleAnim();
                    break;
            }
        }
    };


    /**
     * 设置标题
     * @param txt
     */
    public void setTitle(String txt){
        tvTitle.setText(txt);
    }


    /**
     * 触发一个发散波纹动画
     */
    private void addOneMoveCircleAnim() {
        final ImageView imageView = new ImageView(getContext());
        LayoutParams lp = new LayoutParams(dip2px(getContext(), 100), dip2px(getContext(), 100));
        lp.gravity = Gravity.CENTER;
        imageView.setLayoutParams(lp);
        imageView.setImageResource(R.drawable.outcircle);
        fl_move_circle.addView(imageView);
        ObjectAnimator outCircleAnimX = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 5f);
        ObjectAnimator outCircleAnimY = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 5f);
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(imageView, "alpha", 0.6f, 0);
        outCircleAnimX.setDuration(5000);
        outCircleAnimY.setDuration(5000);
        alphaAnim.setDuration(5000);
        moveCircleAnimatorSet = new AnimatorSet();
        moveCircleAnimatorSet.playTogether(outCircleAnimX, outCircleAnimY, alphaAnim);
        moveCircleAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d(TAG, "onAnimationStart：开始扩大波纹动画");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(TAG, "onAnimationEnd：结束扩大波纹动画");

            }
            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d(TAG, "onAnimationCancel：销毁扩大波纹动画");
                //移除掉刚才添加的波纹
                fl_move_circle.removeView(imageView);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        moveCircleAnimatorSet.start();
    }

    /**
     * 开始循环的放大缩小波纹
     */
    private void initOutCircleAnim() {
        ObjectAnimator outCircleAlpha = ObjectAnimator.ofFloat(ivRipple, "alpha", 0.2f, 0.6f);
        outCircleAlpha.setDuration(1000);
        ObjectAnimator outCircleAnimX = ObjectAnimator.ofFloat(ivRipple, "scaleX", 1f, 1.18f, 1f);
        ObjectAnimator outCircleAnimY = ObjectAnimator.ofFloat(ivRipple, "scaleY", 1f, 1.18f, 1f);
        outCircleAnimX.setDuration(2000);
        outCircleAnimY.setDuration(2000);
        outCircleAnimX.setRepeatCount(ValueAnimator.INFINITE);
        outCircleAnimY.setRepeatCount(ValueAnimator.INFINITE);
        outCircleAnimX.setInterpolator(new LinearInterpolator());
        outCircleAnimY.setInterpolator(new LinearInterpolator());
        outCircleAnimatorSet = new AnimatorSet();
        outCircleAnimatorSet.playTogether(outCircleAnimX, outCircleAnimY, outCircleAlpha);
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 初始化指针动画
     */
    private void initPointAnimation() {
        pointerAnimatorSet = new AnimatorSet();
        ObjectAnimator scaleYIn = ObjectAnimator.ofFloat(ivNeedle, "rotation", 0f, 360f);
        scaleYIn.setDuration(1800);
        scaleYIn.setInterpolator(new LinearInterpolator());
        scaleYIn.setRepeatCount(ValueAnimator.INFINITE);
        pointerAnimatorSet.play(scaleYIn);
    }

    /**
     * 扫描开始后的动画
     */
    public void startScanAnimation(){
        Log.d(TAG, "startScanAnimation：开始扫描动画");

        //隐藏呼吸的波纹
//        outCircleAnimatorSet.end();
        ivRipple.setVisibility(GONE);

        //指针开始转
        pointerAnimatorSet.start();

        // 创建和启动放大波纹动画的定时器
        if(moveCircleTimer == null){
            moveCircleTimer = new Timer();
            Log.d(TAG, "startScanAnimation：新建timer");

        }
        if(timerTask == null){
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(1);
                }
            };
            Log.d(TAG, "startScanAnimation：新建timerTask");
        }
        moveCircleTimer.schedule(timerTask, 0, 1800);
        Log.d(TAG, "startScanAnimation：扫描动画已开始");

    }

    /**
     * 扫描结束后的动画
     */
    public void stopScanAnimation(){
        Log.d(TAG, "stopScanAnimation：结束扫描动画");

        //停止和销毁放大波纹动画的定时器
        if(moveCircleTimer != null)
            moveCircleTimer.cancel();
        moveCircleTimer = null;

        if(timerTask != null)
            timerTask.cancel();
        timerTask = null;

        Log.d(TAG, "stopScanAnimation：timer和timerTask已销毁");


        //销毁放大波纹动画
        if(moveCircleAnimatorSet != null)
            moveCircleAnimatorSet.cancel();
        moveCircleAnimatorSet = null;

        //指针停止转
        pointerAnimatorSet.end();
//        outCircleAnimatorSet.start();
        //显示呼吸的波纹
        ivRipple.setVisibility(VISIBLE);

        Log.d(TAG, "stopScanAnimation：扫描动画已结束");


    }
}
