package com.onion.paper.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onion.paper.R;
import com.onion.paper.model.ExcelFile;
import com.onion.paper.view.SwipeView;
import com.onion.paper.view.SwipeView.OnSwipeStatusChangeListener;
import com.onion.paper.view.SwipeView.SwipeStatus;

public class SwipeAdapter extends BaseAdapter {

	private ViewHolder holder;
	private Context mContext;
	private List<ExcelFile> mExcelFiles;

	private List<SwipeView> unClosedSwipeViews;
	private OnSwipeStatusChangeListener mOnSwipeStatusChangeListener;

	public void setPapers(List<ExcelFile> excelFiles) {
		this.mExcelFiles = excelFiles;
	}

	public SwipeAdapter(Context context, List<SwipeView> views) {
		this.mContext = context;
		this.unClosedSwipeViews = views;

		// 滑动监听
		mOnSwipeStatusChangeListener = new OnSwipeStatusChangeListener() {

			@Override
			public void onOpen(SwipeView openSwipeView) {

				for (int i = 0; i < unClosedSwipeViews.size(); i++) {
					SwipeView sv = unClosedSwipeViews.get(i);
					if (sv != openSwipeView)
						sv.close();
				}

				if (!unClosedSwipeViews.contains(openSwipeView))
					unClosedSwipeViews.add(openSwipeView);
			}

			@Override
			public void onClose(SwipeView closeSwipeView) {
				unClosedSwipeViews.remove(closeSwipeView);
			}

			@Override
			public void onSwiping(SwipeView swipingSwipeView) {
				if (!unClosedSwipeViews.contains(swipingSwipeView)) {
					closeAllSwipeViews();
				}
			}
		};
	}

	@Override
	public int getCount() {
		return mExcelFiles.size();
	}

	@Override
	public Object getItem(int position) {
		return mExcelFiles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.swipe_view_list_cell, parent, false);
			holder = new ViewHolder();
			holder.rl_delete = (RelativeLayout) convertView.findViewById(R.id.rl_delete);
			holder.rl_share = (RelativeLayout) convertView.findViewById(R.id.rl_share);
			holder.ll_content = (LinearLayout) convertView.findViewById(R.id.ll_content);
			holder.tv_excel_name = (TextView) convertView.findViewById(R.id.tv_excel_name);
			holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_create_time);
			holder.swipeView = (SwipeView) convertView.findViewById(R.id.swipeView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final ExcelFile excelFile = mExcelFiles.get(position);

		holder.swipeView.fastClose();
		holder.tv_excel_name.setText(excelFile.getExcelName());
		holder.tv_create_time.setText(excelFile.getCreateTime());
		
		// 删除某一项
		holder.rl_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 去数据库删除相应的问卷再更新UI
				if (onExcelFileDeleteListener != null) {
					onExcelFileDeleteListener.onExcelFileDelete(excelFile);
				}
			}
		});
		
		// 分享某一项
		holder.rl_share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 分享Excel
				if (onExcelShareListener != null) {
					onExcelShareListener.onExcelShare(excelFile);
				}
			}
		});

		holder.swipeView.setOnSwipeStatusChangeListener(mOnSwipeStatusChangeListener);

		holder.ll_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (unClosedSwipeViews.size() > 0) {
					// 关闭打开的选项
					closeAllSwipeViews();
				} else if (onExcelFileClickListener != null) {
					onExcelFileClickListener.onExcelClick(excelFile);
				}
			}

		});
		return convertView;
	}

	private class ViewHolder {
		SwipeView swipeView;
		TextView tv_excel_name;
		TextView tv_create_time;
		LinearLayout ll_content;
		RelativeLayout rl_delete;
		RelativeLayout rl_share;
	}
	
	/**
	 * 分享Excel
	 * @author 詹子聪
	 *
	 */
	public interface OnExcelShareListener {
		void onExcelShare(ExcelFile excelFile);
	}
	private OnExcelShareListener onExcelShareListener;
	public void setOnExcelShareListener(OnExcelShareListener onExcelShareListener) {
		this.onExcelShareListener = onExcelShareListener;
	}

	/**
	 * 点击某一个Excel文件
	 * @author Jason
	 *
	 */
	public interface OnExcelFileClickListener {
		void onExcelClick(ExcelFile excelFile);
	}
	private OnExcelFileClickListener onExcelFileClickListener;
	public void setOnExcelFileClickListener(OnExcelFileClickListener onExcelFileClickListener) {
		this.onExcelFileClickListener = onExcelFileClickListener;
	}
	
	/**
	 * 监听删除某一个Excel文件的动作
	 * @author Jason
	 *
	 */
	public interface OnExcelFileDeleteListener {
		void onExcelFileDelete(ExcelFile excelFile);
	}
	private OnExcelFileDeleteListener onExcelFileDeleteListener;
	public void setOnExcelFileDeleteListener(OnExcelFileDeleteListener onExcelFileDeleteListener) {
		this.onExcelFileDeleteListener = onExcelFileDeleteListener;
	}

	/**
	 * 关闭所有SwipeView
	 */
	public void closeAllSwipeViews() {
		for (int i = 0; i < unClosedSwipeViews.size(); i++) {
			SwipeView sv = unClosedSwipeViews.get(i);
			if (sv.getCurrentSwipeStatus() != SwipeStatus.Close)
				sv.close();
		}
	}
}