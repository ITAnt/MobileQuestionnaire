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

public class WebPaperAdapter extends BaseAdapter {

	private Context mContext;
	private ViewHolder mViewHolder;
	private List<Paper> mWebPapers;
	
	public void setPapers(List<Paper> webPapers) {
		this.mWebPapers = webPapers;
	}

	public WebPaperAdapter(Context context) {
		this.mContext = context;
	}
	
	@Override
	public int getCount() {
		return mWebPapers == null ? 0 : mWebPapers.size();
	}

	@Override
	public Object getItem(int position) {
		if (mWebPapers != null) {
			return mWebPapers.get(position);
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_web_paper, parent, false);
			mViewHolder = new ViewHolder();
			mViewHolder.ll_web_paper_item = (LinearLayout) convertView.findViewById(R.id.ll_web_paper_item);
			mViewHolder.tv_web_paper_name = (TextView) convertView.findViewById(R.id.tv_web_paper_name);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		
		final Paper webPaper = mWebPapers.get(position);
		mViewHolder.tv_web_paper_name.setText(webPaper.getPaperName());
		
		mViewHolder.ll_web_paper_item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mOnWebPaperClickListener != null) {
					mOnWebPaperClickListener.onWebPaperClick(webPaper);
				}
			}
		});
		return convertView;
	}

	private class ViewHolder {
		LinearLayout ll_web_paper_item;
		TextView tv_web_paper_name;
	}
	
	public interface OnWebPaperClickListener {
		void onWebPaperClick(Paper paper);
	}
	
	private OnWebPaperClickListener mOnWebPaperClickListener;

	public void setOnWebPaperClickListener(OnWebPaperClickListener mWebPaperClickListener) {
		this.mOnWebPaperClickListener = mWebPaperClickListener;
	}
	
}
