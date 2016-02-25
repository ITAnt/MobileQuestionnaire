package com.onion.paper.activity.monitor;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import com.onion.paper.R;
import com.onion.paper.adapter.QuestionAdapter;
import com.onion.paper.adapter.QuestionAdapter.OnDelClickListener;
import com.onion.paper.model.web.MonitorStudentMap;
import com.onion.paper.model.web.Paper;
import com.onion.paper.model.web.PaperUserMap;
import com.onion.paper.tool.HttpTool;
import com.onion.paper.view.CustomedActionBar;
import com.onion.paper.view.CustomedActionBar.OnLeftIconClickListener;
import com.onion.paper.view.CustomedActionBar.OnRightTextClickListener;
import com.onion.paper.view.ProgressDialogUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 增加问卷的界面
 * @author 詹子聪
 *
 */
public class AddQuestionnaireActivity extends Activity {

	private List<String> mQuestionList;
	private ListView lv_new_question;
	private QuestionAdapter mAdapter;
	
	private EditText et_questionnaire_name;
	private EditText et_new_question;
	private LinearLayout ll_add_question;
	private QuestionListener mQuestionListener;
	private Context mContext;
	
	private ProgressDialogUtils pDlgUtl;
	private CustomedActionBar ab_new_questionnaire;
	
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
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		//overridePendingTransition(R.anim.slide_in_right, anim.fade_out);
		setContentView(R.layout.activity_add_questionnaire);
		
