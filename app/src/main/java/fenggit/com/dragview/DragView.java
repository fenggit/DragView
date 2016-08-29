package fenggit.com.dragview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;

/**
 * Author: river
 * Date: 2016/8/23 11:59
 * Description: 拖动组件
 * <p/>
 * getLeft：相对父布局
 * getRawX：相对于屏幕
 * getX：相对于自身View
 */
public class DragView extends ImageView implements View.OnTouchListener {
    private String TAG = DragView.class.getSimpleName();
    /**
     * 记录View与X轴的距离
     */
    private int mLastX;
    /**
     * 记录View与Y轴的距离
     */
    private int mLastY;
    /**
     * 屏幕宽度
     */
    private int mScreenWidth;
    /**
     * 屏幕高度
     */
    private int mScreenHeight;
    /**
     * 状态栏高度
     */
    private final static int STATUS_HEIGHT = 48;
    /**
     * View的距离
     */
    private int left, top, right, bottom;
    /**
     * 是否移动标记
     */
    private boolean isMove = false;

    private int mTouchSlop = 0;

    float mTouchX, mTouchY;

    public DragView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setClickable(true);
        setOnTouchListener(this);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels - STATUS_HEIGHT;

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Log.i(TAG, "mTouchSlop=" + mTouchSlop);
//        Log.i(TAG, "w : h " + mScreenWidth + " : " + mScreenHeight);
    }

    int firstX;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isMove = false;

                // 相对于屏幕的坐标点
                mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();

                // 相对于自身组件的坐标点
                mTouchX = event.getX();
                mTouchY = event.getY();

                break;

            case MotionEvent.ACTION_MOVE:
//                Log.d(TAG, "x  = " + (event.getX() - mTouchX) + "   Y = " + Math.abs(event.getY() - mTouchY));
                // 滑动的最小距离
                if ((Math.abs(event.getX() - mTouchX) > mTouchSlop) || (Math.abs(event.getY() - mTouchY) > mTouchSlop)) {
//                    Log.d(TAG, "move");
                    isMove = true;

                    // 滑动距离
                    int dx = (int) (event.getRawX() - mLastX);
                    int dy = (int) (event.getRawY() - mLastY);

                    left = v.getLeft() + dx;
                    right = v.getRight() + dx;
                    top = v.getTop() + dy;
                    bottom = v.getBottom() + dy;

                    // 下面判断移动是否超出屏幕
                    if (left < 0) {
                        left = 0;
                        right = left + v.getWidth();
                    }

                    if (right > mScreenWidth) {
                        right = mScreenWidth;
                        left = right - v.getWidth();
                    }

                    if (top < 0) {
                        top = 0;
                        bottom = top + v.getHeight();
                    }

                    if (bottom > mScreenHeight) {
                        bottom = mScreenHeight;
                        top = bottom - v.getHeight();
                    }

//                Log.i(TAG, "left= " + left + ";top= " + top + ";right= " + right + ";bottom= " + bottom);

                    v.layout(left, top, right, bottom);

                    mLastX = (int) event.getRawX();
                    mLastY = (int) event.getRawY();

                    return true;
                }

                break;

            case MotionEvent.ACTION_UP:
                if (isMove) {
                    Log.d(TAG, "左右吸附");
                    isMove = false;
                    // 左右吸附
                    if (left > mScreenWidth / 2) {
                        right = mScreenWidth;
                        left = right - v.getWidth();
                    } else {
                        right = v.getWidth();
                        left = 0;
                    }

                    // 上下吸附
//                if (top > mScreenHeight / 2) {
//                    top = mScreenHeight - v.getHeight();
//                    bottom = mScreenHeight;
//                } else {
//                    top = 0;
//                    bottom = v.getHeight();
//                }

                    v.layout(left, top, right, bottom);

                    return true;
                } else {
//                    Log.d(TAG, "click");
                }
                mTouchX = mTouchY = 0;
                break;
        }

        return false;
    }

    /**
     * 获取到状态栏的高度
     *
     * @return
     */
    private int getStatusHeight() {
        Rect frame = new Rect();
        getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }
}
