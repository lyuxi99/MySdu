package com.lv.sdumap.ui.exam_schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lv.sdumap.R;
import com.lv.sdumap.utils.BaseFragment;

/**
 * 考试安排查询，未开发
 */
public class ExamScheduleFragment extends BaseFragment {

    public static ExamScheduleFragment newInstance() {
        return new ExamScheduleFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exam_schedule, container, false);
    }

}