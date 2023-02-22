package com.iot.shoumengou.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterPager extends FragmentStatePagerAdapter {
	private final ArrayList<Fragment> fragmentArrayList;

	public AdapterPager(FragmentManager fm, ArrayList<Fragment> fragmentArrayList) {
		super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		this.fragmentArrayList = fragmentArrayList;
	}

	@NotNull
	@Override
	public Fragment getItem(int position) {
		return fragmentArrayList.get(position);
	}

	@Override
	public int getCount() {
		return this.fragmentArrayList.size();
	}
}