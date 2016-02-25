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
import com.onion.paper.model.web.Paper;

public class QuestionnaireAdapter extends BaseAdapter {

	private Context mContext;
	private ViewHolder mViewHolder;
	private List<Paper> mQuestionnaires;
	
	public void setQuestionnaires(List<Paper> questionnaires) {
		this.mQuestionnaires = questionnaires;
	}

	public QuestionnaireAdapter(Context context) {
		this.mContext = context;
	}
	
	@Override
	public int getCount() {
		return mQuestionnaires == null ? 0 : mQuestionnaires.size();
	}

	@Override
	public Object getItem(int position) {
		if (mQuestionnaires != null) {
			return mQuestionnaires.get(position);
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_student_questionnaire, parent, false);
			mViewHolder = new ViewHolder();
			mViewHolder.ll_questionnaire_item = (LinearLayout) convertView.findViewById(R.id.ll_member_item);
			mViewHolder.tv_questionnaire_name = (TextView) convertView.findViewById(R.id.tv_member_name);
			mViewHolder.tv_questionnaire_status = (TextView) convertView.findViewById(R.id.tv_member_status);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		
		final Paper paper = mQuestionnaires.get(position);
		mViewHolder.tv_questionnaire_name.setText(paper.getPaperName());
		
		if (paper.getHasSubmit()) {
			mViewHolder.tv_questionnaire_status.setBackgroundResource(R.drawable.shape_questionnaire_status_submitted);
		} else {
			mViewHolder.tv_questionnaire_status.setBackgroundResource(R.drawable.shape_questionnaire_status_unsubmit);
		}
		
		mViewHolder.ll_questionnaire_item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mOnQuestionnaireClickListener != null) {
					mOnQuestionnaireClickListener.onQuestionnaireClick(paper);
				}
			}
		});
		return convertView;
	}

	private class ViewHolder {
		LinearLayout ll_questionnaire_item;
		TextView tv_questionnaire_name;
		TextView tv_questionnaire_status;
	}
	
	public interface OnQuestionnaireClickListener {
		void onQuestionnaireClick(Paper paper);
	}
	
	private OnQuestionnaireClickListener mOnQuestionnaireClickListener;

	public void setOnQuestionnaireClickListener(OnQuestionnaireClickListener mOnQuestionnaireClickListener) {
		this.mOnQuestionnaireClickListener = mOnQuestionnaireClickListener;
	}
	
}
