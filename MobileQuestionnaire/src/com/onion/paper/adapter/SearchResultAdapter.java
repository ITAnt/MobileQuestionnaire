package com.onion.paper.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onion.paper.R;
import com.onion.paper.model.web.User;

public class SearchResultAdapter extends BaseAdapter {

	private Context mContext;
	private ViewHolder mViewHolder;
	private List<User> mMonitors;
	
	public void setMonitorNames(List<User> names) {
		this.mMonitors = names;
	}

	public SearchResultAdapter(Context context) {
		this.mContext = context;
	}
	
	@Override
	public int getCount() {
		return mMonitors == null ? 0 : mMonitors.size();
	}

	@Override
	public Object getItem(int position) {
		if (mMonitors != null) {
			return mMonitors.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_result, parent, false);
			mViewHolder = new ViewHolder();
			mViewHolder.tv_monitor_name = (TextView) convertView.findViewById(R.id.tv_monitor_name);
			mViewHolder.rl_favorite = (RelativeLayout) convertView.findViewById(R.id.rl_favorite);
			mViewHolder.iv_favorite = (ImageView) convertView.findViewById(R.id.iv_favorite);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		
		final User monitor = mMonitors.get(position);
		mViewHolder.tv_monitor_name.setText(monitor.getUsername());
		if (monitor.getHasBeenFavorited()) {
			mViewHolder.iv_favorite.setImageResource(R.drawable.right);
		} else {
			mViewHolder.iv_favorite.setImageResource(R.drawable.add);
		}
		
		
		mViewHolder.rl_favorite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mOnFavoriteClickListener != null) {
					mOnFavoriteClickListener.onFavoriteClick(monitor.getObjectId());
				}
			}
		});
		return convertView;
	}

	private class ViewHolder {
		TextView tv_monitor_name;
		ImageView iv_favorite;
		RelativeLayout rl_favorite;
	}
	
	/**
	 * 点击关注
	 * @author 詹子聪
	 *
	 */
	public interface OnFavoriteClickListener {
		void onFavoriteClick(String objectId);
	}
	
	private OnFavoriteClickListener mOnFavoriteClickListener;

	public void setOnFavoriteClickListener(OnFavoriteClickListener onFavoriteClickListener) {
		this.mOnFavoriteClickListener = onFavoriteClickListener;
	}
	
}
