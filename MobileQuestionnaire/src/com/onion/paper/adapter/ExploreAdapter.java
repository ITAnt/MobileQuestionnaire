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
import com.onion.paper.model.ExploreItem;

public class ExploreAdapter extends BaseAdapter {

	private Context mContext;
	private ViewHolder mViewHolder;
	private List<ExploreItem> exploreItems;
	
	public void setExploreItems(List<ExploreItem> exploreItems) {
		this.exploreItems = exploreItems;
	}

	public ExploreAdapter(Context context) {
		this.mContext = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return exploreItems == null ? 0 : exploreItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return exploreItems == null ? null : exploreItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			mViewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_explore, parent, false);
			mViewHolder.rl_explore_item = (RelativeLayout) convertView.findViewById(R.id.rl_explore_item);
			mViewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
			mViewHolder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		
		final ExploreItem exploreItem = exploreItems.get(position);
		
		mViewHolder.iv_icon.setBackgroundResource(exploreItem.getIconResourceId());
		mViewHolder.tv_text.setText(exploreItem.getText());
		mViewHolder.rl_explore_item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 点击某一项
				if (onFolderClickListener != null) {
					onFolderClickListener.onFolderClick(exploreItem.getId());
				}
			}
		});
		
		return convertView;
	}

	
	private class ViewHolder {
		ImageView iv_icon;
		TextView tv_text;
		RelativeLayout rl_explore_item;
	}
	
	public interface OnFolderClickListener {
		void onFolderClick(int id);
	}
	
	private OnFolderClickListener onFolderClickListener;

	public void setOnFolderClickListener(OnFolderClickListener onFolderClickListener) {
		this.onFolderClickListener = onFolderClickListener;
	}
}
