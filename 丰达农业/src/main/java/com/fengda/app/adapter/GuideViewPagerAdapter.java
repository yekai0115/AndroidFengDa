package com.fengda.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class GuideViewPagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> fragmentList = new ArrayList<Fragment>();

	public GuideViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public GuideViewPagerAdapter(FragmentManager fragmentManager,
                                 List<Fragment> arrayList) {
		super(fragmentManager);
		this.fragmentList = arrayList;
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragmentList.get(arg0);
	}

	@Override
	public int getCount() {
		return fragmentList.size();
	}

}
