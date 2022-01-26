package com.vine.projectdemo.View;

/**
 * Created by user on 2017/5/10.
 */

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TouchImageView extends androidx.appcompat.widget.AppCompatImageView {

    // 現在狀態的Matrix
    // 這會在每次觸發觸控事件時變化
    private Matrix mMatrix = new Matrix();
    // 用來保持上個狀態的Matrix
    // 由於postTranslate使用相對座標
    // 保存好上個狀態，以便使用set設定為基底使用
    private Matrix mSavedMatrix = new Matrix();
    // 狀態旗標
    private static final int NONE = 0; // 什麼都不做
    private static final int DRAG = 1; // 拖曳中
    private static final int ZOOM = 2; // 調整尺寸中

    //設定縮放最小比例
    private float mMinScale = 1.0f;
    //設定縮放最大比例
    private float mMaxScale = 5.0f;

    // 現在狀態
    private int mMode = NONE;
    // 存放拖曳起始座標
    private PointF mStartPoint = new PointF();
    // 存放調整尺寸起始距離
    private float mOldDistance;
    // 存放調整尺寸的中央座標
    private PointF mMidPoint = new PointF();
    public TouchImageView(Context context) {
        super(context);
        init(context);
    }
    public TouchImageView(Context context, AttributeSet attrs,
                          int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    public TouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    //圖片縮放層級設定
    private void Scale()
    {
        //取得圖片縮放的層級
        float level[] = new float[9];
        mMatrix.getValues(level);

        //狀態為縮放時進入
        if (mMode == ZOOM)
        {
            //若層級小於1則縮放至原始大小
            if (level[0] < mMinScale)
            {
                mMatrix.setScale(mMinScale, mMinScale);
                mMatrix.postTranslate(mMidPoint.x,mMidPoint.y);
            }

            //若縮放層級大於最大層級則顯示最大層級
            if (level[0] > mMaxScale)  mMatrix.set(mSavedMatrix);
        }
    }

    // 初始化
    private void init(Context context) {
        // 可以點選
        setClickable(true);
        // 使用Matrix調整尺寸
        setScaleType(ScaleType.MATRIX);
        // 設定OnTouchListener
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    // 開始觸控（開始拖曳）
                    case MotionEvent.ACTION_DOWN:
                        mMode = DRAG;
                        mStartPoint.set(event.getX(), event.getY());
                        mSavedMatrix.set(mMatrix);
                        break;
                    // 手指觸控中而移動的狀態
                    // 切換拖曳中與調整尺寸的行為狀態
                    case MotionEvent.ACTION_MOVE:
                        if(mMode == DRAG) {
                            mMatrix.set(mSavedMatrix);
                            float x = event.getX() - mStartPoint.x;
                            float y = event.getY() - mStartPoint.y;
                            mMatrix.postTranslate(x, y);
                        } else if (mMode == ZOOM) {
                            float newDist = culcDistance(event);
                            float scale = newDist / mOldDistance;
                            mMatrix.set(mSavedMatrix);
                            mMatrix.postScale(scale, scale,
                                    mMidPoint.x, mMidPoint.y);
                        }
                        break;
                    // 觸控結束
                    case MotionEvent.ACTION_UP:
                        mMode = NONE;
                        break;
                    // 多點觸控開始（開始調整尺寸）
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mMode = ZOOM;
                        mOldDistance = culcDistance(event);
                        culcMidPoint(mMidPoint, event);
                        break;
                    // 多點觸控結束（結束調整尺寸）
                    case MotionEvent.ACTION_POINTER_UP:
                        mMode = NONE;
                        break;
                    default:
                        break;
                }
                // 以postTranslate方法改變座標
                // 並以postScale方法改變尺寸後
                // 將變化的Matrix反映給Image
                setImageMatrix(mMatrix);
                //縮放設定
                Scale();
                return true;
            }
        });
    }
    // 計算多點觸控2點之間的距離並傳回
    private float culcDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }
    // 計算多點觸控2點之間的中間座標並設定給midPoint
    private void culcMidPoint(PointF midPoint, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        midPoint.set(x / 2, y / 2);
    }
}