		initComponent();
	}
	
	private void initComponent() {
		mContext = getApplicationContext();
		
		mQuestionListener = new QuestionListener();
		
		ab_new_questionnaire = (CustomedActionBar) findViewById(R.id.ab_new_questionnaire);
		
		et_questionnaire_name = (EditText) findViewById(R.id.et_questionnaire_name);
		
		et_new_question = (EditText) findViewById(R.id.et_new_question);
		lv_new_question = (ListView) findViewById(R.id.lv_new_question);
		mAdapter = new QuestionAdapter(mContext);
		mQuestionList = new ArrayList<>();
		mAdapter.setQuestions(mQuestionList);
		mAdapter.setOnDelClickListener(new OnDelClickListener() {
			
			@Override
			public void onDelClick(String question) {
				// 点击删除
				deleteQuestionDialog(question);
			}
		});
		lv_new_question.setAdapter(mAdapter);
		
		ll_add_question = (LinearLayout) findViewById(R.id.ll_add_question);
		ll_add_question.setOnClickListener(mQuestionListener);
		
		// 点击提交
		ab_new_questionnaire.setOnRightTextClickListener(new OnRightTextClickListener() {
			
			@Override
			public void onRightTextClick() {
				// TODO Auto-generated method stub
				if (HttpTool.isNetworkConnected(AddQuestionnaireActivity.this)) {
					String questionnaireName = et_questionnaire_name.getText().toString();
					if (!TextUtils.isEmpty(questionnaireName)) {
						if (mQuestionList == null || mQuestionList.size() < 1) {
							
							Toast.makeText(mContext, R.string.msg_questionnaire_cannot_be_empty, Toast.LENGTH_SHORT).show();
						} else {
							// 将问卷提交给服务器
							if (pDlgUtl == null) {
								pDlgUtl = new ProgressDialogUtils(AddQuestionnaireActivity.this);
								pDlgUtl.show();
							}
							BmobUser user = BmobUser.getCurrentUser(mContext);
							if (user != null) {
								Paper paper = new Paper();
								paper.setPaperName(questionnaireName);
								paper.setUserId(user.getObjectId());
								paper.setHasSubmit(false);
								paper.setContent("");
								StringBuffer paperColumn = new StringBuffer();
								for (int i = 0, j = mQuestionList.size(); i < j; i++) {
									paperColumn.append(mQuestionList.get(i));
									if (i != j-1) {
										paperColumn.append("|");
									}
								}
								paper.setColumn(paperColumn.toString());
								paper.save(mContext, new SaveListener() {
									
									@Override
									public void onSuccess() {
										// TODO Auto-generated method stub
										Toast.makeText(mContext, R.string.msg_questionnaire_commit_suc, Toast.LENGTH_SHORT).show();
										
										// 发送成功之后
										// 首先根据目前用户的Id查找发布的所有问卷
										final String objectId = BmobUser.getCurrentUser(AddQuestionnaireActivity.this).getObjectId();
										BmobQuery<Paper> papers = new BmobQuery<Paper>();
										papers.addWhereEqualTo("userId", objectId);
										papers.findObjects(AddQuestionnaireActivity.this, new FindListener<Paper>() {

											@Override
											public void onError(int arg0, String arg1) {
												if (pDlgUtl != null) {
													pDlgUtl.hide();
												}
												AddQuestionnaireActivity.this.finish();
												
											}

											@Override
											public void onSuccess(final List<Paper> papers) {
												
												if (papers != null && papers.size() > 0) {
													// 查找关注了我的所有学生id
													BmobQuery<MonitorStudentMap> query = new BmobQuery<MonitorStudentMap>();
													query.addWhereEqualTo("monitorId", objectId);
													query.findObjects(AddQuestionnaireActivity.this, new FindListener<MonitorStudentMap>() {
														
														@Override
														public void onError(int arg0, String arg1) {
															// TODO Auto-generated method stub
															if (pDlgUtl != null) {
																pDlgUtl.hide();
															}
															AddQuestionnaireActivity.this.finish();
														}
														
														@Override
														public void onSuccess(List<MonitorStudentMap> maps) {
															if (maps != null && maps.size() > 0) {
																// 将问卷与关注了我的用户关联起来
																for (final Paper paper : papers) {
																	
																	for (final MonitorStudentMap map : maps) {
																		map.getStudentId();
																		
																		BmobQuery<PaperUserMap> query = new BmobQuery<PaperUserMap>();
																		query.addWhereEqualTo("paperId", paper.getObjectId());
																		query.addWhereEqualTo("userId", map.getStudentId());
																		query.findObjects(AddQuestionnaireActivity.this, new FindListener<PaperUserMap>() {
																			
																			@Override
																			public void onError(int arg0, String arg1) {
																				// TODO Auto-generated method stub
																				AddQuestionnaireActivity.this.finish();
																			}
																			
																			@Override
																			public void onSuccess(List<PaperUserMap> paperUserMaps) {
																				// TODO Auto-generated method stub
																				if (paperUserMaps == null || paperUserMaps.size() == 0) {
																					// 没有找到映射关系
																					
																					PaperUserMap paperUserMap = new PaperUserMap();
																					paperUserMap.setPaperId(paper.getObjectId());
																					paperUserMap.setUserId(map.getStudentId());
																					paperUserMap.setIfSubmit(false);
																					paperUserMap.save(AddQuestionnaireActivity.this);
																				}
																				if (pDlgUtl != null) {
																					pDlgUtl.hide();
																				}
																				AddQuestionnaireActivity.this.finish();
																			}
																		});
																	}
																}
															} else {
																if (pDlgUtl != null) {
																	pDlgUtl.hide();
																}
																AddQuestionnaireActivity.this.finish();
															}
														}
													});
												} else {
													AddQuestionnaireActivity.this.finish();
												}
											}
										});
									}
									
									@Override
									public void onFailure(int arg0, String exception) {
										// TODO Auto-generated method stub
										if (pDlgUtl != null) {
											pDlgUtl.hide();
										}
										Toast.makeText(mContext, getString(R.string.msg_questionnaire_commit_fail) + exception, Toast.LENGTH_SHORT).show();
									}
								});
							} else {
								if (pDlgUtl != null) {
									pDlgUtl.hide();
								}
								Toast.makeText(mContext, R.string.msg_please_relogin, Toast.LENGTH_SHORT).show();
							}
							
						}
					} else {
						Toast.makeText(mContext, R.string.msg_questionnaire_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(mContext, R.string.msg_check_network, Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		// 点击后退
		ab_new_questionnaire.setOnLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onLeftIconClick() {
				// TODO Auto-generated method stub
				exitClick();
			}
			
		});
	}
	
	
	
	/**
	 * 将要退出
	 */
	private void exitClick() {
		String questionnaireName = et_questionnaire_name.getText().toString();
		if (!TextUtils.isEmpty(questionnaireName) || (mQuestionList != null && mQuestionList.size() > 0)) {
			exitQuestionnaireDialog();
		} else {
			AddQuestionnaireActivity.this.finish();
		}
	}
	
	/**
	 * 点击监听器
	 * @author 詹子聪
	 *
	 */
	private class QuestionListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.ll_add_question:
				// 增加一个问题(选项)
				String question = et_new_question.getText().toString();
				if (!TextUtils.isEmpty(question)) {
					if (!question.contains("|") && !question.contains("~")) {
						mQuestionList.add(question);
						et_new_question.setText("");
						mAdapter.notifyDataSetChanged();
						
						// 滚到最低
						lv_new_question.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								lv_new_question.setSelection(lv_new_question.getBottom());
							}
						});
					} else {
						Toast.makeText(mContext, R.string.msg_contains_special, Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(mContext, R.string.msg_please_input_content, Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		exitClick();
	}
	
	/**
	 * 删除问题对话框
	 * @param question
	 */
	private void deleteQuestionDialog(final String question) {
		final AlertDialog dialog = new AlertDialog.Builder(AddQuestionnaireActivity.this).create();
		dialog.show();
		dialog.setContentView(R.layout.dialog_customed);
		
		TextView tv_dialog_title = (TextView) dialog.findViewById(R.id.tv_dialog_title);
		TextView tv_dialog_desc = (TextView) dialog.findViewById(R.id.tv_dialog_desc);
		tv_dialog_title.setText(R.string.label_delete_questionnaire_title);
		tv_dialog_desc.setText(R.string.label_delete_questionnaire_desc);
		TextView tv_confirm = (TextView) dialog.findViewById(R.id.tv_confirm);
		TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
		
		tv_confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.cancel();
				mQuestionList.remove(question);
				mAdapter.notifyDataSetChanged();
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
	 * 离开问卷对话框
	 * @param question
	 */
	private void exitQuestionnaireDialog() {
		final AlertDialog dialog = new AlertDialog.Builder(AddQuestionnaireActivity.this).create();
		dialog.show();
		dialog.setContentView(R.layout.dialog_customed);
		
		TextView tv_dialog_title = (TextView) dialog.findViewById(R.id.tv_dialog_title);
		TextView tv_dialog_desc = (TextView) dialog.findViewById(R.id.tv_dialog_desc);
		tv_dialog_title.setText(R.string.label_exit_questionnaire_title);
		tv_dialog_desc.setText(R.string.label_exit_questionnaire__desc);
		TextView tv_confirm = (TextView) dialog.findViewById(R.id.tv_confirm);
		TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
		
		tv_confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.cancel();
				AddQuestionnaireActivity.this.finish();
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
