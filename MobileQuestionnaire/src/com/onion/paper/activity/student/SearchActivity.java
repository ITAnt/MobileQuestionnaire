package com.onion.paper.activity.student;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;

import com.onion.paper.R;
import com.onion.paper.adapter.SearchResultAdapter;
import com.onion.paper.adapter.SearchResultAdapter.OnFavoriteClickListener;
import com.onion.paper.model.web.MonitorStudentMap;
import com.onion.paper.model.web.Paper;
import com.onion.paper.model.web.PaperUserMap;
import com.onion.paper.model.web.User;
import com.onion.paper.tool.HttpTool;
import com.onion.paper.view.CustomedActionBar;
import com.onion.paper.view.CustomedActionBar.OnLeftIconClickListener;
import com.onion.paper.view.ProgressDialogUtils;
import com.umeng.analytics.MobclickAgent;
import com.zxing.activity.CaptureActivity;

public class SearchActivity extends Activity implements OnClickListener {

	private CustomedActionBar ab_register;
	private LinearLayout ll_scan;
	private EditText et_phone;
	
	private RelativeLayout rl_search;
	private TextView tv_search_result;
	
	private SearchResultAdapter mAdapter;
	private List<User> mMonitors;
	private ListView lv_search_result;
	
	private BmobUser mCurrentUser;
	
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
		setContentView(R.layout.activity_search);
		
