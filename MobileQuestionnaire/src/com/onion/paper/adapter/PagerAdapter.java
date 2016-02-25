package com.onion.paper.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> mFragments;
	
	public PagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}
	
	public PagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
		super(fragmentManager);
		this.mFragments = fragments;
	}

	@Override
	public Fragment getItem(int index) {
		// TODO Auto-generated method stub
		return mFragments.get(index);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFragments.size();
	}
	
}
