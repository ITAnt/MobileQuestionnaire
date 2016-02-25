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
import android.widget.GridView;

import com.onion.paper.R;
import com.onion.paper.activity.monitor.AddQuestionnaireActivity;
import com.onion.paper.activity.monitor.ExcelListActivity;
import com.onion.paper.activity.monitor.FansListActivity;
import com.onion.paper.activity.monitor.MonitorActivity;
import com.onion.paper.adapter.ExploreAdapter;
import com.onion.paper.adapter.ExploreAdapter.OnFolderClickListener;
import com.onion.paper.model.ExploreItem;
import com.umeng.analytics.MobclickAgent;

/**
 * 学生界面的班级Tab
 * @author 詹子聪
 *
 */
public class StudentExploreFragment extends Fragment {
	private Context mContext;
	private Activity mActivity;
	private GridView gv_explore;
	private ExploreAdapter mAdapter;
	private List<ExploreItem> mExploreItems;
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("MainScreen"); //统计页面
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("MainScreen"); 
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_student_explore, null);
		initComponent(view);
		return view;
	}
	/*IUiListener qqShareListener = new IUiListener() {
        @Override
        public void onCancel() {
            
        }
        @Override
        public void onComplete(Object response) {
            // TODO Auto-generated method stub
           
        }
        @Override
        public void onError(UiError e) {
            // TODO Auto-generated method stub
           
        }
    };
	
	private void doShareToQQ(final Bundle params) {
		// QQ分享要在主线程做
        ThreadManager.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
            	Tencent tencent = Tencent.createInstance(SuperConstants.TENCENT_APP_ID, mActivity);
            	tencent.shareToQQ(mActivity, params, qqShareListener); 
            }
        });
	}*/
	private void initComponent(View view) {
		mActivity = getActivity();
		mContext = mActivity.getApplicationContext();
		
		mExploreItems = new ArrayList<>();
		
		ExploreItem excelFolder = new ExploreItem();
		excelFolder.setId(1);
		excelFolder.setIconResourceId(R.drawable.folder_excel);
		excelFolder.setText(getActivity().getString(R.string.label_excel_form));
		mExploreItems.add(excelFolder);
		
		ExploreItem publishFolder = new ExploreItem();
		publishFolder.setId(2);
		publishFolder.setIconResourceId(R.drawable.folder_publish);
		publishFolder.setText(getActivity().getString(R.string.label_published));
		mExploreItems.add(publishFolder);
		
		ExploreItem addFolder = new ExploreItem();
		addFolder.setId(3);
		addFolder.setIconResourceId(R.drawable.folder_add);
		addFolder.setText(getActivity().getString(R.string.label_new_publish));
		mExploreItems.add(addFolder);
		
		ExploreItem fansFolder = new ExploreItem();
		fansFolder.setId(4);
		fansFolder.setIconResourceId(R.drawable.folder_fans);
		fansFolder.setText(getActivity().getString(R.string.label_my_fans));
		mExploreItems.add(fansFolder);
		
		gv_explore = (GridView) view.findViewById(R.id.gv_explore);
		mAdapter = new ExploreAdapter(mContext);
		mAdapter.setExploreItems(mExploreItems);
		mAdapter.setOnFolderClickListener(new OnFolderClickListener() {
			
			@Override
			public void onFolderClick(int id) {
				// TODO Auto-generated method stub
				switch (id) {
				case 1:
					// 查看本地Excel报表
					/*final Bundle params = new Bundle();
                	params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                	params.putString(QQShare.SHARE_TO_QQ_TITLE, "问卷");
                    params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, new File(Environment.getExternalStorageDirectory()+"/paper/haha.doc").getAbsolutePath());
	                doShareToQQ(params);*/
					startActivity(new Intent(mActivity, ExcelListActivity.class));
					
					break;
				case 2:
					// 已发布问卷
					startActivity(new Intent(mActivity, MonitorActivity.class));
					break;
				case 3:
					// 发布新问卷
					startActivity(new Intent(mActivity, AddQuestionnaireActivity.class));
					break;
				case 4:
					// 查看关注我的人
					startActivity(new Intent(mActivity, FansListActivity.class));
					break;
				default:
					break;
				}
			}
		});
		gv_explore.setAdapter(mAdapter);
	}
}
