package com.lv.sdumap.ui.syllabus;

import androidx.appcompat.app.AlertDialog;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lv.sdumap.R;
import com.lv.sdumap.utils.AutomatedWebView;
import com.lv.sdumap.ui.syllabus.custom.MyOperator;
import com.lv.sdumap.ui.syllabus.model.MySubject;
import com.lv.sdumap.utils.BaseFragment;
import com.lv.sdumap.utils.Storage;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.OnItemBuildAdapter;
import com.zhuangfei.timetable.listener.OnSlideBuildAdapter;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.view.WeekView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 课程表
 */
public class SyllabusFragment extends BaseFragment {
    TimetableView mTimetableView;
    WeekView mWeekView;
    List<MySubject> mySubjects;
    AutomatedWebView hiddenWebView;
    View loadingCover;
    boolean isShowWeekends = false;
    Handler emptyHandler = new Handler();
    int currentWeek = 1;

    public static SyllabusFragment newInstance() {
        return new SyllabusFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_syllabus, container, false);
        mySubjects = new ArrayList<>();
        mWeekView = root.findViewById(R.id.id_weekview);
        mTimetableView = root.findViewById(R.id.id_timetableView);
        hiddenWebView = root.findViewById(R.id.syllabus_hidden_web_view);
        loadingCover = root.findViewById(R.id.loading_cover);
        hiddenWebView.init();
        isShowWeekends = Storage.getSingleton().getLong(Storage.KEY_SETTING_SYLLABUS_SHOW_WEEKEND, 0) == 1;

