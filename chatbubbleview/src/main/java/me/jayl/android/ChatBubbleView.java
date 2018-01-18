package me.jayl.android;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Chat bubble view
 *
 * Created by J on 2018/1/15.
 */

public class ChatBubbleView extends RelativeLayout {
    public static final int ARROW_HEIGHT_DEFAULT = 14;
    public static final int ARROW_POSITION_DEFAULT = 50;

    private View mViewTop;
    private View mViewArrow;
    private View mViewBottom;

    // backgroud drawables
    private int mResTop;
    private int mResArrow;
    private int mResBottom;

    private int mResTopPressed;
    private int mResArrowPressed;
    private int mResBottomPressed;

    // arrow position
    private int mArrowPosition;
    // arrow height
    private int mArrowHeight;

    public ChatBubbleView(Context context) {
        super(context);
        init();
    }

    public ChatBubbleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        init();
    }

    public ChatBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        init();
    }

    public void setBubbleResource(int topRes, int arrowRes, int bottomRes) {
        mResTop = topRes;
        mResArrow = arrowRes;
        mResBottom = bottomRes;

        if (!bPressed) {
            invalidateBubbleResource(mResTop, mResArrow, mResBottom);
        }
    }

    private boolean invalidateBubbleResource(int resRop, int resArrow, int resBottom) {
        boolean changed = false;
        if (invalidateViewRes(mViewTop, resRop)) {
            changed = true;
        }

        if (invalidateViewRes(mViewArrow, resArrow)) {
            changed = true;
        }

        if (invalidateViewRes(mViewBottom, resBottom)) {
            changed = true;
        }

        return changed;
    }

    public void setArrowHeight(int height) {
        if (mArrowHeight != height) {
            mArrowHeight = height;
            layoutBubbleViews(mHorizontalSpaceTotal, mVerticalSpaceTotal);
        }
    }

    public void setArrowPosition(int position) {
        if (mArrowPosition != position) {
            mArrowPosition = position;
            layoutBubbleViews(mHorizontalSpaceTotal, mVerticalSpaceTotal);
        }
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ChatBubbleView);
        mResTop = ta.getResourceId(R.styleable.ChatBubbleView_cbv_top_src, 0);
        mResArrow = ta.getResourceId(R.styleable.ChatBubbleView_cbv_arrow_src, 0);
        mResBottom = ta.getResourceId(R.styleable.ChatBubbleView_cbv_bottom_src, 0);
        mResTopPressed = ta.getResourceId(R.styleable.ChatBubbleView_cbv_top_src_pressed, 0);
        mResArrowPressed = ta.getResourceId(R.styleable.ChatBubbleView_cbv_arrow_src_pressed, 0);
        mResBottomPressed = ta.getResourceId(R.styleable.ChatBubbleView_cbv_bottom_src_pressed, 0);
        mArrowHeight = ta.getDimensionPixelSize(R.styleable.ChatBubbleView_cbv_arrow_height, ARROW_HEIGHT_DEFAULT);
        mArrowPosition = ta.getDimensionPixelSize(R.styleable.ChatBubbleView_cbv_arrow_position, ARROW_POSITION_DEFAULT);
        ta.recycle();

        // invalidate preserve view heights
        //invalidatePreservedHeights();
    }

    /*private void invalidatePreservedHeights() {
        if (0 != mResTop) {
            mPreservedTopHeight = getResources().getDrawable(mResTop).getIntrinsicHeight();
        }

        if (0 != mResArrow) {
            mPreservedArrowHeight = getResources().getDrawable(mResArrow).getIntrinsicHeight();
        }

        if (0 != mResBottom) {
            mPreservedBottomHeight = getResources().getDrawable(mResBottom).getIntrinsicHeight();
        }
    }*/

    private void init() {
        setClipToPadding(false);
        // init Views
        mViewTop = new BubbleContainerView(getContext());
        mViewArrow = new BubbleContainerView(getContext());
        mViewBottom = new BubbleContainerView(getContext());

        // init view attrs
        //invalidateBubbleViews();
        // init res
        invalidateViewRes(mViewTop, mResTop);
        invalidateViewRes(mViewArrow, mResArrow);
        invalidateViewRes(mViewBottom, mResBottom);

        addView(mViewTop);
        addView(mViewArrow);
        addView(mViewBottom);
    }

    private boolean invalidateViewRes(View target, int resId) {
        target.setBackgroundResource(resId);

        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mHorizontalSpaceTotal = r - l;
        mVerticalSpaceTotal = b - t;
        layoutBubbleViews(mHorizontalSpaceTotal, mVerticalSpaceTotal);
    }

    private void layoutBubbleViews(int widthTotal, int heightTotal) {
        int line1 = mArrowPosition - mArrowHeight / 2;
        int line2 = mArrowPosition + mArrowHeight / 2;

        mViewTop.layout(0, 0, widthTotal, line1);
        mViewArrow.layout(0, line1, widthTotal, line2);
        mViewBottom.layout(0, line2, widthTotal, heightTotal);
    }

    private boolean bPressed;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                log("down");
                bPressed = true;
                updateStateRes();
                return true;

            case MotionEvent.ACTION_UP:
                log("up");
                bPressed = false;
                updateStateRes();
                judgeClick(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_MOVE:
                //log("move");
                return bPressed;

            case MotionEvent.ACTION_CANCEL:
                bPressed = false;
                updateStateRes();
                return true;

            default:
                return false;
        }
    }

    private void updateStateRes() {
        if (bPressed) {
            invalidateBubbleResource(mResTopPressed, mResArrowPressed, mResBottomPressed);
        } else {
            invalidateBubbleResource(mResTop, mResArrow, mResBottom);
        }
    }

    private int mHorizontalSpaceTotal;
    private int mVerticalSpaceTotal;
    private void judgeClick(float upX, float upY) {
        if (upX >= 0 && upX <= mHorizontalSpaceTotal
                && upY >= 0 && upY <= mVerticalSpaceTotal) {
            this.callOnClick();
        }
    }

    private static final boolean DEBUG = true;
    private void log(String msg) {
        if (DEBUG) {
            Log.i("ChatBubbleView", msg);
        }
    }

    private static class BubbleContainerView extends View {

        private LayoutParams mFakeParam = new LayoutParams(0, 0);

        public BubbleContainerView(Context context) {
            super(context);
        }

        @Override
        public LayoutParams getLayoutParams() {
            return mFakeParam;
        }
    }
}
