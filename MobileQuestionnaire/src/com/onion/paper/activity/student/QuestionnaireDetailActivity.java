package com.onion.paper.activity.student;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.onion.paper.R;
import com.onion.paper.model.web.Paper;
import com.onion.paper.model.web.PaperUserMap;
import com.onion.paper.tool.HttpTool;
import com.onion.paper.view.CustomedActionBar;
import com.onion.paper.view.CustomedActionBar.OnLeftIconClickListener;
import com.onion.paper.view.CustomedActionBar.OnRightTextClickListener;
import com.onion.paper.view.ProgressDialogUtils;
import com.umeng.analytics.MobclickAgent;


public class QuestionnaireDetailActivity extends Activity implements OnClickListener, TextWatcher {
	private Context mContext;
	//private LinearLayout ll_retry;
	private CustomedActionBar ab_questionnaire_detail;
	private Paper mPaper;
	
	private Button btn_last_question;
	private Button btn_next_question;
	private TextView tv_question_name;
	private EditText et_question_answer;
	private int currentIndex;
	private int questionSize;
	private String[] columns;
	private String[] mAnswers;
	
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
		setContentView(R.layout.activity_questionnaire_detail);
		
		initComponent();
	}

	private void initComponent() {
		mContext = getApplicationContext();
		
		btn_last_question = (Button) findViewById(R.id.btn_last_question);
		btn_last_question.setOnClickListener(this);
		btn_next_question = (Button) findViewById(R.id.btn_next_question);
		btn_next_question.setOnClickListener(this);
		tv_question_name = (TextView) findViewById(R.id.tv_question_name);
		et_question_answer = (EditText) findViewById(R.id.et_question_answer);
		et_question_answer.addTextChangedListener(this);
		mPaper = (Paper) getIntent().getSerializableExtra("paper");
		//ll_retry = (LinearLayout) findViewById(R.id.ll_retry);
		//ll_retry.setOnClickListener(this);
		
		if (mPaper != null) {
			String columnString = mPaper.getColumn();
			columns = columnString.split("\\|");
		}
		
		if (columns != null && columns.length > 0) {
			questionSize = columns.length;
			tv_question_name.setText(columns[0] + "(" + (currentIndex+1) + "/" + questionSize + ")");
			mAnswers = new String[questionSize];
		} else {
			tv_question_name.setText(R.string.label_no_question);
			et_question_answer.setVisibility(View.GONE);
		}
		
		// 后退
		ab_questionnaire_detail = (CustomedActionBar) findViewById(R.id.ab_questionnaire_detail);
		ab_questionnaire_detail.setOnLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onLeftIconClick() {
				if (isQuestionnairedChanged()) {
					// 提示用户是否离开
					exitQuestionnaireDialog();
				} else {
					onBackPressed();
				}
			}
		});
		
		// 提交
		ab_questionnaire_detail.setOnRightTextClickListener(new OnRightTextClickListener() {
			
			@Override
			public void onRightTextClick() {
				// TODO Auto-generated method stub
				if (HttpTool.isNetworkConnected(QuestionnaireDetailActivity.this)) {
					
					String currentAnswerStr = et_question_answer.getText().toString();
					if (currentAnswerStr.contains("~") || currentAnswerStr.contains("|")) {
						Toast.makeText(QuestionnaireDetailActivity.this, R.string.msg_no_special, Toast.LENGTH_SHORT).show();
					} else {
						mAnswers[currentIndex] = currentAnswerStr;
						if (isQuestionnairedFinished()) {
							StringBuffer answers = new StringBuffer();
							for (String answer : mAnswers) {
								answers.append(answer).append("|");
							}
							answers.setCharAt(answers.length() - 1, '~');
							
							if (pDlgUtl == null) {
								pDlgUtl = new ProgressDialogUtils(QuestionnaireDetailActivity.this);
								pDlgUtl.show();
							}
							
							Paper submitPaper = new Paper();
							submitPaper.setContent(mPaper.getContent() + answers.toString());
							submitPaper.update(QuestionnaireDetailActivity.this, mPaper.getObjectId(), new UpdateListener() {
								
								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									Toast.makeText(QuestionnaireDetailActivity.this, R.string.msg_commit_suc, Toast.LENGTH_SHORT).show();
									// 更改PaperUserMap中提交状态
									BmobQuery<PaperUserMap> query = new BmobQuery<PaperUserMap>();
									query.addWhereEqualTo("paperId", mPaper.getObjectId());
									query.addWhereEqualTo("userId", BmobUser.getCurrentUser(QuestionnaireDetailActivity.this).getObjectId());
									query.findObjects(QuestionnaireDetailActivity.this, new FindListener<PaperUserMap>() {
										
										@Override
										public void onSuccess(List<PaperUserMap> maps) {
											// TODO Auto-generated method stub
											if (pDlgUtl != null) {
												pDlgUtl.hide();
											}
											if (maps != null && maps.size() > 0) {
												PaperUserMap paperUserMap = maps.get(0);
												paperUserMap.setIfSubmit(true);
												paperUserMap.update(QuestionnaireDetailActivity.this);
											}
											QuestionnaireDetailActivity.this.setResult(6);
											QuestionnaireDetailActivity.this.finish();
										}
										
										@Override
										public void onError(int arg0, String arg1) {
											// TODO Auto-generated method stub
											if (pDlgUtl != null) {
												pDlgUtl.hide();
											}
										}
									});
								}
								
								@Override
								public void onFailure(int arg0, String arg1) {
									// TODO Auto-generated method stub
									if (pDlgUtl != null) {
										pDlgUtl.hide();
									}
								}
							});
							
						} else {
							Toast.makeText(mContext, R.string.msg_fill_form, Toast.LENGTH_LONG).show();
						}
					}
				} else {
					Toast.makeText(QuestionnaireDetailActivity.this, R.string.msg_check_network, Toast.LENGTH_SHORT).show();
				}
				
			}
		});
	}
	
	/**
	 * 判断问卷是否填写完毕
	 * @return
	 */
	private boolean isQuestionnairedFinished() {
		if (mAnswers == null || mAnswers.length == 0) {
			return false;
		}
		
		for (String answer : mAnswers) {
			if (TextUtils.isEmpty(answer)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 判断问卷是否有填写
	 * @return
	 */
	private boolean isQuestionnairedChanged() {
		for (String answer : mAnswers) {
			if (!TextUtils.isEmpty(answer)) {
				return true;
			}
		}
		return false;
	}
	
	

	@Override
	public void onClick(View v) {
		
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_last_question:
			// 上一题
			String currentAnswerString = et_question_answer.getText().toString();
			if (currentAnswerString.contains("~") || currentAnswerString.contains("|")) {
				Toast.makeText(QuestionnaireDetailActivity.this, R.string.msg_no_special, Toast.LENGTH_SHORT).show();
			} else {
				mAnswers[currentIndex] = currentAnswerString;
				if (currentIndex > 0) {
					currentIndex--;
					tv_question_name.setText(columns[currentIndex] + "(" + (currentIndex+1) + "/" + questionSize + ")");
					et_question_answer.setText(mAnswers[currentIndex]);
				}
			}
			break;
		case R.id.btn_next_question:
			// 下一题
			String currentAnswerStr = et_question_answer.getText().toString();
			if (currentAnswerStr.contains("~") || currentAnswerStr.contains("|")) {
				Toast.makeText(QuestionnaireDetailActivity.this, R.string.msg_no_special, Toast.LENGTH_SHORT).show();
			} else {
				mAnswers[currentIndex] = currentAnswerStr;
				if (currentIndex < questionSize - 1) {
					currentIndex++;
					tv_question_name.setText(columns[currentIndex]+ "(" + (currentIndex+1) + "/" + questionSize + ")");
					et_question_answer.setText(mAnswers[currentIndex]);
				}
			}
			break;

		default:
			break;
		}
	}
	
	/**
	 * 离开问卷对话框
	 * @param question
	 */
	private void exitQuestionnaireDialog() {
		final AlertDialog dialog = new AlertDialog.Builder(QuestionnaireDetailActivity.this).create();
		dialog.show();
		dialog.setContentView(R.layout.dialog_customed);
		
		TextView tv_dialog_title = (TextView) dialog.findViewById(R.id.tv_dialog_title);
		TextView tv_dialog_desc = (TextView) dialog.findViewById(R.id.tv_dialog_desc);
		tv_dialog_title.setText(R.string.label_not_complete);
		tv_dialog_desc.setText(R.string.label_not_complete_desc);
		TextView tv_confirm = (TextView) dialog.findViewById(R.id.tv_confirm);
		TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
		
		tv_confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.cancel();
				QuestionnaireDetailActivity.this.finish();
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
	
	/**
	 * 将要退出
	 */
	private void exitClick() {
		if (isQuestionnairedChanged()) {
			exitQuestionnaireDialog();
		} else {
			QuestionnaireDetailActivity.this.finish();
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		exitClick();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		String answer = s.toString();
		if (!TextUtils.isEmpty(answer)) {
			if (currentIndex == 0) {
				btn_last_question.setEnabled(false);
			} else {
				btn_last_question.setEnabled(true);
			}
			
			if (currentIndex == questionSize -1) {
				btn_next_question.setEnabled(false);
			} else {
				btn_next_question.setEnabled(true);
			}
		} else {
			btn_last_question.setEnabled(false);
			btn_next_question.setEnabled(false);
		}
	}
}
