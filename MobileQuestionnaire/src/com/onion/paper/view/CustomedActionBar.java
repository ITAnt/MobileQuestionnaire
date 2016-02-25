package com.onion.paper.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onion.paper.R;

public class CustomedActionBar extends RelativeLayout {

	private int INVALID_ID = -1;
	private RelativeLayout rl_left;
	private ImageView iv_left;
	private TextView tv_title;
	private RelativeLayout rl_right;
	private ImageView iv_right;
	private TextView tv_right;
	
	
	public CustomedActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater.from(context).inflate(R.layout.view_actionbar, this);
		rl_left = (RelativeLayout) findViewById(R.id.rl_left);
		iv_left = (ImageView) findViewById(R.id.iv_left);
		tv_title = (TextView) findViewById(R.id.tv_title);
		rl_right = (RelativeLayout) findViewById(R.id.rl_right);
		iv_right = (ImageView) findViewById(R.id.iv_right);
		tv_right = (TextView) findViewById(R.id.tv_right);
		
		rl_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 点击左侧图标
				if (onLeftIconClickListener != null) {
					onLeftIconClickListener.onLeftIconClick();
				}
			}
		});
		
		rl_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 点击右侧图标
				if (onRightIconClickListener != null) {
					onRightIconClickListener.onRightIconClick();
				}
			}
		});
		
		tv_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 点击右侧文字
				if (onRightTextClickListener != null) {
					onRightTextClickListener.onRightTextClick();
				}
			}
		});
		
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomedActionBar);
		
		// 右侧图标
		int rightIconId = typedArray.getResourceId(R.styleable.CustomedActionBar_rightIcon, INVALID_ID);
		if (rightIconId != INVALID_ID) {
			tv_right.setVisibility(View.GONE);
			iv_right.setBackgroundResource(rightIconId);
		}
		
		// 设置标题内容
		String title = typedArray.getString(R.styleable.CustomedActionBar_title);
		if (!TextUtils.isEmpty(title)) {
			tv_title.setText(title);
		}
		
		// 设置右侧文字内容
		String rightText = typedArray.getString(R.styleable.CustomedActionBar_rightText);
		if (!TextUtils.isEmpty(rightText)) {
			rl_right.setVisibility(View.GONE);
			tv_right.setText(rightText);
		}
		
		typedArray.recycle();
	}
	
	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitle(String title) {
		if (!TextUtils.isEmpty(title)) {
			tv_title.setText(title);
		}
	}
	
	public interface OnLeftIconClickListener {
		void onLeftIconClick();
	}
	private OnLeftIconClickListener onLeftIconClickListener;
	public void setOnLeftIconClickListener(
			OnLeftIconClickListener onLeftIconClickListener) {
		this.onLeftIconClickListener = onLeftIconClickListener;
	}

	public interface OnRightIconClickListener {
		void onRightIconClick();
	}
	private OnRightIconClickListener onRightIconClickListener;
	public void setOnRightIconClickListener(
			OnRightIconClickListener onRightIconClickListener) {
		this.onRightIconClickListener = onRightIconClickListener;
	}
	
	public interface OnRightTextClickListener {
		void onRightTextClick();
	}
	private OnRightTextClickListener onRightTextClickListener;
	public void setOnRightTextClickListener(
			OnRightTextClickListener onRightTextClickListener) {
		this.onRightTextClickListener = onRightTextClickListener;
	}
}
