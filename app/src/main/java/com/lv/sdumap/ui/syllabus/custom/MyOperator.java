package com.lv.sdumap.ui.syllabus.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lv.sdumap.utils.SizeUtils;
import com.zhuangfei.timetable.R;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.model.ScheduleSupport;
import com.zhuangfei.timetable.operater.SimpleOperater;

import java.util.List;

/**
 * 课表业务操作者
 */
public class MyOperator extends SimpleOperater {

    @Override
    protected void initAttr(AttributeSet attrs) {
        super.initAttr(attrs);
        final SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        int itemHeight = sharedPreferences.getInt("itemHeight", 0);
        mView.itemHeight(itemHeight);
        mView.post(() -> {
            int itemHeight1 =Math.max((mView.getHeight()-mView.marTop()*3-getDateLayout().getHeight())/5-mView.marTop(), SizeUtils.dp2px(80));
            mView.itemHeight(itemHeight1);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("itemHeight", itemHeight1);
            editor.apply();
        });
    }

    @Override
    public void startTodo(){
        for (int i = 0; i < 7; i++) {
            data[i].clear();
        }
        List<Schedule> source = mView.dataSource();
        for (int i = 0; i < source.size(); i++) {
            Schedule bean = source.get(i);
            if (bean.getDay() != -1)
                data[bean.getDay() - 1].add(bean);
        }

        //排序、填充课程
        ScheduleSupport.sortList(data);
        for (int i = 0; i < panels.length; i++) {
            panels[i].removeAllViews();
            drawWeekday(panels[i], data[i], mView.curWeek());
        }
    }
    @Override
    public void changeWeek(int week, boolean isCurWeek) {
//        Log.e(TAG, "change week");
        for (int i = 0; i < panels.length; i++) {
            drawWeekday(panels[i], data[i], week);
        }
        if (isCurWeek) {
            mView.curWeek(week);
        } else {
            mView.onWeekChangedListener().onWeekChanged(week);
        }
    }

    void drawWeekday(LinearLayout layout, final List<Schedule> data, int curWeek) {
//        Log.e(TAG, "draw weekday");
        if (layout == null || data == null) return;
        layout.removeAllViews();

        //遍历
        List<Schedule> filter = ScheduleSupport.fliterSchedule(data, curWeek, mView.isShowNotCurWeek());
        int cur = 1;
        for (int i = 0; i < filter.size(); i++) {
            while (cur < filter.get(i).getStart()) {
//                Log.e(TAG, "empty item");
                View view = newEmptyView();
                if (view != null) layout.addView(view);
                cur++;
            }
            final Schedule subject = filter.get(i);
            View view = newItemView(data, filter, subject, i, curWeek);
            if (view != null) {
                layout.addView(view);
            }
            cur = filter.get(i).getStart() + filter.get(i).getStep();
        }
        while(cur<=mView.maxSlideItem()){
            View view = newEmptyView();
            if (view != null) layout.addView(view);
            cur++;
        }
    }

    private View newEmptyView() {
        //宽高
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = mView.itemHeight();

        //边距
        int left = mView.marLeft() / 2, right = mView.marLeft() / 2;
        int top = mView.marTop();

        // 设置Params
        View view = inflater.inflate(R.layout.item_timetable, null, false);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        lp.setMargins(left, top, right, 0);

        view.setBackgroundColor(Color.TRANSPARENT);
        FrameLayout layout = view.findViewById(R.id.id_course_item_framelayout);
        layout.setLayoutParams(lp);

//        boolean isThisWeek = ScheduleSupport.isThisWeek(subject, curWeek);
        TextView textView = (TextView) view.findViewById(R.id.id_course_item_course);
        TextView countTextView = (TextView) view.findViewById(R.id.id_course_item_count);
//        textView.setText(mView.onItemBuildListener().getItemText(subject, isThisWeek));
        countTextView.setText("");
        countTextView.setVisibility(View.GONE);

        GradientDrawable gd = new GradientDrawable();
        textView.setTextColor(mView.itemTextColorWithNotThis());
        gd.setCornerRadius(mView.corner(false));
//        gd.setStroke(2, Color.GRAY);
        gd.setStroke(2, Color.argb(255,200,200,200));
        textView.setBackgroundDrawable(gd);
        return view;
    }

    private View newItemView(final List<Schedule> originData, final List<Schedule> data, final Schedule subject, int i, int curWeek) {
        //宽高
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = mView.itemHeight() * subject.getStep() + mView.marTop() * (subject.getStep() - 1);

        //边距
        int left = mView.marLeft() / 2, right = mView.marLeft() / 2;
        int top = mView.marTop();

        if (i != 0 && top < 0) return null;

        // 设置Params
        View view = inflater.inflate(R.layout.item_timetable, null, false);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        lp.setMargins(left, top, right, 0);

        view.setBackgroundColor(Color.TRANSPARENT);
        view.setTag(subject);
        FrameLayout layout = view.findViewById(R.id.id_course_item_framelayout);
        layout.setLayoutParams(lp);

        boolean isThisWeek = ScheduleSupport.isThisWeek(subject, curWeek);
        TextView textView = (TextView) view.findViewById(R.id.id_course_item_course);
        TextView countTextView = (TextView) view.findViewById(R.id.id_course_item_count);
        textView.setText(mView.onItemBuildListener().getItemText(subject, isThisWeek));
        textView.setGravity(Gravity.CENTER);

        countTextView.setText("");
        countTextView.setVisibility(View.GONE);

        GradientDrawable gd = new GradientDrawable();
        if (isThisWeek) {
            textView.setTextColor(mView.itemTextColorWithThisWeek());
            gd.setColor(mView.colorPool().getColorAutoWithAlpha(subject.getColorRandom(), mView.itemAlpha()));
            gd.setCornerRadius(mView.corner(true));

            List<Schedule> clist = ScheduleSupport.findSubjects(subject, originData);
            int count = 0;
            if (clist != null) {
                for (int k = 0; k < clist.size(); k++) {
                    Schedule p = clist.get(k);
                    if (p != null && ScheduleSupport.isThisWeek(p, curWeek)) count++;
                }
            }
            if (count > 1) {
                countTextView.setVisibility(View.VISIBLE);
                countTextView.setText(count + "");
            }
        } else {
            textView.setTextColor(mView.itemTextColorWithNotThis());
            gd.setColor(mView.colorPool().getUselessColorWithAlpha(mView.itemAlpha()));
            gd.setCornerRadius(mView.corner(false));
        }

        textView.setBackgroundDrawable(gd);
        mView.onItemBuildListener().onItemUpdate(layout, textView, countTextView, subject, gd);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Schedule> result = ScheduleSupport.findSubjects(subject, originData);
                mView.onItemClickListener().onItemClick(v, result);
            }
        });

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mView.onItemLongClickListener().onLongClick(view, subject.getDay(), subject.getStart());
                return true;
            }
        });

        return view;
    }
}
