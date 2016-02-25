package com.onion.paper.activity.monitor;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.GetListener;

import com.onion.paper.R;
import com.onion.paper.SuperConstants;
import com.onion.paper.model.web.Paper;
import com.onion.paper.tool.ExcelTool;
import com.onion.paper.tool.HttpTool;
import com.onion.paper.tool.SystemTool;
import com.onion.paper.view.CustomedActionBar;
import com.onion.paper.view.CustomedActionBar.OnLeftIconClickListener;
import com.onion.paper.view.CustomedActionBar.OnRightIconClickListener;
import com.onion.paper.view.ProgressDialogUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 班级操作问卷界面
 * 
 * @author 詹子聪
 *
 */
public class OperateQuestionnairActivity extends Activity implements
		OnClickListener {
	private CustomedActionBar cab_operate_questionnaire;
	private LinearLayout ll_delete_questionnaire;
	private LinearLayout ll_export;

	private Paper mPaper;
	private TextView tv_paper_name;

	private WebView wv_paper_content;
	
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
		// overridePendingTransition(R.anim.slide_in_right, anim.fade_out);
		setContentView(R.layout.activity_operate_questionnaire);

		initComponent();
	}

	private void initComponent() {
		mPaper = (Paper) getIntent().getSerializableExtra("paper");

		cab_operate_questionnaire = (CustomedActionBar) findViewById(R.id.cab_operate_questionnaire);
		cab_operate_questionnaire
				.setOnLeftIconClickListener(new OnLeftIconClickListener() {

					@Override
					public void onLeftIconClick() {
						onBackPressed();
					}
				});

		cab_operate_questionnaire
				.setOnRightIconClickListener(new OnRightIconClickListener() {

					@Override
					public void onRightIconClick() {
						// 点击右侧刷新按钮，刷新表单提交情况（比例）
						if (HttpTool.isNetworkConnected(OperateQuestionnairActivity.this)) {
							refreshPaper();
						} else {
							Toast.makeText(OperateQuestionnairActivity.this, R.string.msg_check_network, Toast.LENGTH_SHORT).show();
						}
					}
				});

		ll_delete_questionnaire = (LinearLayout) findViewById(R.id.ll_delete_questionnaire);
		ll_delete_questionnaire.setOnClickListener(this);

		ll_export = (LinearLayout) findViewById(R.id.ll_export);
		ll_export.setOnClickListener(this);

		tv_paper_name = (TextView) findViewById(R.id.tv_paper_name);
		tv_paper_name.setText(mPaper.getPaperName());

		wv_paper_content = (WebView) findViewById(R.id.wv_paper_content);
		WebSettings settings = wv_paper_content.getSettings();  
        settings.setSupportZoom(true);          //支持缩放  
        settings.setBuiltInZoomControls(true);  //启用内置缩放装置  
        settings.setJavaScriptEnabled(true);    //启用JS脚本 
        settings.setDefaultTextEncodingName("utf-8");
		
        drawTable(mPaper);
	}
	
	private void refreshPaper() {
		if (pDlgUtl == null) {
			pDlgUtl = new ProgressDialogUtils(OperateQuestionnairActivity.this);
			pDlgUtl.show();
		}
		BmobQuery<Paper> query = new BmobQuery<>();
		query.getObject(OperateQuestionnairActivity.this, mPaper.getObjectId(), new GetListener<Paper>() {
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				if (pDlgUtl != null) {
					pDlgUtl.hide();
				}
			}
			
			@Override
			public void onSuccess(Paper paper) {
				// TODO Auto-generated method stub
				if (pDlgUtl != null) {
					pDlgUtl.hide();
				}
				mPaper = paper;
				drawTable(paper);
			}
		});
	}

	/**
	 * 画出已提交的问卷表格
	 */
	private void drawTable(Paper paper) {
		StringBuilder html = new StringBuilder();
        String head = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\" /><style type=\"text/css\">#customers{font-family:\"Trebuchet MS\", Arial, Helvetica, sans-serif;width:100%;border-collapse:collapse;}#customers td, #customers th{font-size:1em;border:1px solid #98bf21;padding:3px 7px 2px 7px;}#customers th   {  font-size:1.1em;  text-align:left;  padding-top:5px;  padding-bottom:4px;  background-color:#A7C942;  color:#ffffff;  }  #customers tr.alt td   {  color:#000000;  background-color:#EAF2D3;  }  </style>  </head>  <body>  <table id=\"customers\">";
        String bottom = "</table></body></html>";
        StringBuilder table = new StringBuilder();
        table.append("<tr>");
        String[] columns = paper.getColumn().split("\\|");
        for (String column : columns) {
        	table.append("<th>").append(column).append("</th>");
        }
        table.append("</tr>");
        
        String answerString = paper.getContent();
        if (!TextUtils.isEmpty(answerString)) {
        	String[] answers = answerString.split("\\~");
        	tv_paper_name.setText(mPaper.getPaperName() + "(" + answers.length + ")");
    		for (int i = 0, j = answers.length; i < j; i++) {
    			String[] answerColumns = answers[i].split("\\|");
    			if ((i % 2) != 0) {
    				// 奇数行，加阴影
    				table.append("<tr class=\"alt\">");
    			} else {
    				table.append("<tr>");
    			}
    			for (String answerColumn : answerColumns) {
    	        	table.append("<td>").append(answerColumn).append("</td>");
    	        }
    	        table.append("</tr>");
    		}
        }
        
        String htmlCode = html.append(head).append(table).append(bottom).toString();
        wv_paper_content.loadData(htmlCode, "text/html; charset=UTF-8", null);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ll_export:
			// 导出为Excel表格
			if (SystemTool.isSDCardEnable()) {
		        String[] columns = mPaper.getColumn().split("\\|");
		        String answerString = mPaper.getContent();
		        if (!TextUtils.isEmpty(answerString)) {
		        	String[] answers = answerString.split("\\~");
		        	int j = answers.length;
		        	String[][] allRows = new String[j+1][columns.length];
		        	allRows[0] = columns;
		        	for (int i = 0; i < j; i++) {
		        		allRows[i+1] = answers[i].split("\\|");
		        	}
		        	
		        	File file = new File(SuperConstants.EXCEL_DIRECTORY + mPaper.getPaperName() +".xls"); 
		        	if (file.exists()) {
		        		if (file.delete()) {
		        			saveExcel(allRows);
		        		} else {
		        			Toast.makeText(this, R.string.msg_export_fail, Toast.LENGTH_SHORT).show();
						}
		        	} else {
		        		saveExcel(allRows);
					}
		        }
			} else {
				Toast.makeText(this, R.string.msg_no_sd_card, Toast.LENGTH_SHORT).show();
				
			}
			break;
		case R.id.ll_delete_questionnaire:
			// 删除问卷
			if (HttpTool.isNetworkConnected(this)) {
				deleteQuestionnaireDialog();
			} else {
				Toast.makeText(this, R.string.msg_check_network, Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}

	private void saveExcel(String[][] allRows) {
		if (ExcelTool.saveExcelFile(this, mPaper.getPaperName(), allRows)) {
			// 导出成功，弹出对话框分享
			Intent share = new Intent(Intent.ACTION_SEND);   
			share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(SuperConstants.EXCEL_DIRECTORY + mPaper.getPaperName() +".xls")));
			share.setType("*/*");//此处可发送多种文件
			startActivity(Intent.createChooser(share, getString(R.string.label_share_title)));
		} else {
			Toast.makeText(this, R.string.msg_export_fail, Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 删除问题对话框
	 * 
	 * @param question
	 */
	private void deleteQuestionnaireDialog() {
		final AlertDialog dialog = new AlertDialog.Builder(
				OperateQuestionnairActivity.this).create();
		dialog.show();
		dialog.setContentView(R.layout.dialog_customed);

		TextView tv_dialog_title = (TextView) dialog
				.findViewById(R.id.tv_dialog_title);
		TextView tv_dialog_desc = (TextView) dialog
				.findViewById(R.id.tv_dialog_desc);
		tv_dialog_title.setText(R.string.label_delete_paper);
		tv_dialog_desc.setText(R.string.label_delete_paper_desc);
		TextView tv_confirm = (TextView) dialog.findViewById(R.id.tv_confirm);
		TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);

		tv_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
				if (pDlgUtl == null) {
					pDlgUtl = new ProgressDialogUtils(OperateQuestionnairActivity.this);
					pDlgUtl.show();
				}
				// 删除问卷，然后finish，然后刷新列表
				mPaper.delete(OperateQuestionnairActivity.this, new DeleteListener() {
					
					@Override
					public void onSuccess() {
						if (pDlgUtl != null) {
							pDlgUtl.hide();
						}
						// TODO Auto-generated method stub
						Toast.makeText(OperateQuestionnairActivity.this, "删除问卷成功", Toast.LENGTH_SHORT).show();
						setResult(6);
						finish();
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						if (pDlgUtl != null) {
							pDlgUtl.hide();
						}
						// TODO Auto-generated method stub
						Toast.makeText(OperateQuestionnairActivity.this, "删除问卷失败", Toast.LENGTH_SHORT).show();
					}
				});
				
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
}
