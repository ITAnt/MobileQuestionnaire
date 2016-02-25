package com.onion.paper.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onion.paper.R;
import com.onion.paper.model.web.User;

public class FansAdapter extends BaseAdapter {

	private Context mContext;
	private ViewHolder mViewHolder;
	private List<User> mMembers;
	
	public void setMembers(List<User> members) {
		this.mMembers = members;
	}

	public FansAdapter(Context context) {
		this.mContext = context;
	}
	
	@Override
	public int getCount() {
		return mMembers == null ? 0 : mMembers.size();
	}

	@Override
	public Object getItem(int position) {
		if (mMembers != null) {
			return mMembers.get(position);
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fans, parent, false);
			mViewHolder = new ViewHolder();
			mViewHolder.ll_member_item = (LinearLayout) convertView.findViewById(R.id.ll_member_item);
			mViewHolder.tv_member_name = (TextView) convertView.findViewById(R.id.tv_member_name);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		
		User member = mMembers.get(position);
		mViewHolder.tv_member_name.setText(member.getUsername());
		
		mViewHolder.ll_member_item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mOnMemberClickListener != null) {
					mOnMemberClickListener.onMemberClick();
				}
			}
		});
		return convertView;
	}

	private class ViewHolder {
		LinearLayout ll_member_item;
		TextView tv_member_name;
	}
	
	public interface OnMemberClickListener {
		void onMemberClick();
	}
	
	private OnMemberClickListener mOnMemberClickListener;

	public void setOnMemberClickListener(OnMemberClickListener mMemberClickListener) {
		this.mOnMemberClickListener = mMemberClickListener;
	}
	
}
