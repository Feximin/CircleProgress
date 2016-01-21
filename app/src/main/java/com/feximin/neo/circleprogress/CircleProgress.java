package com.feximin.neo.circleprogress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Neo on 16/1/20.
 */
public class CircleProgress extends View {
    private int mOuterBorderWidth;
    private int mOuterBorderColor = 0xFF1195DB;
    private int mBorderGap = 2;//dp
    private int mProgressColor = 0xFFEA9518;
    private int mProgressWidth = 3;  //dp
    private int mProgressBackdropColor;
    private int mMax;
    private int mCurProgress;
    private int mTextSize = 12; //sp
    private int mTextColor = 0xFF666666;
    private int mBackdropColor;
    private int mImageResId;
    private int mMode = MODE_TEXT;          //默认显示百分比
    private Paint mPaint;
    private boolean mIsClockWise;        //默认是顺时针
    private float mTextHeight;
    private float mStartAngle = -90;
    public CircleProgress(Context context) {
        super(context);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CircleProgress);
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        float density = metrics.density;
        float scaleDensity = metrics.scaledDensity;
        mProgressWidth *= density;
        mProgressWidth = ta.getDimensionPixelSize(R.styleable.CircleProgress_progress_width, mProgressWidth);
        mProgressColor = ta.getColor(R.styleable.CircleProgress_progress_color, mProgressColor);
        mBorderGap *= density;
        mBorderGap = ta.getDimensionPixelSize(R.styleable.CircleProgress_border_gap, mBorderGap);
        mOuterBorderWidth = ta.getDimensionPixelSize(R.styleable.CircleProgress_outer_border_width, 0);
        if(mOuterBorderWidth > 0)  mOuterBorderColor = ta.getColor(R.styleable.CircleProgress_outer_border_color, mOuterBorderColor);
        mProgressBackdropColor = ta.getColor(R.styleable.CircleProgress_progress_backdrop, 0);
        mStartAngle = ta.getFloat(R.styleable.CircleProgress_start_angle, mStartAngle);
        mMax = ta.getInt(R.styleable.CircleProgress_android_max, 100);
        mIsClockWise = ta.getBoolean(R.styleable.CircleProgress_clockwise, true);
        mCurProgress = ta.getInt(R.styleable.CircleProgress_android_progress, 0);
        mTextSize *= scaleDensity;
        mTextSize = ta.getDimensionPixelSize(R.styleable.CircleProgress_android_textSize, mTextSize);
        mTextColor = ta.getColor(R.styleable.CircleProgress_android_textColor, mTextColor);
        mBackdropColor = ta.getColor(R.styleable.CircleProgress_backdrop, mBackdropColor);
        mMode = ta.getInt(R.styleable.CircleProgress_mode, MODE_TEXT);
        mImageResId = ta.getResourceId(R.styleable.CircleProgress_android_src, -1);

        ta.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
        mPaint.setTypeface(Typeface.DEFAULT);
        onTextSizeChange();
    }

    private RectF oval = new RectF();
    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        if(width == 0 || width != height) throw new IllegalArgumentException("width and height should be equal with each other");
        int center = width / 2;
        mPaint.setColor(mBackdropColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(center, center, center, mPaint);
        if(mOuterBorderWidth >0){
            int radius = center - mOuterBorderWidth / 2;
            mPaint.setStrokeWidth(mOuterBorderWidth);
            mPaint.setColor(mOuterBorderColor);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(center, center, radius, mPaint);
        }

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mProgressWidth);
        oval.left = mOuterBorderWidth + mProgressWidth / 2 + (mOuterBorderWidth >0 ?mBorderGap:0);
        oval.top = oval.left;
        oval.right = width - oval.left;
        oval.bottom = oval.right;
        if(mProgressBackdropColor != 0){
            mPaint.setColor(mProgressBackdropColor);
            canvas.drawArc(oval, 0, 360, false, mPaint);
        }
        mPaint.setColor(mProgressColor);
        float swipeAngle = 360 * mCurProgress / mMax;
        if(!mIsClockWise) swipeAngle = -swipeAngle;
        canvas.drawArc(oval, mStartAngle, swipeAngle, false, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        if(mMode == MODE_TEXT) {
            int percent = mCurProgress * 100 / mMax;

            String text = String.format("%s%%", percent);
            mPaint.setColor(mTextColor);
            float textW = mPaint.measureText(text);
            float x = center - textW / 2;
            float y = center + mTextHeight / 2;
            canvas.drawText(text, x, y, mPaint);
        }else if(mMode == MODE_IMAGE){
            if(mImageResId < 0) return;
            Bitmap bitmap = getBitmap(mImageResId);
            int x = center - bitmap.getWidth() / 2;
            int y = center - bitmap.getHeight() / 2;
            canvas.drawBitmap(bitmap, x, y, mPaint);
        }
    }

    private void onTextSizeChange(){
        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        mTextHeight = metrics.descent - metrics.ascent;
        mTextHeight *= 0.9f;            //稍微有一点偏移
    }


    public void setTextSize(int pixel){
        if(pixel == mTextSize) return;
        mTextSize = pixel;
        onTextSizeChange();
        invalidate();
    }



    public void setMax(int max){
        if(max < 0) return;
        if(mMax == max) return;
        mMax = max;
    }

    public void setTextColor(int color){
        if(color == mTextColor) return;
        mTextColor = color;
        invalidate();
    }

    public void setProgress(int progress){
        if(progress == mCurProgress) return;
        mCurProgress = progress;
        invalidate();
    }


    public void setImageRes(int imgResId){
        if(mMode != MODE_IMAGE) return;
        if(imgResId == mImageResId) return;
        mImageResId = imgResId;
        invalidate();
    }

    private SparseArray<Bitmap> mBitmapCache = new SparseArray<>(2);
    private Bitmap getBitmap(int imgResId){
        Bitmap bitmap = mBitmapCache.get(imgResId);
        if(bitmap == null){
            bitmap = BitmapFactory.decodeResource(getResources(), imgResId);
            mBitmapCache.put(imgResId, bitmap);
        }
        return bitmap;
    }

    public static final int MODE_NONE = 0;
    public static final int MODE_TEXT = 1;
    public static final int MODE_IMAGE = 2;

    @IntDef({MODE_NONE, MODE_TEXT, MODE_IMAGE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode{}

    public void setMode(@Mode int mode){
        mMode = mode;
    }
}
