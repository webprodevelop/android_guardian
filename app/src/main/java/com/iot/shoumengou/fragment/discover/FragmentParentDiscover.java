package com.iot.shoumengou.fragment.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.iot.shoumengou.R;
import com.iot.shoumengou.fragment.FragmentDevice;

import java.util.List;
import java.util.Stack;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class FragmentParentDiscover extends Fragment {
    private final Stack<String> tagStack = new Stack<>();

    public FragmentParentDiscover() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parent_discover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FragmentNewDiscover childFragment = new FragmentNewDiscover();
        pushChildFragment(childFragment, FragmentNewDiscover.class.getSimpleName());
    }

    public void pushChildFragment(Fragment child, String tag) {
        if (getChildFragment(tag) != null)
            return;

        tagStack.push(tag);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, child, tag).addToBackStack(tag).commit();
    }

    public int getBackStackSize() {
        return tagStack.size();
    }

    public void popChildFragment(boolean updatedMonitoringDevice) {
        if (tagStack.size() == 1) {
            return;
        }
        getChildFragmentManager().popBackStack(tagStack.pop(), FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Fragment lastInstance = getChildFragmentManager().findFragmentByTag(tagStack.lastElement());

        if (lastInstance.getClass().getSimpleName().equals(FragmentNewDiscover.class.getSimpleName())) {
            if (updatedMonitoringDevice) {
                ((FragmentNewDiscover) lastInstance).loadLastHealthData();
            }
            ((FragmentNewDiscover) lastInstance).updateMonitoringWatchStatus();
        }
        else if (lastInstance.getClass().getSimpleName().equals(FragmentReport.class.getSimpleName())) {
            ((FragmentReport)lastInstance).updateStatus();
        }
    }

    public void showReportFromPushNotification(int type) {
        if (tagStack.lastElement().equals(FragmentReport.class.getSimpleName())) {
            List<Fragment> childList = getChildFragmentManager().getFragments();
            FragmentReport instance = (FragmentReport)childList.get(childList.size() - 1);
            instance.updateStatus();
            return;
        }
        while(tagStack.size() > 1) {
            getChildFragmentManager().popBackStack(tagStack.pop(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        FragmentReport instance = new FragmentReport();
        instance.setActiveTabIndex(type);
        pushChildFragment(instance, FragmentReport.class.getSimpleName());
    }

    public void updateDeviceListIfNeeded() {
        if (tagStack.lastElement().equals(FragmentDevice.class.getSimpleName())) {
            List<Fragment> childList = getChildFragmentManager().getFragments();
            FragmentDevice instance = (FragmentDevice)childList.get(childList.size() - 1);
            instance.getWatchList(false);
            instance.getSensorList();
        }
    }

    public Fragment getChildFragment(String tag) {
        return getChildFragmentManager().findFragmentByTag(tag);
    }
}