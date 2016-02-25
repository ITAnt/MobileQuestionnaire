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
import com.onion.paper.view.CircleImage;

public class GradeAdapter extends BaseAdapter {

	private Context mContext;
	private ViewHolder mViewHolder;
	private List<User> mGrades;
	
	public void setGrades(List<User> users) {
		this.mGrades = users;
	}

	public GradeAdapter(Context context) {
		this.mContext = context;
	}
	
	@Override
	public int getCount() {
		return mGrades == null ? 0 : mGrades.size();
	}

	@Override
	public Object getItem(int position) {
		if (mGrades != null) {
			return mGrades.get(position);
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grade, parent, false);
			mViewHolder = new ViewHolder();
			mViewHolder.ll_grade_item = (LinearLayout) convertView.findViewById(R.id.ll_grade_item);
			mViewHolder.ci_grade = (CircleImage) convertView.findViewById(R.id.ci_grade);
			mViewHolder.tv_grade_name = (TextView) convertView.findViewById(R.id.tv_grade_name);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		
		final User grade = mGrades.get(position);
		mViewHolder.tv_grade_name.setText(grade.getUsername());
		//mViewHolder.tv_unsubmit_number.setText(String.valueOf(grade.getUnsubmitNumber()));
		
		mViewHolder.ll_grade_item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mOnGradeClickListener != null) {
					mOnGradeClickListener.onGradeClick(grade.getObjectId());
				}
			}
		});
		return convertView;
	}

	private class ViewHolder {
		LinearLayout ll_grade_item;
		CircleImage ci_grade;
		TextView tv_grade_name;
	}
	
	public interface OnGradeClickListener {
		void onGradeClick(String objectId);
	}
	
	private OnGradeClickListener mOnGradeClickListener;

	public void setmOnGradeClickListener(OnGradeClickListener mOnGradeClickListener) {
		this.mOnGradeClickListener = mOnGradeClickListener;
	}
	
}
