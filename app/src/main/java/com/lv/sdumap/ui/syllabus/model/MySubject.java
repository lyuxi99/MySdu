package com.lv.sdumap.ui.syllabus.model;

import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.model.ScheduleEnable;

import java.util.List;

/**
 * 一个时间槽的一个课程项
 */
public class MySubject implements ScheduleEnable {

    public static final String EXTRAS_ID = "extras_id";

    private int id = 0;

    private String name;

    private String time;

    private String room;

    private String teacher;

    /**
     * 第几周至第几周上
     */
    private List<Integer> weekList;

    /**
     * 开始上课的节次
     */
    private int start;

    /**
     * 上课节数
     */
    private int step;

    /**
     * 周几上
     */
    private int day;

    private String courseID;

    private int colorRandom = 0;

    private String xf;

    public String getXf() {
        return xf;
    }

    public void setXf(String xf) {
        this.xf = xf;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getCourseID() {
        return courseID;
    }

    public MySubject(String courseID, String name, String room, String teacher, List<Integer> weekList, int start, int step, int day, int colorRandom, String time) {
        super();
        this.courseID = courseID;
        this.name = name;
        this.room = room;
        this.teacher = teacher;
        this.weekList = weekList;
        this.start = start;
        this.step = step;
        this.day = day;
        this.colorRandom = colorRandom;
        this.time = time;
    }

    public MySubject(String courseID, String name, String xf, String room, String teacher, List<Integer> weekList, int start, int step, int day, int colorRandom, String time) {
        this.courseID = courseID;
        this.xf = xf;
        this.name = name;
        this.room = room;
        this.teacher = teacher;
        this.weekList = weekList;
        this.start = start;
        this.step = step;
        this.day = day;
        this.colorRandom = colorRandom;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setWeekList(List<Integer> weekList) {
        this.weekList = weekList;
    }

    public List<Integer> getWeekList() {
        return weekList;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getColorRandom() {
        return colorRandom;
    }

    public void setColorRandom(int colorRandom) {
        this.colorRandom = colorRandom;
    }

    @Override
    public Schedule getSchedule() {
        Schedule schedule = new Schedule();
        schedule.setDay(getDay());
        schedule.setName(getName());
        schedule.setRoom(getRoom());
        schedule.setStart(getStart());
        schedule.setStep(getStep());
        schedule.setTeacher(getTeacher());
        schedule.setWeekList(getWeekList());
        schedule.setColorRandom(2);
        schedule.setXf(getXf());
        schedule.putExtras(EXTRAS_ID, getId());
        return schedule;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "MySubject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", room='" + room + '\'' +
                ", teacher='" + teacher + '\'' +
                ", weekList=" + weekList +
                ", start=" + start +
                ", step=" + step +
                ", day=" + day +
                ", courseID='" + courseID + '\'' +
                ", colorRandom=" + colorRandom +
                ", xf='" + xf + '\'' +
                '}';
    }
}
