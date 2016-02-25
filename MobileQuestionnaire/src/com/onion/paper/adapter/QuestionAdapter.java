package com.onion.paper.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.onion.paper.R;


public class QuestionAdapter extends BaseAdapter {

	private Context mContext;
	private ViewHolder mViewHolder;
	private List<String> mQuestions;
	
	public void setQuestions(List<String> questions) {
		this.mQuestions = questions;
	}

	public QuestionAdapter(Context context) {
		this.mContext = context;
	}
	
	@Override
	public int getCount() {
		return mQuestions == null ? 0 : mQuestions.size();
	}

	@Override
	public Object getItem(int position) {
		if (mQuestions != null) {
			return mQuestions.get(position);
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_add_question, parent, false);
			mViewHolder = new ViewHolder();
			mViewHolder.tv_new_question = (TextView) convertView.findViewById(R.id.tv_new_question);
			mViewHolder.tv_del_question = (TextView) convertView.findViewById(R.id.tv_del_question);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		
		final String question = mQuestions.get(position);
		mViewHolder.tv_new_question.setText(question);
		// 点击删除
		mViewHolder.tv_del_question.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (onDelClickListener != null) {
					onDelClickListener.onDelClick(question);
				}
			}
		});
		
		return convertView;
	}
	

	private class ViewHolder {
		TextView tv_new_question;
		TextView tv_del_question;
	}
	
	private OnDelClickListener onDelClickListener;
	
	public void setOnDelClickListener(OnDelClickListener onDelClickListener) {
		this.onDelClickListener = onDelClickListener;
	}

	public interface OnDelClickListener {
		void onDelClick(String question);
	}
}
