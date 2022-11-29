package com.lv.sdumap.ui.grade;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.lv.sdumap.R;
import com.lv.sdumap.utils.SizeUtils;

/**
 * 成绩类
 */
public class Grade implements Comparable<Grade> {
    public String semester;
    public String name;
    public String gradePart1;
    public String gradePart2;
    public String grade;
    public String gpa;
    public String level;
    public String credits;

    /**
     * 用于按学分降序排序
     */
    @Override
    public int compareTo(Grade o) {
        return -Float.compare(Float.parseFloat(credits), Float.parseFloat(o.credits));
    }

    @Override
    public String toString() {
        return "Grade{" +
                "semester='" + semester + '\'' +
                ", name='" + name + '\'' +
                ", gradePart1='" + gradePart1 + '\'' +
                ", gradePart2='" + gradePart2 + '\'' +
                ", grade='" + grade + '\'' +
                ", gpa='" + gpa + '\'' +
                ", level='" + level + '\'' +
                ", credits='" + credits + '\'' +
                '}';
    }

    /**
     * 向 gridLayout 中绘制该成绩
     *
     * @param context    context
     * @param gridLayout gridLayout
     * @param index      index
     */
    public void addRow(Context context, GridLayout gridLayout, int index) {
        boolean isGray = index % 2 == 1;
        gridLayout.addView(newTextView(context, semester, isGray, 100));
        gridLayout.addView(newTextViewLong(context, name, isGray));
        gridLayout.addView(newTextView(context, credits, isGray, 55));
        gridLayout.addView(newTextView(context, grade, isGray, 55));
        gridLayout.addView(newTextView(context, gpa, isGray, 55));
        gridLayout.addView(newTextView(context, level, isGray, 55));
        gridLayout.addView(newTextView(context, gradePart1, isGray, 80));
        gridLayout.addView(newTextView(context, gradePart2, isGray, 80));
    }

    /**
     * 创建 TextView
     */
    private TextView newTextView(Context context, String text, boolean isGray, int width) {
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        int margin = SizeUtils.dp2px(2);
        layoutParams.setMargins(margin, margin, margin, margin);
        TextView tv = new TextView(context);
        tv.setTextColor(ContextCompat.getColor(context, R.color.black));
        tv.setText(text);
        tv.setWidth(SizeUtils.dp2px(width));
        tv.setHeight(SizeUtils.dp2px(30));
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(layoutParams);

        if (isGray) tv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.gray));
        return tv;
    }

    /**
     * 创建 TextView
     */
    private TextView newTextViewLong(Context context, String text, boolean isGray) {
        TextView tv = newTextView(context, text, isGray, 55);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tv.setSingleLine(true);
        tv.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        tv.setWidth(SizeUtils.dp2px(120));
        tv.setHeight(SizeUtils.dp2px(30));
        return tv;
    }
}
