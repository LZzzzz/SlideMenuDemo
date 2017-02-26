package com.lzj.slidemenudemo.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by LZJ on 2017/2/26.
 */

public class DragerView extends FrameLayout {

    private ViewDragHelper dragHelper;
    private ViewGroup mMenu;
    private ViewGroup mContent;
    private int mWidth;
    private int mHeight;
    private int mRange;
    private Status status = Status.CLOSE;

    public enum Status {
        OPEN,
        CLOSE,
        DRAG
    }

    public DragerView(Context context) {
        super(context);
        init();
    }

    public DragerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //2.敏感度，默认值为1.0f，这个值越大越敏感
        dragHelper = ViewDragHelper.create(this, 1.0f, callback);
    }

    ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mRange;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mContent) {
                left = fixLeft(left);
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == mMenu) {
                //固定菜单
                mMenu.layout(0, 0, 0 + mWidth, 0 + mHeight);
                //更新主面板位置
                int moveleft = mContent.getLeft() + dx;
                moveleft = fixLeft(moveleft);
                mContent.layout(moveleft, 0, moveleft + mWidth, 0 + mHeight);
            }
            disPatchEvent();
            //跟新界面
            invalidate();
        }


        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (xvel == 0 && mContent.getLeft() > 05f * mRange) {
                openMenu();
            } else if (xvel > 0) {
                openMenu();
            } else {
                closeMenu();
            }
        }
    };

    /**
     * 分发拖拽事件
     */
    private void disPatchEvent() {
        float precent = mContent.getLeft() * 1.0f / mRange;
        startAnim(precent);
        if (listener != null) {
            listener.Draging(precent);
        }

        Status lastStatus = status;
        status = updateStatus(precent);
        if (status != lastStatus && listener != null) {
            if (status == Status.OPEN) {
                listener.Opened();
            } else {
                listener.Closed();
            }
        }
    }

    /**
     * 更新状态
     *
     * @param precent
     * @return
     */
    private Status updateStatus(float precent) {
        if (precent == 0) {
            status = Status.CLOSE;
        } else if (precent == 1) {
            status = Status.OPEN;
        } else {
            status = Status.DRAG;
        }
        return status;
    }

    /**
     * 执行动画
     *
     * @param precent
     */
    private void startAnim(float precent) {
        ViewCompat.setTranslationX(mMenu, evaluate(precent, -mWidth * 0.5f, 0));
        Drawable drawable = getBackground();
        drawable.setColorFilter((Integer) evaluateColor(precent, Color.BLACK, Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }


    /**
     * FloatEvaluator
     * 类型估值器
     *
     * @param fraction   分度值
     * @param startValue 开始值
     * @param endValue   结束值
     * @return 中间某个时刻的值
     */
    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }

    /**
     * 颜色估值器
     *
     * @param fraction   分度值
     * @param startValue 开始颜色
     * @param endValue   结束颜色
     * @return
     */
    public Object evaluateColor(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int) ((startA + (int) (fraction * (endA - startA))) << 24) |
                (int) ((startR + (int) (fraction * (endR - startR))) << 16) |
                (int) ((startG + (int) (fraction * (endG - startG))) << 8) |
                (int) ((startB + (int) (fraction * (endB - startB))));
    }

    /**
     * 打开菜单
     */
    private void openMenu() {
        open(true);
    }

    public void open(boolean b) {
        int left = mRange;
        if (b) {
            if (dragHelper.smoothSlideViewTo(mContent, left, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            mContent.layout(left, 0, left + mWidth, mHeight);
        }
    }

    /**
     * 关闭菜单
     */
    private void closeMenu() {
        close(true);
    }

    public void close(boolean b) {
        int left = 0;
        if (b) {
            if (dragHelper.smoothSlideViewTo(mContent, left, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            mContent.layout(left, 0, left + mWidth, mHeight);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * 修正滑动距离
     *
     * @param left
     * @return
     */
    private int fixLeft(int left) {
        if (left <= 0) {
            left = 0;
        } else if (left >= mRange) {
            left = mRange;
        }
        return left;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            dragHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() < 2) {
            throw new IllegalArgumentException("Your viewgroup must contains 2 child at least.");
        } else if (!(getChildAt(0) instanceof ViewGroup) || !(getChildAt(1) instanceof ViewGroup)) {
            throw new IllegalArgumentException("Your child must be an instance of ViewGroup.");
        }
        mMenu = (ViewGroup) getChildAt(0);
        mContent = (ViewGroup) getChildAt(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mRange = (int) (mContent.getMeasuredWidth() * 0.7f);
    }

    private onDragChangedListener listener;

    public void setonDragChangedListener(onDragChangedListener listener) {
        this.listener = listener;
    }

    public interface onDragChangedListener {
        void Opened();

        void Closed();

        void Draging(float precent);
    }
}