        resetCurrentWeek();
        initTimetableView();
        emptyHandler.post(this::updateData);
        emptyHandler.postDelayed(() -> {
            mTimetableView.source(mySubjects).updateView();
            mWeekView.source(mySubjects).showView();
        }, 1000);
        return root;
    }

    /**
     * 重设当前周
     */
    void resetCurrentWeek() {
        try {
            String strTime = Storage.getSingleton().getString(Storage.KEY_CLOUD_SYLLABUS_FIRST_DAY, "2022-09-05");
            long firstDay = new SimpleDateFormat("yyyy-MM-dd").parse(strTime).getTime() / 1000 / 3600 / 24;
            long today = new Date().getTime() / 1000 / 3600 / 24;
            long dif = today - firstDay;
            currentWeek = (int) (dif / 7) + 1;
        } catch (ParseException e) {
            e.printStackTrace();
            currentWeek = 1;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mTimetableView.onDateBuildListener().onHighLight();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_syllabus, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * 菜单点击事件
     *
     * @param item 菜单项
     * @return 点击事件是否被处理
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_syllabus_refresh) {
            retrieveSyllabus();
            return true;
        } else if (item.getItemId() == R.id.menu_syllabus_show_weekend) {
            isShowWeekends = !isShowWeekends;
            updateShowWeekends();
            return true;
        } else if (item.getItemId() == R.id.menu_syllabus_switch_week) {
            if (mWeekView.isShowing()) {
                mWeekView.isShow(false);
            } else {
                mWeekView.isShow(true);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 初始化课程表
     */
    private void initTimetableView() {
        // 设置周次选择
        mWeekView.source(mySubjects)
                .curWeek(currentWeek)
                .callback(week -> {
                    int cur = mTimetableView.curWeek();
                    mTimetableView.onDateBuildListener()
                            .onUpdateDate(cur, week);
                    mTimetableView.changeWeekOnly(week);
                    currentWeek = week;
                })
                .isShow(false)
                .showView();
        // 设置课程表
        mTimetableView.source(mySubjects)
                .operater(new MyOperator())
                .isShowWeekends(isShowWeekends)
                .curWeek(currentWeek)
                .maxSlideItem(5)
                .monthWidthDp(30)
                .isShowNotCurWeek(false)
                .callback((ISchedule.OnItemClickListener) (v, scheduleList) -> display(scheduleList))
                .showView();
        mTimetableView.cornerAll(20).marTop(5).marLeft(5)
                .monthWidthDp(50)
                .isShowFlaglayout(false)
                .callback(new OnItemBuildAdapter() {
                    @Override
                    public String getItemText(Schedule schedule, boolean isThisWeek) {
                        if (schedule == null || TextUtils.isEmpty(schedule.getName()))
                            return "null";
                        if (schedule.getRoom() == null) {
                            if (!isThisWeek)
                                return "[非本周]" + schedule.getName();
                            return schedule.getName();
                        }
                        String r = schedule.getName() + "\n@" + schedule.getRoom();
                        if (!isThisWeek) {
                            r = "[非本周]" + r;
                        }
                        return r;
                    }

                    @Override
                    public void onItemUpdate(FrameLayout layout, TextView textView, TextView countTextView, Schedule schedule, GradientDrawable gd) {
                        super.onItemUpdate(layout, textView, countTextView, schedule, gd);
                        textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                    }
                })
                .updateView();
        showTime();
    }

    /**
     * 显示时间槽的时间
     */
    protected void showTime() {
        String strTime = Storage.getSingleton().getString(Storage.KEY_CLOUD_SYLLABUS_TIME, "8:00\nam,10:10\nam,2:00\npm,4:10\npm,7:00\npm");
        String[] times = strTime.split(",");
        OnSlideBuildAdapter listener = (OnSlideBuildAdapter) mTimetableView.onSlideBuildListener();
        listener.setTimes(times).setTimeTextColor(Color.BLACK);
        mTimetableView.updateSlideView();
    }

    /**
     * 用户刷新课程表后显示 hiddenWebView 并拉取课程表
     */
    public void retrieveSyllabus() {
        hiddenWebView.setVisibility(View.VISIBLE);
        hiddenWebView.open(new AutomatedWebView.AutoProcedure(
                loadingCover, true,
                "http://bkzhjx.wh.sdu.edu.cn/sso.jsp",
                "https://bkzhjx.wh.sdu.edu.cn/jsxsd/framework/xsMainV.htmlx",
                "https://bkzhjx.wh.sdu.edu.cn/jsxsd/xskb/xskb_list.do",
                null, null,
                html -> emptyHandler.post(() -> {
                    hiddenWebView.setVisibility(View.INVISIBLE);
                    Storage.getSingleton().setString(Storage.KEY_SETTING_SYLLABUS_HTML, html);
                    updateData();
                    Toast.makeText(SyllabusFragment.this.getContext(), "刷新成功！", Toast.LENGTH_SHORT).show();
                })
        ));
    }


    /**
     * 更新是否显示周末
     */
    void updateShowWeekends() {
        mTimetableView.isShowWeekends(isShowWeekends).updateView();
        Storage.getSingleton().setLong(Storage.KEY_SETTING_SYLLABUS_SHOW_WEEKEND, isShowWeekends ? 1 : 0);
    }

    /**
     * 用刚获取的数据刷新 ui
     */
    private void updateData() {
        mySubjects = null;
        try {
            String html = Storage.getSingleton().getString(Storage.KEY_SETTING_SYLLABUS_HTML, "");
            if (!html.isEmpty()) {
                SyllabusParser parser = new SyllabusParser();
                mySubjects = parser.parseHtml(html);
            }
        } catch (Exception e) {
            mySubjects = null;
            e.printStackTrace();
        }
        if (mySubjects == null) mySubjects = new ArrayList<>();
        mTimetableView.source(mySubjects).updateView();
        mWeekView.source(mySubjects).showView();
        showTime();
        if (mySubjects.isEmpty()) {
            Toast.makeText(getActivity(), "当前没有课程信息，请点击右上角【刷新课程表】。", Toast.LENGTH_LONG).show();
        }
    }

    String getCommentStorageKey(String name) {
        return Storage.KEY_SETTING_SYLLABUS_COURSE_COMMENT_PREFIX + name + "_s";
    }

    /**
     * 点击了一个时间槽，显示课程详情对话框
     *
     * @param schedules 该时间槽的课程
     */
    protected void display(List<Schedule> schedules) {
        Schedule schedule = schedules.get(0);
        for (Schedule schedule1 : schedules) {
            if (schedule1.getWeekList().contains(currentWeek)) schedule = schedule1;
        }
        final String title = schedule.getName();
        String message = "教室： " + schedule.getRoom()
                + "\n教师： " + schedule.getTeacher()
                + "\n备注： " + Storage.getSingleton().getString(getCommentStorageKey(schedule.getName()), "<无>");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message);
        builder.setNeutralButton("编辑备注", (dialog, which) -> {
            dialog.dismiss();
            SyllabusFragment.this.showDialogEditComment(title);
        });
        builder.setPositiveButton("确定", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog ad = builder.create();
        ad.setCancelable(true);
        ad.setCanceledOnTouchOutside(true);
        ad.show();
    }

    /**
     * 显示编辑课程备注对话框
     *
     * @param courseName 课程名
     */
    private void showDialogEditComment(final String courseName) {
        final EditText inputServer = new EditText(getActivity());
        inputServer.setText(Storage.getSingleton().getString(getCommentStorageKey(courseName), ""));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("编辑备注 - " + courseName).setView(inputServer)
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("确定", (dialog, which) -> {
            String str = inputServer.getText().toString();
            if (str.length() == 0) {
                Storage.getSingleton().remove(getCommentStorageKey(courseName));
            } else {
                Storage.getSingleton().setString(getCommentStorageKey(courseName), str);
            }
            Toast.makeText(getContext(), "已保存。", Toast.LENGTH_SHORT).show();
        });
        AlertDialog ad = builder.create();
        ad.setCancelable(true);
        ad.setCanceledOnTouchOutside(false);
        ad.show();
    }
}