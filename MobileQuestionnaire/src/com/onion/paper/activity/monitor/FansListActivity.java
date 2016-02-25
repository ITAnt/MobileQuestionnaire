package com.onion.paper.activity.monitor;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

import com.itant.view.pulltorefresh.ILoadingLayout;
import com.itant.view.pulltorefresh.PullToRefreshBase;
import com.itant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.itant.view.pulltorefresh.PullToRefreshListView;
import com.onion.paper.R;
import com.onion.paper.adapter.FansAdapter;
import com.onion.paper.adapter.FansAdapter.OnMemberClickListener;
import com.onion.paper.model.web.MonitorStudentMap;
import com.onion.paper.model.web.User;
import com.onion.paper.tool.HttpTool;
import com.onion.paper.view.CustomedActionBar;
import com.onion.paper.view.CustomedActionBar.OnLeftIconClickListener;
import com.onion.paper.view.CustomedActionBar.OnRightIconClickListener;
import com.umeng.analytics.MobclickAgent;

public class FansListActivity extends Activity {

	private PullToRefreshListView ptrlv_member_list;
	private FansAdapter mMemberAdapter;
	private Context mContext;
	private List<User> mMembers;
	
	private CustomedActionBar cab_member_list;

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
		//overridePendingTransition(R.anim.slide_in_right, anim.fade_out);
		setContentView(R.layout.activity_fans_list);

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
				// 刷新学生状态
				
			}
		});

		ptrlv_member_list = (PullToRefreshListView) findViewById(R.id.ptrlv_member_list);
		ptrlv_member_list.setMode(Mode.PULL_FROM_START);
		ILoadingLayout startLabels = ptrlv_member_list.getLoadingLayoutProxy(true, false);
		// 刚下拉时，显示的提示
		startLabels.setPullLabel(getString(R.string.label_pull)); 
		// 刷新时
		startLabels.setRefreshingLabel(getString(R.string.label_loading)); 
		// 下来达到一定距离时，显示的提示
		startLabels.setReleaseLabel(getString(R.string.label_release));
		mMemberAdapter = new FansAdapter(mContext);
		mMembers = new ArrayList<>();
		mMemberAdapter.setMembers(mMembers);
		ptrlv_member_list.setAdapter(mMemberAdapter);
		ptrlv_member_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				if (HttpTool.isNetworkConnected(FansListActivity.this)) {
					findAllFans();
				} else {
					Toast.makeText(FansListActivity.this, R.string.msg_check_network, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				
			}
		});

		mMemberAdapter
				.setOnMemberClickListener(new OnMemberClickListener() {

					@Override
					public void onMemberClick() {
						// 点击查看学生的信息
					}
				});

		/*QuestionnaireMember member = new QuestionnaireMember();
		member.setMemberName("张三");
		member.setStatus(1);
		mMembers.add(member);
		mMemberAdapter.notifyDataSetChanged();*/
		
		findAllFans();
		
	}

	/**
	 * 根据当前objectId寻找当前用户的粉丝
	 */
	private void findAllFans() {
		// TODO Auto-generated method stub
		BmobQuery<MonitorStudentMap> query = new BmobQuery<MonitorStudentMap>();
		query.addWhereEqualTo("monitorId", BmobUser.getCurrentUser(this).getObjectId());
		query.findObjects(this, new FindListener<MonitorStudentMap>() {
			
			@Override
			public void onSuccess(List<MonitorStudentMap> userFansMaps) {
				// TODO Auto-generated method stub
				if (userFansMaps != null && userFansMaps.size() > 0) {
					
					List<BmobQuery<User>> orList = new ArrayList<BmobQuery<User>>();
					for (MonitorStudentMap map : userFansMaps) {
						BmobQuery<User> fansQuery = new BmobQuery<User>();
						fansQuery.addWhereEqualTo("objectId", map.getStudentId());
						orList.add(fansQuery);
					}
					BmobQuery<User> orQuery = new BmobQuery<User>();
					orQuery.or(orList);
					orQuery.findObjects(FansListActivity.this, new FindListener<User>() {

						@Override
						public void onError(int arg0, String arg1) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onSuccess(List<User> fans) {
							ptrlv_member_list.onRefreshComplete();
							// TODO Auto-generated method stub
							if (fans != null && fans.size() > 0) {
								if (mMembers != null && mMembers.size() > 0) {
									mMembers.clear();
								}
								mMembers.addAll(fans);
								mMemberAdapter.notifyDataSetChanged();
							}
						}
					});
					
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ptrlv_member_list.onRefreshComplete();
			}
		});
	}
}