		initComponent();
	}

	private void initComponent() {
		mCurrentUser = BmobUser.getCurrentUser(this);
		// TODO Auto-generated method stub
		ab_register = (CustomedActionBar) findViewById(R.id.ab_register);
		ab_register.setOnLeftIconClickListener(new OnLeftIconClickListener() {
			@Override
			public void onLeftIconClick() {
				onBackPressed();
			}
		});
		
		et_phone = (EditText) findViewById(R.id.et_phone);
		
		tv_search_result = (TextView) findViewById(R.id.tv_search_result);
		
		ll_scan = (LinearLayout) findViewById(R.id.ll_scan);
		ll_scan.setOnClickListener(this);
		
		rl_search = (RelativeLayout) findViewById(R.id.rl_search);
		rl_search.setOnClickListener(this);
		
		lv_search_result = (ListView) findViewById(R.id.lv_search_result);
		mAdapter = new SearchResultAdapter(this);
		mMonitors = new ArrayList<>();
		mAdapter.setMonitorNames(mMonitors);
		mAdapter.setOnFavoriteClickListener(new OnFavoriteClickListener() {
			
			@Override
			public void onFavoriteClick(final String objectId) {
				if (HttpTool.isNetworkConnected(SearchActivity.this)) {
					// 点击收藏
					if (mCurrentUser != null) {
						if (!mMonitors.get(0).getHasBeenFavorited()) {
							// 还没收藏
							if (pDlgUtl == null) {
								pDlgUtl = new ProgressDialogUtils(SearchActivity.this);
								pDlgUtl.show();
							}
							MonitorStudentMap monitorStudentMap = new MonitorStudentMap();
							monitorStudentMap.setMonitorId(objectId);
							monitorStudentMap.setStudentId(mCurrentUser.getObjectId());
							monitorStudentMap.save(SearchActivity.this, new SaveListener() {
								
								@Override
								public void onSuccess() {
									// 关注
									
									Toast.makeText(SearchActivity.this, R.string.msg_follow_suc, Toast.LENGTH_SHORT).show();
									mMonitors.get(0).setHasBeenFavorited(true);
									mAdapter.notifyDataSetChanged();
									
									// 关注成功之后，将该班长发布的所有问卷都关联到该用户Id
									// 首先查找班长发布的所有问卷
									BmobQuery<Paper> papers = new BmobQuery<Paper>();
									papers.addWhereEqualTo("userId", objectId);
									papers.findObjects(SearchActivity.this, new FindListener<Paper>() {
										
										@Override
										public void onError(int arg0, String arg1) {
											// TODO Auto-generated method stub
											if (pDlgUtl != null) {
												pDlgUtl.hide();
											}
										}
										
										@Override
										public void onSuccess(List<Paper> papers) {
											if (pDlgUtl != null) {
												pDlgUtl.hide();
											}
											// 将问卷与当前用户关联
											String userObjectId = BmobUser.getCurrentUser(SearchActivity.this).getObjectId();
											for (Paper paper : papers) {
												PaperUserMap paperUserMap = new PaperUserMap();
												paperUserMap.setPaperId(paper.getObjectId());
												paperUserMap.setUserId(userObjectId);
												paperUserMap.setIfSubmit(false);
												paperUserMap.save(SearchActivity.this);
											}
											
											setResult(6);
										}
									});
								}
								
								@Override
								public void onFailure(int arg0, String arg1) {
									// TODO Auto-generated method stub
									if (pDlgUtl != null) {
										pDlgUtl.hide();
									}
									Toast.makeText(SearchActivity.this, R.string.msg_follow_fail, Toast.LENGTH_SHORT).show();
								}
							});
						} else {
							Toast.makeText(SearchActivity.this, R.string.msg_have_followed, Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(SearchActivity.this, R.string.msg_relogin, Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(SearchActivity.this, R.string.msg_check_network, Toast.LENGTH_SHORT).show();
				}
			}
		});
		lv_search_result.setAdapter(mAdapter);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.ll_scan:
			// 打开扫二维码界面
			if (HttpTool.isNetworkConnected(this)) {
				startActivityForResult(new Intent(SearchActivity.this, CaptureActivity.class), 6);
			} else {
				Toast.makeText(SearchActivity.this, R.string.msg_check_network, Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.rl_search:
			// 搜索班级
			if (HttpTool.isNetworkConnected(this)) {
				String nameOrPhone = et_phone.getText().toString();
				if (!TextUtils.isEmpty(nameOrPhone)) {
					searchMonitorsByName(nameOrPhone);
				} else {
					Toast.makeText(this, R.string.msg_search_content_empty, Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(SearchActivity.this, R.string.msg_check_network, Toast.LENGTH_SHORT).show();
			}
			break;
			
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 6) {
			final String objectId = data.getExtras().getString("objectId");
			if (!TextUtils.isEmpty(objectId)) {
				if (pDlgUtl == null) {
					pDlgUtl = new ProgressDialogUtils(SearchActivity.this);
					pDlgUtl.show();
				}
				BmobQuery<User> query = new BmobQuery<>();
				query.getObject(this, objectId, new GetListener<User>() {
					
					@Override
					public void onFailure(int arg0, String arg1) {
						if (pDlgUtl != null) {
							pDlgUtl.hide();
						}
						// TODO Auto-generated method stub
						if (mMonitors != null && mMonitors.size() > 0) {
							mMonitors.clear();
						}
						tv_search_result.setText(R.string.msg_result_null);
						mAdapter.notifyDataSetChanged();
					}
					
					@Override
					public void onSuccess(User user) {
						if (pDlgUtl != null) {
							pDlgUtl.hide();
						}
						// TODO Auto-generated method stub
						if (mMonitors != null && mMonitors.size() > 0) {
							mMonitors.clear();
						}
						
						if (user != null) {
							mMonitors.add(user);
							checkIfFavorited(objectId);
						} else {
							tv_search_result.setText(R.string.msg_result_null);
							mAdapter.notifyDataSetChanged();
						}
					}
				});
			}
		}
	}

	/**
	 * 根据用户名搜索班级
	 */
	private void searchMonitorsByName(final String name) {
		// TODO Auto-generated method stub
		if (mMonitors != null && mMonitors.size() > 0) {
			mMonitors.clear();
		}
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("username", name);
		query.findObjects(this, new FindListener<User>() {
			
			@Override
			public void onSuccess(List<User> users) {
				// TODO Auto-generated method stub
				
				if (users != null && users.size() > 0) {
					tv_search_result.setText(R.string.msg_search_result);
					User user = users.get(0);
					mMonitors.add(user);
					checkIfFavorited(user.getObjectId());
				} else {
					tv_search_result.setText(R.string.msg_result_null);
					mAdapter.notifyDataSetChanged();
				}
				
				
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(SearchActivity.this, getString(R.string.msg_search_fail) + arg1, Toast.LENGTH_SHORT).show();
				tv_search_result.setText(R.string.label_search_error);
				mAdapter.notifyDataSetChanged();
			}
		});
	}
	
	/**
	 * 查看是否已经收藏了
	 */
	private void checkIfFavorited(String monitorId) {
		// TODO Auto-generated method stub
		BmobQuery<MonitorStudentMap> query = new BmobQuery<MonitorStudentMap>();
		query.addWhereEqualTo("monitorId", monitorId);
		query.addWhereEqualTo("studentId", mCurrentUser.getObjectId());
		query.findObjects(SearchActivity.this, new FindListener<MonitorStudentMap>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onSuccess(List<MonitorStudentMap> maps) {
				// TODO Auto-generated method stub
				if (maps != null && maps.size() > 0) {
					mMonitors.get(0).setHasBeenFavorited(true);
				} else {
					mMonitors.get(0).setHasBeenFavorited(false);
				}
				
				mAdapter.notifyDataSetChanged();
			}
		});
	}
}
