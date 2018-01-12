package com.thl.finger;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thl.fingerlib.FingerprintIdentify;
import com.thl.fingerlib.base.BaseFingerprint;

/**
 * 作者：taohaili
 * 时间：2018/1/12 10:11
 * 邮箱：1312398581@qq.com
 * 功能介绍：指纹识别
 */
public class MainActivity extends AppCompatActivity {

    private FingerprintIdentify mFingerprintIdentify;
    private static final int MAX_AVAILABLE_TIMES = 3;
    private TimeCount mTimeCount;

    private TextView tvMsg;
    private ImageView ivZhiwen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMsg = (TextView) findViewById(R.id.tv_msg);
        ivZhiwen = (ImageView) findViewById(R.id.iv_zhiwen);

        mFingerprintIdentify = new FingerprintIdentify(this, null);
        if (mFingerprintIdentify.isHardwareEnable()) {
            if (!mFingerprintIdentify.isRegisteredFingerprint()) {
                Toast.makeText(this, "请先录入指纹", Toast.LENGTH_SHORT).show();
            } else {
                mTimeCount = new TimeCount(30000, 1000);
                startFinger();
            }
        } else {
            Toast.makeText(this, "硬件不支持", Toast.LENGTH_SHORT).show();
        }
    }

    private void startFinger() {
        mFingerprintIdentify.startIdentify(MAX_AVAILABLE_TIMES, new BaseFingerprint.FingerprintIdentifyListener() {
            @Override
            public void onSucceed() {
                Toast.makeText(MainActivity.this, "解锁成功", Toast.LENGTH_SHORT).show();
                tvMsg.setTextColor(Color.parseColor("#ff333333"));
                tvMsg.setText("解锁成功");
            }

            @Override
            public void onNotMatch(int availableTimes) {
                Log.e("Fingerprint", "onNotMatch");
                tvMsg.setTextColor(Color.parseColor("#ffff0101"));
                tvMsg.setText("密码错了，还可输入" + availableTimes + "次");
                translate(ivZhiwen);
            }

            @Override
            public void onFailed(boolean isDeviceLocked) {
                tvMsg.setTextColor(Color.parseColor("#ffff0101"));
                tvMsg.setText("指纹验证太过频繁，请稍后重试或者输入密码登录");
                mTimeCount.start();
                translate(ivZhiwen);
            }

            @Override
            public void onStartFailedByDeviceLocked() {
                tvMsg.setTextColor(Color.parseColor("#ffff0101"));
                tvMsg.setText("指纹验证太过频繁，请稍后重试或者输入密码登录");
                mTimeCount.start();
                translate(ivZhiwen);
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mFingerprintIdentify != null) {
            mFingerprintIdentify.cancelIdentify();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFingerprintIdentify != null) {
            mFingerprintIdentify.resumeIdentify();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFingerprintIdentify != null) {
            mFingerprintIdentify.cancelIdentify();
        }
    }

    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            if (mFingerprintIdentify != null) {
                startFinger();
                tvMsg.setText("请重试");
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            tvMsg.setText(millisUntilFinished / 1000 + "秒后重试");
        }
    }


    protected void translate(View view) {
        Animation translateAnimation = AnimationUtils.loadAnimation(this, R.anim.myanim);
        view.startAnimation(translateAnimation);
    }

}
