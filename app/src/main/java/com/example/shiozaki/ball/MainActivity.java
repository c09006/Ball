package com.example.shiozaki.ball;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainActivity extends AppCompatActivity implements SensorEventListener, SurfaceHolder.Callback{
    //フィールド
    //定数
    static final float RADIUS = 25.0f;//半径の設定
    static final float COEF = 1000.0f;//移動量調整

    //変数
    //センサーの設定
    SensorManager mSensorManager;
    Sensor mAccSensor;

    //SurfaceViewの設定
    SurfaceHolder mHolder;
    int mSurfaceWidth;
    int mSurfaceHeight;

    //ボールの設定
    float mBallx;//ボールの現在のX座標
    float mBally;//ボールの現在のY座標
    float mVX;//ボールのX軸方向へのスピード
    float mVY;//ボールのY軸方向へのスピード

    //時間の設定
    long mFrom;//前回センサーから加速度を取得した時間
    long mTo;//今回センサーから加速度を取得した時間

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//画面が回転しないように固定
        setContentView(R.layout.activity_main);

        //センサーの設定、加速度センサーを利用する
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //SurfaceViewの設定、SurfaceViewにイベントを追加する
        SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //取得したセンサーデータの選別
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){//加速度センサーだったら
            //デバッグ用、x,y,z軸の加速度をログデータとして表示する
            Log.d("MainActivity",
                    "x=" + String.valueOf(event.values[0]) +
                    "y=" + String.valueOf(event.values[1]) +
                    "z=" + String.valueOf(event.values[2]));
            //デバッグここまで

            //センサーデータの取得[0],[1],[2]にそれぞれx,y,zのデータが格納されている
            float x = -event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            //物理計算用,ミリ秒を秒に変換
            mTo = System.currentTimeMillis();
            float t = (float)(mTo - mFrom);
            t = t / 1000.0f;

            //ボールの移動距離を計算

            //実習用
            mBallx += x;
            mBally += y;

            //(2)リアルに動かそう！（物理計算）
            //d = V0 * t + 1/2 * a * t^2
            //ここから
         /*
            float dx = mVX * t + x * t * t / 2.0f;
            float dy = mVY * t + y * t * t / 2.0f;
            mBallx = mBallx + dx * COEF;
            mBally = mBally + dy * COEF;
            mVX = mVX + x * t;
            mVY = mVY + y * t;
         */
            //ここまでコメントアウト

            //(3)動ける範囲を制限しよう！
            //ボールが壁面に到達したときの跳ね返り処理
            /*
            if(mBallx - RADIUS < 0 && mVX < 0){
                mVX = -mVX / 1.5f;
                mBallx = RADIUS;
            }else if(mBallx + RADIUS > mSurfaceWidth && mVX > 0){
                mVX = -mVX / 1.5f;
                mBallx = mSurfaceWidth - RADIUS;
            }
            if(mBally - RADIUS < 0 && mVY < 0){
                mVY = -mVY / 1.5f;
                mBally = RADIUS;
            }else if(mBally + RADIUS > mSurfaceHeight && mVY > 0){
                mVY = -mVY / 1.5f;
                mBally = mSurfaceHeight - RADIUS;
            }
            */

            //移動終了時間を取得
            mFrom = System.currentTimeMillis();

            //(1)描画してみよう！
            //drawCanvas();//描画用メソッド、自作メソッドを呼び出すため初期ではエラーが起こる

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
/*
    //デバッグ用、SurfaceHolderに同じ内容を記述しているため、ここを有効にするとエラーが起こる
    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this,mAccSensor,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
*/
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mFrom = System.currentTimeMillis();
        mSensorManager.registerListener(this,mAccSensor,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        mBallx = width / 2;
        mBally = height / 2;
        mVX = 0;
        mVY = 0;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSensorManager.unregisterListener(this);
    }

    //描画用メソッド
    //SurfaceViewに描画する
    private void drawCanvas(){
        Canvas c = mHolder.lockCanvas();
        c.drawColor(Color.YELLOW);
        Paint paint = new Paint();
        paint.setColor(Color.MAGENTA);
        c.drawCircle(mBallx, mBally, RADIUS, paint);
        mHolder.unlockCanvasAndPost(c);
    }

}
