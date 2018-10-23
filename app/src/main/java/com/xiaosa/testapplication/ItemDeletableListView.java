package com.xiaosa.testapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * @author hexiaosa
 * @date 2018/10/19
 */

public class ItemDeletableListView extends ListView {

    private TextView mTvDelete; // 删除按钮
    private PopupWindow mPwDelete;
    private OnDeleteItemListener mDeleteItemListener;

    private float downX;
    private float downY;
    private float x;
    private float y;

    private int touchSlop;

    private int downPosition;

    private boolean isSliding = false;

    private boolean isPopupShow = false;

    private boolean handleTouchEvent = false; // 是否需要处理触摸事件(是否设置删除监听)

    public ItemDeletableListView(Context context) {
        this(context, null);
    }

    public ItemDeletableListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemDeletableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = View.inflate(context, R.layout.layout_delete, null);
        mTvDelete = view.findViewById(R.id.tv_delete);
        mPwDelete = new PopupWindow(view, Dp2PxUtil.dip2px(context, 80), Dp2PxUtil.dip2px(context, 40));

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.e("ItemDeletableListView", "dispatchTouchEvent action : " + ev.getAction() + ", handleTouchEvent:" + handleTouchEvent + ", isPopupShow:" + isPopupShow);
        Log.e("ItemDeletableListView", "dispatchTouchEvent mPwDelete.isShowing:" + mPwDelete.isShowing());
        if (!handleTouchEvent) {
            // 如果不需要处理触摸事件，那么久不做处理
            return super.dispatchTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isPopupShow) {
                    // 已经展示了 popup 的话，再次触发 down 事件，则消失，并不再分发事件
                    mPwDelete.dismiss();
                    isPopupShow = false;
                    return false;
                }
                downX = ev.getX();
                downY = ev.getY();
                downPosition = pointToPosition(((int) downX), ((int) downY));
                break;
            case MotionEvent.ACTION_MOVE:
                if (isPopupShow) {
                    // 如果 move 过程中展示了 popupwindow, 那么不再分发事件
                    return false;
                }
                x = ev.getX();
                y = ev.getY();
                if (downX-x > touchSlop && downX-x > Math.abs(downY-y)) {
                    isSliding = true;
                } else {
                    isSliding = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                isSliding = false;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.e("ItemDeletableListView", "onTouchEvent action : " + ev.getAction() + ", isSliding:" + isSliding + ", downPosition:" + downPosition);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (isSliding) {
                    View child = getChildAt(downPosition);
                    if (child == null) {
                        return super.onTouchEvent(ev);
                    }
                    isPopupShow = true;
                    int x = getWidth()/2+mPwDelete.getWidth()/2;
                    int y = child.getTop()+child.getHeight()/2;
                    Log.e("TAG", "popup x:" + x + ", y:" + y);
                    mPwDelete.showAtLocation(child, Gravity.TOP | Gravity.LEFT, x, y);
                    mPwDelete.update();
                    mTvDelete.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDeleteItemListener.deleteItem(downPosition);
                            mPwDelete.dismiss();
                            isPopupShow = false;
                        }
                    });
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setOnItemDeleteListener(OnDeleteItemListener listener) {
        handleTouchEvent = true;
        this.mDeleteItemListener = listener;
    }

    public interface OnDeleteItemListener {
        void deleteItem(int position);
    }
}
