//@formatter:off
package com.iot.shoumengou.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.iot.shoumengou.R;
import com.iot.shoumengou.activity.ActivityDiscoverDetail;
import com.iot.shoumengou.activity.ActivityMain;
import com.iot.shoumengou.adapter.AdapterInfo;
import com.iot.shoumengou.fragment.discover.FragmentParentDiscover;
import com.iot.shoumengou.model.ItemDiscover;
import com.iot.shoumengou.util.Util;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentDiscover extends Fragment implements View.OnClickListener {
	private ImageView ivBack;
	private ListView  mDiscoverList;
	private ArrayList<ItemDiscover>		mDiscoverArray = new ArrayList<>();

	public boolean isDiscovery;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_discover, container, false);

		loadDiscoverList();
		initControls(rootView);
		setEventListener();

		return rootView;
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.ID_IMGVIEW_BACK) {
			onBack();
		}
	}

	public void setIsDiscover(boolean isDiscover) {
		this.isDiscovery = isDiscover;
	}

	public void onBack() {
		FragmentParentDiscover parentFrag = ((FragmentParentDiscover) FragmentDiscover.this.getParentFragment());
		Objects.requireNonNull(parentFrag).popChildFragment(false);
	}

	private void initControls(View layout) {
		ivBack = layout.findViewById(R.id.ID_IMGVIEW_BACK);
		TextView ivTitle = layout.findViewById(R.id.ID_TXTVIEW_TITLE);
		mDiscoverList = layout.findViewById(R.id.ID_LSTVIEW_DISCOVER);

		AdapterInfo mDiscoverAdapter = new AdapterInfo(getActivity(), mDiscoverArray);
		mDiscoverList.setAdapter(mDiscoverAdapter);

		if (isDiscovery)
			ivTitle.setText(R.string.str_discover_title);
		else
			ivTitle.setText(R.string.str_hot_news);
	}

	private void setEventListener() {
		ivBack.setOnClickListener(this);
		mDiscoverList.setOnItemClickListener((adapterView, view, position, id) -> {
			ItemDiscover itemDiscover = mDiscoverArray.get(position);

			Intent intent = new Intent(getContext(), ActivityDiscoverDetail.class);
			intent.putExtra("discover_data", itemDiscover);
			startActivityForResult(intent, ActivityMain.REQUEST_DISCOVER_DETAIL);
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ActivityMain.REQUEST_DISCOVER_DETAIL) {
			boolean bNotification = false;
			for (ItemDiscover itemDiscover : mDiscoverArray) {
				if (itemDiscover.readCnt.equals("0")) {
					bNotification = true;
					break;
				}
			}

			((ActivityMain) Objects.requireNonNull(getActivity())).showDiscoverNotification(bNotification);
		}
	}

	public void loadDiscoverList() {
		if (isDiscovery)
			mDiscoverArray = Util.discoverList;
		else
			mDiscoverArray = Util.recommendedList;
	}
}
