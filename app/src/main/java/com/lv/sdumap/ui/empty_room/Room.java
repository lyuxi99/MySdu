package com.lv.sdumap.ui.empty_room;

import java.util.Arrays;
import java.util.Locale;

/**
 * 教室类
 */
public class Room implements Comparable<Room> {
    String name; // 教室名
    String numSeats; // 座位数
    String[] status; // 5个时间槽分别的状态

    public Room() {
        status = new String[5];
        for (int i = 0; i < 5; i++) status[i] = "";
    }

    /**
     * 获取教室名和座位数，用于显示
     * @return 教室名 (座位数)
     */
    public String getNameWithSeats() {
        return String.format(Locale.CHINESE, "%s (%s)", name, numSeats);
    }

    /**
     * 用于按教室名升序排序
     */
    @Override
    public int compareTo(Room o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return "Room{" +
                "name='" + name + '\'' +
                ", numSeats=" + numSeats +
                ", status=" + Arrays.toString(status) +
                '}';
    }
}
