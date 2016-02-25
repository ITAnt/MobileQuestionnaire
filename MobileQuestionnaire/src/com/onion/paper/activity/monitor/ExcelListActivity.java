package com.onion.paper.activity.monitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.onion.paper.R;
import com.onion.paper.adapter.SwipeAdapter;
import com.onion.paper.adapter.SwipeAdapter.OnExcelFileClickListener;
import com.onion.paper.adapter.SwipeAdapter.OnExcelFileDeleteListener;
import com.onion.paper.adapter.SwipeAdapter.OnExcelShareListener;
import com.onion.paper.model.ExcelFile;
import com.onion.paper.tool.FileTool;
import com.onion.paper.view.CustomedActionBar;
import com.onion.paper.view.CustomedActionBar.OnLeftIconClickListener;
import com.onion.paper.view.CustomedActionBar.OnRightIconClickListener;
import com.onion.paper.view.ProgressDialogUtils;
import com.onion.paper.view.SwipeView;
import com.umeng.analytics.MobclickAgent;

public class ExcelListActivity extends Activity implements OnClickListener, OnScrollListener {

	private SwipeAdapter mAdapter;
	/**
	 * 未关闭的SwipeView的集合
	 */
	private List<SwipeView> unClosedSwipeViews = new ArrayList<SwipeView>();
	private OnExcelFileClickListener mOnPaperClickListener;
	private OnExcelFileDeleteListener mOnPaperDeleteListener;
	private OnExcelShareListener mOnExcelShareListener;
	
	
	
	private ListView ptrlv_member_list;
	private Context mContext;
	private List<ExcelFile> mExcels;
	private CustomedActionBar cab_member_list;
	
	private ProgressDialogUtils pDlgUtl;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_excel_list);
		initComponent();
	}

	private void initComponent() {
		mContext = getApplicationContext();

		cab_member_list = (CustomedActionBar) findViewById(R.id.cab_member_list);
		// 点击左侧图标
		cab_member_list.setOnLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onLeftIconClick() {
				onBackPressed();
			}
		});
		
		// 点击右侧图标
		cab_member_list.setOnRightIconClickListener(new OnRightIconClickListener() {
			
			@Override
			public void onRightIconClick() {
				// 刷新Excel列表
				new ExcelTask().execute();
			}
		});
		
		// 点击某一项
		mOnPaperClickListener = new OnExcelFileClickListener() {

			@Override
			public void onExcelClick(ExcelFile excelFile) {
				File file = new File(excelFile.getPath());
				if (file.exists()) {
					// 打开Excel
					Intent intent = new Intent("android.intent.action.VIEW");
			        intent.addCategory("android.intent.category.DEFAULT");
			        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        Uri uri = Uri.fromFile(file);
			        intent.setDataAndType(uri, "application/vnd.ms-excel");
			        startActivity(intent);
				}
			}
		};
		
		mOnExcelShareListener = new OnExcelShareListener() {
			
			@Override
			public void onExcelShare(ExcelFile excelFile) {
				if (new File(excelFile.getPath()).exists()) {
					// 分享Excel
					Intent share = new Intent(Intent.ACTION_SEND);   
					share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(excelFile.getPath())));
					share.setType("*/*");//此处可发送多种文件
					startActivity(Intent.createChooser(share, getString(R.string.label_share_title)));
				} else {
					Toast.makeText(ExcelListActivity.this, R.string.msg_file_not_exists, Toast.LENGTH_SHORT).show();
				}
			}
		};
		
		// 删除某一项
		mOnPaperDeleteListener = new OnExcelFileDeleteListener() {

			@Override
			public void onExcelFileDelete(ExcelFile excelFile) {
				// 删除问卷
				deletePaperDialog(excelFile.getPath());
			}
		};
		
		
		mExcels = new ArrayList<>();
		mAdapter = new SwipeAdapter(mContext, unClosedSwipeViews);
		mAdapter.setPapers(mExcels);
		mAdapter.setOnExcelFileClickListener(mOnPaperClickListener);
		mAdapter.setOnExcelFileDeleteListener(mOnPaperDeleteListener);
		mAdapter.setOnExcelShareListener(mOnExcelShareListener);
		ptrlv_member_list = (ListView) findViewById(R.id.ptrlv_excel_list);
		ptrlv_member_list.setAdapter(mAdapter);
		ptrlv_member_list.setOnScrollListener(this);

		new ExcelTask().execute();
		
	}

	private class ExcelTask extends AsyncTask<Void, Void, List<ExcelFile>> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (pDlgUtl == null) {
				pDlgUtl = new ProgressDialogUtils(ExcelListActivity.this);
				pDlgUtl.show();
			}
		}
		
		@Override
		protected List<ExcelFile> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return FileTool.findLocalExcels(mContext);
		}
		
		@Override
		protected void onPostExecute(List<ExcelFile> excelFiles) {
			// TODO Auto-generated method stub
			super.onPostExecute(excelFiles);
			
			if (pDlgUtl != null) {
				pDlgUtl.hide();
			}
			
			if (mExcels != null && mExcels.size() > 0) {
				mExcels.clear();
			}
			if (excelFiles != null) {
				mExcels.addAll(excelFiles);
			}
			mAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 删除问卷对话框
	 * @param question
	 */
	private void deletePaperDialog(final String path) {
		final AlertDialog dialog = new AlertDialog.Builder(ExcelListActivity.this).create();
		dialog.show();
		dialog.setContentView(R.layout.dialog_customed);
		
		TextView tv_dialog_title = (TextView) dialog.findViewById(R.id.tv_dialog_title);
		TextView tv_dialog_desc = (TextView) dialog.findViewById(R.id.tv_dialog_desc);
		tv_dialog_title.setText(R.string.label_delete_questionnaire_title);
		tv_dialog_desc.setText(R.string.label_delete_local_questionnaire_desc);
		TextView tv_confirm = (TextView) dialog.findViewById(R.id.tv_confirm);
		TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
		
		tv_confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.cancel();
				// 删除相应的问卷再更新UI
				File file = new File(path);
				if (file.exists()) {
					file.delete();
				}
				new ExcelTask().execute();
			}
		});
		
		tv_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		default:
			break;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
			if (mAdapter != null && unClosedSwipeViews.size() > 0)
				mAdapter.closeAllSwipeViews();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}
}
