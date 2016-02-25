package com.onion.paper.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.onion.paper.activity.student.QuestionnaireListActivity;
import com.onion.paper.adapter.GradeAdapter;
import com.onion.paper.adapter.GradeAdapter.OnGradeClickListener;
import com.onion.paper.model.web.MonitorStudentMap;
import com.onion.paper.model.web.User;
import com.onion.paper.tool.HttpTool;
import com.umeng.analytics.MobclickAgent;

/**
 * 学生界面的班级Tab
 * @author 詹子聪
 *
 */
public class StudentGradeFragment extends Fragment {
	
	private PullToRefreshListView ptrlv_student_class;
	private GradeAdapter mGradeAdapter;
	private Context mContext;
	private Activity mActivity;
	private List<User> mGrades;
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("ExploreScreen"); //统计页面
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("ExploreScreen"); 
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_student_grade, container, false);
		initComponent(view);
		return view;
	}
	
	private void initComponent(View view) {
		mActivity = getActivity();
		mContext = mActivity.getApplicationContext();
		
		ptrlv_student_class = (PullToRefreshListView) view.findViewById(R.id.ptrlv_student_class);
		ptrlv_student_class.setMode(Mode.PULL_FROM_START);
		ILoadingLayout startLabels = ptrlv_student_class.getLoadingLayoutProxy(true, false);
		// 刚下拉时，显示的提示
		startLabels.setPullLabel(getActivity().getString(R.string.label_pull)); 
		// 刷新时
		startLabels.setRefreshingLabel(getActivity().getString(R.string.label_loading)); 
		// 下来达到一定距离时，显示的提示
		startLabels.setReleaseLabel(getActivity().getString(R.string.label_release));
		
		mGradeAdapter = new GradeAdapter(mContext);
		mGrades = new ArrayList<>();
		mGradeAdapter.setGrades(mGrades);
		ptrlv_student_class.setAdapter(mGradeAdapter);
		ptrlv_student_class.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				if (HttpTool.isNetworkConnected(mActivity)) {
					findFavoritedIds(BmobUser.getCurrentUser(mActivity).getObjectId());
				} else {
					Toast.makeText(mActivity, R.string.msg_check_network, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mGradeAdapter.setmOnGradeClickListener(new OnGradeClickListener() {
			
			@Override
			public void onGradeClick(String objectId) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mActivity, QuestionnaireListActivity.class);
				intent.putExtra("objectId", objectId);
				mActivity.startActivityForResult(intent, 6);
				
			}
		});
		
		findFavoritedIds(BmobUser.getCurrentUser(mActivity).getObjectId());
	}
	
	/**
	 * 根据当前id在MonitorStudentMap查找已经关注的id
	 */
	private void findFavoritedIds(String currentUserId) {
		BmobQuery<MonitorStudentMap> query = new BmobQuery<>();
		query.addWhereEqualTo("studentId", currentUserId);
		
		query.findObjects(mActivity, new FindListener<MonitorStudentMap>() {
			
			@Override
			public void onSuccess(List<MonitorStudentMap> maps) {
				// TODO Auto-generated method stub
				if (mGrades != null && mGrades.size() > 0) {
					mGrades.clear();
				}
				if (maps != null && maps.size() > 0) {
					findFavoritedUsers(maps);
				} else {
					mGradeAdapter.notifyDataSetChanged();
					ptrlv_student_class.onRefreshComplete();
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	/**
	 * 根据id查找并添加已收藏的用户
	 * @param userId
	 * @param currentIndex
	 * @param mapSize
	 */
	private void findFavoritedUsers(List<MonitorStudentMap> maps) {
		List<BmobQuery<User>> orQueries = new ArrayList<BmobQuery<User>>();
		for (MonitorStudentMap map : maps) {
			BmobQuery<User> query = new BmobQuery<>();
			query.addWhereEqualTo("objectId", map.getMonitorId());
			orQueries.add(query);
		}
		
		BmobQuery<User> mainQuery = new BmobQuery<User>();
		mainQuery.or(orQueries);
		
		mainQuery.findObjects(mActivity, new FindListener<User>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				mGradeAdapter.notifyDataSetChanged();
				ptrlv_student_class.onRefreshComplete();
			}

			@Override
			public void onSuccess(List<User> users) {
				// TODO Auto-generated method stub
				mGrades.addAll(users);
				mGradeAdapter.notifyDataSetChanged();
				ptrlv_student_class.onRefreshComplete();
			}
		});
	}

	/**
	 * 在里面删除了刷新班级列表
	 */
	public void refreshGrades() {
		// TODO Auto-generated method stub
		findFavoritedIds(BmobUser.getCurrentUser(mActivity).getObjectId());
	}
}
