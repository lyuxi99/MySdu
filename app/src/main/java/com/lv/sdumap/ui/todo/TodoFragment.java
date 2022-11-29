package com.lv.sdumap.ui.todo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.lv.sdumap.R;
import com.lv.sdumap.ui.todo.view.MyViewPagerAdapter;
import com.lv.sdumap.utils.BaseFragment;

/**
 * 待办事项
 */
public class TodoFragment extends BaseFragment {

    TabLayout tabLayout;
    ViewPager2 viewPager;
    MyViewPagerAdapter viewPagerAdapter;
    public static TodoFragment newInstance() {
        return new TodoFragment();
    }
    public View rootView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_todo, container, false);
        rootView = root;
        tabLayout = root.findViewById(R.id.id_tab_layout);
        viewPager = root.findViewById(R.id.view_pager);
        viewPagerAdapter = new MyViewPagerAdapter(getActivity());
        viewPager.setUserInputEnabled(false);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
        TodoDataManager.getSingleton().lazyLoad();
        return root;
    }
}