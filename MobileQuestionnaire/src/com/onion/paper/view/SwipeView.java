package com.onion.paper.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 因为FrameLayout已经实现了onMeasure(),只需实现onLayout()
 * 使用ViewDragHelper，它封装了对触摸位置，触摸速度，触摸距离的检测，以及Scroller,需要我们指定什么时候滑动，滑动多少
 * 
 * @author 温坤哲
 * @date 2015-7-7
 */
public class SwipeView extends FrameLayout {

	private View contentView;
	private View deleteView;

	/**
	 * 内容控件宽度
	 */
	private int contentWidth;

	/**
	 * 内容控件高度
	 */
	private int contentHeight;

	/**
	 * 删除控件的宽度
	 */
	private int deleteWidth;

	/**
	 * 删除控件的高度
	 */
	private int deleteHeight;

	private ViewDragHelper helper;

	public SwipeView(Context context) {
		super(context);
	}

	public SwipeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init();
	}

	/**
	 * 初始化方法
	 */
	private void init() {
		helper = ViewDragHelper.create(this, cb);
	}

	/**
	 * 从xml文件中加载完布局，不可以获取内部控件的宽高，只知道自己有多少个子View，而没有对其进行测量
	 * 一般可以初始化子View的引用，不可以使用findViewById(int id)
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.contentView = this.getChildAt(0);
		this.deleteView = this.getChildAt(1);
	}

	/**
	 * 测量完子View调用,只要大小高度发生改变都可以调用，在这里可以获取子View的高度
	 * getMeasuredWidth():只要在onMeasured()执行完之后，便可以通过调用此方法获取得到宽度，推荐用这个
	 * getWidth():只有在onLayout执行完之后，才可以通过调用此方法获取得到宽度
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		this.contentWidth = contentView.getMeasuredWidth();
		this.contentHeight = contentView.getMeasuredHeight();

		this.deleteWidth = deleteView.getMeasuredWidth();
		this.deleteHeight = deleteView.getMeasuredHeight();
	}

	/**
	 * 
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		contentView.layout(0, 0, contentWidth, contentHeight);
		deleteView.layout(contentWidth, 0, contentWidth + deleteWidth,
				deleteHeight);
	}

	/**
	 * 由ViewDragHelper来管理触摸事件
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return helper.shouldInterceptTouchEvent(ev);
	}

	int lastX, lastY;

	/**
	 * 将touch事件交予ViewDragHelper管理,然后必须返回true
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			int deltaX = x - lastX;
			int deltaY = y - lastY;
			if (Math.abs(deltaX) > Math.abs(deltaY)) {
				// 若左右互动距离大于上下滑动的距离，则对触摸事件进行拦截，不让其父控件响应
				this.requestDisallowInterceptTouchEvent(true);
			}
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		lastX = x;
		lastY = y;
		helper.processTouchEvent(event);
		return true;
	}

	private ViewDragHelper.Callback cb = new Callback() {

		/**
		 * 每次触摸都会调用这个方法以判断是否要响应这次触摸事件，返回true的话就响应
		 * 
		 * @param child
		 *            子控件
		 * @param id
		 *            子控件id
		 */
		@Override
		public boolean tryCaptureView(View child, int id) {
			return child == contentView || child == deleteView;
		}

		/**
		 * 当被触摸时这个方法会被回调
		 * 
		 * @param capturedChild
		 *            子控件
		 */
		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			super.onViewCaptured(capturedChild, activePointerId);
		}

		/**
		 * 获取水平方向拖拽的范围，返回0即可
		 */
		@Override
		public int getViewHorizontalDragRange(View child) {
			return deleteWidth;
		}

		/**
		 * 控制child实际上滑动多少
		 * 
		 * @param child
		 *            改变的子View
		 * @param left
		 *            应该被上滑动的距离
		 * @param dx
		 *            实际上滑动的距离 打个比方，滑动了10，dx == 10，而left就是你期望滑动的距离
		 * @return 想让child的Left等于多少
		 */
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (child == contentView) {
				if (left > 0)
					left = 0;
				if (left < -deleteWidth)
					left = -deleteWidth;
			} else if (child == deleteView) {
				int distance = contentWidth - deleteWidth;
				if (left < distance)
					left = distance;

				if (left > contentWidth)
					left = contentWidth;
			}
			return left;
		}

		/**
		 * View滑动后的回调
		 */
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			if (changedView == contentView) {
				deleteView.layout(deleteView.getLeft() + dx, 0,
						deleteView.getRight() + dx, deleteView.getBottom());
			} else if (changedView == deleteView) {
				contentView.layout(contentView.getLeft() + dx, 0,
						contentView.getRight() + dx, contentView.getBottom());
			}

			// 用contentView的left去决定它的状态
			int mleft = contentView.getLeft();
			if (mleft == 0 && mStatus != SwipeStatus.Close) {
				mStatus = SwipeStatus.Close;
				if (onSwipeStatusChangeListener != null)
					onSwipeStatusChangeListener.onClose(SwipeView.this);
			} else if (mleft == -deleteWidth && mStatus != SwipeStatus.Open) {
				mStatus = SwipeStatus.Open;
				if (onSwipeStatusChangeListener != null)
					onSwipeStatusChangeListener.onOpen(SwipeView.this);
			} else if (mStatus != SwipeStatus.Swiping) {
				mStatus = SwipeStatus.Swiping;
				if (onSwipeStatusChangeListener != null)
					onSwipeStatusChangeListener.onSwiping(SwipeView.this);
			}
		}

		/**
		 * touchUp的回调
		 */
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			if (contentView.getLeft() < -deleteWidth / 2) {
				open();
			} else {
				close();
			}
		}

	};

	/**
	 * 打开SwipeView
	 */
	public void open() {
		helper.smoothSlideViewTo(contentView, -deleteWidth, 0);
		ViewCompat.postInvalidateOnAnimation(SwipeView.this);
	}

	/**
	 * 关闭SwipeView
	 */
	public void close() {
		helper.smoothSlideViewTo(contentView, 0, 0);
		ViewCompat.postInvalidateOnAnimation(SwipeView.this);
	}

	/**
	 * 快速关闭
	 */
	public void fastClose() {
		contentView.layout(0, 0, contentWidth, contentHeight);
		deleteView.layout(contentWidth, 0, contentWidth + deleteWidth,
				deleteHeight);
		mStatus = SwipeStatus.Close;
		if (onSwipeStatusChangeListener != null)
			onSwipeStatusChangeListener.onClose(SwipeView.this);
	}

	public void computeScroll() {
		// 返回true动画没结束，返回false，动画结束
		if (helper.continueSettling(true)) {
			// 动画没结束，则继续刷新
			ViewCompat.postInvalidateOnAnimation(SwipeView.this);
		}
	}

	private SwipeStatus mStatus;

	/**
	 * 返回现在的状态
	 * 
	 * @return
	 */
	public SwipeStatus getCurrentSwipeStatus() {
		return mStatus;
	}

	/**
	 * SwipeView的状态
	 * 
	 * @author 温坤哲
	 * @date 2015-7-8
	 */
	public enum SwipeStatus {
		/**
		 * 打开
		 */
		Open,
		/**
		 * 关闭
		 */
		Close,
		/**
		 * 滑动中
		 */
		Swiping;
	}

	public interface OnSwipeStatusChangeListener {
		/**
		 * 状态变成打开状态时回调此方法
		 * 
		 * @param openSwipeView
		 *            作用的SwipeView
		 */
		void onOpen(SwipeView openSwipeView);

		/**
		 * 状态变成关闭时回调此方法
		 * 
		 * @param closeSwipeView
		 *            作用的SwipeView
		 */
		void onClose(SwipeView closeSwipeView);

		/**
		 * 状态变成滑动时回调此方法
		 * 
		 * @param closeSwipeView
		 *            作用的SwipeView
		 */
		void onSwiping(SwipeView swipingSwipeView);
	}

	private OnSwipeStatusChangeListener onSwipeStatusChangeListener;

	public OnSwipeStatusChangeListener getOnSwipeStatusChangeListener() {
		return onSwipeStatusChangeListener;
	}

	public void setOnSwipeStatusChangeListener(
			OnSwipeStatusChangeListener onSwipeStatusChangeListener) {
		this.onSwipeStatusChangeListener = onSwipeStatusChangeListener;
	}

}
