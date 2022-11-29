package com.zhuangfei.timetable.model;

import android.content.Context;
import android.graphics.Color;

import com.zhuangfei.timetable.R;
import com.zhuangfei.timetable.utils.ColorUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 颜色池，管理课程项可挑选的颜色
 */

public class ScheduleColorPool {

    Context context;

    //课程不在本周时的背景色
    private int uselessColor;

    public ScheduleColorPool(Context context) {
        this.context = context;
        setUselessColor(context.getResources().getColor(R.color.useless));
        reset();
    }

    //使用集合维护颜色池
    private List<Integer> colorPool;

    /**
     * 获取非本周课程颜色
     *
     * @return
     */
    public int getUselessColor() {
        return uselessColor;
    }

    /**
     * 获取非本周课程颜色
     *
     * @return
     */
    public int getUselessColorWithAlpha(float alpha) {
        return ColorUtils.alphaColor(uselessColor, alpha);
    }

    /**
     * 设置非本周课程颜色
     *
     * @param uselessColor 非本周课程的颜色
     * @return
     */
    public ScheduleColorPool setUselessColor(int uselessColor) {
        this.uselessColor = uselessColor;
        return this;
    }

    /**
     * 得到颜色池的实例，即List集合
     *
     * @return
     */
    public List<Integer> getPoolInstance() {
        if (colorPool == null) colorPool = new ArrayList<>();
        return colorPool;
    }

    /**
     * 从颜色池中取指定透明度的颜色
     *
     * @param random
     * @param alpha
     * @return
     */
    public int getColorAutoWithAlpha(int random, float alpha) {
        if (random < 0) return getColorAuto(-random);
        return ColorUtils.alphaColor(getColor(random % size()), alpha);
    }

    /**
     * 根据索引获取颜色，索引越界默认返回 Color.GRAY
     *
     * @param i 索引
     * @return
     */
    public int getColor(int i) {
        if (i < 0 || i >= size()) return Color.GRAY;
        return colorPool.get(i);
    }

    /**
     * 使用模运算根据索引从颜色池中获取颜色,
     * 如果i<0，转换为正数,
     * 否则：重新计算索引j=i mod size
     *
     * @param i 索引
     * @return 颜色
     */
    public int getColorAuto(int i) {
        if (i < 0) return getColorAuto(-i);
        return getColor(i % size());
    }

    /**
     * 将指定集合中的颜色加入到颜色池中
     *
     * @param ownColorPool 集合
     * @return
     */
    public ScheduleColorPool addAll(Collection<? extends Integer> ownColorPool) {
        getPoolInstance().addAll(ownColorPool);
        return this;
    }

    /**
     * 颜色池的大小
     *
     * @return
     */
    public int size() {
        if (getPoolInstance() == null) return 0;
        return getPoolInstance().size();
    }

    /**
     * 清空颜色池，清空默认颜色
     *
     * @return
     */
    public ScheduleColorPool clear() {
        getPoolInstance().clear();
        return this;
    }

    /**
     * 在颜色池中添加一些自定义的颜色
     *
     * @param colorIds 多个颜色
     * @return
     */
    public ScheduleColorPool add(int... colorIds) {
        if (colorIds != null) {
            for (int i = 0; i < colorIds.length; i++) {
                colorPool.add(colorIds[i]);
            }
        }
        return this;
    }

    /**
     * 重置，先在池子里添加一些默认的课程项颜色
     *
     * @return
     */
    public ScheduleColorPool reset() {
        int[] colors = new int[]{
                R.color.my_color_1, R.color.my_color_2,
                R.color.my_color_3, R.color.my_color_4,
                R.color.my_color_5, R.color.my_color_6,
                R.color.my_color_7, R.color.my_color_8,
                R.color.my_color_9, R.color.my_color_10,
                R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4,
                R.color.color_5, R.color.color_6, R.color.color_7, R.color.color_8,
                R.color.color_9, R.color.color_10, R.color.color_11, R.color.color_31,
                R.color.color_32, R.color.color_33, R.color.color_34, R.color.color_35,
        };

        clear();

        for (int i = 0; i < colors.length; i++) {
            add(context.getResources().getColor(colors[i]));
        }
        return this;
    }
}
